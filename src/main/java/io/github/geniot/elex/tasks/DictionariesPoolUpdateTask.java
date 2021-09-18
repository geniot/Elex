package io.github.geniot.elex.tasks;

import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ServerSettingsManager;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Task;
import io.github.geniot.elex.model.TaskStatus;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Scope("prototype")
@Getter
@Setter
public class DictionariesPoolUpdateTask implements Runnable {
    Logger logger = LoggerFactory.getLogger(DictionariesPoolUpdateTask.class);
    @Value("${path.data}")
    private String pathToData;
    @Autowired
    private DictionariesPool dictionariesPool;
    @Autowired
    private ServerSettingsManager serverSettingsManager;
    @Autowired
    private AsynchronousService asynchronousService;
    private Task task;

    @Override
    public void run() {
        try {
            task.setStatus(TaskStatus.RUNNING);
            long t1 = System.currentTimeMillis();
//            dictionariesPool.getDictionaries().clear();
            File dataFolder = new File(pathToData);
            dataFolder.mkdirs();
            File[] dicFiles = dataFolder.listFiles();
            //installing
            for (File dicFile : dicFiles) {
                if (dicFile.isDirectory()) {
                    continue;
                }
                String shortName = FilenameUtils.removeExtension(dicFile.getName());
                if (!serverSettingsManager.isDisabled(shortName)) {
                    if (dicFile.getName().endsWith(".ezp")) {
                        try {
                            if (!dictionariesPool.getDictionaries().containsKey(dicFile.getName())) {
                                logger.info("Installing: " + dicFile);
                                dictionariesPool.getDictionaries().put(dicFile.getName(), new ElexDictionary(dicFile.getAbsolutePath(), "r"));
                            }
                        } catch (Exception ex) {
                            logger.error("Couldn't install the dictionary: " + dicFile.getAbsolutePath());
                            logger.error(ex.getMessage(), ex);
                        }
                    } else if (dicFile.getName().endsWith(".ezr")) {
                        try {
                            if (!dictionariesPool.getResources().containsKey(dicFile.getName())) {
                                logger.info("Installing: " + dicFile);
                                dictionariesPool.getResources().put(dicFile.getName(), new ElexDictionary(dicFile.getAbsolutePath(), "r"));
                            }
                        } catch (Exception ex) {
                            logger.error("Couldn't install the resources file: " + dicFile.getAbsolutePath());
                            logger.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
            long t2 = System.currentTimeMillis();
            logger.info("Reloaded dictionaries in: " + (t2 - t1) + " ms");
        } catch (Exception e) {
            logger.error("Couldn't update state");
            logger.error(e.getMessage(), e);
        } finally {
            task.setStatus(TaskStatus.SUCCESS);
        }
    }
}
