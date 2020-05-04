import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private ArrayList<ArrayList<Integer>> board = new ArrayList<>(); //Array of arrays to ensure dynamic board length

    public Board(int iLength, int jLength)
    {
        for(int i=0;i<iLength;i++)
        {
            ArrayList<Integer> row = new ArrayList<>();
            for(int j=0;j<jLength;j++)
                row.add(0);
            board.add(row); //this probably could have been done with a List instead of ArrayList, but I took no chances
        }
    }

    public int place(boolean isBlack, int iPos, int jPos)
    {
        if((iPos > board.size()) || (jPos > board.get(0).size())) return -2; //out of bounds error
        if(board.get(iPos).get(jPos)==0) //0 for empty
        {
            if(isBlack) board.get(iPos).set(jPos, 1); //1 for black
            else board.get(iPos).set(jPos, -1); //-1 for white

            return 0; //
        }
        else return -1; //already occupied error
    }

    public int get(int i, int j)
    {
        if((i>board.size()) || (j>board.get(0).size())) return 0; //if out of bounds, behave as if neutral
        return board.get(i).get(j);
    }

    public ArrayList<String> getBoardState() //prints a list of strings with the boardstate
    {
        ArrayList<String> result = new ArrayList<>();
        for(int i=0;i<board.size();i++)
        {
            StringBuilder line = new StringBuilder("");
            for(int j=0;j<board.get(0).size();j++)
            {
                if(board.get(i).get(j)==0) line.append("."); //neutral
                else if(board.get(i).get(j)==1) line.append("X"); //black stone
                else if(board.get(i).get(j)==-1) line.append("O"); //white stone
            }
            result.add(line.toString());
        }
        return result;
    }
}
