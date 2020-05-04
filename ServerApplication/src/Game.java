import java.util.ArrayList;

public class Game {
    private Board gameboard; //board for the game to be played on, keeps track of the pieces
    private boolean isFinished; //if set true, no new moves will be allowed
    private int iLen, jLen, winLen; //game parameters, as it is not limited to classic 15x15 with lines of 5
    private boolean blacksTurn, blackConnected, whiteConnected; //connected players and whose turn it is
    private int playersConnected; //number of connections

    public Game(int iLength, int jLength, int winningLength) //constructor gets parameters for game specs
    {
        iLen = iLength;
        jLen = jLength;
        winLen = winningLength;
        gameboard = new Board(iLen, jLen);
        blacksTurn = true; //like in Go, black begins
        playersConnected = 0; //initially, no players are connected; the creator player will be later connected through the dedicate function
        isFinished = false;
    }

    private int lineCounter(int iStart, int jStart, int iMod, int jMod, int color) //this function counts all matching stones in a straight pattern as denoted by the mods
    {
        int currentLen = 0;
        int i=iStart, j=jStart;
        while(gameboard.get(i,j)==color)
        {
            currentLen++;
            i+=iMod;
            j+=jMod; //never checks for board edge because it is considered neutral, and will break the chain regardless
        }

        return currentLen-1;
    }

    private boolean checkVictory(int iPos, int jPos) //note: this could have probably been more elegant, but this form does the job and is very clear to understand,
    {
        int maxLenFound = 0;
        int currentLen;
        int color = gameboard.get(iPos, jPos);

        currentLen=1;
        currentLen+=lineCounter(iPos,jPos,-1,0, color); //upwards
        currentLen+=lineCounter(iPos,jPos,+1,0, color); //downwards
        if(currentLen>maxLenFound) maxLenFound = currentLen;

        currentLen=1;
        currentLen+=lineCounter(iPos,jPos,0,-1, color); //leftwards
        currentLen+=lineCounter(iPos,jPos,0,+1, color); //rightwards
        if(currentLen>maxLenFound) maxLenFound = currentLen;

        currentLen=1;
        currentLen+=lineCounter(iPos,jPos,-1,-1, color); //upleftwards
        currentLen+=lineCounter(iPos,jPos,+1,+1, color); //downrightwards
        if(currentLen>maxLenFound) maxLenFound = currentLen;

        currentLen=1;
        currentLen+=lineCounter(iPos,jPos,-1,+1, color); //uprightwards
        currentLen+=lineCounter(iPos,jPos,+1,-1, color); //downleftwards
        if(currentLen>maxLenFound) maxLenFound = currentLen;

        if(maxLenFound >= winLen) return true;
        else return false;
    }

    public int move(int i, int j, boolean isBlack)
    {
        if(isFinished) return -4; //Game already over error

        if(blacksTurn != isBlack) return -3; //Wrong player error
        else
        {
            int errorCode = gameboard.place(isBlack, i, j); //Handles both errors and piece placement
            if(errorCode!=0) return errorCode;
            if(checkVictory(i,j)==true) { isFinished=true; return 1;} //current player won

            blacksTurn = !blacksTurn;
        }
        return 0; //game continues
    }

    public boolean isBlacksTurn() //this is basically a getter with another name due to its usage
    {
        return blacksTurn;
    }

    public boolean isWhiteConnected() //this is basically a getter with another name due to its usage
    {
        return whiteConnected;
    }

    public boolean isBlackConnected() //this is basically a getter with another name due to its usage
    {
        return blackConnected;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }

    public int connectPlayer(boolean isBlack)
    {

        if(isBlack)
            if(blackConnected) return -1; //error player already exists
            else blackConnected=true;
        else if(whiteConnected) return -1; //same error as above
            else whiteConnected=true;
        playersConnected++;
        return 0; //connection successful
    }

    public int disconnectPlayer(boolean isBlack)
    {

        if(isBlack)
            if(!blackConnected) return -1; //error player absent already
            else blackConnected=false;
        else if(!whiteConnected) return -1; //same error as above
        else whiteConnected=false;
        playersConnected--;
        return 0; //connection successful
    }

    public ArrayList<String> getBoardState()
    {
        return gameboard.getBoardState();
    }
}
