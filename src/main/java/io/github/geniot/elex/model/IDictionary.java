package io.github.geniot.elex.model;


import io.github.geniot.indexedtreemap.IndexedTreeSet;

import java.util.Properties;
import java.util.SortedMap;

public interface IDictionary {

    public enum ContentType {
        DSL, HTML
    }

    public enum DictionaryProperty {
        NAME, ANNOTATION, INDEX_LANGUAGE, CONTENTS_LANGUAGE, CONTENTS_TYPE
    }

    void createOrUpdate(String headword, String entry) throws Exception;

    void bulkCreateOrUpdate(SortedMap<String, String> entries) throws Exception;

    String read(String headword) throws Exception;

    void delete(String headword) throws Exception;

    IndexedTreeSet<Headword> getIndex() throws Exception;

    IndexedTreeSet<SearchResult> search(String query) throws Exception;

    Properties getProperties() throws Exception;

    void setProperties(Properties properties) throws Exception;

    byte[] getIcon() throws Exception;

    void setIcon(byte[] iconBytes) throws Exception;

    void close() throws Exception;
}

