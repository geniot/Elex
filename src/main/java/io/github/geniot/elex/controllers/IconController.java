package io.github.geniot.elex.controllers;

import io.github.geniot.elex.DictionariesPool;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class IconController {
    Logger logger = LoggerFactory.getLogger(IconController.class);

    @Autowired
    DictionariesPool dictionariesPool;

    private byte[] defaultIcon;

    private byte[] getDefaultIcon() {
        try {
            if (defaultIcon == null) {
                defaultIcon = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/user.png"));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            defaultIcon = new byte[]{};
        }
        return defaultIcon;
    }

    @RequestMapping(value = "/icon", method = RequestMethod.GET)
    public void handle(HttpServletResponse response,
                       @RequestParam int id) {
        try {
            byte[] iconBytes = getDefaultIcon();
            byte[] bbs = dictionariesPool.getIcon(id);
            if (bbs != null) {
                iconBytes = bbs;
            }
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.getOutputStream().write(iconBytes);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
