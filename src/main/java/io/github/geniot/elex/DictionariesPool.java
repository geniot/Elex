package io.github.geniot.elex;

import io.github.geniot.dictiographer.model.CachedZipDictionary;
import io.github.geniot.dictiographer.model.Headword;
import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.elex.model.Model;
import io.github.geniot.indexedtreemap.IndexedTreeSet;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.*;

public class DictionariesPool extends FileAlterationListenerAdaptor {
    private Set<IDictionary> dictionaries = Collections.synchronizedSet(new HashSet<>());
    private FileAlterationObserver observer;
    private static DictionariesPool INSTANCE;
    private static String DATA_FOLDER_NAME = "data";
    private Map<String, IndexedTreeSet<Headword>> combinedIndexesMap = new HashMap<>();

    public static DictionariesPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DictionariesPool();
        }
        return INSTANCE;
    }

    private DictionariesPool() {
        update();
        try {
            observer = new FileAlterationObserver(DATA_FOLDER_NAME);
            observer.addListener(this);
            long interval = 1000;
            FileAlterationMonitor monitor = new FileAlterationMonitor(interval);
            monitor.addObserver(observer);
            monitor.start();
        } catch (Exception e) {
            Logger.getInstance().log(e);
            e.printStackTrace();
        }
    }


    private void update() {
        try {
            long t1 = System.currentTimeMillis();
            for (IDictionary dictionary : dictionaries) {
                dictionary.close();
            }
            dictionaries.clear();
            File[] dicFiles = new File(DATA_FOLDER_NAME).listFiles();
            //installing
            for (File dicFile : dicFiles) {
                if (dicFile.isDirectory()) {
                    //skip
                } else if (dicFile.isFile() && dicFile.getName().endsWith(".zip")) {
                    try {
                        Logger.getInstance().log("Installing: " + dicFile);
                        CachedZipDictionary cachedZipDictionary = new CachedZipDictionary(dicFile);
                        dictionaries.add(cachedZipDictionary);
                    } catch (Exception ex) {
                        Logger.getInstance().log("Couldn't install the dictionary: " + dicFile.getAbsolutePath());
                        Logger.getInstance().log(ex);
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            Logger.getInstance().log("Reloaded dictionaries in: " + (t2 - t1) + " ms");
        } catch (Exception e) {
            Logger.getInstance().log("Couldn't update state");
            Logger.getInstance().log(e);
        }
    }

    public Set<IDictionary> getDictionaries() {
        return dictionaries;
    }

    @Override
    public void onFileCreate(final File file) {
        update();
    }

    @Override
    public void onFileChange(final File file) {
        update();
    }

    @Override
    public void onFileDelete(final File file) {
        update();
    }

    public IndexedTreeSet<Headword> getCombinedIndex(Model model) {
        String key = getActiveShelfKey(model);
        IndexedTreeSet<Headword> combinedIndex = combinedIndexesMap.get(key);

        if (combinedIndex == null) {
            combinedIndex = new IndexedTreeSet<>();
            for (IDictionary dictionary : dictionaries) {
                Properties properties = dictionary.getProperties();
                String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
                if (model.isDictionaryCurrentSelected(name)) {
                    combinedIndex.addAll(dictionary.getIndex());
                }
            }
        }
        combinedIndexesMap.put(key, combinedIndex);

        return combinedIndex;
    }

    private String getActiveShelfKey(Model model) {
        StringBuffer stringBuffer = new StringBuffer();
        for (IDictionary dictionary : dictionaries) {
            Properties properties = dictionary.getProperties();
            String name = properties.getProperty(IDictionary.DictionaryProperty.NAME.name());
            if (model.isDictionaryCurrentSelected(name)) {
                stringBuffer.append(name);
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }

}
