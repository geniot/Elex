package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.Logger;
import io.github.geniot.elex.ezip.model.DslProperty;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.ftindexer.FtServer;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Model;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FullTextHitsUpdater {
    private Comparator<Float> backwardFloatsComparator = Comparator.reverseOrder();

    public void updateFullTextHits(Model model) {
        if (model.getLockFullText()) {
            return;
        }
        try {
            SortedMap<Float, FullTextHit> hits = new TreeMap<>(backwardFloatsComparator);
            Map<String, ElexDictionary> dictionarySet = DictionariesPool.getInstance().getElexDictionaries(model);
            for (String fileName : dictionarySet.keySet()) {
                ElexDictionary elexDictionary = dictionarySet.get(fileName);
                Properties properties = elexDictionary.getProperties();
                String name = properties.getProperty(DslProperty.NAME.name());
                if (model.isDictionaryCurrentSelected(name)) {
                    String search = model.getUserInput();
                    SortedMap<Float, String[]> results = FtServer.getInstance().search(fileName, search, 100);
                    model.setSearchResultsFor(search);

                    for (Float score : results.keySet()) {
                        String[] value = results.get(score);
//                        StringUtils.isNotEmpty(value[1]) &&
                        if (!model.getSearchResultsFor().equals(value[0])) {
                            FullTextHit hit = new FullTextHit();
                            hit.setDictionaryId(fileName.hashCode());
                            hit.setHeadword(new Headword(value[0]));
                            hit.setExtract(value[1]);
                            hit.setScore(score);
                            hits.put(score, hit);
                        }
                    }
                }
            }

            model.setSearchResults(hits.values().toArray(new FullTextHit[hits.size()]));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }
}
