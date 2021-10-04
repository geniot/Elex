package io.github.geniot.elex.tools.convert;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class HtmlUtils {
    static Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    public static String preTag = "<span class=\"highlight\">";
    public static String postTag = "</span>";

    public static String toHtml(String baseApiUrl,
                                String dicId,
                                boolean shouldHighlight,
                                String searchWord,
                                String article,
                                Properties dicProperties,
                                List<Analyzer> analyzerList) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] lines = article.split("\n");
        int mValue = 1;
        for (String line : lines) {
            DslLine dslLine = new DslLine(line, mValue, baseApiUrl, dicId);
            mValue = dslLine.getMValue();
            stringBuilder.append(dslLine.toHtml(baseApiUrl, dicId, shouldHighlight, searchWord, dicProperties, analyzerList));
            stringBuilder.append("<br/>\n");
        }
        return stringBuilder.toString();
    }

    public static String htmlName(String dslName) {
        if (dslName.equals("*")) {
            return "opt";
        } else if (dslName.equals("!trs")) {
            return "trs";
        } else if (dslName.equals("'")) {
            return "stress";
        } else {
            return dslName;
        }
    }

    public static String highlight(String searchWord, String text, String preTag, String postTag, List<Analyzer> analyzerList) {
        try {
            for (Analyzer analyzer : analyzerList) {
                Query q = new QueryParser("content", analyzer).parse(QueryParser.escape(searchWord));
                SimpleHTMLFormatter formatter = new SimpleHTMLFormatter(preTag, postTag);
                Highlighter highlighter = new Highlighter(formatter, new QueryScorer(q));
                highlighter.setTextFragmenter(new SimpleFragmenter(text.length() * 10));
                highlighter.setMaxDocCharsToAnalyze(text.length());
                String fragment = highlighter.getBestFragment(analyzer, "", text);
                if (fragment != null) {
                    return fragment;
                }
            }
            return text;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return text;
        }
    }

}

