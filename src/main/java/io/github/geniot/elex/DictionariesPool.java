package io.github.geniot.elex;

import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.dictiographer.model.ZipDictionary;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class DictionariesPool extends FileAlterationListenerAdaptor {
    private Set<IDictionary> dictionaries = Collections.synchronizedSet(new HashSet<>());
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
        for (IDictionary cd : dictionaries) {
            String[] dicLangs = getDicLangs(cd.getProperties());
            String langPair = dicLangs[0] + " ⇨ " + dicLangs[1];
            langs.add(langPair);
        }
        return langs.toArray(new String[langs.size()]);
    }

    private String[] getDicLangs(Properties props) {
        String sl = props.getProperty(IDictionary.DictionaryProperty.INDEX_LANGUAGE.name());
        String tl = props.getProperty(IDictionary.DictionaryProperty.CONTENTS_LANGUAGE.name());
        sl = sl == null ? "un" : sl;
        tl = tl == null ? "un" : tl;
        return new String[]{sl.toLowerCase(), tl.toLowerCase()};
    }

    private void update() {
        try {
            dictionaries.clear();
            File[] dicFiles = new File(DATA_FOLDER_NAME).listFiles();
            //installing
            for (File dicFile : dicFiles) {
                if (dicFile.isDirectory()) {
                    //skip
                } else if (dicFile.isFile() && dicFile.getName().endsWith(".zip")) {
                    try {
                        Logger.getInstance().log("Installing: " + dicFile);
                        dictionaries.add(new ZipDictionary(dicFile));
                    } catch (Exception ex) {
                        Logger.getInstance().log("Couldn't install the dictionary: " + dicFile.getAbsolutePath());
                        Logger.getInstance().log(ex);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getInstance().log("Couldn't update state");
            Logger.getInstance().log(e);
        }
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

    public Set<IDictionary> getDictionaries() {
        return dictionaries;
    }

    public Properties[] getDictionaries(String sourceLang, String targetLang) {
        Set<Properties> dics = new HashSet<Properties>();
        for (IDictionary cd : dictionaries) {
            Properties props = cd.getProperties();
            String[] dicLangs = getDicLangs(props);
            if (dicLangs[0].equalsIgnoreCase(sourceLang) && dicLangs[1].equalsIgnoreCase(targetLang)) {
                Properties propsShort = new Properties();
                propsShort.put("DICTIONARY_ID", String.valueOf(cd.hashCode() & 0x7fffffff));
                propsShort.put("name", props.getProperty(IDictionary.DictionaryProperty.NAME.name()));
                dics.add(propsShort);
            }
        }
        return dics.toArray(new Properties[dics.size()]);
    }

    public IDictionary getDictionaryById(String id) {
        for (IDictionary cd : dictionaries) {
            if (id.equals(String.valueOf(cd.hashCode() & 0x7fffffff))) {
                return cd;
            }
        }
        return null;
    }
}
