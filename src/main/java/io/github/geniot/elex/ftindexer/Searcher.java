package io.github.geniot.elex.ftindexer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class Searcher {
    Logger logger = LoggerFactory.getLogger(Searcher.class);
    private Comparator<Float> backwardFloatsComparator = Comparator.reverseOrder();

    public SortedMap<Float, String[]> search(Directory directory, String queryStr, int hitsPerPage) {
        try {
            SortedMap<Float, String[]> resultsMap = new TreeMap<>(backwardFloatsComparator);
            EnglishAnalyzer analyzer = new EnglishAnalyzer();
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                    new String[]{"headword", "article"},
                    analyzer);
            Query query = queryParser.parse(QueryParser.escape(queryStr));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs hits = searcher.search(query, hitsPerPage);
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter();
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, queryStr.length() * 5);
            highlighter.setTextFragmenter(fragmenter);
            highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
            for (int i = 0; i < hits.scoreDocs.length; i++) {
                int docid = hits.scoreDocs[i].doc;
                Document doc = searcher.doc(docid);
                String headword = doc.get("headword");
                String text = doc.get("article");
                TokenStream tokenStream = analyzer.tokenStream("article", text);
                String frags = highlighter.getBestFragments(tokenStream, text, 10, "...");
                resultsMap.put(hits.scoreDocs[i].score, new String[]{headword, frags});
            }
            return resultsMap;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new TreeMap<>();
        }
    }
}
