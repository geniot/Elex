package io.github.geniot.elex;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class Logger {
    JTextComponent jTextComponent;
    JScrollPane scrollPane;

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

    public void setScrollPane(JScrollPane sp) {
        this.scrollPane = sp;
    }

    public void log(String msg) {
        try {
            if (jTextComponent != null) {
                jTextComponent.getDocument().insertString(jTextComponent.getDocument().getLength(), msg + "\n", null);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JScrollBar vertical = scrollPane.getVerticalScrollBar();
                        vertical.setValue(vertical.getMaximum());
                    }
                });
            }
            System.out.println(msg);
        } catch (Exception ex) {
            System.out.println(msg);
            ex.printStackTrace();
        }
    }

    public void log(Throwable ex) {
        log(ExceptionUtils.getStackTrace(ex));
    }
}
