package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Headword;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            int size = Integer.parseInt(map.get("size"));
//            Logger.getInstance().log(String.valueOf(size));
            //todo use other parameters
            Gson gson = new Gson();
            List<Headword> headwords = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                headwords.add(genHeadword(String.valueOf(i)));
            }
            String s = gson.toJson(headwords.toArray(new Headword[headwords.size()]));
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private Headword genHeadword(String name) {
        Headword en = new Headword();
        en.setText(String.valueOf(name.hashCode()));
        return en;
    }
}
