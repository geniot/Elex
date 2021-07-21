package io.github.geniot.elex.handlers;

import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class StylesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> paramsMap = queryToMap(httpExchange.getRequestURI().getQuery());
        Set<IDictionary> set = getDics(paramsMap.get("dics"));
        writeTxt(httpExchange, "", textTypes.get(ContentType.CSS.label));
    }
}