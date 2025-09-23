package com.joiner.ebus.io.mock.server;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class EbusServer {

    private final EbusProperties props;
    private final List<Socket> clients = new CopyOnWriteArrayList<>();

    public EbusServer(EbusProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(props.getPort())) {
                log.info("Mock eBUS server listening on port " + props.getPort());

                while (true) {
                    Socket newClient = serverSocket.accept();
                    clients.add(newClient);
                    log.info("Client connected: " + newClient.getInetAddress());

                    new Thread(() -> handleClient(newClient), "ClientHandlerThread").start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "EbusServerThread").start();
    }

    private void handleClient(Socket client) {
        try (Socket c = client) {
            while (!c.isClosed()) {
                Thread.sleep(100);
            }
        } catch (Exception ignored) {
        } finally {
            clients.remove(client);
            log.info("Client disconnected: " + client.getInetAddress());
        }
    }

    public List<Socket> getClients() {
        return clients;
    }
}
