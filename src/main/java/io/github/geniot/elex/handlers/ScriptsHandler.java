package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class ScriptsHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> paramsMap = queryToMap(httpExchange.getRequestURI().getQuery());
        writeTxt(httpExchange, "", textTypes.get(ContentType.JSON.label));
    }
}
