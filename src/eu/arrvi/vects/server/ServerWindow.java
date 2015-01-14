package eu.arrvi.vects.server;

import eu.arrvi.common.UIUtilities;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Kris on 2014-11-14.
 */
class ServerWindow extends JFrame implements PropertyChangeListener {
    private JTable playerTable;
    private JLabel status;

    private PlayerTableModel playerTableModel = new PlayerTableModel();

    public ServerWindow() throws HeadlessException {
        super("Vects Server");

        createGUI();

        UIUtilities.packAndShow(this);
    }

    private void createGUI() {
        JPanel pane = new JPanel(new BorderLayout(0, 15)), outer = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(15,15,15,15), BorderFactory.createTitledBorder("Players")));
        outer.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        status = new JLabel(" ");
        pane.add(status, BorderLayout.NORTH);

        playerTable = new JTable(playerTableModel);

        pane.add(new JScrollPane(playerTable), BorderLayout.CENTER);

        outer.add(new JLabel(new ImageIcon("res/VectsServerLogo.png")), BorderLayout.NORTH);
        outer.add(pane, BorderLayout.CENTER);
        this.add(outer);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            getClass().getMethod(evt.getPropertyName()+"Change", Object.class, Object.class).invoke(this, evt.getOldValue(), evt.getNewValue());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void statusChange(Object oldValue, Object newValue) {
        status.setText("<html><b>Status:</b> "+Game.getStatusString((int)newValue));
    }

    public void playerListChange(Object oldValue, Object newValue) {
        playerTableModel.setData((List<ServerSocketHandler>) newValue);
    }
}
