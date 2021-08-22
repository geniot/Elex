package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.model.Entry;
import io.github.geniot.elex.model.Model;
import io.github.geniot.elex.util.HtmlUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntriesUpdater {

    public void updateEntries(Model model) throws IOException {
        String article = DictionariesPool.getInstance().getArticle(model);
        List<Entry> entries = new ArrayList<>();
        if (article != null) {
            article = HtmlUtils.toHtml(article);
            entries.add(genEntry(model.getSelectedHeadword(), article));
        }
        model.setEntries(entries.toArray(new Entry[entries.size()]));
    }

    private Entry genEntry(String hwd, String article) {
        Entry entry = new Entry();
        entry.setHeadword(hwd);
        entry.setBody(article);
        return entry;
    }
}
