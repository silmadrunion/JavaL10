import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientThread implements Runnable{

    private Socket clientSock = null;

    public ClientThread(Socket clientSocket)
    {
        this.clientSock = clientSocket;
    } //Gets the socket from server on creation

    public void run() {
        String connectedGame=null; //the connected game is made "global" so that in case of socket exception, it can be read and disconnected
        boolean isBlack = true; //Client-game interaction variables; also, same as above why it's made "global"
        try
        {
            InputStream input = clientSock.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input)); //Setup the input stream

            OutputStream output = clientSock.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true); //Setup the output stream


            //connectedGame is the name (which also serves as key in the server hashmap) of the game to which the client is current connected, if any
            //isBlack denotes the player's colour; true for black, false for white, obviously

            String text=""; //variable in which to gather client communication

            while (true) {  //Everything is put in the same while loop; this is "caveman style coding", but I had no time to implement anything cleaner
                text = reader.readLine();

                if(text.equals("Create game"))  //Game creation command
                {
                        int iLen, jLen, wLen;
                        String gameName; //variables necessary for the Game() constructor

                        writer.println("Please enter game name");
                        text=reader.readLine();
                        if(GameServer.games.containsKey(text)) {writer.println("Error, game with that name already exists"); continue;} //Verification in order to avoid erros being thrown
                        gameName = text;

                        writer.println("Please enter board height");
                        text=reader.readLine();
                        if(!text.matches("\\d+")) {writer.println("Error, not a positive integer"); continue;} //Verification that the string is digits-only using a regex
                        iLen = Integer.parseInt(text);

                        writer.println("Please enter board width");
                        text=reader.readLine();
                        if(!text.matches("\\d+")) {writer.println("Error, not a positive integer"); continue;}//Verification that the string is digits-only using a regex
                        jLen = Integer.parseInt(text);

                        writer.println("Please enter sequence length needed to win");
                        text = reader.readLine();
                        if(!text.matches("\\d+")) {writer.println("Error, not a positive integer"); continue;}//Verification that the string is digits-only using a regex
                        wLen = Integer.parseInt(text);

                        writer.println("Do you want to play as black? Y/N");
                        text=reader.readLine();
                        if(text.equals("Y")) isBlack = true; //first time isBlack might be set
                        else if(text.equals("N")) isBlack = false;
                        else {writer.println("Error, answer is not Y or N"); continue;}

                        GameServer.games.put(gameName, new Game(iLen, jLen, wLen)); //calling the Game() constructor with the gathered parameters
                        connectedGame = gameName;
                        GameServer.games.get(gameName).connectPlayer(isBlack); //automatically connect to a newly created game
                        writer.println("Successfully created and connected to game!");
                        continue;
                }

                if(text.equals("Join game")) //game connection command
                {
                    if(connectedGame!=null) {writer.println("Error! Already connected to a game!"); continue;}

                    writer.println("Please enter a game name");
                    text=reader.readLine();
                    if(!GameServer.games.containsKey(text)) {writer.println("Error, game with that name does not exist"); continue;} //Verification similar to the one in creation
                    if(GameServer.games.get(text).getPlayersConnected()>=2) {writer.println("Error, game with that name already has maximum players!"); continue;} //Only 2 clients may be connected to the same game at a time

                    connectedGame = text;
                    if(GameServer.games.get(connectedGame).isBlackConnected()) isBlack=false; //Connect to game with the color black if available, white otherwise
                    else isBlack=true;
                    GameServer.games.get(connectedGame).connectPlayer(isBlack);
                    writer.println("Successfully connected to game!");
                    continue;
                }

                if(text.equals("Disconnect")) //Disconnect from game
                {
                    if(connectedGame==null) {writer.println("Error! Not connected to a game!"); continue;} //Only works if connected to a game, obviously

                    GameServer.games.get(connectedGame).disconnectPlayer(isBlack);
                    connectedGame=null;
                    writer.println("Successfully disconnected from game!");
                    continue;
                }

                if(text.equals("Delete game")) //Delete an empty game; this may be done by anyone, not just the creator
                {
                    writer.println("Please enter game name");
                    text=reader.readLine();
                    if(!GameServer.games.containsKey(text)) {writer.println("Error, game with that name does not exist"); continue;}

                    if(GameServer.games.get(text).getPlayersConnected()>0) {writer.println("Error! Game still has players connected!"); continue;} //Only works if no players are currently connected to the game

                    GameServer.games.remove(text);
                    writer.println("Successfully deleted game!");
                    continue;
                }

                if(text.equals("Print board")) //Function to print the current game state
                {
                    if(connectedGame==null) {writer.println("Error! Not connected to a game!"); continue;}

                    writer.println("BOARD PRINTING"); //Since a long sequence of lines is about to be printed, client is informed to wait for it to end before continuing
                    ArrayList<String> gamestate = GameServer.games.get(connectedGame).getBoardState();
                    for (String k: gamestate
                         ) {
                        writer.println(k);
                    }
                    writer.println("DONE PRINTING");
                    continue;
                }

                if(text.equals("Make move")) //Move command
                {
                    if(connectedGame==null) {writer.println("Error! Not connected to a game!"); continue;} //Move happens in the game to which the client is connected

                    if(GameServer.games.get(connectedGame).isBlacksTurn() != isBlack) {writer.println("Error! Not your turn!"); continue;} //You can only move on your turn

                    writer.println("Please enter move coordinates");
                    text=reader.readLine();
                    StringTokenizer tokenizer = new StringTokenizer(text, " "); //This is where I realized Java does have a Strtok variant. It would have been useful on earlier commands like Create game, but too late now

                    int x, y;
                    text = tokenizer.nextToken();
                    if(!text.matches("\\d+")) {writer.println("Error, first coordinate is not a positive integer"); continue;} //Regex checks
                    x=Integer.parseInt(text);

                    text = tokenizer.nextToken();
                    if(!text.matches("\\d+")) {writer.println("Error, second coordinate is not a positive integer"); continue;} //Regex checks
                    y=Integer.parseInt(text);

                    int moveCode = GameServer.games.get(connectedGame).move(x,y,isBlack); //Makes the move, or gets the reason why a move could not be made
                    if(moveCode==-1) {writer.println("Error, place already occupied!"); continue;}
                    if(moveCode==-2) {writer.println("Error, coordinates out of bounds!"); continue;}
                    if(moveCode==-4) {writer.println("Error, game is already over!"); continue;}
                    if(moveCode==1){writer.println("You have won!"); continue;} //Also checks for victory; players are NOT disconnected on game end, so that they can still look at the table
                    if(moveCode==0){writer.println("Move submitted!"); continue;}

                }

                if(text.equals("stop")) //misleading, as clients can not actually stop the server, but rather simply their own thread in it
                {
                    writer.println("Server stopped");
                    break;
                }

                writer.println("Server recevied unrecognized command: \"" + text+"\""); //Anything else goes here
            }


            clientSock.close();
            input.close();
            output.close();
        } catch(IOException e)
        {
            if(connectedGame!=null) GameServer.games.get(connectedGame).disconnectPlayer(isBlack);
            e.printStackTrace();
        }
    }

}
