package io.github.geniot.elex.dao;

import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;

import java.util.ArrayList;
import java.util.List;

public class DictionaryDAO {
    private static DictionaryDAO instance;

    public static DictionaryDAO getInstance() {
        if (instance == null) {
            instance = new DictionaryDAO();
        }
        return instance;
    }

    private DictionaryDAO() {
    }


    public List<Dictionary> getDictionaries(Model model) throws Exception {
        List<Dictionary> dictionaries = new ArrayList<>();
//        Dictionary uiDictionary = new Dictionary();
//        uiDictionary.setId(dictionaryId);
//        uiDictionary.setName(shortName);
//        uiDictionary.setIndexLanguageCode(indexLanguage);
//        uiDictionary.setContentsLanguageCode(contentsLanguage);
//        uiDictionary.setSelected(model.isDictionarySelected(shortName));
//        dictionaries.add(uiDictionary);
        return dictionaries;
    }

    public byte[] getIcon(int id) throws Exception {
        return null;
    }
}
