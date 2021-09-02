package io.github.geniot.elex.ui;

import io.github.geniot.elex.ElexHttpServer;
import io.github.geniot.elex.ElexLauncher;
import io.github.geniot.elex.ezip.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ElexApplication extends DesktopApplication {

    MainPanel mainPanel;

    public ElexApplication() {
        setTitle("Elex");

        mainPanel = new MainPanel(this);
        Logger.getInstance().setTextComponent(mainPanel.textArea);
        Logger.getInstance().setScrollPane(mainPanel.scrollPane);
        ElexHttpServer.getInstance().addObserver(mainPanel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ElexLauncher.onShutDown();
            }
        });

        getContentPane().add(mainPanel.contentPanel, BorderLayout.CENTER);
        pack();
    }

}
