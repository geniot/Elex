package io.github.geniot.elex;

import io.github.geniot.elex.ElexPreferences.Prop;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

import static io.github.geniot.elex.ElexLauncher.setLAF;

public class MainPanel implements Observer {
    public static final ImageIcon CONNECT_ICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/connect.png"));
    public static final ImageIcon DISCONNECT_ICON = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("images/disconnect.png"));

    public JPanel contentPanel;
    public JButton openButton;
    public JTextArea textArea;
    private JToggleButton pinButton;
    public JToggleButton connectButton;
    private JButton clearButton;
    private JButton helpButton;
    public JScrollPane scrollPane;
    private JTextField hostTextField;
    private JButton resetButton;
    private JButton exitButton;
    private JComboBox themeComboBox;
    private JPanel settingsPanel;
    private JLabel spacerLabel;

    private ElexApplication frame;
    private ElexServer server;

    private static final String UNPIN_MSG = "Unpin this window from top";
    private static final String PIN_MSG = "Pin this window on top";
    private static final String OPEN_MSG = "Open Elex in the default browser";
    private static final String STOP_MSG = "Stop the server";
    private static final String START_MSG = "Start the server";

    @Override
    public void update(Observable o, Object arg) {
        if (server.status.equals(Prop.STARTED)) {
            connectButton.setSelected(true);
            connectButton.setIcon(CONNECT_ICON);
            connectButton.setToolTipText(STOP_MSG);
        } else {
            connectButton.setSelected(false);
            connectButton.setIcon(DISCONNECT_ICON);
            connectButton.setToolTipText(START_MSG);
        }
    }


    public MainPanel(ElexApplication f, ElexServer s) {
        this.frame = f;
        this.server = s;

        int margin = 5;
        textArea.setMargin(new Insets(margin, margin, margin, margin));

        settingsPanel.setLayout(new WrapLayout());

        openButton.setToolTipText(OPEN_MSG);
        openButton.addActionListener(e -> {
            try {
                String host = ElexPreferences.get(Prop.HOST.name(), "localhost");
                int port = ElexPreferences.getInt(Prop.PORT.name(), 8000);

                Desktop.getDesktop().browse(URI.create("http://" + host + ":" + port));
            } catch (IOException ioException) {
                Logger.getInstance().log(ioException);
            }
        });

        pinButton.setSelected(ElexPreferences.getBoolean(Prop.PIN.name(), false));
        frame.setAlwaysOnTop(pinButton.isSelected());


        pinButton.setToolTipText(pinButton.isSelected() ? UNPIN_MSG : PIN_MSG);
        pinButton.addActionListener(e -> {
            ElexPreferences.pubBoolean(Prop.PIN.name(), pinButton.isSelected());
            frame.setAlwaysOnTop(pinButton.isSelected());
            pinButton.setToolTipText(pinButton.isSelected() ? UNPIN_MSG : PIN_MSG);
        });

        connectButton.addActionListener(e -> {
            if (connectButton.isSelected()) {
                server.start();
            } else {
                server.stop();
            }
        });

        clearButton.addActionListener(e -> {
            textArea.setText("");
        });

        helpButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/geniot/elex"));
            } catch (IOException ioException) {
                Logger.getInstance().log(ioException);
            }
        });

        resetButton.addActionListener(e -> {
            try {
                ElexPreferences.put(Prop.HOST.name(), "localhost");
                ElexPreferences.putInt(Prop.PORT.name(), 8000);
                reset();
            } catch (Exception ioException) {
                Logger.getInstance().log(ioException);
            }
        });

        exitButton.addActionListener(e -> {
            try {
                frame.dispose();
                System.exit(0);
            } catch (Exception ioException) {
                Logger.getInstance().log(ioException);
            }
        });

        //host
        hostTextField.setBorder(BorderFactory.createCompoundBorder(
                hostTextField.getBorder(),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        hostTextField.getCaret().setBlinkRate(0);

        // Listen for changes in the text
        hostTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                save();
            }

            public void removeUpdate(DocumentEvent e) {
                save();
            }

            public void insertUpdate(DocumentEvent e) {
                save();
            }

            public void save() {
                try {
                    if (!StringUtils.isEmpty(hostTextField.getText())) {
                        String[] splits = hostTextField.getText().split(":");
                        ElexPreferences.put(Prop.HOST.name(), splits[0]);
                        ElexPreferences.putInt(Prop.PORT.name(), Integer.parseInt(splits[1]));
                    }
                } catch (Exception ex) {
                    Logger.getInstance().log(ex);
                }
            }
        });

        themeComboBox.setSelectedItem(ElexPreferences.get(Prop.LAF.name(), "Luna"));
        themeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newLaf = themeComboBox.getSelectedItem().toString();
                ElexPreferences.put(Prop.LAF.name(), newLaf);
                setLAF(newLaf, frame);
            }
        });

        reset();


    }

    private void reset() {
        String host = ElexPreferences.get(Prop.HOST.name(), "localhost");
        int port = ElexPreferences.getInt(Prop.PORT.name(), 8000);
        hostTextField.setText(host + ":" + port);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.requestFocus();
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        contentPanel.add(panel1, BorderLayout.NORTH);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        openButton = new JButton();
        openButton.setFocusPainted(false);
        openButton.setFocusable(false);
        openButton.setIcon(new ImageIcon(getClass().getResource("/images/world_go.png")));
        openButton.setMaximumSize(new Dimension(40, 40));
        openButton.setMinimumSize(new Dimension(40, 40));
        openButton.setPreferredSize(new Dimension(40, 40));
        openButton.setText("");
        panel1.add(openButton);
        pinButton = new JToggleButton();
        pinButton.setFocusPainted(false);
        pinButton.setFocusable(false);
        pinButton.setIcon(new ImageIcon(getClass().getResource("/images/location_pin.png")));
        pinButton.setMaximumSize(new Dimension(40, 40));
        pinButton.setMinimumSize(new Dimension(40, 40));
        pinButton.setPreferredSize(new Dimension(40, 40));
        pinButton.setText("");
        panel1.add(pinButton);
        connectButton = new JToggleButton();
        connectButton.setFocusPainted(false);
        connectButton.setFocusable(false);
        connectButton.setIcon(new ImageIcon(getClass().getResource("/images/disconnect.png")));
        connectButton.setMaximumSize(new Dimension(40, 40));
        connectButton.setMinimumSize(new Dimension(40, 40));
        connectButton.setPreferredSize(new Dimension(40, 40));
        connectButton.setText("");
        panel1.add(connectButton);
        clearButton = new JButton();
        clearButton.setFocusPainted(false);
        clearButton.setFocusable(false);
        clearButton.setIcon(new ImageIcon(getClass().getResource("/images/document_empty.png")));
        clearButton.setMaximumSize(new Dimension(40, 40));
        clearButton.setMinimumSize(new Dimension(40, 40));
        clearButton.setPreferredSize(new Dimension(40, 40));
        clearButton.setText("");
        panel1.add(clearButton);
        helpButton = new JButton();
        helpButton.setFocusPainted(false);
        helpButton.setFocusable(false);
        helpButton.setIcon(new ImageIcon(getClass().getResource("/images/help.png")));
        helpButton.setMaximumSize(new Dimension(40, 40));
        helpButton.setMinimumSize(new Dimension(40, 40));
        helpButton.setPreferredSize(new Dimension(40, 40));
        helpButton.setText("");
        panel1.add(helpButton);
        prefsButton = new JButton();
        prefsButton.setFocusPainted(false);
        prefsButton.setFocusable(false);
        prefsButton.setIcon(new ImageIcon(getClass().getResource("/images/Style.png")));
        prefsButton.setMaximumSize(new Dimension(40, 40));
        prefsButton.setMinimumSize(new Dimension(40, 40));
        prefsButton.setPreferredSize(new Dimension(40, 40));
        prefsButton.setText("");
        panel1.add(prefsButton);
        exitButton = new JButton();
        exitButton.setFocusPainted(false);
        exitButton.setFocusable(false);
        exitButton.setIcon(new ImageIcon(getClass().getResource("/images/door_in.png")));
        exitButton.setMaximumSize(new Dimension(40, 40));
        exitButton.setMinimumSize(new Dimension(40, 40));
        exitButton.setPreferredSize(new Dimension(40, 40));
        exitButton.setText("");
        panel1.add(exitButton);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        contentPanel.add(panel2, BorderLayout.CENTER);
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane = new JScrollPane();
        panel2.add(scrollPane, BorderLayout.CENTER);
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setViewportView(textArea);
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel2.add(settingsPanel, BorderLayout.NORTH);
        settingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(10);
        label1.setHorizontalTextPosition(10);
        label1.setText("Bind address: http://");
        settingsPanel.add(label1);
        hostTextField = new JTextField();
        settingsPanel.add(hostTextField);
        resetButton = new JButton();
        resetButton.setFocusPainted(false);
        resetButton.setFocusable(false);
        resetButton.setText("Reset");
        settingsPanel.add(resetButton);
        spacerLabel = new JLabel();
        spacerLabel.setMaximumSize(new Dimension(30, 0));
        spacerLabel.setMinimumSize(new Dimension(30, 0));
        spacerLabel.setPreferredSize(new Dimension(30, 0));
        spacerLabel.setText("");
        settingsPanel.add(spacerLabel);
        final JLabel label2 = new JLabel();
        label2.setText("Look-and-feel:");
        settingsPanel.add(label2);
        themeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Acryl");
        defaultComboBoxModel1.addElement("Aero");
        defaultComboBoxModel1.addElement("Aluminium");
        defaultComboBoxModel1.addElement("Bernstein");
        defaultComboBoxModel1.addElement("Fast");
        defaultComboBoxModel1.addElement("Graphite");
        defaultComboBoxModel1.addElement("HiFi");
        defaultComboBoxModel1.addElement("Luna");
        defaultComboBoxModel1.addElement("McWin");
        defaultComboBoxModel1.addElement("Mint");
        defaultComboBoxModel1.addElement("Noire");
        defaultComboBoxModel1.addElement("Smart");
        defaultComboBoxModel1.addElement("Texture");
        themeComboBox.setModel(defaultComboBoxModel1);
        settingsPanel.add(themeComboBox);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPanel;
    }

}
