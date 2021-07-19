package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.DictionariesPool;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class InfoHandler extends BaseHttpHandler {

    public InfoHandler(Logger f) {
        super(f);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StringBuffer result = new StringBuffer();
        try {
            Map<String, String> paramsMap = queryToMap(httpExchange.getRequestURI().getQuery());
            String action = paramsMap.get("a");
            if (action == null) {
                throw new Exception(EXCEPTION_EMPTY);
            }
            if (action.equals("langs")) {
                Gson gson = new Gson();
                String[] langs = DictionariesPool.getInstance().getAvailableLanguages();
                String s = gson.toJson(langs);
                result.append(s);
            } else if (action.equals("dics")) {
                String sourceLang = paramsMap.get("sl").toLowerCase();
                String targetLang = paramsMap.get("tl").toLowerCase();
                if (sourceLang == null || targetLang == null) {
                    throw new Exception(EXCEPTION_EMPTY);
                } else {
                    Gson gson = new Gson();
                    Properties[] dics = DictionariesPool.getInstance().getDictionaries(sourceLang, targetLang);
                    String s = gson.toJson(dics);
                    result.append(s);
                }
            } else {
                throw new Exception(EXCEPTION_NOT_EXISTS);
            }
            writeTxt(httpExchange, result.toString(), "application/json");
        } catch (Exception e) {
            logger.log(e);
        }
    }

}