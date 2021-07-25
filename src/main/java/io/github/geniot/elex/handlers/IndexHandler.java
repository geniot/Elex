package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            int size = Integer.parseInt(map.get("size"));
            String search = map.get("search");
            Logger.getInstance().log(String.valueOf(map.get("dics")));
            //todo use other parameters
            Gson gson = new Gson();
            SearchResult searchResult = new SearchResult();
            List<Headword> headwords = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                headwords.add(genHeadword(String.valueOf(i)));
            }
            searchResult.setHeadwords(headwords.toArray(new Headword[headwords.size()]));

            List<FullTextHit> hits = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                hits.add(genHit(String.valueOf(i)));
            }
            searchResult.setHits(hits.toArray(new FullTextHit[hits.size()]));

            String s = gson.toJson(searchResult);
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

    private FullTextHit genHit(String name) {
        FullTextHit en = new FullTextHit();
        en.setDictionaryId("Some fancy dictionary".hashCode());
        Headword hw = new Headword();
        hw.setText(name);
        en.setHeadword(hw);
        en.setExtract("some text and then comes <b>bold</b>" + System.currentTimeMillis());
        return en;
    }
}
