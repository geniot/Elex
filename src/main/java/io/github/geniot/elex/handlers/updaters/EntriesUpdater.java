package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Entry;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.HtmlUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.geniot.elex.ezip.DslUtils.*;

public class EntriesUpdater {

    public void updateEntries(Model model) throws Exception {
        List<Entry> entries = new ArrayList<>();
        if (model.getHeadwords().length > 0) {
            Map<String, String> articlesMap = DictionariesPool.getInstance().getArticles(model);
            for (String id : articlesMap.keySet()) {
                String article = articlesMap.get(id);

                if (model.getAction().equals(Action.FT_LINK)) {
                    article = highlight(model, article);
                }

                article = article.replaceAll("(<<)([^>]+)(>>)", "[ref]$2[/ref]");
                article = article.replaceAll("\\{\\{Roman\\}\\}", "");
                article = article.replaceAll("\\{\\{/Roman\\}\\}", "");

                article = StringEscapeUtils.escapeHtml4(article);
                article = HtmlUtils.toHtml(model.getBaseApiUrl(), id, article);

                entries.add(genEntry(id, model.getSelectedHeadword(), article));
            }
        }
        model.setEntries(entries.toArray(new Entry[entries.size()]));
    }

    private String highlight(Model model, String article) throws Exception {
        EnglishAnalyzer analyzer = new EnglishAnalyzer();
        Query q = new QueryParser("content", analyzer).parse(model.getSearchResultsFor());
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("[highlight]", "[/highlight]");
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(q));
        highlighter.setTextFragmenter(new SimpleFragmenter());
        String[] tokens = tokenize(article);
        for (int i = 0; i < tokens.length; i++) {
            if (!isTag(tokens[i])) {
                String fragment = highlighter.getBestFragment(analyzer, "", tokens[i]);
                if (fragment != null) {
                    tokens[i] = fragment;
                }
            }
        }
        return glue(tokens);
    }

    private Entry genEntry(String dicId, String hwd, String article) {
        Entry entry = new Entry();
        entry.setDicId(dicId);
        entry.setHeadword(hwd);
        entry.setBody(article);
        return entry;
    }
}
