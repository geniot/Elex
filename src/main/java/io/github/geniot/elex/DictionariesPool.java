package io.github.geniot.elex;

import io.github.geniot.dictiographer.model.IDictionary;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.*;

public class DictionariesPool extends FileAlterationListenerAdaptor {
    private Map<Integer, IDictionary> DICTIONARIES = Collections.synchronizedMap(new HashMap<Integer, IDictionary>());
    private FileAlterationObserver observer;
    private static DictionariesPool INSTANCE;
    private static String DATA_FOLDER_NAME = "data";

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

    public String[] getAvailableLanguages() {
        Set<String> langs = new HashSet<String>();
        for (IDictionary cd : DICTIONARIES.values()) {
            Properties props = cd.getProperties();
            String sl = props.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name());
            String tl = props.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name());
            sl = sl == null ? "un" : sl;
            tl = tl == null ? "un" : tl;
            String langPair = sl.toLowerCase() + " ⇨ " + tl;
            langs.add(langPair);
        }
        return langs.toArray(new String[langs.size()]);
    }

    private void update() {
        try {
            DICTIONARIES.clear();
            Logger.getInstance().log("Update");
        } catch (Exception e) {
            Logger.getInstance().log("Couldn't update state");
            Logger.getInstance().log(e);
        }
    }

    @Override
    public void onFileCreate(final File file) {
        Logger.getInstance().log(file.getAbsolutePath());
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
}
