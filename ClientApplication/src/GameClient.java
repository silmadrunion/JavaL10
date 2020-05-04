import java.io.*;
import java.net.Socket;

public class GameClient { //considered making this a singleton, since each client should be unique in its own instance, but never got around to it

    private static Socket sock = null;

    public static void main(String[] args)
    {
        try {
            sock = new Socket("127.0.0.1", 8000); //localhost connection; this probably works on remote servers, but I didn't try
        } catch (IOException e) {
            throw new RuntimeException("Cannot create socket", e);
        }


        try {

            OutputStream output = sock.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true); //setting up the output stream

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); //setting up the console input stream

            InputStream input = sock.getInputStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(input)); //setting up the input stream

            String text; //variable to handle input/output

            do {
                text = consoleReader.readLine(); //read from console...

                writer.println(text); //...send over to server ...

                text = inputReader.readLine(); //...and see what it replied...

                if(text.equals("BOARD PRINTING")) //if BOARD PRINTING was recevied, it means multiple lines follow so they must be all read before client can continue
                {
                    while(!text.equals("DONE PRINTING"))
                    {
                        text=inputReader.readLine();
                        System.out.println(text);
                    }
                    continue;
                }

                System.out.println(text);//...then print it

            } while (!text.equals("stop")); //and repeat until stop command is sent


            sock.close();
        } catch (IOException e) {
            throw new RuntimeException("I/O Error", e);
        }
    }
    //will need a function to act as main
}
