package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.CompressedDictionary;
import io.github.geniot.elex.model.DictionariesPool;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class BaseHttpHandler implements HttpHandler {
    public static final String EXCEPTION_EMPTY = "EXCEPTION_EMPTY";
    public static final String EXCEPTION_NOT_EXISTS = "EXCEPTION_NOT_EXISTS";
    Logger logger;

    public BaseHttpHandler(Logger f) {
        this.logger = f;
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
            logger.log(ex);
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
            logger.log(ex);
        }
    }

    protected Set<CompressedDictionary> getDics(Map<String, String> paramsMap) {
        if (paramsMap.get("dics") == null) {
            Set<CompressedDictionary> set = new HashSet<CompressedDictionary>();
            set.addAll(DictionariesPool.DICTIONARIES.values());
            return set;
        }
        Set<CompressedDictionary> set = new HashSet<CompressedDictionary>();
        String[] dicIdsStr = paramsMap.get("dics").split("_");
        for (String di : dicIdsStr) {
            if (!di.equals("")) {
                int id = Integer.parseInt(di);
                CompressedDictionary cd = DictionariesPool.DICTIONARIES.get(id);
                set.add(cd);
            }
        }
        return set;
    }

}
