package io.github.geniot.elex;

import javax.swing.*;

public class ElexLauncher {

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            ElexApplication application = new ElexApplication();
            application.setVisible(true);
        });
    }

}
