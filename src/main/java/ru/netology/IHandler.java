package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public interface IHandler {
    void handle(Request request, BufferedOutputStream responseStream) throws IOException;
}