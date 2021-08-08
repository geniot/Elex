package io.github.geniot.elex;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class ElexApplication extends DesktopApplication {

    MainPanel mainPanel;

    ElexHttpServer elexHttpServer;
    DatabaseServer databaseServer;


    public ElexApplication() {
        setTitle("Elex");

        elexHttpServer = new ElexHttpServer();
        databaseServer = new DatabaseServer();

        mainPanel = new MainPanel(this, elexHttpServer);
        Logger.getInstance().setTextComponent(mainPanel.textArea);
        Logger.getInstance().setScrollPane(mainPanel.scrollPane);
        elexHttpServer.addObserver(mainPanel);

        getContentPane().add(mainPanel.contentPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                elexHttpServer.start();
            }
        });

        String mode = System.getProperty("mode");
        if (StringUtils.isEmpty(mode)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    databaseServer.start();
                }
            });
        }
        pack();
    }

}
