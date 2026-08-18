package com.joiner.ebus.communication.link;

import static com.joiner.ebus.communication.protherm.MasterData.SYN;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EbusReaderWriter {

    private static final int MAX_SIZE = 64;

    @Value("${ebus.adapter.host:127.0.0.1}")
    private String host;

    @Value("${ebus.adapter.port:3333}")
    private int port;

    @Value("${ebus.timeout:2000}")
    private int readTimeout;

    @Value("${ebus.watchdog.interval:30000}")
    private int watchdogInterval;

    @Value("${ebus.reconnect.pause:2000}")
    private int reconnectPause;

    @Value("${ebus.sync-bytes-between-telegrams:5}")
    private int syncBytesBetweenTelegrams;

    private volatile Socket socket;
    private volatile InputStream in;
    private volatile OutputStream out;

    private volatile boolean running = true;

    /**
     * Buffer for the currently received telegram.
     *
     * Accessed only by the readWriteLoop thread.
     */
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(MAX_SIZE);

    /**
     * Number of consecutive SYN bytes received.
     *
     * Accessed only by the readWriteLoop thread.
     */
    private int synCount = 0;

    /**
     * Queue of MasterData telegrams waiting to be sent to the eBUS adapter.
     *
     * The scheduler thread may add or replace entries in the queue. The
     * readWriteLoop thread removes entries from the queue.
     *
     * LinkedBlockingQueue is used because the queue is accessed by multiple
     * threads.
     */
    private final BlockingQueue<MasterData> masterDataQueue = new LinkedBlockingQueue<>();

    @Autowired
    private DataParser dataParser;

    @Autowired
    private DataEventFactory dataEventFactory;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void start() {
        Thread thread = new Thread(this::readWriteLoop, "readWriteLoop");
        thread.setDaemon(true);
        thread.start();

        log.info("eBUS reader/writer thread started");
    }

    @PreDestroy
    public void shutdown() {
        log.info("Stopping eBUS reader/writer");

        running = false;
        closeConnection();
    }

    /**
     * Atomically replaces the current MasterData queue with a new set of telegrams.
     *
     * This method may be called from another thread, for example by the scheduled
     * DataCollector.
     */
    public synchronized void replaceMasterDataQueue(List<MasterData> data) {
        masterDataQueue.clear();

        if (data != null && !data.isEmpty()) {
            masterDataQueue.addAll(data);
        }

        log.debug("MasterData queue replaced, size={}", masterDataQueue.size());
    }

    /**
     * Main communication loop.
     *
     * This thread is responsible for: - reading data from the socket - processing
     * received eBUS data - writing data to the socket
     */
    private void readWriteLoop() {

        long lastDataTime = System.currentTimeMillis();
        int reconnectAttempt = 0;

        while (running) {

            try {

                if (socket == null || socket.isClosed() || !socket.isConnected()) {

                    connect();

                    lastDataTime = System.currentTimeMillis();
                    reconnectAttempt = 0;
                }

                int b;

                try {
                    b = in.read();

                } catch (SocketTimeoutException e) {

                    /*
                     * A socket timeout is not an error by itself. The watchdog is used to detect a
                     * connection that appears to be alive but is no longer delivering data.
                     */
                    if (System.currentTimeMillis() - lastDataTime > watchdogInterval) {

                        throw new IOException(
                                "No data from adapter for " + watchdogInterval + " ms - assuming dead connection");
                    }

                    continue;
                }

                if (b == -1) {
                    throw new EOFException("End of stream reached");
                }

                lastDataTime = System.currentTimeMillis();

                processReceivedByte(b);

            } catch (Exception e) {

                if (!running) {
                    break;
                }

                reconnectAttempt++;

                int shift = Math.min(reconnectAttempt - 1, 5);
                int delay = Math.min(500 * (1 << shift), 30000);

                log.warn("Lost connection to {}:{}, retrying in {} ms " + "(attempt {}): {}", host, port, delay,
                        reconnectAttempt, e.toString());

                closeConnection();

                try {
                    Thread.sleep(delay);

                } catch (InterruptedException interrupted) {

                    Thread.currentThread().interrupt();

                    if (!running) {
                        break;
                    }
                }
            }
        }

        log.info("eBUS reader/writer thread stopped");
    }

    /**
     * Connects to the eBUS adapter.
     */
    private void connect() throws InterruptedException {

        int attempt = 0;

        while (running) {

            Socket newSocket = null;

            try {

                newSocket = new Socket(host, port);
                newSocket.setSoTimeout(readTimeout);

                InputStream newIn = newSocket.getInputStream();
                OutputStream newOut = newSocket.getOutputStream();

                /*
                 * Publish the new connection only after the socket and both streams have been
                 * successfully initialized.
                 */
                socket = newSocket;
                in = newIn;
                out = newOut;

                /*
                 * Discard any partially received telegram from the previous connection.
                 */
                resetReceiveState();

                log.info("Connected to eBUS server at {}:{}", newSocket.getInetAddress(), newSocket.getPort());

                /*
                 * Give the adapter some time to initialize after establishing the TCP
                 * connection.
                 */
                Thread.sleep(reconnectPause);

                break;

            } catch (Exception e) {

                attempt++;

                if (newSocket != null) {
                    try {
                        newSocket.close();
                    } catch (IOException ignored) {
                    }
                }

                int wait = Math.min(100 * attempt, 2000);

                if (attempt % 5 == 0) {
                    log.warn("Still waiting for eBUS server at {}:{} " + "after {} attempts. {}", host, port, attempt,
                            e.getMessage());
                }

                Thread.sleep(wait);
            }
        }
    }

    /**
     * Processes one byte received from the eBUS adapter.
     */
    private void processReceivedByte(int b) {

        if (b == SYN) {

            synCount++;

            /*
             * SYN is used as a synchronization point.
             *
             * If MasterData telegrams are waiting in the queue and enough consecutive SYN
             * bytes have been received, send the next MasterData telegram.
             */
            if (!masterDataQueue.isEmpty() && synCount >= syncBytesBetweenTelegrams) {

                sendMasterData();
                synCount = 0;

            } else {

                /*
                 * SYN marks the end of a Master telegram or acts as a synchronization point for
                 * the next telegram.
                 */
                processMasterData();
            }

            return;
        }

        /*
         * A normal data byte terminates the current SYN sequence.
         */
        synCount = 0;

        processByte(b);
    }

    /**
     * Processes one data byte belonging to a MasterSlave telegram.
     */
    private void processByte(int b) {

        buffer.write(b);

        byte[] data = buffer.toByteArray();

        MasterSlaveData masterSlaveData = dataParser.getMasterSlaveData(data);

        if (masterSlaveData != null) {

            publisher.publishEvent(dataEventFactory.getDataReadyEvent(masterSlaveData));

            buffer.reset();

            return;
        }

        /*
         * Prevent the buffer from growing indefinitely if an unknown or corrupted
         * telegram is received.
         */
        if (buffer.size() > MAX_SIZE) {

            log.warn("Frame buffer overflow (>{} bytes), resetting", MAX_SIZE);

            buffer.reset();
        }
    }

    /**
     * Processes a Master telegram terminated by a SYN byte.
     */
    private void processMasterData() {

        if (buffer.size() == 0) {
            return;
        }

        byte[] data = buffer.toByteArray();

        MasterData masterData = dataParser.getMasterData(data);

        if (masterData != null) {

            publisher.publishEvent(dataEventFactory.getDataReadyEvent(masterData));

            buffer.reset();
        }
    }

    /**
     * Sends the next MasterData telegram from the queue.
     *
     * This method is called only by the readWriteLoop thread.
     */
    private void sendMasterData() {

        MasterData data = masterDataQueue.poll();

        if (data == null) {
            return;
        }

        if (out == null) {

            log.warn("Cannot send MasterData - eBUS output stream is null");

            /*
             * Put the telegram back into the queue so it can be sent after the connection
             * is restored.
             */
            masterDataQueue.offer(data);

            return;
        }

        try {

            byte[] masterData = data.getMasterData();

            out.write(masterData);
            out.flush();

            log.debug("MasterData sent, {} bytes", masterData.length);

        } catch (IOException e) {

            /*
             * Put the telegram back into the queue before triggering the reconnect
             * procedure.
             */
            masterDataQueue.offer(data);

            throw new RuntimeException("Failed to write MasterData to eBUS", e);
        }
    }

    /**
     * Resets the state of the receive parser.
     *
     * This is important after reconnecting so that a partially received telegram
     * from the previous TCP connection is never combined with data received from
     * the new connection.
     */
    private void resetReceiveState() {
        buffer.reset();
        synCount = 0;
    }

    /**
     * Closes the current eBUS connection.
     */
    private synchronized void closeConnection() {

        /*
         * Never carry a partially received telegram over to a new connection.
         */
        resetReceiveState();

        InputStream currentIn = in;
        OutputStream currentOut = out;
        Socket currentSocket = socket;

        in = null;
        out = null;
        socket = null;

        try {
            if (currentIn != null) {
                currentIn.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (currentOut != null) {
                currentOut.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (currentSocket != null) {
                currentSocket.close();
            }
        } catch (Exception ignored) {
        }
    }
}