package eu.arrvi.vects.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Kris on 2014-11-14.
 */
class Server implements Runnable {

    private final Thread serverThread;
    private final Game game;
    private int port = 9595;
    private ServerSocket listener;

    public Server(Game game) {
        this.game = game;
        serverThread = new Thread(this);
    }

    public void start() throws IOException {
        listener = new ServerSocket(port);
        serverThread.start();
    }

    @Override
    public void run() {
        System.out.println("Server listening at port "+port+"...");

        try {
            while (true) {
                Socket socket = listener.accept();
                System.out.println("connected " + socket.getPort());
                new ServerSocketHandler(socket, game);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
