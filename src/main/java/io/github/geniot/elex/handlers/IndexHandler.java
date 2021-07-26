package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.indexedtreemap.IndexedTreeSet;

import java.util.*;


public class IndexHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
//            Set<String> inputIds = new HashSet(Arrays.asList(map.get("dics").split(",")));
            int page = Integer.parseInt(map.get("page"));
            int pageSize = Integer.parseInt(map.get("pageSize"));

            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            IndexedTreeSet<String> index = dictionarySet.iterator().next().getIndex();
            String exact = index.exact(page * pageSize);
            SortedSet<String> tailSet = index.tailSet(exact);
            SortedSet<String> pageSet = new TreeSet<>();
            Iterator<String> iterator = tailSet.iterator();
            while (iterator.hasNext() && pageSet.size() < pageSize) {
                pageSet.add(iterator.next());
            }

//            for (IDictionary dictionary : dictionarySet) {
//                Properties properties = dictionary.getProperties();
//                String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
//                int id = Dictionary.idFromName(name);
//                if (inputIds.contains(String.valueOf(id))) {
//                    index.addAll(dictionary.getIndex());
//                }
//            }
            Gson gson = new Gson();

//            List<FullTextHit> hits = new ArrayList<>();
//            for (int i = 0; i < size; i++) {
//                hits.add(genHit(String.valueOf(i)));
//            }
//            searchResult.setHits(hits.toArray(new FullTextHit[hits.size()]));

            String s = gson.toJson(pageSet);
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
