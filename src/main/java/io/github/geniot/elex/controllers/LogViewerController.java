package io.github.geniot.elex.controllers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;

@RestController
public class LogViewerController {
    Logger logger = LoggerFactory.getLogger(LogViewerController.class);

    @GetMapping(value = "/logs", produces = MediaType.TEXT_HTML_VALUE)
    public String index2() {
        try {
            String s = FileUtils.readFileToString(new File("logs/app.log"), StandardCharsets.UTF_8);
            String[] splits = s.split("\n");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<html><body>");
            //reverse order for now to see the latest events on top
            for (int i = splits.length - 1; i >= 0; i--) {
                String split = splits[i];
                stringBuffer.append("<span>");
                stringBuffer.append(split);
                stringBuffer.append("</span><br/>\n");
            }
            stringBuffer.append("</body></html>");
            return stringBuffer.toString();
        } catch (Exception ex) {
            logger.error("Couldn't read the log file.", ex);
            return ExceptionUtils.getStackTrace(ex);
        }
    }
}
