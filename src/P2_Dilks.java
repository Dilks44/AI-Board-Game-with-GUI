import java.util.ArrayList;


/** READ ME**/
// These methods are in a somewhat logical order. The important methods are at the
//  top of this file and as you go down this class you get to less important (helper) methods
// I highly suggest collapsing the methods to make scrolling through this class easier

public class P2_Dilks implements Game.Player {

	private final static int[] startposRed = {1,0,4,0,8,12,11,12};
	private final static int[] startposGreen = {0,2,0,5,12,10,12,7};
	private final static int[] startposBlue = {2,8,5,11,7,1,10,4};

	// This helps speed up calculating the locations in each row
	//  it's a 2d arrayList where the first Dimention is the row number and the
	//   second is all locations in that row
	private static ArrayList<ArrayList<int[]>> globUp = new ArrayList<ArrayList<int[]>>();
	private static ArrayList<ArrayList<int[]>> globLeft = new ArrayList<ArrayList<int[]>>();

	// init globals by giving them all 13 rows
	public P2_Dilks() {
		for (int i = 0; i < 13; i++) {
			ArrayList<int[]> temp = new ArrayList<int[]>();
			globUp.add(temp);
			ArrayList<int[]> temp2 = new ArrayList<int[]>();
			globLeft.add(temp2);
		}
	}


	/** This is the only method called by Game.java **/
	/** Strategies that I used to calculate the best move to make include **/
	// itterative deepening
	// game tree search
	// weighting based on most efficient win
	// In this class I itterate over every possible possition every piece can
	//  make and then calculate the evaluation for each new move
	//  This is slow but it works very well
	public int[] move(int[] red, int[] green, int[] blue, int player, int time) {
		int[] originalPlayerPieces;
		if (player == 0) {
			originalPlayerPieces = red;
		}
		else if (player == 1) {
			originalPlayerPieces = green;
		}
		else {
			originalPlayerPieces = blue;
		}
		ArrayList<int[]> allPlayers = new ArrayList<int[]>(3);
		allPlayers.add(red);
		allPlayers.add(green);
		allPlayers.add(blue);

		treeNode newTree = new treeNode(allPlayers);

		int[] returnArray = {-1,-1,-1,-1};

		// returnBoard is the solution
		ArrayList<int[]> returnBoard = itterativeDeep(2, player, allPlayers, newTree);


		// Now that I have the best solution, I check to see which piece I moved
		int[] playerPieces = returnBoard.get(player);

		for (int i = 0; i < 8; i +=2) {
			// if this is the piece that moved write it in the return array
			if (!(originalPlayerPieces[i] == playerPieces[i] && originalPlayerPieces[i+1] == playerPieces[i+1])) {
				returnArray[0] = originalPlayerPieces[i];
				returnArray[1] = originalPlayerPieces[i+1];
				returnArray[2] = playerPieces[i];
				returnArray[3] = playerPieces[i+1];
				// break because only one piece can move at time
				break;
			}
		}
		return returnArray;
	}

	// ItterativeDeep returns an ArrayList of every players pieces along with "player" 's new move
	// I used itterative deepening because I can find the best move given a certain depth
	//  and if I have more available time I call this function again with depth +1
	private ArrayList<int[]> itterativeDeep(int depth, int player, ArrayList<int[]> allPlayers, treeNode t ) {
		int returnVal = Integer.MAX_VALUE;
		ArrayList<int[]> returnList = new ArrayList<int[]>(1);

		for (int i = 0; i < depth; i++) {
			ArrayList<int[]> finaltree = gameTreeSearch(player, allPlayers, i, t);
			int bestMoveVal = eval(player, finaltree);

			// if this move is the best AND a piece actually moved
			if (bestMoveVal <= returnVal && compareBoard(finaltree.get(player),allPlayers.get(player)) != 0) {
				returnVal = bestMoveVal;
				returnList = finaltree;
			}
		}
		return returnList;
	}

