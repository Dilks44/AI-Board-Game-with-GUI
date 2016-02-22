/**
 * This is a player that looks one move ahead, applies a heuristic to all possible
 * moves, then picks the one that gives it the best score.
 * This is the same as OnePlyPlayer, but with a different heuristic
 * The heuristic heavily prioritizes having your pieces in a target row:
 * +10 points for having 4 unblocked pieces in a row (winning is the best)
 * -5 points for enemy having 4 unblocked pieces in a row
 *   (enemy winning is ok if you do as well)
 * -3 points for having a piece in a starting location
 * -1 point for visiting a state you've already visited
 * -1 point for each unit a piece is away from the target row (default row 6)
 */

import java.util.Hashtable;
public class OneLinePlayer implements Game.Player
{
    static final int MAXMOVEDISTANCE = 2;
    static final int BOARDSIZE = 13;

    // Heuristic weights
    static final double STARTPIECEVAL = -3; // value for a piece being in a start location
    static final double WINVAL = 10; // value for winning the game
    static final double ENEMYWINVAL = -5; // value for enemy winning the game
    static final double REVISITVAL = -1; // value for visiting a configuration again
    static final double NOTTARGETROWVAL = -0.5;

    // If the difference in coordinates is bigger than this, the space isn't valid.
    static final int HALFSIZE = BOARDSIZE/2;

    // Starting locations
    public int[] redStart = {1,0,4,0,8,12,11,12};
    public int[] greenStart = {0,2,0,5,12,10,12,7};
    public int[] blueStart = {2,8,5,11,7,1,10,4};

    // Store the previous arrangements of our pieces to encourage exploration
    public Hashtable<String, Integer> history;

    // The row we will be aggressively shooting for
    public int targetrow = 6;

    /** Default constructor.*/
    public OneLinePlayer()
    {
      history = new Hashtable<String, Integer>();
    }

    /** Helper method to count pieces in starting locations. */
    public int piecesInStartingLocations(int pID, int[] pieces)
    {
      int count=0;
      int[] start;

      if(pID == 0)
      { 
        start = redStart;
      }
      else if(pID == 1)
      { 
        start = greenStart;
      }
      else
      { 
        start = blueStart;
      }

      for(int i=0; i<8; i+=2)
      {
        for(int j=0; j<8; j+=2)
        {
          if(start[i] == pieces[j] && start[i+1] == pieces[j+1])
            count++;
        }
      }
      return count;
    }

    /** Helper method to check if a space is empty (and actually on the board) */
    public static boolean spaceEmpty(int x, int y, int[] red, int[] green, int[] blue)
    {
      if(x<0 || y<0 || x>=BOARDSIZE || y>=BOARDSIZE || Math.abs(x-y) > HALFSIZE)
        return false;
      for(int i=0; i<8; i+=2)
        if((red[i] == x && red[i+1] == y) ||
           (green[i] == x && green[i+1] == y) ||
           (blue[i] == x && blue[i+1] == y))
        {
          return false;
        }
      return true;
    }

    /** Helper method to check if one number a is between two others b and c. */
    public static boolean between(int a, int b, int c)
    {
      return a>=b && a<=c;
    }

    /** Check to see if the player has won, returns appropriate point value.
      * Win conditions:
      * (All x coordinates of pieces are the same OR
      * All y coordinates of pieces are the same OR
      * All (x-y) values of pieces are the same) AND
      * No enemy pieces are blocking. AND
      * No pieces are in starting location (handled elsewhere) */
    public boolean checkWin(int pID, int[] red, int[] green, int[] blue)
    {
      int[][] pieces = new int[3][];
      if(pID == 0)
      {
        pieces[0] = red;
        pieces[1] = green;
        pieces[2] = blue;
      }
      else if(pID == 1)
      {
        pieces[0] = green;
        pieces[1] = red;
        pieces[2] = blue;
      }
      else
      {
        pieces[0] = blue;
        pieces[1] = green;
        pieces[2] = red;
      }

      // Check all the ways to win simultaneously, one variable per direction.
      boolean dir1 = true;
      boolean dir2 = true;
      boolean dir3 = true;

      int s1 = pieces[0][0];
      int s2 = pieces[0][1];
      int s3 = s1-s2;

      // See if we have 4 in a line
      for(int k=2; k<8; k+=2)
      {
        dir1 = dir1 && pieces[0][k] == s1;
        dir2 = dir2 && pieces[0][k+1] == s2;
        dir3 = dir3 && pieces[0][k]-pieces[0][k+1] == s3;
      }

      // Make sure there are no blocks
      if(dir1)
      {
        int min = s2;
        int max = s2;

        // Find the endpoints of the line
        for(int k=3; k<8; k+=2)
        {
          if(min > pieces[0][k])
            min = pieces[0][k];
          else if(max < pieces[0][k])
            max = pieces[0][k];
        }

        // Make sure no other colors have a point in between those endpoints
        for(int k=0; k<8; k+=2)
          if((pieces[1][k] == s1 && between(pieces[1][k+1], min, max)) ||
             (pieces[2][k] == s1 && between(pieces[2][k+1], min, max)))
          {
            return false;
          }
 
        // We won if we made it this far!
        return true;
      }
      else if(dir2)
      {
        int min = s1;
        int max = s1;

        // Find the endpoints of the line
        for(int k=2; k<8; k+=2)
        {
          if(min > pieces[0][k])
            min = pieces[0][k];
          else if(max < pieces[0][k])
            max = pieces[0][k];
        }

        // Make sure no other colors have a point in between those endpoints
        for(int k=0; k<8; k+=2)
          if((pieces[1][k+1] == s2 && between(pieces[1][k], min, max)) ||
             (pieces[2][k+1] == s2 && between(pieces[2][k], min, max)))
          {
            return false;
          }
 
        // We won if we made it this far!
        return true;
      }
      else if(dir3)
      {
        // Doesn't matter if we use s1 or s2, just need to be consistent
        int min = s1;
        int max = s1;

        // Find the endpoints of the line
        for(int k=2; k<8; k+=2)
        {
          if(min > pieces[0][k])
            min = pieces[0][k];
          else if(max < pieces[0][k])
            max = pieces[0][k];
        }

        // Make sure no other colors have a point in between those endpoints
        for(int k=0; k<8; k+=2)
          if((pieces[1][k] - pieces[1][k+1] == s3 && between(pieces[1][k], min, max)) ||
             (pieces[2][k] - pieces[2][k+1] == s3 && between(pieces[2][k], min, max)))
          {
            return false;
          }
 
        // We won if we made it this far!
        return true;
      }

      // Else, no line (blocked or otherwise) in any of the 3 directions.
      return false;
    }

