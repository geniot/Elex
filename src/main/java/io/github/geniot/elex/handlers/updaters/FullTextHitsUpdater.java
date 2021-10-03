package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.ftindexer.FtServer;
import io.github.geniot.elex.model.*;
import io.github.geniot.elex.tools.convert.DslProperty;
import io.github.geniot.elex.tools.convert.HtmlUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FullTextHitsUpdater {
    Logger logger = LoggerFactory.getLogger(FullTextHitsUpdater.class);

    @Autowired
    DictionariesPool dictionariesPool;
    @Autowired
    FtServer ftServer;

    String preTag = "<B>";
    String postTag = "</B>";

    public void updateFullTextHits(Model model, FtModel ftModel) {
        if (!model.getAction().equals(Action.FT_LINK)) {
            try {
                String search = model.getUserInput().trim();
                ftModel.setSearchResultsFor(search);

                SortedSet<FullTextHit> hits = new TreeSet<>();
                Map<String, ElexDictionary> dictionarySet = dictionariesPool.getElexDictionaries(model);

                for (String fileName : dictionarySet.keySet()) {

                    ElexDictionary elexDictionary = dictionarySet.get(fileName);
                    Properties properties = elexDictionary.getProperties();
                    String indexLanguage = properties.getProperty(DslProperty.INDEX_LANGUAGE.name()).toLowerCase();
                    String contentsLanguage = properties.getProperty(DslProperty.CONTENTS_LANGUAGE.name()).toLowerCase();
                    String name = properties.getProperty(DslProperty.NAME.name());

                    if (model.isDictionarySelected(name) && model.isDictionaryCurrent(name)) {

                        SortedMap<Float, String[]> results = ftServer.search(fileName, search, 100, indexLanguage, contentsLanguage);

                        for (Float score : results.keySet()) {
                            String[] value = results.get(score);
                            String headword = value[0];
                            String extract = value[1];
                            extract = extract.replaceAll("\\{[^}]*\\}", "");

                            if (!ftModel.getSearchResultsFor().equals(headword)) {
                                FullTextHit hit = getByHeadwordOrCreate(hits, headword);
                                hit.setDictionaryIds(ArrayUtils.add(hit.getDictionaryIds(), fileName.hashCode() & 0xfffffff));

                                hit.setScores(ArrayUtils.add(hit.getScores(), score));

                                Headword hwd = new Headword(headword);
                                String nameHighlighted = HtmlUtils.highlight(ftModel.getSearchResultsFor(), headword, preTag, postTag);
                                hwd.setNameHighlighted(nameHighlighted);
                                hit.setHeadword(hwd);
                                if (extract.startsWith(nameHighlighted)) {
                                    extract = extract.substring(nameHighlighted.length());
                                }
                                if (extract.startsWith(headword)) {
                                    extract = extract.substring(headword.length());
                                }
                                extract = extract.trim();
                                hit.setExtracts(ArrayUtils.add(hit.getExtracts(), extract));

                                hits.add(hit);
                            }
                        }
                    }
                }

                ftModel.setSearchResults(hits.toArray(new FullTextHit[hits.size()]));
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
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
