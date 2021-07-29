package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Index;
import io.github.geniot.indexedtreemap.IndexedTreeSet;

import java.util.*;


public class IndexHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            Set<String> inputIds = new HashSet(Arrays.asList(map.get("dics").split(",")));
            int visibleSize = Integer.parseInt(map.get("visibleSize"));
            int selectedIndex = visibleSize / 2;
            if (map.containsKey("selectedIndex")) {
                selectedIndex = Integer.parseInt(map.get("selectedIndex"));
            }
            if (selectedIndex > visibleSize - 1) {
                selectedIndex = visibleSize - 1;
            }
            String selectedHeadword = map.get("selectedHeadword");
            if (selectedHeadword == null) {
                selectedHeadword = "welcome";
            }

            Index index = new Index();

            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            IndexedTreeSet<String> combinedIndex = new IndexedTreeSet<>();

            for (IDictionary dictionary : dictionarySet) {
                Properties properties = dictionary.getProperties();
                String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
                if (inputIds.contains(String.valueOf(Dictionary.idFromName(name)))) {
                    combinedIndex.addAll(dictionary.getIndex());
                    break;
                }
            }

            SortedSet<String> headwords = new TreeSet<>();
            if (combinedIndex.size() <= visibleSize) {
                for (String s : combinedIndex) {
                    headwords.add(s);
                }
                index.setNeedsPagination(false);
            } else {
                index.setNeedsPagination(true);
                Iterator<String> tailIterator = combinedIndex.tailSet(selectedHeadword).iterator();
                Iterator<String> headIterator = combinedIndex.headSet(selectedHeadword).iterator();
                for (int i = 0; i < selectedIndex; i++) {
                    if (headIterator.hasNext()) {
                        headwords.add(headIterator.next());
                    }
                }
                for (int i = selectedIndex; i < visibleSize; i++) {
                    if (tailIterator.hasNext()) {
                        headwords.add(tailIterator.next());
                    }
                }
                while (headwords.size() < visibleSize && (headIterator.hasNext() || tailIterator.hasNext())) {
                    if (headIterator.hasNext()) {
                        headwords.add(headIterator.next());
                    }
                    if (tailIterator.hasNext()) {
                        headwords.add(tailIterator.next());
                    }
                }
            }
            Headword[] headwordsArray = new Headword[headwords.size()];
            int counter = 0;
            for (String s : headwords) {
                Headword hw = new Headword(s);
                if (s.equals(selectedHeadword)) {
                    hw.setSelected(true);
                }
                headwordsArray[counter++] = hw;
            }

            index.setHeadwords(headwordsArray);

            Gson gson = new Gson();
            String s = gson.toJson(index);
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private FullTextHit genHit(String name) {
        FullTextHit en = new FullTextHit();
        en.setDictionaryId("Some fancy dictionary".hashCode());
        Headword hw = new Headword(name);
        en.setHeadword(hw);
        en.setExtract("some text and then comes <b>bold</b>" + System.currentTimeMillis());
        return en;
    }
}
