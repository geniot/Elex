package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Entry;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.tools.convert.HtmlUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntriesUpdater {
    Logger logger = LoggerFactory.getLogger(EntriesUpdater.class);

    @Autowired
    DictionariesPool dictionariesPool;

    public void updateEntries(Model model) throws Exception {
        List<Entry> entries = new ArrayList<>();
        if (model.getHeadwords().length > 0) {
            entries = dictionariesPool.getArticles(model);
            for (Entry entry : entries) {
                String article = entry.getBody();

                article = article.replaceAll("(<<)([^>]+)(>>)", "[ref]$2[/ref]");
                article = article.replaceAll("\\{\\{[^}]+\\}\\}", "");
                article = article.replaceAll("\\{\\{/[^}]+\\}\\}", "");
                article = article.replaceAll("\\n\\t\\\\\\s+\\n", "\n");

                article = StringEscapeUtils.escapeHtml4(article);

                boolean shouldHighlight = model.getAction().equals(Action.FT_LINK);
                String searchWord = model.getSearchResultsFor();

                String headword = HtmlUtils.toHtml(model.getBaseApiUrl(), entry.getDicId(), shouldHighlight, searchWord, entry.getHeadword(), dictionariesPool.getProperties(entry.getDicId()));
                entry.setHeadword(headword);
//                if (shouldHighlight) {
//                    entry.setHeadword(HtmlUtils.highlight(model.getSearchResultsFor(), entry.getHeadword(), preTag, postTag));
//                }

                article = HtmlUtils.toHtml(model.getBaseApiUrl(), entry.getDicId(), shouldHighlight, searchWord, article, dictionariesPool.getProperties(entry.getDicId()));

                entry.setBody(article);
            }
        }
        model.setEntries(entries.toArray(new Entry[entries.size()]));
    }
}
