package eu.arrvi.vects.spectator;

import eu.arrvi.common.Pair;
import eu.arrvi.vects.common.GameInfo;
import eu.arrvi.vects.events.CommandEvent;
import eu.arrvi.vects.events.CommandEventListener;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for table that lists servers
 */
public class ServerListTableModel extends AbstractTableModel implements CommandEventListener {
    private List<GameInfoCapsule> data = new ArrayList<>(4);
    private List<Pair<String, Method>> columnMethods = new ArrayList<>();

    public ServerListTableModel() {
        Class<?> cls = GameInfoCapsule.class;
        try {
            columnMethods.add(new Pair<>("Address", cls.getDeclaredMethod("getAddressAndPort")));
            columnMethods.add(new Pair<>("Players", cls.getDeclaredMethod("getPlayersString")));
            columnMethods.add(new Pair<>("Track", cls.getDeclaredMethod("getTrackString")));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getAddress(int index) {
        return data.get(index).getGame().getAddress();
    }
    
    public int getPort(int index) {
        return data.get(index).getGame().getPort();
    }


    @Override
    public void commandReceived(CommandEvent event) {
        if (!event.getCommand().getName().equals("GAM")) return;

        if (!(event.getCommand().getFirstParam() instanceof GameInfo)) return;
        GameInfoCapsule param = new GameInfoCapsule((GameInfo) event.getCommand().getFirstParam());

        if (data.contains(param)) {
            int index = data.indexOf(param);
            data.get(index).setGame(param.getGame());
            fireTableRowsUpdated(index, index);
        }
        else {
            data.add(param);
            fireTableRowsInserted(data.indexOf(param), data.indexOf(param));
        }
    }


    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnMethods.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            return columnMethods.get(columnIndex).getValue().invoke(data.get(rowIndex));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columnMethods.get(column).getKey();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    @SuppressWarnings("unused")
    private class GameInfoCapsule {
        private GameInfo game;

        public GameInfoCapsule(GameInfo game) {
            this.game = game;
        }

        public GameInfo getGame() {
            return game;
        }

        public void setGame(GameInfo game) {
            this.game = game;
        }

        public String getAddressAndPort() {
            return String.format("%s:%d", game.getAddress().getHostName(), game.getPort());
        }

        public String getPlayersString() {
            return String.format("%d/%d", game.getPlayers(), game.getMaxPlayers());
        }
        
        public String getTrackString() {
            return String.format("%s (%d x %d)", game.getTrack().getPath(), game.getResolution(), game.getResolution()); 
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GameInfoCapsule that = (GameInfoCapsule) o;

            return game.equals(that.game);
        }

        @Override
        public int hashCode() {
            return game.hashCode();
        }
    }
}
