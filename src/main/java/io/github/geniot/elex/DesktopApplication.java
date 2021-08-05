package io.github.geniot.elex;


import io.github.geniot.elex.ElexPreferences.Prop;

import javax.swing.*;
import java.awt.*;

public abstract class DesktopApplication extends JFrame {


    public static final Image ICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/favicon_io/favicon-32x32.png")).getImage();

    public DesktopApplication() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    }



}

