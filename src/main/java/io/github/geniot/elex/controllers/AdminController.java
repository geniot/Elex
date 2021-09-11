package io.github.geniot.elex.controllers;

import com.google.gson.Gson;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.handlers.updaters.DictionariesUpdater;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AdminController.class);

    Gson gson = new Gson();

    @Autowired
    DictionariesPool dictionariesPool;
    @Autowired
    DictionariesUpdater dictionariesUpdater;

    @PostMapping("/admin")
    public String handle(@RequestBody String payload) {
        try {
            long t1 = System.currentTimeMillis();

            Model model = gson.fromJson(payload, Model.class);
            List<Dictionary> dictionaryList = dictionariesPool.getDictionaries(model);
            dictionariesUpdater.updateDictionaries(model, dictionaryList);

            long t2 = System.currentTimeMillis();
            logger.info((t2 - t1) + " ms ");

            return gson.toJson(model);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ExceptionUtils.getStackTrace(ex);
        }
    }
}