	// GameTreeSearch is a recursive function that is called by itterativeDeeping
	// Its 3 player search function that is recursively called until depth is 0
	// It tries to take into account other players moves in order to decide what it best for it
	private ArrayList<int[]> gameTreeSearch(int player, ArrayList<int[]> allPlayers, int depth, treeNode t) {
		if (depth == 0) {
			t.eval = eval(player, t.board);
			return t.board;
		}
		else {
			// for every possible derivitive of this board call this fxn with depth -1;
			// gets all children of head tree
			if (t.children.isEmpty()) {
				ArrayList<ArrayList<int[]>> allDerivBoards = getDerivBoards(player, allPlayers);
				for (ArrayList<int[]> board : allDerivBoards) {
					// might need to be player + 1;
					// mighht need to change eval to return 3 values
					treeNode newNode = new treeNode(board);
					gameTreeSearch(player, board, depth-1, newNode);
					t.children.add(newNode);
				}
			}

			// All children have their eval values now, itterate over them to
			//  see which move is the best move
			ArrayList<int[]> retBoard = new ArrayList<int[]>(1);
			int bestEval = Integer.MAX_VALUE;
			for (treeNode bestBoard : t.children) {
				// if this move is the best move and it actually involves a moved piece
				if (bestBoard.eval <= bestEval &&
						compareBoard(bestBoard.board.get(player),allPlayers.get(player)) != 0) {
					bestEval = bestBoard.eval;
					retBoard = bestBoard.board;
				}
			}
			return retBoard;
		}
	}


	// My eval function takes a board and returns a value based on how close
	//  the player is to winning. I use a steps to target approach which itterates
	//   over every possible line it can get into and sees which one it can get to
	//    in the fewest moves
	private int eval(int player, ArrayList<int[]> allPlayers) {
		int smallest = Integer.MAX_VALUE;
		String dir = "up";
		// for all 3 directions
		for (int j = 0; j < 3; j++) {
			// for every row in this direction
			for (int i = 0; i < 13; i++) {
				int x = evalHelper(dir,i,player,allPlayers);
				if (x < smallest) {
					smallest = x;
				}
			}
			if (dir.equals("up")) {
				// bottom left -> top right
				dir = "left";
			}
			else {
				// top right -> bottom left
				dir = "right";
			}
		}
		return smallest;
	}


	// This is where all the heavy lifting of eval is done. Eval was made small to 
	//  keep debugging simple and easy to follow.
	private int evalHelper(String direction, int rowNum, int player, ArrayList<int[]> allPlayers) {
		int[] curPlayer = allPlayers.get(player);
		int returnVal = 0;
		ArrayList<int[]> row;

		if (direction.equals("up")) {
			row = retRowList(direction, 6, rowNum);
		}
		else {
			// other directions
			row = retRowList(direction, rowNum, 6);
		}

		// here I check to see if there are any players in the way of the current
		//  row that I am checking. I make otherPlayers array to pass into numPlayersInWay
		ArrayList<int[]> otherPlayers = new ArrayList<int[]>(2);
		if (player == 0) {
			otherPlayers.add(allPlayers.get(1));
			otherPlayers.add(allPlayers.get(2));
		}
		else if ( player == 1) {
			otherPlayers.add(allPlayers.get(0));
			otherPlayers.add(allPlayers.get(2));
		}
		else {
			otherPlayers.add(allPlayers.get(0));
			otherPlayers.add(allPlayers.get(1));
		}

		// add wight to path if other players are in way
		int numInWay = numPlayersInWay(otherPlayers, row);
		returnVal += numInWay*3;

		// add wight to path if players are in their original positions
		// right now is two but could be more later on
		// this is to incentivize players to get out of their original locations
		if (startPosInRow(row, player) == true) {
			returnVal += 2;
		}

		// add the shortest distance for each piece to get to the current row
		returnVal += shortestDistanceHelper(row,curPlayer[0],curPlayer[1]);;
		returnVal += shortestDistanceHelper(row,curPlayer[2],curPlayer[3]);
		returnVal += shortestDistanceHelper(row,curPlayer[4],curPlayer[5]);
		returnVal += shortestDistanceHelper(row,curPlayer[6],curPlayer[7]);
		return returnVal;
	}



	/** From about here and down are all less important methods **/
	// mostly just number crunching to make other code readable.



