package io.github.geniot.elex.ui;

import io.github.geniot.elex.ElexHttpServer;
import io.github.geniot.elex.ezip.Logger;

import javax.swing.*;
import java.awt.*;

public class ElexApplication extends DesktopApplication {

    MainPanel mainPanel;

    public ElexApplication() {
        setTitle("Elex");

        mainPanel = new MainPanel(this);
        Logger.getInstance().setTextComponent(mainPanel.textArea);
        Logger.getInstance().setScrollPane(mainPanel.scrollPane);
        ElexHttpServer.getInstance().addObserver(mainPanel);

        getContentPane().add(mainPanel.contentPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ElexHttpServer.getInstance().start();
            }
        });

        pack();
    }

}
