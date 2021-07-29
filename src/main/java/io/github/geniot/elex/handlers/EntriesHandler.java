package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.HtmlUtils;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Entry;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EntriesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getRawQuery());
            String headword = URLDecoder.decode(map.get("hwd"), StandardCharsets.UTF_8.name());
            Set<String> inputIds = new HashSet(Arrays.asList(map.get("dics").split(",")));

            String article = "";
            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            for (IDictionary dictionary : dictionarySet) {
                Properties properties = dictionary.getProperties();
                String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
                if (inputIds.contains(String.valueOf(Dictionary.idFromName(name)))) {
                    article = dictionary.read(headword);
                    break;
                }
            }

            Gson gson = new Gson();
            List<Entry> entries = new ArrayList<>();
            if (article != null) {
                article = HtmlUtils.toHtml(article);
                entries.add(genEntry(headword, article));
            }
            String s = gson.toJson(entries.toArray(new Entry[entries.size()]));
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private Entry genEntry(String hwd, String article) {
        Entry entry = new Entry();
        entry.setHeadword(hwd);
        entry.setBody(article);
        return entry;
    }
}
