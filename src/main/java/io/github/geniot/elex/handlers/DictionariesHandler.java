package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictionariesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());

            Gson gson = new Gson();

            List<Dictionary> dictionaries = new ArrayList<>();

            dictionaries.add(genDictionary("Some fancy dictionary"));
            dictionaries.add(genDictionary("Explanatory dictionary"));
            dictionaries.add(genDictionary("French dictionary"));

            String s = gson.toJson(dictionaries.toArray(new Dictionary[dictionaries.size()]));
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private Dictionary genDictionary(String name) {
        Dictionary en = new Dictionary();
        en.setName(name);
        return en;
    }
}
