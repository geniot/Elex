package io.github.geniot.elex;

import io.github.geniot.dictiographer.model.IDictionary;
import io.github.geniot.dictiographer.model.ZipDictionary;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
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


    private void update() {
        try {
            long t1 = System.currentTimeMillis();
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
            long t2 = System.currentTimeMillis();
            Logger.getInstance().log("Reloaded dictionaries in: " + (t2 - t1) + " ms");
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
}
