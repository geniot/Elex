package io.github.geniot.elex;

import io.github.geniot.elex.ftindexer.FtServer;
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
            onShutDown();
        }));

        SwingUtilities.invokeLater(() -> {
            if (System.getProperty("desktop") != null) {
                application = new ElexApplication();
                setLAF(ElexPreferences.get(Prop.LAF.name(), "Luna"), application);
                application.setVisible(true);
            }
        });

        new Thread(() -> DictionariesPool.getInstance()).start();
        new Thread(() -> FtServer.getInstance()).start();
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

    /**
     * There is a 'better' way to check whether FileAlterationMonitor is running, described here:
     * https://stackoverflow.com/questions/36673817/commons-io-2-4-how-control-the-state-of-filealterationlistener-and-restart
     * <p>
     * Meanwhile I'll just use this boolean constant.
     */
    private static boolean isFtServerRunning = true;

    /**
     * On Linux onShutDown hook wasn't consistent (wasn't called every time the app got closed).
     * So this method is called from onWindowCLosing, addShutdownHook and exitButton.addActionListener.
     * As such it may be called twice or even thrice. And it's OK for now.
     */
    public static void onShutDown() {
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

        DictionariesPool.getInstance().close();

        if (isFtServerRunning) {
            FtServer.getInstance().stop();
            isFtServerRunning = false;
        }

    }
}
