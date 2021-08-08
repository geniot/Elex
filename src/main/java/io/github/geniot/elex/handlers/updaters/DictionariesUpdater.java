package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;

import java.util.List;


public class DictionariesUpdater {
    public void updateDictionaries(Model model, List<Dictionary> dictionaryList) throws Exception {
        for (Dictionary dictionary : dictionaryList) {
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
