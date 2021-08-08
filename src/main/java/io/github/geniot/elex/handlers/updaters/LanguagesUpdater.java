package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Model;


public class LanguagesUpdater {

    public void updateLanguages(Model model) throws Exception {
//        SortedMap<String, Language> resultLanguagesMap = new TreeMap<>();
//        Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
//        for (IDictionary dictionary : dictionarySet) {
//
//            Properties properties = dictionary.getProperties();
//            String dictionarySourceLanguage = properties.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name()).toUpperCase();
//            String dictionaryTargetLanguage = properties.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name()).toUpperCase();
//
//            Language sourceLanguage = resultLanguagesMap.get(dictionarySourceLanguage);
//            if (sourceLanguage == null) {
//                sourceLanguage = new Language(dictionarySourceLanguage);
//            }
//            if (dictionarySourceLanguage.equals(model.getSelectedSourceLanguage())) {
//                sourceLanguage.setSelected(true);
//            }
//
//            Language targetLanguage = new Language(dictionaryTargetLanguage);
//            if (dictionaryTargetLanguage.equals(model.getSelectedTargetLanguage())) {
//                targetLanguage.setSelected(true);
//            }
//            sourceLanguage.getTargetLanguages().add(targetLanguage);
//            resultLanguagesMap.put(dictionarySourceLanguage, sourceLanguage);
//        }
//        //what if there are no selections?
//        if (StringUtils.isEmpty(model.getSelectedSourceLanguage())) {
//            if (resultLanguagesMap.size() > 0) {
//                resultLanguagesMap.values().iterator().next().setSelected(true);
//            }
//        }
//        if (StringUtils.isEmpty(model.getSelectedTargetLanguage())) {
//            if (resultLanguagesMap.size() > 0) {
//                resultLanguagesMap.values().iterator().next().getTargetLanguages().first().setSelected(true);
//            }
//        }
//
//        model.setSourceLanguages(resultLanguagesMap.values().toArray(new Language[resultLanguagesMap.size()]));
    }
}
