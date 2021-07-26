package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Dictionary;

import java.util.*;

public class DictionariesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            String sl = map.get("sl");
            String tl = map.get("tl");

            Set<String> inputIds = new HashSet();
            if (map.get("deselected") != null) {
                inputIds.addAll(Arrays.asList(map.get("deselected").replaceAll("\\[|\\]", "").split(",")));
            }

            List<Dictionary> dictionaries = new ArrayList<>();
            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            for (IDictionary dictionary : dictionarySet) {
                Properties properties = dictionary.getProperties();
                String sourceLanguage = properties.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name()).toUpperCase();
                String contentsLanguage = properties.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name()).toUpperCase();
                if (sourceLanguage.equals(sl) && contentsLanguage.equals(tl)) {
                    String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
                    Dictionary uiDictionary = new Dictionary();
                    uiDictionary.setName(name);
                    uiDictionary.setSelected(!inputIds.contains(String.valueOf(uiDictionary.getId())));
                    dictionaries.add(uiDictionary);
                }
            }

            Gson gson = new Gson();
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
