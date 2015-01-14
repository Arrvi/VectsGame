package eu.arrvi.vects.server;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Created by Kris on 2015-01-14.
 */
public class ServerList extends JPanel implements ListDataListener {
    private final ListModel<Server> model = new DefaultListModel<>();
    
    
    public ServerList() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public ListModel<Server> getModel() {
        return model;
    }

    /**
     * Sent after the indices in the index0,index1
     * interval have been inserted in the data model.
     * The new interval includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the
     *          event information
     */
    @Override
    public void intervalAdded(ListDataEvent e) {
        
    }

    /**
     * Sent after the indices in the index0,index1 interval
     * have been removed from the data model.  The interval
     * includes both index0 and index1.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the
     *          event information
     */
    @Override
    public void intervalRemoved(ListDataEvent e) {

    }

    /**
     * Sent when the contents of the list has changed in a way
     * that's too complex to characterize with the previous
     * methods. For example, this is sent when an item has been
     * replaced. Index0 and index1 bracket the change.
     *
     * @param e a <code>ListDataEvent</code> encapsulating the
     *          event information
     */
    @Override
    public void contentsChanged(ListDataEvent e) {

    }
}