	// This method takes two boards and says whether they are the same or not 
	private int compareBoard(int[] a, int[] b) {
		boolean same = true;
		for (int i = 0; i < 8; i++) {
			if (a[i] != b[i]) {
				same = false;
				break;
			}
		}
		if (same == false) {
			return 1;
		}
		else {
			return 0;
		}
	}


	// GetDerivBoards takes the current player and returns all possible boards
	//  given the moves it is allowed to make for each piece
	private ArrayList<ArrayList<int[]>> getDerivBoards(int player, ArrayList<int[]> allPlayers) {

		ArrayList<ArrayList<int[]>> returnBoards = new ArrayList<ArrayList<int[]>>();

		int[][] playersArr = {allPlayers.get(0), allPlayers.get(1),allPlayers.get(2)};
		int[] myPositions = allPlayers.get(player);
		int xLoc = 0, yLoc = 1;

		// for all 4 pieces
		for (int i = 0; i < 4; i++) {

			int x = myPositions[xLoc], y = myPositions[yLoc];
			ArrayList<int[]> allPossibleMoves = possiblePositions(allPlayers, new int[] {x,y});

			// for every possible move for piece 1, add it to returnBoards
			for (int[] thisMove : allPossibleMoves) {
				// create new board.
				int[] newPositions = myPositions.clone();
				newPositions[xLoc] = thisMove[0];
				newPositions[yLoc] = thisMove[1];

				playersArr[player] = newPositions;
				ArrayList<int[]> newBoard = new ArrayList<int[]>(4);
				newBoard.add(playersArr[0]);
				newBoard.add(playersArr[1]);
				newBoard.add(playersArr[2]);

				returnBoards.add(newBoard);
			}
			xLoc +=2;
			yLoc +=2;
		}
		return returnBoards;
	}


	// This is a helper function to tell if any start positions are in the row
	//  that I am checking
	private boolean startPosInRow(ArrayList<int[]> row, int player) {
		int[] startLoc;
		if (player == 0) {
			startLoc = startposRed;
		}
		else if (player == 1) {
			startLoc = startposGreen;
		}
		else {
			startLoc = startposBlue;
		}
		// for every pos in the row to check
		for (int[] position : row) {
			for (int i = 0; i < 8; i+=2) {
				if (compareCoord(position, new int[] {startLoc[i], startLoc[i+1]}) == true) {
					return true;
				}
			}
		}
		return false;
	}


	// Given the other players, this fxn will return the number of pieces in this row
	private int numPlayersInWay(ArrayList<int[]> otherPlayers, ArrayList<int[]> row) {
		int returnVal = 0;
		// for each player (not current player)
		for( int[] player : otherPlayers) {
			// for all pieces of that player
			for (int i = 0; i < 8; i+=2) {
				int x = player[i], y = player[i+1];
				// do any of those pieces lie on this line?
				// for each location in the row
				for (int[] rowTile : row) {
					if (compareCoord(rowTile,new int[] {x,y}) == true) {
						returnVal++;
					}
				}
			}
		}
		return returnVal;
	}


	// This is a helper that checks to see if two coordinates are the same
	private boolean compareCoord(int[] pos, int[] pos2) {
		if (pos[0] == pos2[0] && pos[1] == pos2[1]) {
			return true;
		}
		else {
			return false;
		}
	}


	// This helper returns the number of moves a player would have to make
	//  to get this piece to a specific row
	private int shortestDistanceHelper(ArrayList<int[]> row, int x, int y) {
		int shortest = Integer.MAX_VALUE;
		for (int[] cord : row) {
			int numMoves = numMovesToLoc(new int[] {x,y}, cord);

			if (numMoves < shortest) {
				shortest = numMoves;
			}
		}
		return shortest;
	}


