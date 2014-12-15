package eu.arrvi.vects.server;

import com.oracle.docs.layout.SpringUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Kris on 2014-11-14.
 */
class ServerSettingsWindow extends JFrame {
    private final VectsServer controller;
    JTextField portInput = new JTextField("9595"), resolutionInput = new JTextField("100"), playersInput = new JTextField("2");
    JComboBox trackInput;

    JLabel message = new JLabel();

    public ServerSettingsWindow(VectsServer vectsServer) throws HeadlessException {
        super("Vects Server settings");
        controller = vectsServer;
        createGUI();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void createGUI() {
        JPanel windowPane = new JPanel(new BorderLayout());
//        JPanel settingsPane = new JPanel(new GridLayout(0, 2, 10, 10));
        JPanel settingsPane = new JPanel(new SpringLayout());

        windowPane.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        windowPane.add(new JLabel(new ImageIcon("res/VectsServerLogo.png")), BorderLayout.NORTH);

        settingsPane.setBorder(BorderFactory.createTitledBorder("Server settings"));

        trackInput = new JComboBox(Track.getAvailableTracks());
        trackInput.setRenderer(new TrackInputRenderer());

        settingsPane.add(new JLabel("Port: "));
        settingsPane.add(portInput);
        settingsPane.add(new JLabel("Track: "));
        settingsPane.add(trackInput);
        settingsPane.add(new JLabel("Resolution: "));
        settingsPane.add(resolutionInput);
        settingsPane.add(new JLabel("Players: "));
        settingsPane.add(playersInput);
        settingsPane.add(new JButton(exitAction));
        settingsPane.add(new JButton(startAction));

        SpringUtilities.makeCompactGrid(settingsPane, 5, 2, 5, 5, 5, 5);

        windowPane.add(settingsPane, BorderLayout.CENTER);

        message.setForeground(Color.RED.darker());
        windowPane.add(message, BorderLayout.SOUTH);

        this.add(windowPane);
    }

    private Action exitAction = new AbstractAction() {
        {
            putValue(NAME, "Exit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    private Action startAction = new AbstractAction() {
        {
            putValue(NAME, "Start server");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            message.setText("");
            try {
                controller.startGame(
                        Integer.parseInt(portInput.getText()),
                        (String) trackInput.getSelectedItem(),
                        Integer.parseInt(resolutionInput.getText()),
                        Integer.parseInt(playersInput.getText()));

                ServerSettingsWindow.this.setVisible(false);
            } catch (Exception e) {
                message.setText("<html><b>"+e.getClass().getSimpleName()+"</b><br>"+e.getMessage());
                e.printStackTrace();
            }
        }
    };

    private class TrackInputRenderer extends JLabel implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
//                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
//                setForeground(list.getForeground());
            }


            ImageIcon icon = new ImageIcon(getResizedImage((String)value));
            setIcon(icon);
            setText((String)value);
            setFont(list.getFont());

            return this;
        }

        public BufferedImage resize(BufferedImage image, int width, int height) {
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
            Graphics2D g2d = (Graphics2D) bi.createGraphics();
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
            g2d.drawImage(image, 0, 0, width, height, null);
            g2d.dispose();
            return bi;
        }

        public Image getResizedImage(String path) {
            BufferedImage image;
            try {
                 image = ImageIO.read(new File(path));
            } catch (IOException e) {
                return null;
            }
            return resize(image, 70, 70);
        }
    }

}
