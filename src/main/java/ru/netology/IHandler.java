package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class IHandler implements Runnable {
    private final Socket socket;

    private final Server server;

    IHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try (
                final var in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                final var out = new BufferedOutputStream(this.socket.getOutputStream());) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var request = Request.parse(in);

            if (request == null) {
                out.write(("HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: " + 0 + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());
                out.flush();
                return;
            }

            var methodMap = server.getHandlers().get(request.getMethod());

            if (methodMap == null) {
                out.write(("HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + 0 + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());
                out.flush();
                return;
            }

            var handler = methodMap.get(request.getPath());
            if (handler == null) {
                out.write(("HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + 0 + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());
                out.flush();
                return;
            }

            handler.handle(request, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