	// This is a helper that returns the number of legal moves it takes to get
	//  from one positoin on the board to another. very hard to read
	private int numMovesToLoc(int[] start, int[] end) {
		int numMoves = 0;
		int startX = start[0];
		int startY = start[1];
		int endX = end[0];
		int endY = end[1];
		while (startX != endX || startY != endY) {
			if (endX > startX && endY > startY) {
				// further up and to the left
				if (endX >= startX+2 && endY >= startY+2) {
					//more than one distance away
					startX += 2;
					startY += 2;
					numMoves++;
				}
				else if (endX >= startX+2) {
					startX +=2;
					numMoves++;
				}
				else if (endY >= startY+2) {
					startY +=2;
					numMoves++;
				}
				else if (endX == startX+1 && endY == startY+1) {
					//only off by one
					startX += 1;
					startY += 1;
					numMoves++;
				}
				else if (endX == startX+1) {
					startX +=1;
					numMoves++;
				}
				else if (endY == startY+1) {
					startY+=1;
					numMoves++;
				}
			}
			else if (endX < startX && endY < startY) {
				// further down and to the right
				if (endX <= startX-2 && endY <= startY-2) {
					startX -= 2;
					startY -= 2;
					numMoves++;
				}
				else if (endX <= startX-2) {
					startX -=1;
					numMoves++;
				}
				else if (endY <= startY-2) {
					startY -=1;
					numMoves++;
				}
				else if (endX == startX-1 && endY == startY-1) {
					//only off by one
					startX -= 1;
					startY -= 1;
					numMoves++;
				}
				else if (endX == startX-1) {
					startX -=1;
					numMoves++;
				}
				else if (endY == startY-1) {
					startY -=1;
					numMoves++;
				}
			}
			else if (endX > startX) {
				if (endX >= startX+2) {
					startX+=2;
					numMoves++;
				}
				else {
					startX+=1;
					numMoves++;
				}
			}
			else if (endX < startX) {
				if (endX<= startX-2) {
					startX-=2;
					numMoves++;
				}
				else {
					startX-=1;
					numMoves++;
				}
			}
			else if (endY > startY) {
				if (endY >= startY+2) {
					startY +=2;
					numMoves++;
				}
				else {
					startY +=1;
					numMoves++;
				}
			}
			else if (endY < startY) {
				if (endY <= startY-2) {
					startY -=2;
					numMoves++;
				}
				else {
					startY-=1;
					numMoves++;
				}
			}
		}
		return numMoves;
	}

	// this is a helper function that returns all of the legal moves given a
	//  player's position and all other players positions
	private ArrayList<int[]> possiblePositions(ArrayList<int[]> currentLocations, int[] myCurrentPos) {
		ArrayList<int[]> returnPositions = new ArrayList<int[]>();
		int x = myCurrentPos[0];
		int y = myCurrentPos[1];

		// do this for every one of the 12 possibilities
		int newx = 0;
		int newy = 0;

		// up
		newx = x - 1;
		newy = y;
		if (possiblePosHelper(currentLocations, newx, newy) == true) {
			int[] toAdd = {newx,newy};
			returnPositions.add(toAdd);
			newx--;
			if (possiblePosHelper(currentLocations, newx, newy) == true) {
				int[] moreToAdd = {newx,newy};
				returnPositions.add(moreToAdd);
			}
		}

		// up right
		newx = x;
		newy = y+1;
		if (possiblePosHelper(currentLocations, newx, newy) == true) {
			int[] toAdd = {newx,newy};
			returnPositions.add(toAdd);
			newy++;
			if (possiblePosHelper(currentLocations, newx, newy) == true) {
				int[] moreToAdd = {newx,newy};
				returnPositions.add(moreToAdd);
			}
		}

		// down right
		newx = x+1;
		newy = y+1;
		if (possiblePosHelper(currentLocations, newx, newy) == true) {
			int[] toAdd = {newx,newy};
			returnPositions.add(toAdd);
			newy++;
			newx++;
			if (possiblePosHelper(currentLocations, newx, newy) == true) {
				int[] moreToAdd = {newx,newy};
				returnPositions.add(moreToAdd);
			}
		}

		// down
		newx = x+1;
		newy = y;
		if (possiblePosHelper(currentLocations, newx, newy) == true) {
			int[] toAdd = {newx,newy};
			returnPositions.add(toAdd);
			newx++;
			if (possiblePosHelper(currentLocations, newx, newy) == true) {
				int[] moreToAdd = {newx,newy};
				returnPositions.add(moreToAdd);
			}
		}

		// down left
		newx = x;
		newy = y-1;
		if (possiblePosHelper(currentLocations, newx, newy) == true) {
			int[] toAdd = {newx,newy};
			returnPositions.add(toAdd);
			newy--;
			if (possiblePosHelper(currentLocations, newx, newy) == true) {
				int[] moreToAdd = {newx,newy};
				returnPositions.add(moreToAdd);
			}
		}

		// up left
		newx = x-1;
		newy = y-1;
		if (possiblePosHelper(currentLocations, newx, newy) == true) {
			int[] toAdd = {newx,newy};
			returnPositions.add(toAdd);
			newy--;
			newx--;
			if (possiblePosHelper(currentLocations, newx, newy) == true) {
				int[] moreToAdd = {newx,newy};
				returnPositions.add(moreToAdd);
			}
		}
		return returnPositions;
	}


