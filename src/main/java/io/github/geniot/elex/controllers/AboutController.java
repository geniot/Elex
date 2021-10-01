package io.github.geniot.elex.controllers;

import com.google.gson.Gson;
import io.github.geniot.elex.DictionariesPool;
import io.github.geniot.elex.model.AboutModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AboutController {
    Logger logger = LoggerFactory.getLogger(AboutController.class);

    Gson gson = new Gson();

    @Autowired
    DictionariesPool dictionariesPool;

    @PostMapping("/about")
    public String handle(@RequestBody String payload) {
        try {
            long t1 = System.currentTimeMillis();
            AboutModel model = gson.fromJson(payload, AboutModel.class);
            model.setAbouts(dictionariesPool.getAbouts(model.getDictionary().getFileName()));
            String res = gson.toJson(model);
            long t2 = System.currentTimeMillis();
            logger.info((t2 - t1) + " ms About Dictionary");
            return res;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return ExceptionUtils.getStackTrace(ex);
        }
    }
}
