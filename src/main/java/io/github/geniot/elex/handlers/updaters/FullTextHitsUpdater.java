package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.Logger;
import io.github.geniot.elex.ezip.model.DslProperty;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.ftindexer.FtServer;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Model;

import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

public class FullTextHitsUpdater {
    public void updateFullTextHits(Model model) {
        if (model.getLockFullText()) {
            return;
        }
        try {
            SortedMap<Float, FullTextHit> hits = new TreeMap<>();
            Map<String, ElexDictionary> dictionarySet = DictionariesPool.getInstance().getElexDictionaries(model);
            for (String fileName : dictionarySet.keySet()) {
                ElexDictionary elexDictionary = dictionarySet.get(fileName);
                Properties properties = elexDictionary.getProperties();
                String name = properties.getProperty(DslProperty.NAME.name());
                if (model.isDictionaryCurrentSelected(name)) {
                    String search = model.getUserInput();
                    SortedMap<Float, String[]> results = FtServer.getInstance().search(fileName, search, 30);
                    model.setSearchResultsFor(search);

                    for (Float score : results.keySet()) {
                        String[] value = results.get(score);
                        FullTextHit hit = new FullTextHit();
                        hit.setDictionaryId(name.hashCode());
                        hit.setHeadword(new Headword(value[0]));
                        hit.setExtract(value[1]);
                        hit.setScore(score);
                        hits.put(score, hit);
                    }
                }
            }
            model.setSearchResults(hits.values().toArray(new FullTextHit[hits.size()]));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}
