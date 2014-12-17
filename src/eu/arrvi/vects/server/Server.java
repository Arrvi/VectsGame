package eu.arrvi.vects.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Vects server which accepts incoming connections from clients. 
 */
class Server implements Runnable, PropertyChangeListener {

    private final Thread serverThread;
    private final Game game;
    private int port = 9595;
    private ServerSocket listener;

    /**
     * Creates server which accepts new connection for given game.
     * @param game game to which new clients will be added
     */
    public Server(Game game) {
        this.game = game;
        game.addPropertyChangeListener("status", this);
        serverThread = new Thread(this);
    }

    /**
     * Creates server socket and starts listener thread. Thread closes when game starts.
     * @throws IOException on problems with creating server socket
     */
    public void start() throws IOException {
        listener = new ServerSocket(port);
        serverThread.start();
    }

    /**
     * Accepts new connections until Exception is thrown. In most cases it would be SocketException thrown on socket 
     * closing.
     */
    @Override
    public void run() {
        System.out.println("Server listening on port "+port+"...");

        while (true) {
            try {
                Socket socket = listener.accept();
                System.out.println("connected " + socket.getPort());
                new ServerSocketHandler(socket, game);
            }
            catch (SocketException e) {
                System.out.println("Server does not accept further connections");
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Returns a port on which server is listening.
     * @return port on which server is listening
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets new port for server to be listened on. Takes effect only before calling `start()`.
     * @param port new port to be listened on
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Listening for game changes to accept new clients only when game is waiting for them.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if( !evt.getNewValue().equals(Game.WAITING) ) {
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
