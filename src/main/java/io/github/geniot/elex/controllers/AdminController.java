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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
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

    @GetMapping("/admin/download")
    public ResponseEntity<Resource> download(@RequestParam int id,
                                             @RequestParam String type) {
        try {
            String path = dictionariesPool.getDownloadFilePath(id, type);
            if (path != null) {
                String filename = path.substring(path.lastIndexOf(File.separator) + 1);
                InputStreamResource resource = new InputStreamResource(new FileInputStream(path));

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(new File(path).length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

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
            } else if (model.getAction().equals(Action.TOGGLE_DICTIONARY_STATE)) {
                AdminDictionary selectedDictionary = model.getSelectedDictionary();
                if (selectedDictionary != null) {
                    dictionariesPool.changeState(selectedDictionary);
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
