package io.github.geniot.elex;

import javax.swing.*;
import java.awt.*;

public class ElexApplication extends DesktopApplication {

    MainPanel mainPanel;

    ElexServer server;


    public ElexApplication() {
        setTitle("Elex");
        server = new ElexServer();

        mainPanel = new MainPanel(this, server);
        Logger.getInstance().setTextComponent(mainPanel.textArea);
        Logger.getInstance().setScrollPane(mainPanel.scrollPane);
        server.addObserver(mainPanel);


        getContentPane().add(mainPanel.contentPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                server.start();
            }
        });

        pack();
    }

    @Override
    public void onWindowClosing() {
        server.stop();
    }

}
