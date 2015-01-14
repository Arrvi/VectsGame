package eu.arrvi.vects.server;

import eu.arrvi.common.UIUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Kris on 2015-01-14.
 */
class LobbyWindow extends JFrame {

    private final ServerList serverList;

    public LobbyWindow() throws HeadlessException {
        super();
        
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.setBorder(BorderFactory.createTitledBorder("Server list"));
        pane.add(new JButton(createServerAction), BorderLayout.NORTH);
        serverList = new ServerList();
        pane.add(new JScrollPane(serverList));
        
        setContentPane(pane);

        UIUtilities.packAndShow(this);
    }
    
    @SuppressWarnings("FieldCanBeLocal")
    private Action createServerAction = new AbstractAction() {
        {
            putValue(NAME, "Create new server");
            putValue(LARGE_ICON_KEY, new ImageIcon("res/list-add.png"));
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e) {
//            ((DefaultListModel) serverList.getModel()).addElement();
        }
    };
}
