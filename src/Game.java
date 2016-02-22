import java.awt.Color;

/**
 * Game engine for AI Project 2
 * It keeps track of the positions of pieces, and provides functions for game playing.
 */

public class Game {
	// Constants
	static final int NUMPLAYERS = 3; // Number of players (parts of the game hardcoded for 3 at the moment)
	static final int PIECES = 4; // Number of pieces per player
	static final int BOARDSIZE = 13; // Size of longest row (should be odd)
	static final int HALFSIZE = BOARDSIZE/2; // Row midpoint (for odd boards)
	static final int MAXMOVEDISTANCE = 2; // Maximum number of tiles a piece can move
	static final int TIME = 1000; // Allotted move time in milliseconds
	static final int MAXTURNS = 100; // Number of moves before the game ends in a draw

	/** The board is represented as a 2D array of integers.
	 * A value of -1 means a blank space. 0, 1, and 2 mean red, green, and blue.
	 * A value of -2 means a wall that cannot be moved onto (since the board is
	 * a hexagon rather than a rectangle).
	 * board[i][j] can be thought of as diagonal row i and column j in the slides
	 */
	public int[][] board;
	public Player[] players;
	public ThreePlayerHexGame newDrawing;

	public boolean verbose = true;

	// why the hell can you not init arrays like this in the constructor :/ LOL
	public int[] redStart = {1,0,4,0,8,12,11,12};
	public int[] greenStart = {0,2,0,5,12,10,12,7};
	public int[] blueStart = {2,8,5,11,7,1,10,4};

	/** Initialize a fresh game */
	public Game(Player[] np) {
		newDrawing = new ThreePlayerHexGame();

		board = new int[BOARDSIZE][BOARDSIZE];
		for(int i=0;i<BOARDSIZE;i++) {
			for(int j=0;j<board[i].length;j++) {
				if(Math.abs(i-j) > HALFSIZE)
					board[i][j] = -2;
				else
					board[i][j] = -1;
			}
		}

		for(int i=0;i<8;i+=2) {
			board[redStart[i]][redStart[i+1]] = 0;
			board[greenStart[i]][greenStart[i+1]] = 1;
			board[blueStart[i]][blueStart[i+1]] = 2;
		}

		// Players
		players = np;
	}

	/** Actually make a single move. Return true iff a valid move was returned.
	 * Print out the move if verbose mode is on. */
	public boolean makeMove(int pID) {
		int[] m = players[pID].move(getPieces(0), getPieces(1), getPieces(2), pID, TIME);
		if (validMove(m, pID)) {
			board[m[0]][m[1]] = -1;
			board[m[2]][m[3]] = pID;

			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.printBoard();

			return true;
		} else return false;
	}


	public boolean win(int pID) {
		// If a piece is in one of the starting positions, you don't win.
		if(pID == 0) {
			for(int i=0;i<8;i+=2) {
				if(board[redStart[i]][redStart[i+1]] == pID)
					return false;
			}
		}
		else if(pID == 1) {
			for(int i=0;i<8;i+=2) {
				if(board[greenStart[i]][greenStart[i+1]] == pID)
					return false;
			}
		}
		else {
			for(int i=0;i<8;i+=2) {
				if(board[blueStart[i]][blueStart[i+1]] == pID)
					return false;
			}
		}

		boolean keepgoing=true;

		// Check the rows
		for(int i=0; i<BOARDSIZE; i++) {
			int count = 0;
			for(int j=0; j<BOARDSIZE; j++) {
				if(board[i][j] == pID)
					count++;
				// If another color piece is encountered in the middle, we can't win this way.
				else if(board[i][j] != -1 && count > 0) {
					keepgoing = false;
					break;
				}
			}
			if(count == PIECES)
				return true;
			else if(!keepgoing || count > 0)
				break;
		}

		keepgoing = true;

		// Check the columns
		for(int j=0; j<BOARDSIZE; j++) {
			int count = 0;
			for(int i=0; i<BOARDSIZE; i++) {
				if(board[i][j] == pID)
					count++;
				// If another color piece is encountered in the middle, we can't win this way
				else if(board[i][j] != -1 && count > 0) {
					keepgoing = false;
					break;
				}
			}
			if(count == PIECES)
				return true;
			else if(!keepgoing || count > 0)
				break;
		}

		keepgoing = true;

		// Check some of the other diagonals
		for(int k=0; k<=HALFSIZE; k++) {
			int count = 0;
			for(int j=0;j<BOARDSIZE-k; j++) {
				if(board[j+k][j] == pID)
					count++;
				// If another color piece is encountered in the middle, we can't win this way.
				else if(board[j+k][j] != -1 && count > 0) {
					keepgoing = false;
					break;
				}
			}
			if(count == PIECES)
				return true;
			else if(!keepgoing || count > 0)
				break;
		}

		// Check the rest of the other diagonals
		for(int k=1; k<=HALFSIZE; k++) {
			int count = 0;
			for(int i=0;i<BOARDSIZE-k; i++) {
				if(board[i][i+k] == pID)
					count++;
				// If another color piece is encountered in the middle, we can't win this way.
				else if(board[i][i+k] != -1 && count > 0) {
					keepgoing = false;
					break;
				}
			}
			if(count == PIECES)
				return true;
			else if(!keepgoing || count > 0)
				break;
		}
		return false;
	}

