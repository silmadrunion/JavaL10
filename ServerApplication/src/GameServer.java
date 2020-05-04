import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameServer {

    public static HashMap<String, Game> games = new HashMap<>(); //map containing all games currently in play; in the main thread and public so that two clients can play the same game

    private static ServerSocket createSocket(int port)
    {

        ServerSocket socket = null;

        try {
            socket = new ServerSocket(port); //Server socket
        } catch (IOException e) {
            throw new RuntimeException("Cannot create server socket", e);
        }

        return socket;
    }


    public static void main(String[] args)
    {

        int port = 8000; //port is hardcoded, but put in a variable so it would be easy to change
        ServerSocket sock = createSocket(port);

        while(true)
        {
            Socket clientSock = null; //Setting up the client socket for accepting
            try
            {
                clientSock = sock.accept(); //Listening
            } catch(IOException e)
            {
                throw new RuntimeException("Cannot accept client", e);
            }

            new Thread(new ClientThread(clientSock)).start(); //When accepted new client, pass him over to a thread and go back to listening
            System.out.println("Accepted new client!");
        }

    }
}
