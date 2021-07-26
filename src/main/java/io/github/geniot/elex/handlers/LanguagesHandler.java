package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Language;

import java.util.*;

public class LanguagesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            SortedMap<String, Language> languagesMap = new TreeMap<>();
            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            for (IDictionary dictionary : dictionarySet) {
                Properties properties = dictionary.getProperties();
                String sourceLanguage = properties.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name()).toUpperCase();
                String contentsLanguage = properties.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name()).toUpperCase();
                Language language = languagesMap.get(sourceLanguage);
                if (language == null) {
                    language = new Language();
                    language.setSourceCode(sourceLanguage);
                }
                List<String> targetCodesList = new ArrayList();
                targetCodesList.addAll(Arrays.asList(language.getTargetCodes()));
                if (!targetCodesList.contains(contentsLanguage)) {
                    targetCodesList.add(contentsLanguage);
                }
                String[] targetCodes = targetCodesList.toArray(new String[targetCodesList.size()]);
                Arrays.sort(targetCodes);
                language.setTargetCodes(targetCodes);
                languagesMap.put(sourceLanguage, language);
            }

            Gson gson = new Gson();
            String s = gson.toJson(languagesMap.values().toArray(new Language[languagesMap.size()]));
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));
        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }
    }

    private Language genLanguage(String lang, int num) {
        Language en = new Language();
        en.setSourceCode(lang.toUpperCase());
        en.setTargetCodes(new String[]{"EN" + num, "DE" + num, "FR" + num});
        return en;
    }
}
