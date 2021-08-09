package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.util.Logger;
import io.github.geniot.elex.model.Model;

public class FullTextHitsUpdater {
    public void updateFullTextHits(Model model) {
        if (model.getLockFullText()) {
            return;
        }
        try {
//            List<FullTextHit> hits = new ArrayList<>();
//            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
//            for (IDictionary dictionary : dictionarySet) {
//                Properties properties = dictionary.getProperties();
//                String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
//                if (model.isDictionaryCurrentSelected(name)) {
//                    String search = model.getUserInput();
//                    IndexedTreeSet<SearchResult> results = dictionary.search(search);
//                    model.setSearchResultsFor(search);
//                    for (SearchResult sr : results) {
//                        FullTextHit hit = new FullTextHit();
//                        hit.setDictionaryId(Dictionary.idFromName(name));
//                        hit.setHeadword(new Headword(sr.getHeadword()));
//                        hit.setExtract(getAbstract(model.getUserInput(), sr.getText()));
//                        hit.setScore(sr.getScore());
//                        hits.add(hit);
//                    }
//                }
//            }
//            model.setSearchResults(hits.toArray(new FullTextHit[hits.size()]));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }


    private String stripTags(String entry) {
        entry = entry.replaceAll("\\[[^]]+\\]", "");
        entry = entry.replaceAll("\t|\r|\n", "");
        entry = entry.replaceAll("<[^>]+>", " ");
        entry = entry.replaceAll("\\s\\s", " ");
        entry = entry.replaceAll("  ", " ");
        entry = entry.replaceAll("_", " ");
        return entry;
    }
}
