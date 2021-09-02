package io.github.geniot.elex.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {
    Logger logger = LoggerFactory.getLogger(DataController.class);

    @PostMapping("/data")
    public String index() {

        logger.info("/data post");
        return "est";
    }

    @GetMapping("/data")
    public String index2() {
        logger.info("/data get");
        return "est";
    }
}
