package socketEngine;

import networking.ASocket;
import networking.PacketBuilder;
import networking.SocketAdapter;
import networking.SocketListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking.Request;

/**
 *
 * @author alexisvincent
 */
public class SimpleSocketEngine implements Runnable {

    private ServerSocket serverSocket;
    private ArrayList<ASocket> sockets;
    private ArrayList<SocketListener> socketListeners;
    private int port;
    private Thread serverThread;
    private PacketBuilder requestBuilder;
    private boolean online;

    public SimpleSocketEngine(int port) {
        this.port = port;
        sockets = new ArrayList<>();
        socketListeners = new ArrayList<>();
        requestBuilder = new PacketBuilder();

        addSocketListener(new SocketListener() {
            @Override
            public void socketConnected(ASocket socket) {
                System.out.println("Server: Client Connected");
            }

            @Override
            public void socketDisconnected(ASocket socket) {
                System.out.println("Server: Client Disconnected\n");
            }

            @Override
            public void socketResponded(ASocket socket, String responce) {
                System.out.println("Server: Socket @ " + socket.getSocket().getInetAddress().toString().substring(1) + " Responded");
            }
        });

        addSocketListener(new SocketAdapter() {
            @Override
            public void socketResponded(ASocket socket, String responce) {
                Request request;

                if ((request = requestBuilder.addRequestPiece(responce, socket)) != null) {
                    main.Main.getRequestEngine().postRequest(request);
                };
            }
        });
    }

    public void start() {
        try {
            online = true;
            
            serverSocket = null;
            
            serverSocket = new ServerSocket(port);
            serverThread = new Thread(this);
            serverThread.start();
            System.out.println("Socket Engine started on port: " + port + "\nListening for new connections");
        } catch (IOException ex) {
            Logger.getLogger(SimpleSocketEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        online = false;
        
        for(ASocket socket : sockets) {
            socket.setActive(false);
        }
        
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(SimpleSocketEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        sockets.clear();
        System.out.println("Socket Engine has been stopped");
    }

    public void addSocketListener(SocketListener listener) {
        socketListeners.add(listener);
    }

    public void fireSocketConnected(ASocket socket) {
        for (SocketListener listener : socketListeners) {
            listener.socketConnected(socket);
        }
    }

    public void fireSocketDisconnected(ASocket socket) {
        for (SocketListener listener : socketListeners) {
            listener.socketDisconnected(socket);
        }
    }

    public void fireSocketResponded(ASocket socket, String responce) {
        for (SocketListener listener : socketListeners) {
            listener.socketResponded(socket, responce);
        }
    }

    @Override
    public void run() {
        while (serverSocket != null && online) {
            try {
                final ASocket socket = new ASocket(serverSocket.accept());
                if (online) {
                    sockets.add(socket);
                    socket.addSocketListener(new SocketAdapter() {
                        @Override
                        public void socketResponded(ASocket socket, String responce) {
                            fireSocketResponded(socket, responce);
                        }

                        @Override
                        public void socketDisconnected(ASocket socket) {
                            fireSocketDisconnected(socket);
                        }
                    });
                    socket.setActive(true);
                    fireSocketConnected(socket);
                }

            } catch (IOException ex) {
                System.out.println("ex");
            }
        }
    }

    public static void main(String args[]) {
        SimpleSocketEngine socketEngine = new SimpleSocketEngine(12345);
        socketEngine.start();
    }
}
