package io.github.geniot.elex;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class Logger {
    ElexApplication frame;

    public Logger(ElexApplication f) {
        this.frame = f;
    }

    public void log(String msg) {
        try {
            if (frame.mainPanel != null && frame.mainPanel.textArea != null) {
                frame.mainPanel.textArea.append(msg);
                frame.mainPanel.textArea.append("\n");
            }
        } catch (Exception ex) {
            System.out.println(msg);
            ex.printStackTrace();
        }
    }

    public void log(Exception ex) {
        log(ExceptionUtils.getStackTrace(ex));
    }
}
