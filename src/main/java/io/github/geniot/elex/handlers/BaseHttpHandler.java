package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.geniot.elex.util.Logger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHttpHandler implements HttpHandler {

    public enum ContentType {
        JSON, PNG, CSS, TEXT, HTML, JS, ICO
    }


    public static EnumMap<ContentType, String> contentTypesMap = getContentTypes();

    private static EnumMap<ContentType, String> getContentTypes() {
        EnumMap<ContentType, String> cTypes = new EnumMap<>(ContentType.class);
        cTypes.put(ContentType.JSON, "application/json");
        cTypes.put(ContentType.PNG, "image/png");
        cTypes.put(ContentType.ICO, "image/x-icon");
        cTypes.put(ContentType.CSS, "text/css");
        cTypes.put(ContentType.TEXT, "text/plain");
        cTypes.put(ContentType.HTML, "text/html");
        cTypes.put(ContentType.JS, "text/javascript");
        return cTypes;
    }


    public Map<String, String> queryToMap(String query) {
        if (query == null) {
            return new HashMap<>();
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
            httpExchange.getResponseHeaders().put("Access-Control-Allow-Origin", Collections.singletonList("*"));
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
