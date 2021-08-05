package io.github.geniot.elex.model;



import io.github.geniot.elex.model.lucene.SerializableRAMDirectory;
import io.github.geniot.indexedtreemap.IndexedTreeSet;

import java.io.File;
import java.net.URI;
import java.util.Properties;
import java.util.SortedMap;

public class CachedZipDictionary extends ZipDictionary {
    private Properties properties;
    private IndexedTreeSet<Headword> index;
    private SerializableRAMDirectory fullTextIndex;
    private byte[] icon;

    public CachedZipDictionary(String s) throws Exception {
        super(s);
    }

    public CachedZipDictionary(File s) throws Exception {
        super(s);
    }

    public CachedZipDictionary(URI u) throws Exception {
        super(u);
    }

    @Override
    public Properties getProperties() throws Exception {
        if (properties == null) {
            properties = super.getProperties();
        }
        return properties;
    }

    @Override
    public void setProperties(Properties properties) throws Exception {
        this.properties = null;
        super.setProperties(properties);
    }

    @Override
    public IndexedTreeSet<Headword> getIndex() throws Exception {
        if (index == null) {
            index = super.getIndex();
        }
        return index;
    }

    @Override
    protected SerializableRAMDirectory getFullTextIndex() throws Exception {
        if (fullTextIndex == null) {
            fullTextIndex = super.getFullTextIndex();
        }
        return fullTextIndex;
    }

    @Override
    protected void setFullTextIndex(SerializableRAMDirectory fullTextIndex) throws Exception {
        this.fullTextIndex = null;
        super.setFullTextIndex(fullTextIndex);
    }

    @Override
    public void createOrUpdate(String headword, String entry) throws Exception {
        this.index = null;
        super.createOrUpdate(headword, entry);
    }

    @Override
    public void bulkCreateOrUpdate(SortedMap<String, String> entries) throws Exception {
        this.index = null;
        super.bulkCreateOrUpdate(entries);
    }

    @Override
    public void delete(String headword) throws Exception {
        this.index = null;
        super.delete(headword);
    }

    @Override
    public byte[] getIcon() throws Exception {
        if (icon == null) {
            icon = super.getIcon();
        }
        return icon;
    }

    @Override
    public void setIcon(byte[] iconBytes) throws Exception {
        this.icon = null;
        super.setIcon(iconBytes);
    }

}