	// this is a helper function that possible positions calls to see if its
	//  in bounds or not
	private boolean inBounds(int x, int y) {
		if (x < 0 || y < 0 || x > 12 || y > 12) {
			return false;
		}
		else if (x == 0) {
			if (y >= 0 && y <= 6)
				return true;
			else
				return false;
		}
		else if (x == 1) {
			if (y >= 0 && y <= 7)
				return true;
			else
				return false;
		}
		else if (x == 2) {
			if (y >= 0 && y <= 8)
				return true;
			else
				return false;
		}
		else if (x == 3) {
			if (y >= 0 && y <= 9)
				return true;
			else
				return false;
		}
		else if (x == 4) {
			if (y >= 0 && y <= 10)
				return true;
			else
				return false;
		}
		else if (x == 5) {
			if (y >= 0 && y <= 11)
				return true;
			else
				return false;
		}
		else if (x == 6) {
			if (y >= 0 && y <= 12)
				return true;
			else
				return false;
		}
		else if (x == 7) {
			if (y >= 1 && y <= 12)
				return true;
			else
				return false;
		}
		else if (x == 8) {
			if (y >= 2 && y <= 12)
				return true;
			else
				return false;
		}
		else if (x == 9) {
			if (y >= 3 && y <= 12)
				return true;
			else
				return false;
		}
		else if (x == 10) {
			if (y >= 4 && y <= 12)
				return true;
			else
				return false;
		}
		else if (x == 11) {
			if (y >= 5 && y <= 12)
				return true;
			else
				return false;
		}
		else if (x == 12) {
			if (y >= 6 && y <= 12)
				return true;
			else
				return false;
		}
		else {
			return false;
		}
	}


	// takes in all pieces (curLocatoins) and checks to see if x and y intersect any of them
	// this helper is used to see if anyone is in the way of a possible move
	private boolean possiblePosHelper(ArrayList<int[]> curLocations, int x, int y) {
		boolean retBool = true;

		// out of bounds check
		if ( inBounds(x, y) == false) {
			return false;
		}
		else {
			for (int[] player : curLocations) {
				// player = 8 element x,y coordinate.
				int x1 = player[0];
				int y1 = player[1];

				int x2 = player[2];
				int y2 = player[3];

				int x3 = player[4];
				int y3 = player[5];

				int x4 = player[6];
				int y4 = player[7];

				// if intersects with any plaers
				if ((x == x1 && y == y1) || (x == x2 && y == y2)
						|| (x == x3 && y == y3) || (x == x4 && y == y4)) {
					return false;
				}
			}
			return retBool;
		}
	}


	// This helper takes in coordinates and a direction and returns an arrayList
	//  of coordinates that represents a single row
	// It is optomized to store data in a global structure because it can be called often
	private ArrayList<int[]> retRowList(String dir, int x, int y) {
		ArrayList<int[]> currentRow = new ArrayList<int[]>();
		if (dir.equals("up")) {
			currentRow = getUpRow(y);
			return currentRow;
		}
		// bottom left to top right
		else if (dir.equals("left")){
			currentRow = getLeftRow(x);
			return currentRow;
		}
		// top left to bottom right
		else if (dir.equals("right")) {
			int startX = x;
			int startY = y;

			while (startY <= 12 && startX <= 12) {
				currentRow.add(new int[]{startX, startY});
				startX++;
				startY++;
			}
			startX = x-1;
			startY = y-1;

			while (startY >= 0 && startX >= 0) {
				currentRow.add(new int[]{startX, startY});
				startX--;
				startY--;
			}
			return currentRow;
		}
		else {
			System.out.println("invalid direction entered retRowList");
			return null;
		}
	}

