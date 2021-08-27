package io.github.geniot.elex;

import io.github.geniot.elex.ezip.Logger;
import io.github.geniot.elex.ezip.model.CaseInsensitiveComparator;
import io.github.geniot.elex.ezip.model.DslProperty;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DictionariesPool extends FileAlterationListenerAdaptor {
    private static DictionariesPool instance;
    private Map<String, ElexDictionary> dictionaries = Collections.synchronizedMap(new HashMap<>());
    private Map<String, ElexDictionary> resources = Collections.synchronizedMap(new HashMap<>());
    private FileAlterationObserver observer;
    private static String DATA_FOLDER_NAME = "data";
    CaseInsensitiveComparator caseInsensitiveComparator = new CaseInsensitiveComparator();

    public static DictionariesPool getInstance() {
        if (instance == null) {
            instance = new DictionariesPool();
        }
        return instance;
    }

    private DictionariesPool() {
        update();
        try {
            observer = new FileAlterationObserver(DATA_FOLDER_NAME, pathname -> pathname.getPath().endsWith(".ezp"));
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
            dictionaries.clear();
            File[] dicFiles = new File(DATA_FOLDER_NAME).listFiles();
            //installing
            for (File dicFile : dicFiles) {
                if (dicFile.isDirectory()) {
                    continue;
                }
                if (dicFile.getName().endsWith(".ezp")) {
                    try {
                        Logger.getInstance().log("Installing: " + dicFile);
                        dictionaries.put(dicFile.getName(), new ElexDictionary(dicFile.getAbsolutePath(), "r"));
                    } catch (Exception ex) {
                        Logger.getInstance().log("Couldn't install the dictionary: " + dicFile.getAbsolutePath());
                        Logger.getInstance().log(ex);
                    }
                } else if (dicFile.getName().endsWith(".ezr")) {
                    try {
                        Logger.getInstance().log("Installing: " + dicFile);
                        resources.put(dicFile.getName(), new ElexDictionary(dicFile.getAbsolutePath(), "r"));
                    } catch (Exception ex) {
                        Logger.getInstance().log("Couldn't install the resources file: " + dicFile.getAbsolutePath());
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

    public Map<String, ElexDictionary> getElexDictionaries(Model model) throws IOException {
        Map<String, ElexDictionary> result = new HashMap<>();
        for (String fileName : dictionaries.keySet()) {
            ElexDictionary elexDictionary = dictionaries.get(fileName);
            Properties properties = elexDictionary.getProperties();
            String sl1 = model.getSelectedSourceLanguage();
            String tl1 = model.getSelectedTargetLanguage();
            String sl2 = properties.getProperty(DslProperty.CONTENTS_LANGUAGE.name());
            String tl2 = properties.getProperty(DslProperty.INDEX_LANGUAGE.name());
            String name = properties.getProperty(DslProperty.NAME.name());
            if (sl1.equalsIgnoreCase(sl2) &&
                    tl1.equalsIgnoreCase(tl2) &&
                    model.isDictionarySelected(name)
            ) {
                result.put(fileName, elexDictionary);
            }
        }
        return result;
    }

    public List<Dictionary> getDictionaries(Model model) throws IOException {
        List<Dictionary> result = new ArrayList<>();
        for (String fileName : dictionaries.keySet()) {
            ElexDictionary elexDictionary = dictionaries.get(fileName);
            Dictionary dictionary = new Dictionary();
            dictionary.setId(fileName.hashCode());
            String name = elexDictionary.getProperties().getProperty(DslProperty.NAME.name());
            dictionary.setName(name);
            dictionary.setIndexLanguageCode(elexDictionary.getProperties().getProperty(DslProperty.CONTENTS_LANGUAGE.name()));
            dictionary.setContentsLanguageCode(elexDictionary.getProperties().getProperty(DslProperty.INDEX_LANGUAGE.name()));
            dictionary.setSelected(model.isDictionarySelected(name));
            dictionary.setCurrent(true);
            result.add(dictionary);
        }
        return result;
    }

    public Map<String, String> getArticles(Model model) throws Exception {
        Map<String, String> articlesMap = new HashMap<>();
        for (String fileName : dictionaries.keySet()) {
            ElexDictionary elexDictionary = dictionaries.get(fileName);
            String name = elexDictionary.getProperties().getProperty(DslProperty.NAME.name());
            if (model.isDictionaryCurrentSelected(name)) {
                String article = elexDictionary.readArticle(model.getSelectedHeadword());
                if (article != null) {
                    articlesMap.put(String.valueOf(fileName.hashCode()), article);
                }
            }
        }
        return articlesMap;
    }

    public byte[] getIcon(int id) throws IOException {
        for (String fileName : dictionaries.keySet()) {
            if (fileName.hashCode() == id) {
                return dictionaries.get(fileName).getIcon();
            }
        }
        return null;
    }

    public byte[] getResource(int id, String link) throws Exception {
        for (String fileName : dictionaries.keySet()) {
            if (fileName.hashCode() == id) {
                String resourceFileName = FilenameUtils.removeExtension(fileName) + ".ezr";
                if (resources.containsKey(resourceFileName)) {
                    return resources.get(resourceFileName).readResource(link);
                }
            }
        }
        Logger.getInstance().log("Couldn't find resource for: " + id + "; " + link);
        return new byte[]{};
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

    public String getMinHeadword(Set<ElexDictionary> set) throws Exception {
        String minHw = null;
        for (ElexDictionary cd : set) {
            String hw = cd.first();
            if (minHw == null || caseInsensitiveComparator.compare(minHw, hw) > 0) {
                minHw = hw;
            }
        }
        return minHw;
    }

    public String getMaxHeadword(Set<ElexDictionary> set) throws Exception {
        String maxHw = null;
        for (ElexDictionary cd : set) {
            String hw = cd.last();
            if (maxHw == null || caseInsensitiveComparator.compare(maxHw, hw) < 0) {
                maxHw = hw;
            }
        }
        return maxHw;
    }


    public void close() {
        for (String fileName : dictionaries.keySet()) {
            try {
                ElexDictionary elexDictionary = dictionaries.get(fileName);
                elexDictionary.close();
                Logger.getInstance().log("Closed " + fileName);
            } catch (IOException e) {
                Logger.getInstance().log(e);
            }
        }
    }

}
