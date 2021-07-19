package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class MyHandler extends BaseHttpHandler {


    public MyHandler(Logger f) {
        super(f);
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String path = t.getRequestURI().toString();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf('?'));
        }
        if (path.equals("")) {
            path = "index.html";
        }
        path = "web/" + path;


        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (is == null) {
            logger.log("Not found: " + path);
        }
        byte[] bbs = IOUtils.toByteArray(is);


        String encoding = StandardCharsets.UTF_8.name();
        if (path.endsWith(".html")) {
            String value = "text/html; charset=" + encoding;
            t.getResponseHeaders().put("Content-type", Collections.singletonList(value));

            String str = new String(bbs, encoding);
            str = str.replaceAll("\\$\\{TIMESTAMP\\}", String.valueOf(System.currentTimeMillis()));

            t.sendResponseHeaders(200, str.getBytes(StandardCharsets.UTF_8).length);
            Writer out = new OutputStreamWriter(t.getResponseBody(), encoding);
            out.write(str);
            out.close();
        } else {
            t.sendResponseHeaders(200, bbs.length);
            OutputStream os = t.getResponseBody();
            os.write(bbs);
            os.close();
        }
    }
}
