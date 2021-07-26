package io.github.geniot.elex.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.Logger;
import io.github.geniot.elex.model.Language;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class LanguagesHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Map<String, String> map = queryToMap(httpExchange.getRequestURI().getQuery());
            String sl = map.get("sl");
            String tl = map.get("tl");

            SortedSet<Language> resultLanguagesSet = new TreeSet<>();

            Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
            for (IDictionary dictionary : dictionarySet) {

                Properties properties = dictionary.getProperties();
                String dictionarySourceLanguage = properties.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name()).toUpperCase();
                String dictionaryTargetLanguage = properties.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name()).toUpperCase();

                Language sourceLanguage = new Language(dictionarySourceLanguage);
                if (dictionarySourceLanguage.equals(sl)) {
                    sourceLanguage.setSelected(true);
                }

                Language targetLanguage = new Language(dictionaryTargetLanguage);
                if (dictionaryTargetLanguage.equals(tl)) {
                    targetLanguage.setSelected(true);
                }
                sourceLanguage.getTargetLanguages().add(targetLanguage);

                resultLanguagesSet.add(sourceLanguage);
            }

            //todo: maybe set en-en here
            if (StringUtils.isEmpty(sl) || StringUtils.isEmpty(tl)) {
                if (!resultLanguagesSet.isEmpty()) {
                    Language lang = resultLanguagesSet.first();
                    lang.setSelected(true);
                    if (!lang.getTargetLanguages().isEmpty()) {
                        lang.getTargetLanguages().first().setSelected(true);
                    }
                }
            }

            Gson gson = new Gson();
            String s = gson.toJson(resultLanguagesSet);
            writeTxt(httpExchange, s, contentTypesMap.get(ContentType.JSON));

        } catch (Exception ex) {
            Logger.getInstance().log(ex);
        }

    }

}
