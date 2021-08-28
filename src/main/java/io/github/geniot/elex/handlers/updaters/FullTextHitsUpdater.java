package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.Logger;
import io.github.geniot.elex.ezip.model.DslProperty;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.ftindexer.FtServer;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Headword;
import io.github.geniot.elex.model.Model;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class FullTextHitsUpdater {

    public void updateFullTextHits(Model model) {
        if (model.getLockFullText()) {
            return;
        }
        try {
            SortedSet<FullTextHit> hits = new TreeSet<>();
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
                        String headword = value[0];
                        String extract = value[1];
//                        StringUtils.isNotEmpty(value[1]) &&
                        if (!model.getSearchResultsFor().equals(headword)) {
                            FullTextHit hit = getByHeadwordOrCreate(hits, headword);
                            hit.setDictionaryIds(ArrayUtils.add(hit.getDictionaryIds(), fileName.hashCode()));
                            hit.setExtracts(ArrayUtils.add(hit.getExtracts(), extract));
                            hit.setScores(ArrayUtils.add(hit.getScores(), score));
                            hit.setHeadword(new Headword(headword));
                            hits.add(hit);
                        }
                    }
                }
            }

            model.setSearchResults(hits.toArray(new FullTextHit[hits.size()]));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private FullTextHit getByHeadwordOrCreate(Set<FullTextHit> hits, String s) {
        for (FullTextHit hit : hits) {
            if (hit.getHeadword().getName().equals(s)) {
                return hit;
            }
        }
        return new FullTextHit();
    }
}
