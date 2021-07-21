package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.DictionariesPool;

import java.io.IOException;

public class LangsHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new Gson();
        String[] langs = DictionariesPool.getInstance().getAvailableLanguages();
        String s = gson.toJson(langs);
        writeTxt(httpExchange, s, textTypes.get(ContentType.JSON.label));
    }
}
