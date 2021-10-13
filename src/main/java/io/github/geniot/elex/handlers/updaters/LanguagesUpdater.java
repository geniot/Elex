package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Language;
import io.github.geniot.elex.model.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static io.github.geniot.elex.model.Constants.ANY;

@Component
public class LanguagesUpdater {

    public void updateLanguages(Model model, SortedSet<Dictionary> dictionaryList) {

        SortedMap<String, Language> resultLanguagesMap = new TreeMap<>();
        TreeSet<Language> allTargetLanguages = new TreeSet<>();
        Language selectedSourceLanguage = null;

        for (Dictionary dictionary : dictionaryList) {

            Language sourceLanguage = resultLanguagesMap.get(dictionary.getIndexLanguageCode());
            if (sourceLanguage == null) {
                sourceLanguage = new Language(
                        dictionary.getIndexLanguageCode(),
                        dictionary.getIndexLanguageCode().equals(model.getSelectedSourceLanguage()));
            }
            if (dictionary.getIndexLanguageCode().equals(model.getSelectedSourceLanguage())) {
                selectedSourceLanguage = sourceLanguage;
            }

            Language targetLanguage = new Language(
                    dictionary.getContentsLanguageCode(),
                    dictionary.getContentsLanguageCode().equals(model.getSelectedTargetLanguage()));

            sourceLanguage.getTargetLanguages().add(targetLanguage);
            resultLanguagesMap.put(dictionary.getIndexLanguageCode(), sourceLanguage);

            allTargetLanguages.add(new Language(
                    dictionary.getContentsLanguageCode(),
                    dictionary.getContentsLanguageCode().equals(model.getSelectedTargetLanguage())));
        }
        //any
        Language anySourceLanguage = new Language(ANY, model.getSelectedSourceLanguage().equals(ANY));
        if (model.getSelectedSourceLanguage().equals(ANY)) {
            selectedSourceLanguage = anySourceLanguage;
        }
        //adding all possible contents languages to ANY source
        anySourceLanguage.setTargetLanguages(allTargetLanguages);
        resultLanguagesMap.put(ANY, anySourceLanguage);
        //adding ANY to all target lists
        for (Language sourceLanguage : resultLanguagesMap.values()) {
            Language anyTargetLanguage = new Language(ANY, model.getSelectedTargetLanguage().equals(ANY));
            sourceLanguage.getTargetLanguages().add(anyTargetLanguage);
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
