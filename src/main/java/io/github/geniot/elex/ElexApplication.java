package io.github.geniot.elex;

import javax.swing.*;
import java.awt.*;

public class ElexApplication extends DesktopApplication {

    MainPanel mainPanel;

    ElexServer server;


    public ElexApplication() {
        setTitle("Elex");
        logger = new Logger(this);
        server = new ElexServer(logger);

        mainPanel = new MainPanel(this, logger, server);
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
