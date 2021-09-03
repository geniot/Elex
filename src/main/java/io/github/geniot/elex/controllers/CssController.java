package io.github.geniot.elex.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CssController {
    Logger logger = LoggerFactory.getLogger(CssController.class);

    @RequestMapping(value = "/css", method = RequestMethod.GET)
    public String handle() {
        return "";
    }
}
