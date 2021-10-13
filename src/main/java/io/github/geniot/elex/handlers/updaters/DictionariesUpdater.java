package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;
import org.springframework.stereotype.Component;

import java.util.SortedSet;

import static io.github.geniot.elex.model.Constants.ANY;

@Component
public class DictionariesUpdater {
    public void updateDictionaries(Model model, SortedSet<Dictionary> dictionaryList) {
        for (Dictionary dictionary : dictionaryList) {
            if (model.getAction().equals(Action.INIT)) {
                dictionary.setSelected(true);
            }

            String sl1 = model.getSelectedSourceLanguage();
            String tl1 = model.getSelectedTargetLanguage();

            String sl2 = dictionary.getIndexLanguageCode();
            String tl2 = dictionary.getContentsLanguageCode();

            dictionary.setCurrent(false);
            if ((sl1.equalsIgnoreCase(sl2) || sl1.equals(ANY)) &&
                    (tl1.equalsIgnoreCase(tl2) || tl1.equals(ANY))
            ) {
                dictionary.setCurrent(true);
            }

        }
        Dictionary[] dictionariesArray = dictionaryList.toArray(new Dictionary[dictionaryList.size()]);
        model.setDictionaries(dictionariesArray);
    }
}
