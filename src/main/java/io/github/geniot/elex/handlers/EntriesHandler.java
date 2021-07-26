package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.HtmlUtils;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntriesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            String headword = map.get("hwd");

            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            String article = dictionarySet.iterator().next().read(headword);
            article = HtmlUtils.toHtml(article);

            Gson gson = new Gson();
            List<Entry> entries = new ArrayList<>();
            entries.add(genEntry(headword, article));

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
