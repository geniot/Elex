package io.github.geniot.elex;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class ElexLauncher {

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            ElexApplication application = new ElexApplication();
            application.setVisible(true);
        });
    }

}