	/** Return the player ID if one person has won, -1 if nobody has won.
	 *  -2 if there is a draw. */
	public int checkWin() {
		int ret = -1;
		for(int i=0; i<NUMPLAYERS; i++) {
			if(win(i)) {
				if(ret != -1)
					ret = -2;
				else
					ret = i;
			}
		}
		return ret;
	}

	/** Return true if a move is valid. Requires:
	 *  - Move is a valid format (array of 4 integers)
	 *  - Starting location is on the board and has one of pID's pieces
	 *  - Target location is empty (and on the board)
	 *  - Moves between 1 and MAXMOVEDISTANCE tiles in a single direction
	 *  - No pieces blocking the movement
	 *  - Start and target are different (automatic because of empty requirement)*/
	public boolean validMove(int[] m, int pID) {
		int xdist = m[2] - m[0];
		int ydist = m[3] - m[1];
		int xabs = Math.abs(xdist);
		int yabs = Math.abs(ydist);

		if (m.length == 4 && m[0] >= 0 && m[1] >= 0 && m[2] >= 0 && m[3] >= 0
				&& m[0] < BOARDSIZE && m[1] < BOARDSIZE
				&& m[2] < BOARDSIZE && m[3] < BOARDSIZE
				&& board[m[2]][m[3]] == -1 && board[m[0]][m[1]] == pID
				&& xabs <= MAXMOVEDISTANCE && yabs <= MAXMOVEDISTANCE) {

			if (xdist == 0) {
				int s = Integer.signum(ydist);  // Get sign to determine direction
				for(int j=s; j!=ydist; j+=s)
					if(board[m[0]][m[1] + j] != -1) // If something is in the way
						return false;
				return true;
			}
			else if(ydist == 0) {
				int s = Integer.signum(xdist);  // Get sign to determine direction
				for(int i=s; i!=xdist; i+=s)
					if(board[m[0] + i][m[1]] != -1) // If something is in the way
						return false;
				return true;
			}
			else if(xabs == yabs) {
				int xs = Integer.signum(xdist);  // Get sign to determine direction
				int ys = Integer.signum(ydist);  // Get sign to determine direction
				for(int k=1; k < xabs; k++)
					if(board[m[0] + xs*k][m[1] + ys*k] != -1) // If something is in the way
						return false;
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}



	/** Get the positions of a specified player's pieces.
	 * Positions are returned in an array of consecutive pairs. */
	public int[] getPieces(int pID) {
		int[] ret = new int[PIECES*2];
		int ind = 0;
		for(int i=0; i<board.length; i++) {
			for(int j=0; j<board[i].length;j++) {
				if(board[i][j] == pID) {
					ret[ind] = i; 
					ret[ind+1] = j;
					ind+=2;
				}
			}
		}
		return ret;
	}

	/** Play a game, return the ID of the winner at the end.
	 * Returns -1 if nobody has won, -2 if multiple people have won, -3 if an error
	 * has occurred while attempting to make a move. */
	public int play() {
		int winner = -1;
		for(int turn=0; turn<MAXTURNS; turn++) {
			if(verbose)
				System.out.println("It is now turn " + turn);
			for(int i=0; i<NUMPLAYERS; i++) {
				if(makeMove(i)) {
					winner = checkWin();
				}
				else {
					winner = -3;
				}
				if(winner != -1)
					break;
			}
			if(winner != -1)
				break;
		}
		return winner;
	}


	/** Interface of all players.
	 *  Your player class also needs to implement this interface. */
	interface Player {
		/**
		 * @param red - the position of red's pieces (ID 0)
		 * @param green - the position of green's pieces (ID 1)
		 * @param blue - the position of red's pieces (ID 2)
		 * @param player - your player ID (0, 1, or 2)
		 * @param time - time limit in ms
		 * @return a chosen move as an array of 4 integers
		 */
		int[] move(int[] red, int[] green, int[] blue, int player, int time);
	}


	/** Basic function to display the board as text */
	public void printBoard() {
		newDrawing.clear();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 0) {
					newDrawing.drawLocation(i, j, Color.RED);
				} else if (board[i][j] == 1) {
					newDrawing.drawLocation(i, j, Color.GREEN);
				} else if (board[i][j] == 2) {
					newDrawing.drawLocation(i, j, Color.BLUE);
				}
			}
		}
	}

	/** Helper function for printBoard, returns the character to print for
	 * a given space on the board. */
	public char spaceToChar(int s) {
		switch(s) {
		case 0:
			return 'r';
		case 1:
			return 'g';
		case 2:
			return 'b';
		default:
			return ' ';
		}
	}
}









