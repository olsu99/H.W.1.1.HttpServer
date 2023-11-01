package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private static volatile Server server = null;
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public final ConcurrentHashMap<String, ConcurrentHashMap<String, IHandler>> getHandlers() {
        return handlers;
    }

    private ConcurrentHashMap<String, ConcurrentHashMap<String, IHandler>> handlers = new ConcurrentHashMap<>();

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

    public void run() { // run the service
        try {
            while (true) {
                pool.execute(new Handler(serverSocket.accept(), this));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }

    public void addHandler(String method, String path, IHandler handler) {
        var methodMap = handlers.get(method);

        if (methodMap == null) {
            methodMap = new ConcurrentHashMap<>();
            handlers.put(method, methodMap);
        }

        if (!methodMap.containsKey(path)) {
            methodMap.put(path, handler);
        }
    }
}
