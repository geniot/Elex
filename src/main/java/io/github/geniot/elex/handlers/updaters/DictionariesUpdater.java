package io.github.geniot.elex.handlers.updaters;

import io.github.geniot.elex.model.Model;


public class DictionariesUpdater {
    public void updateDictionaries(Model model) throws Exception {
//        List<Dictionary> dictionaries = new ArrayList<>();
//        Set<IDictionary> dictionarySet = DictionariesPool.getInstance().getDictionaries();
//        for (IDictionary dictionary : dictionarySet) {
//            Properties properties = dictionary.getProperties();
//            String sourceLanguage = properties.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name()).toUpperCase();
//            String contentsLanguage = properties.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name()).toUpperCase();
//
//            String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
//            Dictionary uiDictionary = new Dictionary();
//            uiDictionary.setName(name);
//            uiDictionary.setSelected(model.isDictionarySelected(name));
//
//            if (sourceLanguage.equals(model.getSelectedSourceLanguage()) && contentsLanguage.equals(model.getSelectedTargetLanguage())) {
//                uiDictionary.setCurrent(true);
//            } else {
//                uiDictionary.setCurrent(false);
//            }
//            dictionaries.add(uiDictionary);
//        }
//        Dictionary[] dictionariesArray = dictionaries.toArray(new Dictionary[dictionaries.size()]);
//        model.setDictionaries(dictionariesArray);
    }
}
