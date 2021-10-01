package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Language;
import io.github.geniot.elex.model.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

@Component
public class LanguagesUpdater {

    public void updateLanguages(Model model, SortedSet<Dictionary> dictionaryList) {
        SortedMap<String, Language> resultLanguagesMap = new TreeMap<>();
        Language selectedSourceLanguage = null;
        for (Dictionary dictionary : dictionaryList) {

            Language sourceLanguage = resultLanguagesMap.get(dictionary.getIndexLanguageCode());
            if (sourceLanguage == null) {
                sourceLanguage = new Language(dictionary.getIndexLanguageCode());
            }
            if (dictionary.getIndexLanguageCode().equals(model.getSelectedSourceLanguage())) {
                sourceLanguage.setSelected(true);
                selectedSourceLanguage = sourceLanguage;
            }

            Language targetLanguage = new Language(dictionary.getContentsLanguageCode());
            if (dictionary.getContentsLanguageCode().equals(model.getSelectedTargetLanguage())) {
                targetLanguage.setSelected(true);
            }
            sourceLanguage.getTargetLanguages().add(targetLanguage);
            resultLanguagesMap.put(dictionary.getIndexLanguageCode(), sourceLanguage);
        }
        //what if there are no selections?
        if (StringUtils.isEmpty(model.getSelectedSourceLanguage())) {
            if (resultLanguagesMap.size() > 0) {
                selectedSourceLanguage = resultLanguagesMap.values().iterator().next();
                selectedSourceLanguage.setSelected(true);
            }
        }
        if (StringUtils.isEmpty(model.getSelectedTargetLanguage())) {
            if (resultLanguagesMap.size() > 0 && selectedSourceLanguage != null) {
                selectedSourceLanguage.getTargetLanguages().first().setSelected(true);
            }
        }

        model.setSourceLanguages(resultLanguagesMap.values().toArray(new Language[resultLanguagesMap.size()]));
    }
}
