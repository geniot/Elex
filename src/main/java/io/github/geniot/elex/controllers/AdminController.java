package io.github.geniot.elex.controllers;

import com.google.gson.Gson;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.*;
import io.github.geniot.elex.tasks.AsynchronousService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.SortedSet;

@RestController
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AdminController.class);

    Gson gson = new Gson();

    @Autowired
    DictionariesPool dictionariesPool;
    @Autowired
    AsynchronousService asynchronousService;

    @GetMapping("/admin/tasks")
    public String tasks() {
        TaskExecutorModel taskExecutorModel = new TaskExecutorModel();
        asynchronousService.cleanUp();
        Collection<Task> tasksList = asynchronousService.getRunningTasks().values();
        taskExecutorModel.setTasks(tasksList.toArray(new Task[tasksList.size()]));
        return gson.toJson(taskExecutorModel);
    }

    @PostMapping("/admin/data")
    public String handle(@RequestBody String payload) {
        try {
            long t1 = System.currentTimeMillis();

            AdminModel model = gson.fromJson(payload, AdminModel.class);

            if (model.getAction().equals(Action.REINDEX)) {
                AdminDictionary selectedDictionary = model.getSelectedDictionary();
                if (selectedDictionary != null) {
                    String path = selectedDictionary.getDataPath() + selectedDictionary.getFileName();
                    asynchronousService.reindex(new ElexDictionary(path, "r"));
                }
            }

            SortedSet<AdminDictionary> dictionaryList = dictionariesPool.getAdminDictionaries(model);

            AdminDictionary[] dictionariesArray = dictionaryList.toArray(new AdminDictionary[dictionaryList.size()]);
            model.setAdminDictionaries(dictionariesArray);

            long t2 = System.currentTimeMillis();
            logger.info((t2 - t1) + " ms ");

            model.setAction(Action.INIT);

            return gson.toJson(model);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ExceptionUtils.getStackTrace(ex);
        }
    }
}
