package io.github.geniot.elex.controllers;

import com.google.gson.Gson;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.handlers.updaters.DictionariesUpdater;
import io.github.geniot.elex.handlers.updaters.EntriesUpdater;
import io.github.geniot.elex.handlers.updaters.HeadwordsUpdater;
import io.github.geniot.elex.handlers.updaters.LanguagesUpdater;
import io.github.geniot.elex.model.Dictionary;
import io.github.geniot.elex.model.Model;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.SortedSet;

@RestController
public class DataController {
    Logger logger = LoggerFactory.getLogger(DataController.class);

    Gson gson = new Gson();

    @Autowired
    DictionariesPool dictionariesPool;
    @Autowired
    DictionariesUpdater dictionariesUpdater;
    @Autowired
    LanguagesUpdater languagesUpdater;
    @Autowired
    HeadwordsUpdater headwordsUpdater;
    @Autowired
    EntriesUpdater entriesUpdater;

    @PostMapping("/data")
    public String handle(@RequestBody String payload) {
        try {
            long t1 = System.currentTimeMillis();

            Model model = gson.fromJson(payload, Model.class);
            SortedSet<Dictionary> dictionaryList = dictionariesPool.getDictionaries(model);

            long t2 = System.currentTimeMillis();

            languagesUpdater.updateLanguages(model, dictionaryList);
            dictionariesUpdater.updateDictionaries(model, dictionaryList);

            long t3 = System.currentTimeMillis();
            headwordsUpdater.updateHeadwords(model);
            long t4 = System.currentTimeMillis();
            entriesUpdater.updateEntries(model);

            String res = gson.toJson(model);
            long t5 = System.currentTimeMillis();
            logger.info((t2 - t1) + ":" + (t3 - t2) + ":" + (t4 - t3) + ":" + (t5 - t4) + " ms " + model.getSelectedHeadword());
            return res;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ExceptionUtils.getStackTrace(ex);
        }
    }
}
