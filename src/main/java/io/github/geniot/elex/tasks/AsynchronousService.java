package io.github.geniot.elex.tasks;

import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Task;
import io.github.geniot.elex.model.TaskStatus;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

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

    private long latency = 2000;

    private Map<String, Task> runningTasks = new ConcurrentHashMap<>();

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
