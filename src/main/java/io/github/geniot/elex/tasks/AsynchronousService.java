package io.github.geniot.elex.tasks;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.Task;
import io.github.geniot.elex.model.TaskStatus;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
public class AsynchronousService {

    Logger logger = LoggerFactory.getLogger(AsynchronousService.class);

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private ApplicationContext applicationContext;
    @Value("${path.data}")
    private String pathToData;
    @Value("${name.folder.ft-index}")
    private String ftIndexFolderName;

    private String ftFolderPath;

    private long latency = 2000;

    @PostConstruct
    public void init() {
        ftFolderPath = new File(pathToData + File.separator + ftIndexFolderName).getAbsolutePath();
    }

    private Map<String, Task> runningTasks = new ConcurrentHashMap<>();

    synchronized public void updatePool() {
        DictionariesPoolUpdateTask dictionariesPoolUpdateTask = applicationContext.getBean(DictionariesPoolUpdateTask.class);
        Task uiTask = new Task();
        uiTask.setAction(Action.POOL_UPDATE);
        dictionariesPoolUpdateTask.setTask(uiTask);
        runningTasks.put(Action.POOL_UPDATE.name(), uiTask);
        taskExecutor.execute(dictionariesPoolUpdateTask);
    }

    /**
     * Checks whether indexing is necessary.
     *
     * @param elexDictionary
     */
    public void index(ElexDictionary elexDictionary) throws IOException {
        File indexDir = new File(ftFolderPath + File.separator + FilenameUtils.removeExtension(elexDictionary.getFile().getName()));
        if (!indexDir.exists()) {
            String[] ffs = indexDir.list();
            if (ffs == null || ffs.length == 0) {
                reindex(elexDictionary);
            }
        }
    }

    synchronized public void reindex(ElexDictionary elexDictionary) {
        if (runningTasks.containsKey(elexDictionary.getFile().getName())) {
            return;
        }
        FtIndexTask ftIndexTask = applicationContext.getBean(FtIndexTask.class);
        Task uiTask = new Task();
        ftIndexTask.setElexDictionary(elexDictionary);
        ftIndexTask.setTask(uiTask);
        runningTasks.put(elexDictionary.getFile().getName(), uiTask);
        taskExecutor.execute(ftIndexTask);
    }

    synchronized public void cleanUp() {
        long now = System.currentTimeMillis();
        for (String fileName : runningTasks.keySet()) {
            Task task = runningTasks.get(fileName);
            if (now - task.getFinishedWhen() > latency &&
                    !task.getStatus().equals(TaskStatus.RUNNING)) {
                runningTasks.remove(fileName);
            }
        }
    }
}
