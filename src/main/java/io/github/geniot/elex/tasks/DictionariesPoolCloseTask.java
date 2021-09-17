package io.github.geniot.elex.tasks;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class DictionariesPoolCloseTask implements Runnable {
    Logger logger = LoggerFactory.getLogger(DictionariesPoolCloseTask.class);
    @Autowired
    private DictionariesPool dictionariesPool;

    @Override
    public void run() {
        for (String fileName : dictionariesPool.getDictionaries().keySet()) {
            try {
                ElexDictionary elexDictionary = dictionariesPool.getDictionaries().get(fileName);
                elexDictionary.close();
                logger.info("Closed " + fileName);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
