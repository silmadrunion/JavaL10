import java.io.IOException;
import java.net.ServerSocket;

public class GameServer {

    private ServerSocket sock = null; //required ServerSocket

    private ServerSocket createSocket()
    {
        try {
            sock = new ServerSocket(); //basic try catch autorefractor; will probably change later
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sock; //considered making this method create if sock == null and return; will look again
    }


    public static void main(String[] args)
    {   //empty dummy main
    }
}
