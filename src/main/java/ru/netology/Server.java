package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private static volatile Server server = null;
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    private Server(int port, int poolSize) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public static synchronized Server getInstance(int port, int poolSize) throws IOException {

        if (server == null) {
            synchronized (Server.class) {
                if (server == null) {
                    server = new Server(port, poolSize);
                }
            }
        }
        return server;
    }

    public void run() {
        try {
            while (true) {
                pool.execute(new Handler(serverSocket.accept()));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }
}
