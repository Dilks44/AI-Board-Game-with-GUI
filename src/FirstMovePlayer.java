/**
 * This is a player that picks the very first move it sees.
 * In most cases, it won't be able to win, or even to put up very much of a defense,
 * so if your program can't beat it then it probably can't beat much of anything.
 * (This type of player is known as a "goldfish").
 */
public class FirstMovePlayer implements Game.Player
{
    static final int MAXMOVEDISTANCE = 2;
    static final int BOARDSIZE = 13;

    // If the difference in coordinates is bigger than this, the space isn't valid.
    static final int HALFSIZE = BOARDSIZE/2;

    /** Default constructor. You need to have one of these, although you may also
      * want a more complicated constructor with adjustable parameters for testing. */
    public FirstMovePlayer()
    {
    }

    /** Helper method to check if a space is empty (and actually on the board) */
    public boolean spaceEmpty(int x, int y, int[] red, int[] green, int[] blue)
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

    /** Return the first move found */
    public int[] move(int[] red, int[] green, int[] blue, int player, int time)
    {
      int[] mypieces;
      if(player == 0)
        mypieces = red;
      else if(player == 1)
        mypieces = green;
      else
        mypieces = blue;

      int[] curmove = new int[4]; 
      // For each piece...
      for(int i=0; i<8; i+=2)
      {
        curmove[0] = mypieces[i];
        curmove[1] = mypieces[i+1];

        // For each distance...
        for(int k=1; k<=MAXMOVEDISTANCE; k++)
        {
          curmove[2] = curmove[0] + k;
          curmove[3] = curmove[1];
          if(spaceEmpty(curmove[2],curmove[3],red,green,blue))
            return curmove;

          curmove[2] = curmove[0] - k;
          curmove[3] = curmove[1];
          if(spaceEmpty(curmove[2],curmove[3],red,green,blue))
            return curmove;

          curmove[2] = curmove[0];
          curmove[3] = curmove[1] + k;
          if(spaceEmpty(curmove[2],curmove[3],red,green,blue))
            return curmove;

          curmove[2] = curmove[0];
          curmove[3] = curmove[1] - k;
          if(spaceEmpty(curmove[2],curmove[3],red,green,blue))
            return curmove;

          curmove[2] = curmove[0] + k;
          curmove[3] = curmove[1] + k;
          if(spaceEmpty(curmove[2],curmove[3],red,green,blue))
            return curmove;

          curmove[2] = curmove[0] - k;
          curmove[3] = curmove[1] - k;
          if(spaceEmpty(curmove[2],curmove[3],red,green,blue))
            return curmove;
        }
      }
      return new int[]{0,0,0,0}; // default (should never get here)
    }
}


