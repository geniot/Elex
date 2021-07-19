package io.github.geniot.elex.model;

import java.util.*;

public class DictionariesPool {
    private static DictionariesPool INSTANCE;
    public static Map<Integer, CompressedDictionary> DICTIONARIES = Collections.synchronizedMap(new HashMap<Integer, CompressedDictionary>());

    public static DictionariesPool getInstance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new DictionariesPool();
        }
        return INSTANCE;
    }

    public String[] getAvailableLanguages() {
        return new String[0];
    }

    public Properties[] getDictionaries(String sourceLang, String targetLang) {
        return new Properties[0];
    }

    public CompressedDictionary getDictionaryById(Integer id) {
        return null;
    }

    public String getDictionariesResourcesByMask(Set<CompressedDictionary> set, String s) {
        return "";
    }
}