	// getUpRow returns a row based on a y valse
	private ArrayList<int[]> getUpRow(int y) {
		ArrayList<int[]> returnList = new ArrayList<int[]>();
		ArrayList<int[]> curGlob = globUp.get(y);
		if (curGlob.isEmpty()) {
			switch (y) {
			case 0: 
				globUp.get(y).add(new int[] {0,y});
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				break;
			case 1: 
				globUp.get(y).add(new int[] {0,y});
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				break;
			case 2: 
				globUp.get(y).add(new int[] {0,y});
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				break;
			case 3: 
				globUp.get(y).add(new int[] {0,y});
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				break;
			case 4: 
				globUp.get(y).add(new int[] {0,y});
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				break;
			case 5: 
				globUp.get(y).add(new int[] {0,y});
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				break;
			case 6: 
				globUp.get(y).add(new int[] {0,y});
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				globUp.get(y).add(new int[] {12,y});
				break;
			case 7: 
				globUp.get(y).add(new int[] {1,y});
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				globUp.get(y).add(new int[] {12,y});
				break;
			case 8: 
				globUp.get(y).add(new int[] {2,y});
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				globUp.get(y).add(new int[] {12,y});
				break;
			case 9: 
				globUp.get(y).add(new int[] {3,y});
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				globUp.get(y).add(new int[] {12,y});
				break;
			case 10: 
				globUp.get(y).add(new int[] {4,y});
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				globUp.get(y).add(new int[] {12,y});
				break;
			case 11: 
				globUp.get(y).add(new int[] {5,y});
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				globUp.get(y).add(new int[] {12,y});
				break;
			case 12: 
				globUp.get(y).add(new int[] {6,y});
				globUp.get(y).add(new int[] {7,y});
				globUp.get(y).add(new int[] {8,y});
				globUp.get(y).add(new int[] {9,y});
				globUp.get(y).add(new int[] {10,y});
				globUp.get(y).add(new int[] {11,y});
				globUp.get(y).add(new int[] {12,y});
				returnList = globUp.get(y);
				break;
			}
		}
		returnList = curGlob;
		return returnList;
	}

	// getLeftRow returns a left row based on an x value
	private ArrayList<int[]> getLeftRow(int x) {
		ArrayList<int[]> returnList = new ArrayList<int[]>();
		ArrayList<int[]> curGlob = globLeft.get(x);
		if (curGlob.isEmpty()) {
			switch (x) {
			case 0:
				curGlob.add(new int[] {x,0});
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				break;
			case 1:
				curGlob.add(new int[] {x,0});
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				break;
			case 2:
				curGlob.add(new int[] {x,0});
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				break;
			case 3:
				curGlob.add(new int[] {x,0});
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				break;
			case 4:
				curGlob.add(new int[] {x,0});
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				break;
			case 5:
				curGlob.add(new int[] {x,0});
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				break;
			case 6:
				curGlob.add(new int[] {x,0});
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				curGlob.add(new int[] {x,12});
				break;
			case 7:
				curGlob.add(new int[] {x,1});
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				curGlob.add(new int[] {x,12});
				break;
			case 8:
				curGlob.add(new int[] {x,2});
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				curGlob.add(new int[] {x,12});
				break;
			case 9:
				curGlob.add(new int[] {x,3});
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				curGlob.add(new int[] {x,12});
				break;
			case 10:
				curGlob.add(new int[] {x,4});
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				curGlob.add(new int[] {x,12});
				break;
			case 11:
				curGlob.add(new int[] {x,5});
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				curGlob.add(new int[] {x,12});
				break;
			case 12:
				curGlob.add(new int[] {x,6});
				curGlob.add(new int[] {x,7});
				curGlob.add(new int[] {x,8});
				curGlob.add(new int[] {x,9});
				curGlob.add(new int[] {x,10});
				curGlob.add(new int[] {x,11});
				curGlob.add(new int[] {x,12});
				break;
			}
			returnList = curGlob;
		}
		returnList = globLeft.get(x);
		return returnList;
	}


}







