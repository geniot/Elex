package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntriesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            Logger.getInstance().log(map.get("hwd"));
            Gson gson = new Gson();
            List<Entry> entries = new ArrayList<>();
            entries.add(genEntry(map.get("hwd")));

            String s = gson.toJson(entries.toArray(new Entry[entries.size()]));
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private Entry genEntry(String hwd) {
        Entry entry = new Entry();
        entry.setHeadword(hwd);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0;i<100;i++){
            stringBuffer.append("<p class=\"test\">hello <b>html</b><i>" + hwd + "</i></p>");
        }
        entry.setBody(stringBuffer.toString());
        return entry;
    }
}
