package eu.arrvi.vects.spectator;

import eu.arrvi.vects.common.Command;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Window in which user can select list all servers in local network and connect to one of them.
 */
public class ServerSelectWindow extends JFrame {
    private final JTable serverListTable;
    private final ServerListTableModel serverListTableModel = new ServerListTableModel();
    private DatagramHandler socket;

    public ServerSelectWindow() throws HeadlessException, SocketException, UnknownHostException {
        super("Vects Spectator");
        
        socket = new DatagramHandler(11531);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                socket.close();
            }
        });
        socket.addCommandEventListener("GAM", serverListTableModel); // game info only
        
        serverListTable = new JTable(serverListTableModel);
        serverListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serverListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if ( e.getValueIsAdjusting() ) return;
                boolean isSelected = serverListTable.getSelectedRow() != -1;
                connectAction.setEnabled(isSelected);
            }
        });

        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.setBorder(BorderFactory.createTitledBorder("Select server"));

        pane.add(new JButton(refreshAction), BorderLayout.NORTH);
        pane.add(new JScrollPane(serverListTable));
        pane.add(new JButton(connectAction), BorderLayout.SOUTH);

        setContentPane(pane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void sendRefresh() {
        socket.sendCommand(new Command(Command.TARGET_BROADCAST, "SRV"));
    }
    
    private void createGameWindow(InetAddress address, int port) {
        
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
            int selectedRow = serverListTable.getSelectedRow();
            if ( selectedRow == -1 ) return;
            
            createGameWindow(serverListTableModel.getAddress(selectedRow), serverListTableModel.getPort(selectedRow));
        }
    };
    
    @SuppressWarnings("FieldCanBeLocal")
    private final Action refreshAction = new AbstractAction() {
        {
            putValue(NAME, "Refresh");
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            sendRefresh();
        }
    };
}
