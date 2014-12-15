package eu.arrvi.vects.server;

import eu.arrvi.common.Pair;

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by Kris on 2014-11-14.
*/
class PlayerTableModel extends DefaultTableModel {
    private List<ServerSocketHandler> data = new ArrayList<>();
//    private static List<Map.Entry<String, Method>> columns = new ArrayList<>();
    private static List<Pair<String, Method>> columns = new ArrayList<>();

    {
        try {
//            Map<String, Method> cols = new HashMap<>();
//            cols.put("ID", ServerSocketHandler.class.getDeclaredMethod("getPort"));
//            cols.put("Status", ServerSocketHandler.class.getDeclaredMethod("getStatusString"));
//            cols.put("Position", ServerSocketHandler.class.getDeclaredMethod("getPosition"));
//            cols.put("Tile", ServerSocketHandler.class.getDeclaredMethod("getTile"));
//            cols.put("Speed", ServerSocketHandler.class.getDeclaredMethod("getSpeedString"));
//            columns.add(new Map.Entry<>("A", ServerSocketHandler.class.getDeclaredMethod("getPort")))
//            columns.addAll(cols.entrySet());

            columns.add(new Pair<>("ID", ServerSocketHandler.class.getDeclaredMethod("getPort")));
            columns.add(new Pair<>("Status", ServerSocketHandler.class.getDeclaredMethod("getStatusString")));
            columns.add(new Pair<>("Position", ServerSocketHandler.class.getDeclaredMethod("getPosition")));
            columns.add(new Pair<>("Tile", ServerSocketHandler.class.getDeclaredMethod("getTile")));
            columns.add(new Pair<>("Speed", ServerSocketHandler.class.getDeclaredMethod("getSpeedString")));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public PlayerTableModel() {
        super();
    }

    public void setData(List<ServerSocketHandler> data) {
        this.data = data;
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getRowCount() {
        if ( data == null ) return 0;
        return data.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Method method = columns.get(column).getValue();
        Object value = null;
        try {
            value = method.invoke(data.get(row));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if ( value == null ) return "-";
        return value.toString();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).getKey();
    }
}
