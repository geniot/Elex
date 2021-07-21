package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.geniot.elex.Logger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHttpHandler implements HttpHandler {
    public static final String EXCEPTION_EMPTY = "EXCEPTION_EMPTY";
    public static final String EXCEPTION_NOT_EXISTS = "EXCEPTION_NOT_EXISTS";

    public static final Map<String, String> textTypes = getTextTypes();
    public static final Map<String, String> binaryTypes = getBinaryTypes();

    public enum ContentType {
        JSON("json");
        public final String label;

        private ContentType(String label) {
            this.label = label;
        }
    }

    private static Map<String, String> getTextTypes() {
        Map<String, String> map = new HashMap<>();
        map.put("html", "text/html");
        map.put("css", "text/css");
        map.put("js", "text/javascript");
        map.put("json", "application/json");
        return map;
    }

    private static Map<String, String> getBinaryTypes() {
        Map<String, String> map = new HashMap<>();
        map.put("png", "image/png");
        map.put("ico", "image/x-icon");
        return map;
    }

    public Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    protected void writeTxt(HttpExchange httpExchange, String str, String contentType) {
        try {
            Writer out = new OutputStreamWriter(httpExchange.getResponseBody(), StandardCharsets.UTF_8);
            String value = contentType + "; charset=" + StandardCharsets.UTF_8;
            httpExchange.getResponseHeaders().put("Content-type", Collections.singletonList(value));
            httpExchange.sendResponseHeaders(200, str.getBytes(StandardCharsets.UTF_8).length);
            out.write(str);
            out.close();
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    protected void writeBinary(HttpExchange httpExchange, byte[] str, String contentType) {
        try {
            OutputStream out = httpExchange.getResponseBody();
            httpExchange.getResponseHeaders().put("Content-type", Collections.singletonList(contentType));
            httpExchange.sendResponseHeaders(200, str.length);
            out.write(str);
            out.close();
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }


}
