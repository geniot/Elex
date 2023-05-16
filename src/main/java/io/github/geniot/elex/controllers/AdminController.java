package io.github.geniot.elex.controllers;

import com.google.gson.Gson;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.ezip.model.ElexDictionary;
import io.github.geniot.elex.model.Action;
import io.github.geniot.elex.model.AdminDictionary;
import io.github.geniot.elex.model.AdminModel;
import io.github.geniot.elex.model.DictionaryStatus;
import io.github.geniot.elex.model.Task;
import io.github.geniot.elex.model.TaskExecutorModel;
import io.github.geniot.elex.tasks.AsynchronousService;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AdminController.class);

    Gson gson = new Gson();

    @Autowired
    DictionariesPool dictionariesPool;
    @Autowired
    AsynchronousService asynchronousService;

    @GetMapping("/download")
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

    @GetMapping("/tasks")
    public String tasks() {
        TaskExecutorModel taskExecutorModel = new TaskExecutorModel();
        Collection<Task> tasksList = asynchronousService.getRunningTasks().values();
        taskExecutorModel.setTasks(tasksList.toArray(new Task[0]));
        asynchronousService.cleanUp();
        return gson.toJson(taskExecutorModel);
    }

    @PostMapping("/adminData")
    public String handle(@RequestBody String payload) {
        try {
            long t1 = System.currentTimeMillis();

            AdminModel adminModel = gson.fromJson(payload, AdminModel.class);

            AdminDictionary selectedDictionary = adminModel.getSelectedDictionary();
            if (selectedDictionary != null) {
                if (adminModel.getAction().equals(Action.REINDEX) &&
                        selectedDictionary.getStatus().equals(DictionaryStatus.ENABLED)) {
                    String path = selectedDictionary.getDataPath() + selectedDictionary.getFileName();
                    asynchronousService.reindex(new ElexDictionary(path, "r"));
                } else if (adminModel.getAction().equals(Action.REINDEX_ALL)) {
                    Map<String, ElexDictionary> dictionaryMap = dictionariesPool.getDictionaries();
                    for (ElexDictionary elexDictionary : dictionaryMap.values()) {
                        asynchronousService.reindex(new ElexDictionary(elexDictionary.getFile().getAbsolutePath(), "r"));
                    }
                } else if (adminModel.getAction().equals(Action.TOGGLE_DICTIONARY_STATE)) {
                    dictionariesPool.changeState(selectedDictionary);
                }
            }

            SortedSet<AdminDictionary> dictionaryList = dictionariesPool.getAdminDictionaries(adminModel);
            AdminDictionary[] dictionariesArray = dictionaryList.toArray(new AdminDictionary[0]);
            adminModel.setAdminDictionaries(dictionariesArray);
            adminModel.selectOneDictionary(adminModel.getAction().equals(Action.TOGGLE_DICTIONARY_STATE) ? selectedDictionary : null);

            long t2 = System.currentTimeMillis();
            logger.info((t2 - t1) + " ms ");

            //            adminModel.setAction(Action.INIT);

            return gson.toJson(adminModel);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ExceptionUtils.getStackTrace(ex);
        }
    }
}
