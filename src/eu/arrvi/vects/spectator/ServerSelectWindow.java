package eu.arrvi.vects.spectator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Window in which user can select list all servers in local network and connect to one of them.
 */
public class ServerSelectWindow extends JFrame {
    public ServerSelectWindow() throws HeadlessException {
        super("Vects Spectator");

        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.setBorder(BorderFactory.createTitledBorder("Select server"));

        pane.add(new JScrollPane(new JTable(new ServerListTableModel())));
        pane.add(new JButton(connectAction), BorderLayout.SOUTH);

        setContentPane(pane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Connects to server when selected from list and action is performed. Should become enabled on server selection.
     * After connection select window should close.
     */
    private final Action connectAction = new AbstractAction() {
        {
            putValue(NAME, "Connect");
            setEnabled(false);
        }
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
}
