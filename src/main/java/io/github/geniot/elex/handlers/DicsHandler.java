package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.DictionariesPool;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class DicsHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> paramsMap = queryToMap(httpExchange.getRequestURI().getQuery());
        String sourceLang = paramsMap.get("sl").toLowerCase();
        String targetLang = paramsMap.get("tl").toLowerCase();
        if (sourceLang == null || targetLang == null) {
            writeTxt(httpExchange, EXCEPTION_EMPTY, ContentType.JSON.label);
        } else {
            Gson gson = new Gson();
            Properties[] dics = DictionariesPool.getInstance().getDictionaries(sourceLang, targetLang);
            String s = gson.toJson(dics);
            writeTxt(httpExchange, s, textTypes.get(ContentType.JSON.label));
        }
    }
}
