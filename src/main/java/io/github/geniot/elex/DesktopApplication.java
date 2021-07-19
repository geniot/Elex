package io.github.geniot.elex;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class DesktopApplication extends JFrame {
    public static final Image ICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/favicon_io/favicon-32x32.png")).getImage();
    Logger logger;

    public DesktopApplication() {
        super();
        setIconImage(ICON);
        //Display the window.
        try {
            int width = ElexPreferences.getInt(Prop.WIDTH.name(), 600);
            int height = ElexPreferences.getInt(Prop.HEIGHT.name(), 800);
            setPreferredSize(new Dimension(width, height));
        } catch (Exception ex) {
            setPreferredSize(new Dimension(600, 800));
        }

        try {
            int posX = ElexPreferences.getInt(Prop.POS_X.name(), 50);
            int posY = ElexPreferences.getInt(Prop.POS_Y.name(), 50);
            setLocation(posX, posY);
        } catch (Exception ex) {
            setLocation(0, 0);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    super.windowClosing(e);
                    onWindowClosing();
                    ElexPreferences.putInt(Prop.WIDTH.name(), e.getWindow().getWidth());
                    ElexPreferences.putInt(Prop.HEIGHT.name(), e.getWindow().getHeight());
                    ElexPreferences.putInt(Prop.POS_X.name(), (int) e.getWindow().getLocation().getX());
                    ElexPreferences.putInt(Prop.POS_Y.name(), (int) e.getWindow().getLocation().getY());
                    e.getWindow().dispose();
                } catch (Exception ex) {
                    logger.log(ex);
                } finally {
                    System.exit(0);
                }

            }
        });
    }


    abstract public void onWindowClosing();

    public enum Prop {
        WIDTH, HEIGHT, POS_X, POS_Y
    }

}

