package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DictionariesUpdater {
    public void updateDictionaries(Model model, List<Dictionary> dictionaryList) throws Exception {
        for (Dictionary dictionary : dictionaryList) {
            if (model.getAction().equals(Action.INIT)) {
                dictionary.setSelected(true);
            }
            if (dictionary.getIndexLanguageCode().equals(model.getSelectedSourceLanguage())
                    && dictionary.getContentsLanguageCode().equals(model.getSelectedTargetLanguage())) {
                dictionary.setCurrent(true);
            } else {
                dictionary.setCurrent(false);
            }
        }
        Dictionary[] dictionariesArray = dictionaryList.toArray(new Dictionary[dictionaryList.size()]);
        model.setDictionaries(dictionariesArray);
    }
}