    /** Very simple heuristic.
      * -3 points per piece in a starting location
      * +10 points for 4 in a row.
      * (Like all heuristics, we take in a description of a state, and output a number)*/
    public double heuristic(int[] m, int pID, int[] red, int[] green, int[] blue)
    {
      double val = 0;

      int[] orig; // Positions of your color's pieces before the move
      int[] pieces = new int[8]; // Same as orig, but after making move m

      if(pID == 0)
      {
        orig = red;
      }
      else if(pID == 1)
      {
        orig = green;
      }
      else
      {
        orig = blue;
      }

      for(int i=0; i<8; i+=2)
      {
        if(orig[i] == m[0] && orig[i+1] == m[1])
        {
          pieces[i] = m[2];
          pieces[i+1] = m[3];
        }
        else
        {
          pieces[i] = orig[i];
          pieces[i+1] = orig[i+1];
        }
      }

      // Check for wins
      if(pID == 0)
      {
        if(checkWin(0, pieces, green, blue))
          val+=WINVAL;
        if(checkWin(1, pieces, green, blue))
          val+=ENEMYWINVAL;
        if(checkWin(2, pieces, green, blue))
          val+=ENEMYWINVAL;
      }
      else if(pID == 1)
      {
        if(checkWin(0, red, pieces, blue))
          val+=ENEMYWINVAL;
        if(checkWin(1, red, pieces, blue))
          val+=WINVAL;
        if(checkWin(2, red, pieces, blue))
          val+=ENEMYWINVAL;
      }
      else
      {
        if(checkWin(0, red, green, pieces))
          val+=ENEMYWINVAL;
        if(checkWin(1, red, green, pieces))
          val+=ENEMYWINVAL;
        if(checkWin(2, red, green, pieces))
          val+=WINVAL;
      }

      // Check for pieces in starting locations
      val += STARTPIECEVAL*piecesInStartingLocations(pID,pieces);

      // Discount score for each time we've visited this arrangement of our pieces
      String ps = piecesToString(pieces);
      if(history.get(ps) != null)
        val += REVISITVAL * history.get(ps);

      // Penalty for each distance away from the target row!
      for(int i=0; i<8; i+=2)
        val += NOTTARGETROWVAL * Math.abs(pieces[i] - targetrow);

      return val;
    }
    

    /** Find all first-level moves, evaluate them with the heuristic, and
      * return the best one found. */
    public int[] move(int[] red, int[] green, int[] blue, int player, int time)
    {
      int[] mypieces;
      if(player == 0)
        mypieces = red;
      else if(player == 1)
        mypieces = green;
      else
        mypieces = blue;

      String ps = piecesToString(mypieces);
      int oldcount = 0;
      if(history.get(ps) != null)
        oldcount = history.get(ps);
      history.put(ps, oldcount + 1);

      int[] curmove = new int[4];
      int[][] curend = new int[6][];
      int[] bestmove = {0,0,0,0}; // Hopefully we never return this
      double bestval = -100; // Arbitrary small starting value

      boolean[] blocked = new boolean[6]; // Stop moving in a direction if blocked

      // For each piece...
      for(int i=0; i<8; i+=2)
      {
        curmove[0] = mypieces[i];
        curmove[1] = mypieces[i+1];
        blocked = new boolean[6];

        // For each distance...
        for(int k=1; k<=MAXMOVEDISTANCE; k++)
        {
          curend[0] = new int[]{curmove[0] + k, curmove[1]};
          curend[1] = new int[]{curmove[0] - k, curmove[1]};
          curend[2] = new int[]{curmove[0], curmove[1] + k};
          curend[3] = new int[]{curmove[0], curmove[1] - k};
          curend[4] = new int[]{curmove[0] + k, curmove[1] + k};
          curend[5] = new int[]{curmove[0] - k, curmove[1] - k};
          // For each move...
          for(int m=0; m < 6; m++)
          {
            curmove[2] = curend[m][0];
            curmove[3] = curend[m][1];
            if(!blocked[m] && spaceEmpty(curmove[2],curmove[3],red,green,blue))
            {
              double val = heuristic(curmove,player,red,green,blue);
              if(val > bestval)
              {
                for(int j=0;j<bestmove.length;j++)
                  bestmove[j] = curmove[j];
                bestval = val;
              }
            }
            else
            {
              blocked[m] = true; // Indicate that something stopped us from moving
            }
          }
        }
      }
      return bestmove;
    }

    /** Helper method for our history hash table to turn a list of a player's pieces 
      * into a string (to act as a hash key). */
    public String piecesToString(int[] p)
    {
      String ret = "";
      for(int i:p)
        ret += i + " ";
      return ret;
    }
}


