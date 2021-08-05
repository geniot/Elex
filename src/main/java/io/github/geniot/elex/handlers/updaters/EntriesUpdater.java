package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.HtmlUtils;
import io.github.geniot.elex.model.Entry;
import io.github.geniot.elex.model.IDictionary;
import io.github.geniot.elex.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class EntriesUpdater {

    public void updateEntries(Model model) throws Exception {
        String article = null;
        Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
        for (IDictionary dictionary : dictionarySet) {
            Properties properties = dictionary.getProperties();
            String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
            if (model.isDictionaryCurrentSelected(name)) {
                article = dictionary.read(model.getSelectedHeadword());
                break;
            }
        }

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
