package io.github.geniot.elex;

import io.github.geniot.elex.ui.ElexApplication;
import io.github.geniot.elex.ui.ElexPreferences;
import io.github.geniot.elex.ui.ElexPreferences.Prop;

import javax.swing.*;
import java.awt.*;

public class ElexLauncher {
    static ElexApplication application;
    public static final String DEFAULT_LAF = "com.jtattoo.plaf.smart.SmartLookAndFeel";
    public static String LAF_PREFIX = "com.jtattoo.plaf.";
    public static String LAF_SUFFIX = "LookAndFeel";

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ElexPreferences.putInt(Prop.WIDTH.name(), application.getWidth());
                ElexPreferences.putInt(Prop.HEIGHT.name(), application.getHeight());
                ElexPreferences.putInt(Prop.POS_X.name(), (int) application.getLocation().getX());
                ElexPreferences.putInt(Prop.POS_Y.name(), (int) application.getLocation().getY());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                ElexHttpServer.getInstance().stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        SwingUtilities.invokeLater(() -> {
            application = new ElexApplication();
            setLAF(ElexPreferences.get(Prop.LAF.name(), "Luna"), application);
            application.setVisible(true);
        });
    }


    public static void setLAF(String lafName, Component component) {
        String lafClassName = LAF_PREFIX + lafName.toLowerCase() + "." + lafName + LAF_SUFFIX;
        try {
            if (lafClassName == null) {
                lafClassName = DEFAULT_LAF;
            }
            UIManager.setLookAndFeel(lafClassName);
            SwingUtilities.updateComponentTreeUI(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
