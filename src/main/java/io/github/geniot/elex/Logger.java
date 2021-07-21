package io.github.geniot.elex;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.text.JTextComponent;

public class Logger {
    JTextComponent jTextComponent;
    private static Logger INSTANCE;

    public static Logger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Logger();
        }
        return INSTANCE;
    }

    public void setTextComponent(JTextComponent tc) {
        this.jTextComponent = tc;
    }

    public void log(String msg) {
        try {
            if (jTextComponent != null) {
                jTextComponent.getDocument().insertString(jTextComponent.getDocument().getLength(), msg + "\n", null);
            } else {
                System.out.println(msg);
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
