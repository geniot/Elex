package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.dictiographer.model.Headword;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.dictiographer.model.SearchResult;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.FullTextHit;
import io.github.geniot.elex.model.Model;
import io.github.geniot.indexedtreemap.IndexedTreeSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class FullTextHitsUpdater {
    public void updateFullTextHits(Model model) {
        try {
            List<FullTextHit> hits = new ArrayList<>();
            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            for (IDictionary dictionary : dictionarySet) {
                Properties properties = dictionary.getProperties();
                String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
                if (model.isDictionaryCurrentSelected(name)) {
                    String search = model.getUserInput();
                    IndexedTreeSet<SearchResult> results = dictionary.search(search);
                    model.setSearchResultsFor(search);
                    for (SearchResult sr : results) {
                        FullTextHit hit = new FullTextHit();
                        hit.setDictionaryId(Dictionary.idFromName(name));
                        hit.setHeadword(new Headword(sr.getHeadword()));
                        hit.setExtract(getAbstract(model.getUserInput(), sr.getText()));
                        hit.setScore(sr.getScore());
                        hits.add(hit);
                    }
                }
            }
            model.setSearchResults(hits.toArray(new FullTextHit[hits.size()]));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private String getAbstract(String searchValue, String entry) throws Exception {
        entry = stripTags(entry);
        Analyzer analyzer = new EnglishAnalyzer();
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter();
        Query q = new QueryParser("content", analyzer).parse(QueryParser.escape(searchValue));
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(q));
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(entry));
        TextFragment[] fragments = highlighter.getBestTextFragments(tokenStream, entry, false, 5);
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (TextFragment fragment : fragments) {
            if (fragment.getScore() > 0) {
                if (!isFirst) {
                    sb.append(" ... ");
                }
                sb.append(fragment.toString());
                isFirst = false;
            }
        }
        return sb.toString();
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
