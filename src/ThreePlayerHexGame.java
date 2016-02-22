
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


/**********************************
  This is the main class of a Java program to play a game based on hexagonal tiles.
  The mechanism of handling hexes is in the class hexmech.java that I found online.

  Parts Written by: M.H.
  Majority of Functionality Written by: Andrew Dilks
  Date: December 2012
  Modified: June 2016

 ***********************************/

public class ThreePlayerHexGame {

	//constants and global variables
	final static Color COLOURBACK =  Color.WHITE;
	final static Color COLOURCELL =  Color.GRAY;	 
	final static Color COLOURGRID =  Color.BLACK;	 
	final static Color COLOURONE = new Color(255,255,255,200);
	final static Color COLOURONETXT = Color.BLUE;
	final static Color COLOURTWO = new Color(0,0,0,200);
	final static Color COLOURTWOTXT = new Color(255,100,255);
	final static int EMPTY = 0;
	final static int BSIZE = 15; //board size.
	final static int HEXSIZE = 40;	//hex size in pixels
	final static int BORDERS = 5;  
	final static int SCRSIZE = HEXSIZE * (BSIZE + 1) + BORDERS*3; //screen size (vertical dimension).

	int[][] board;

	// GUI Global Vars
	private JFrame frame;
	private hexmech hexmechTest;
	private static boolean wait;


	// Init fxn
	ThreePlayerHexGame() {
		board = new int[BSIZE][BSIZE];
		frame = new JFrame("3 Player Hex Board Game");
		hexmechTest = new hexmech();
		wait = true;

		initGame();
		createAndShowGUI();
	}

	
	// The most main of all the methods! Uses Game.java for board game rules
	public static void main(String[] args) {

		// The Three players who are playing the game
		// In the future add functionality to allow swapping out players in GUI
		Game.Player[] p = new Game.Player[3];

		p[0] = new OneLinePlayer();
		p[1] = new OneLinePlayer();
		p[2] = new P2_Dilks();

		Game g = new Game(p);

		System.out.println("Playing game...");

		int w = -1;
		int winner = -1;
		for(int turn=0; turn<Game.MAXTURNS; turn++) {

			for(int i=0; i<Game.NUMPLAYERS; i++) {

				// wait for buttom press
				while (wait == true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}


				boolean makeMoveResult = g.makeMove(i);
				wait = true;

				if(makeMoveResult) {
					winner = g.checkWin();
				}
				else {
					winner = -3;
				}

				if(winner != -1) {
					break;
				}


				// Create string "player"
				String player;
				if (i == 2) {
					player =
							"(0) Red                                                  Turn: " + ((turn*3)    +1+i);
				}
				else if (i == 0) {
					player =
							"(1) Green                                               Turn: " + ((turn*3)    +1+i);
				}
				else {
					player =
							"(2) Blue                                                 Turn: " + ((turn*3)    +1+i);
				}

				// Update the GUI Header
				g.newDrawing.setLabelText("The next move belongs to " + player);
			}

			if(winner != -1)
				break;

		}

		// All turns are used or Break was reached

		w = winner;

		if(w==-2) {
			System.out.println("Multiple players won! The game is a draw.");
			g.newDrawing.setLabelText("Multiple players won! The game is a draw.");
			g.newDrawing.dissableButton();
		}
		else if(w==-1) {
			System.out.println("Time up! The game is a draw.");
			g.newDrawing.setLabelText("Time up! The game is a draw.");
			g.newDrawing.dissableButton();
		}
		else if(w==-3) {
			System.out.println("Error: Invalid move!");
			g.newDrawing.setLabelText("Error: Invalid move!");
			g.newDrawing.dissableButton();
		}

		// Someone actually won here!!
		else {
			String num;
			if (w == 0) {
				num = "(0) Red";
			}
			else if (w == 1) {
				num = "(1) Green";
			}
			else {
				num = "(2) Blue";
			}
			g.newDrawing.setLabelText(num +" player wins!        GAME OVER");
			System.out.println(num +" player wins!");
			g.newDrawing.dissableButton();
		}

	}
	
	
	// Sets the board back to default settings
	public void clear() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				drawLocation(i, j, COLOURCELL);
			}
		}
	}
	

	// This method draws the piece on the GUI using conventional coordinates
	public void drawLocation(int x, int y, Color color) {
		int c = 0;
		if (color.equals(Color.RED)) {
			c = 98;
		} else if (color.equals(Color.BLUE)) {
			c = 99;
		} else if (color.equals(Color.GREEN)) {
			c = 97;
		} else if (color.equals(COLOURCELL)) {
			c = -1;
		}

		// convert x to proper location and same with y
		String xy = convertCord(x, y);
		int loc = xy.indexOf(",");

		String a = xy.substring(0, loc);
		String b = xy.substring(loc + 1, xy.length());

		int newX = Integer.parseInt(a);
		int newY = Integer.parseInt(b);

		// don't paint 0,0
		if (!(newX == 0 && newY == 0)) {
			board[newX][newY] = c;
			frame.repaint();
		}
	}

	// this method paints the board with all proper starting colors
	void initGame(){

		// I didn't write hexmech code.
		hexmechTest.setXYasVertex(false); //RECOMMENDED: leave this as FALSE.
		hexmechTest.setHeight(HEXSIZE); //Either setHeight or setSize must be run to initialize the hex
		hexmechTest.setBorders(BORDERS);

		for (int i=0;i<BSIZE;i++) {
			for (int j=0;j<BSIZE;j++) {
				board[i][j]=EMPTY;
			}
		}
		for (int i = 4; i <= 10; i++) {
			board[1][i] = -(int)'#';
		}
		for (int i = 4; i <= 11; i++) {
			board[2][i] = -(int)'#';
		}
		for (int i = 3; i <= 11; i++) {
			board[3][i] = -(int)'#';
		}
		for (int i = 3; i <= 12; i++) {
			board[4][i] = -(int)'#';
		}
		for (int i = 2; i <= 12; i++) {
			board[5][i] = -(int)'#';
		}
		for (int i = 2; i <= 13; i++) {
			board[6][i] = -(int)'#';
		}
		for (int i = 1; i <= 13; i++) {
			board[7][i] = -(int)'#';
		}
		for (int i = 2; i <= 13; i++) {
			board[8][i] = -(int)'#';
		}
		for (int i = 2; i <= 13; i++) {
			board[8][i] = -(int)'#';
		}
		for (int i = 2; i <= 12; i++) {
			board[9][i] = -(int)'#';
		}
		for (int i = 3; i <= 12; i++) {
			board[10][i] = -(int)'#';
		}
		for (int i = 3; i <= 11; i++) {
			board[11][i] = -(int)'#';
		}
		for (int i = 4; i <= 11; i++) {
			board[12][i] = -(int)'#';
		}
		for (int i = 4; i <= 10; i++) {
			board[13][i] = -(int)'#';
		}

		// The default starting locations are drawn here.

		//red
		//{1,0,4,0,8,12,11,12};
		drawLocation( 1, 0, Color.RED);
		drawLocation( 4, 0, Color.RED);
		drawLocation( 8, 12, Color.RED);
		drawLocation( 11, 12, Color.RED);

		//green
		// {0,2,0,5,12,10,12,7};
		drawLocation(0,2, Color.GREEN);
		drawLocation(0,5, Color.GREEN);
		drawLocation(12,10, Color.GREEN);
		drawLocation(12,7, Color.GREEN);

		//blue
		// {2,8,5,11,7,1,10,4};
		drawLocation(2,8, Color.BLUE);
		drawLocation(5,11, Color.BLUE);
		drawLocation(7,1, Color.BLUE);
		drawLocation(10,4, Color.BLUE);

	}


	
	
	// helper vars for the GUI
	private JButton testButton;
	private JLabel testLabel;

	// This is a very importen part of the GUI and does most of the work
	private void createAndShowGUI() {
		//		JFrame.setDefaultLookAndFeelDecorated(true);
		//		frame.setLocationRelativeTo( null );
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setLayout(new BorderLayout());

		// add the grid to the frame
		DrawingPanel panel = new DrawingPanel();
		Container content = frame.getContentPane();
		content.add(panel);
		frame.add(panel);  //-- cannot be done in a static context
		frame.setSize( (int)(SCRSIZE/1.23), SCRSIZE);

		// add the text box to the frame
		testLabel = new JLabel("Test");
		testLabel.setText("Start game by pressing Make move Button ");
		frame.add(testLabel, BorderLayout.NORTH);

		// add the next button to the frame
		testButton = new JButton("Make move");
		testButton.addActionListener(new testListener());
		frame.add(testButton, BorderLayout.SOUTH);

	}
	
	
	// These are the GUI helper methods. Method describes functionality
	private void dissableButton() {
		testButton.setEnabled(false);
	}

	private void setLabelText(String text) {
		testLabel.setText(text);
	}
	

	// This is so ugly. I know one day I'm going to look back at this and realize
	//  there is a much easier way to solve this problem.
	private String convertCord(int x, int y) {
		String testStr = "" + x + ","+ y;
		String retStr = null;

		switch (testStr) {
		case "0,0" : retStr = "1,4";
		break;
		case "0,1" : retStr = "2,4";
		break;
		case "0,2" : retStr = "3,3";
		break;
		case "0,3" : retStr = "4,3";
		break;
		case "0,4" : retStr = "5,2";
		break;
		case "0,5" : retStr = "6,2";
		break;
		case "0,6" : retStr = "7,1";
		break;

		case "1,0" : retStr = "1,5";
		break;
		case "1,1" : retStr = "2,5";
		break;
		case "1,2" : retStr = "3,4";
		break;
		case "1,3" : retStr = "4,4";
		break;
		case "1,4" : retStr = "5,3";
		break;
		case "1,5" : retStr = "6,3";
		break;
		case "1,6" : retStr = "7,2";
		break;
		case "1,7" : retStr = "8,2";
		break;

		case "2,0" : retStr = "1,6";
		break;
		case "2,1" : retStr = "2,6";
		break;
		case "2,2" : retStr = "3,5";
		break;
		case "2,3" : retStr = "4,5";
		break;
		case "2,4" : retStr = "5,4";
		break;
		case "2,5" : retStr = "6,4";
		break;
		case "2,6" : retStr = "7,3";
		break;
		case "2,7" : retStr = "8,3";
		break;
		case "2,8" : retStr = "9,2";
		break;

		case "3,0" : retStr = "1,7";
		break;
		case "3,1" : retStr = "2,7";
		break;
		case "3,2" : retStr = "3,6";
		break;
		case "3,3" : retStr = "4,6";
		break;
		case "3,4" : retStr = "5,5";
		break;
		case "3,5" : retStr = "6,5";
		break;
		case "3,6" : retStr = "7,4";
		break;
		case "3,7" : retStr = "8,4";
		break;
		case "3,8" : retStr = "9,3";
		break;
		case "3,9" : retStr = "10,3";
		break;

		case "4,0" : retStr = "1,8";
		break;
		case "4,1" : retStr = "2,8";
		break;
		case "4,2" : retStr = "3,7";
		break;
		case "4,3" : retStr = "4,7";
		break;
		case "4,4" : retStr = "5,6";
		break;
		case "4,5" : retStr = "6,6";
		break;
		case "4,6" : retStr = "7,5";
		break;
		case "4,7" : retStr = "8,5";
		break;
		case "4,8" : retStr = "9,4";
		break;
		case "4,9" : retStr = "10,4";
		break;
		case "4,10" : retStr = "11,3";
		break;

		case "5,0" : retStr = "1,9";
		break;
		case "5,1" : retStr = "2,9";
		break;
		case "5,2" : retStr = "3,8";
		break;
		case "5,3" : retStr = "4,8";
		break;
		case "5,4" : retStr = "5,7";
		break;
		case "5,5" : retStr = "6,7";
		break;
		case "5,6" : retStr = "7,6";
		break;
		case "5,7" : retStr = "8,6";
		break;
		case "5,8" : retStr = "9,5";
		break;
		case "5,9" : retStr = "10,5";
		break;
		case "5,10" : retStr = "11,4";
		break;
		case "5,11" : retStr = "12,4";
		break;

		case "6,0" : retStr = "1,10";
		break;
		case "6,1" : retStr = "2,10";
		break;
		case "6,2" : retStr = "3,9";
		break;
		case "6,3" : retStr = "4,9";
		break;
		case "6,4" : retStr = "5,8";
		break;
		case "6,5" : retStr = "6,8";
		break;
		case "6,6" : retStr = "7,7";
		break;
		case "6,7" : retStr = "8,7";
		break;
		case "6,8" : retStr = "9,6";
		break;
		case "6,9" : retStr = "10,6";
		break;
		case "6,10" : retStr = "11,5";
		break;
		case "6,11" : retStr = "12,5";
		break;
		case "6,12" : retStr = "13,4";
		break;


		case "7,1" : retStr = "2,11";
		break;
		case "7,2" : retStr = "3,10";
		break;
		case "7,3" : retStr = "4,10";
		break;
		case "7,4" : retStr = "5,9";
		break;
		case "7,5" : retStr = "6,9";
		break;
		case "7,6" : retStr = "7,8";
		break;
		case "7,7" : retStr = "8,8";
		break;
		case "7,8" : retStr = "9,7";
		break;
		case "7,9" : retStr = "10,7";
		break;
		case "7,10" : retStr = "11,6";
		break;
		case "7,11" : retStr = "12,6";
		break;
		case "7,12" : retStr = "13,5";
		break;

		case "8,2" : retStr = "3,11";
		break;
		case "8,3" : retStr = "4,11";
		break;
		case "8,4" : retStr = "5,10";
		break;
		case "8,5" : retStr = "6,10";
		break;
		case "8,6" : retStr = "7,9";
		break;
		case "8,7" : retStr = "8,9";
		break;
		case "8,8" : retStr = "9,8";
		break;
		case "8,9" : retStr = "10,8";
		break;
		case "8,10" : retStr = "11,7";
		break;
		case "8,11" : retStr = "12,7";
		break;
		case "8,12" : retStr = "13,6";
		break;

		case "9,3" : retStr = "4,12";
		break;
		case "9,4" : retStr = "5,11";
		break;
		case "9,5" : retStr = "6,11";
		break;
		case "9,6" : retStr = "7,10";
		break;
		case "9,7" : retStr = "8,10";
		break;
		case "9,8" : retStr = "9,9";
		break;
		case "9,9" : retStr = "10,9";
		break;
		case "9,10" : retStr = "11,8";
		break;
		case "9,11" : retStr = "12,8";
		break;
		case "9,12" : retStr = "13,7";
		break;

		case "10,4" : retStr = "5,12";
		break;
		case "10,5" : retStr = "6,12";
		break;
		case "10,6" : retStr = "7,11";
		break;
		case "10,7" : retStr = "8,11";
		break;
		case "10,8" : retStr = "9,10";
		break;
		case "10,9" : retStr = "10,10";
		break;
		case "10,10" : retStr = "11,9";
		break;
		case "10,11" : retStr = "12,9";
		break;
		case "10,12" : retStr = "13,8";
		break;

		case "11,5" : retStr = "6,13";
		break;
		case "11,6" : retStr = "7,12";
		break;
		case "11,7" : retStr = "8,12";
		break;
		case "11,8" : retStr = "9,11";
		break;
		case "11,9" : retStr = "10,11";
		break;
		case "11,10" : retStr = "11,10";
		break;
		case "11,11" : retStr = "12,10";
		break;
		case "11,12" : retStr = "13,9";
		break;

		case "12,6" : retStr = "7,13";
		break;
		case "12,7" : retStr = "8,13";
		break;
		case "12,8" : retStr = "9,12";
		break;
		case "12,9" : retStr = "10,12";
		break;
		case "12,10" : retStr = "11,11";
		break;
		case "12,11" : retStr = "12,11";
		break;
		case "12,12" : retStr = "13,10";
		break;

		default: retStr = "0,0";
		break;
		}
		return retStr;
	}


	// Pretty basic implementation of a Listener
	//  this provides the stepping button press functionality
	public class testListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			wait = false;
		} 
	}
	
	
	// I didnt write this code. Just modified some of it
	class DrawingPanel extends JPanel {
		private static final long serialVersionUID = 1537261912452632797L;

		//mouse variables here
		//Point mPt = new Point(0,0);

		public DrawingPanel() {	
			setBackground(COLOURBACK);

			// MyMouseListener ml = new MyMouseListener();            
			// addMouseListener(ml);
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
			super.paintComponent(g2);
			//draw grid
			for (int i=0;i<BSIZE;i++) {
				for (int j=0;j<BSIZE;j++) {
					hexmechTest.drawHex(i,j,g2);
				}
			}
			//fill in hexes
			for (int i=0;i<BSIZE;i++) {
				for (int j=0;j<BSIZE;j++) {					
					//if (board[i][j] < 0) hexmech.fillHex(i,j,COLOURONE,-board[i][j],g2);
					//if (board[i][j] > 0) hexmech.fillHex(i,j,COLOURTWO, board[i][j],g2);
					hexmechTest.fillHex(i,j,board[i][j],g2);
				}
			}

			//g.setColor(Color.RED);
			//g.drawLine(mPt.x,mPt.y, mPt.x,mPt.y);
		}

		public void paintComponent() {
			//draw grid
			//fill in hexes
			for (int i=0;i<BSIZE;i++) {
				for (int j=0;j<BSIZE;j++) {					
					//if (board[i][j] < 0) hexmech.fillHex(i,j,COLOURONE,-board[i][j],g2);
					//if (board[i][j] > 0) hexmech.fillHex(i,j,COLOURTWO, board[i][j],g2);
					//					hexmech.fillHex(i,j,board[i][j],g2);
				}
			}

			//g.setColor(Color.RED);
			//g.drawLine(mPt.x,mPt.y, mPt.x,mPt.y);
		}

		//		class MyMouseListener extends MouseAdapter	{	//inner class inside DrawingPanel 
		//			public void mouseClicked(MouseEvent e) { 
		//				int x = e.getX(); 
		//				int y = e.getY(); 
		//				//mPt.x = x;
		//				//mPt.y = y;
		//				Point p = new Point( hexmech.pxtoHex(e.getX(),e.getY()) );
		//				if (p.x < 0 || p.y < 0 || p.x >= BSIZE || p.y >= BSIZE) return;
		//
		//				//DEBUG: colour in the hex which is supposedly the one clicked on
		//				//clear the whole screen first.
		//				/* for (int i=0;i<BSIZE;i++) {
		//					for (int j=0;j<BSIZE;j++) {
		//						board[i][j]=EMPTY;
		//					}
		//				} */
		//
		//				//What do you want to do when a hexagon is clicked?
		//				board[p.x][p.y] = (int)'X';
		//				repaint();
		//			}		 
		//		} //end of MyMouseListener class 
	} // end of DrawingPanel class
}




/** I didn't write this class and I may have not modified it at all either. */
class hexmech {
	/*
	 * Helpful references:
	 * http://www.codeproject.com/Articles/14948/Hexagonal-grid-for-games-and-
	 * other-projects-Part-1
	 * http://weblogs.java.net/blog/malenkov/archive/2009/02/hexagonal_tile.html
	 * http://www.tonypa.pri.ee/tbw/tut25.html
	 */

	/*
	 * #define HEXEAST 0 #define HEXSOUTHEAST 1 #define HEXSOUTHWEST 2 #define
	 * HEXWEST 3 #define HEXNORTHWEST 4 #define HEXNORTHEAST 5
	 */

	//Constants
	public final boolean orFLAT= true;
	public final boolean orPOINT= false;
	public boolean ORIENT= orFLAT;  //this is not used. We're never going to do pointy orientation

	public boolean XYVertex=true;	//true: x,y are the co-ords of the first vertex.
	//false: x,y are the co-ords of the top left rect. co-ord.

	private int BORDERS=50;	//default number of pixels for the border.

	private static int s=0;	// length of one side
	private static int t=0;	// short side of 30o triangle outside of each hex
	private static int r=0;	// radius of inscribed circle (centre to middle of each side). r= h/2
	private static int h=0;	// height. Distance between centres of two adjacent hexes. Distance between two opposite sides in a hex.

	public void setXYasVertex(boolean b) {
		XYVertex=b;
	}
	public void setBorders(int b){
		BORDERS=b;
	}

	/** This functions takes the Side length in pixels and uses that as the basic dimension of the hex.
            It calculates all other needed constants from this dimension.
	 */
	public void setSide(int side) {
		s=side;
		t =  (int) (s / 2);			//t = s sin(30) = (int) CalculateH(s);
		r =  (int) (s * 0.8660254037844);	//r = s cos(30) = (int) CalculateR(s); 
		h=2*r;
	}
	
	public void setHeight(int height) {
		h = height;			// h = basic dimension: height (distance between two adj centresr aka size)
		r = h/2;			// r = radius of inscribed circle
		s = (int) (h / 1.73205);	// s = (h/2)/cos(30)= (h/2) / (sqrt(3)/2) = h / sqrt(3)
		t = (int) (r / 1.73205);	// t = (h/2) tan30 = (h/2) 1/sqrt(3) = h / (2 sqrt(3)) = r / sqrt(3)
	}

	/*********************************************************
	 * Name: hex()
	 * Parameters: (x0,y0) This point is normally the top left corner 
	 * 	of the rectangle enclosing the hexagon. 
	 * 	However, if XYVertex is true then (x0,y0) is the vertex of the 
	 * 	top left corner of the hexagon. 
	 * Returns: a polygon containing the six points.
	 * Called from: drawHex(), fillhex()
	 * Purpose: This function takes two points that describe a hexagon
	 * 	and calculates all six of the points in the hexagon.
	 **********************************************************/
	public Polygon hex (int x0, int y0) {

		int y = y0 + BORDERS;
		int x = x0 + BORDERS; // + (XYVertex ? t : 0); //Fix added for XYVertex = true. 
		// NO! Done below in cx= section
		if (s == 0  || h == 0) {
			System.out.println("ERROR: size of hex has not been set");
			return new Polygon();
		}

		int[] cx,cy;

		//I think that this XYvertex stuff is taken care of in the int x line above. Why is it here twice?
		if (XYVertex) 
			cx = new int[] {x,x+s,x+s+t,x+s,x,x-t};  //this is for the top left vertex being at x,y. Which means that some of the hex is cutoff.
		else
			cx = new int[] {x+t,x+s+t,x+s+t+t,x+s+t,x+t,x};	//this is for the whole hexagon to be below and to the right of this point

		cy = new int[] {y,y,y+r,y+r+r,y+r+r,y+r};
		return new Polygon(cx,cy,6);

		/*
		   x=200;
		   poly = new Polygon();
		   poly.addPoint(x,y);
		   poly.addPoint(x+s,y);
		   poly.addPoint(x+s+t,y+r);
		   poly.addPoint(x+s,y+r+r);
		   poly.addPoint(x,y+r+r);
		   poly.addPoint(x-t,y+r);
		 */
	}

	/********************************************************************
	 * Name: drawHex()
	 * Parameters: (i,j) : the x,y coordinates of the inital point of the hexagon
	 *    g2: the Graphics2D object to draw on.
	 * Returns: void
	 * Calls: hex() 
	 * Purpose: This function draws a hexagon based on the initial point (x,y).
	 * 	The hexagon is drawn in the colour specified in DrawingTest.COLOURELL.
	 **********************************************************************/
	public void drawHex(int i, int j, Graphics2D g2) {
		int x = i * (s+t);
		int y = j * h + (i%2) * h/2;
		Polygon poly = hex(x,y);
		g2.setColor(ThreePlayerHexGame.COLOURCELL);
		//g2.fillPolygon(hexmech.hex(x,y));
		g2.fillPolygon(poly);
		g2.setColor(ThreePlayerHexGame.COLOURGRID);
		g2.drawPolygon(poly);
	}

	/***************************************************************************
	 * Name: fillHex()
	 * Parameters: (i,j) : the x,y coordinates of the initial point of the hexagon
		n   : an integer number to indicate a letter to draw in the hex
		g2  : the graphics context to draw on
	 * Return: void
	 * Called from:
	 * Calls: hex()
	 *Purpose: This draws a filled in polygon based on the coordinates of the hexagon.
	  The colour depends on whether n is negative or positive.
	  The colour is set by DrawingTest.COLOURONE and DrawingTest.COLOURTWO.
	  The value of n is converted to letter and drawn in the hexagon.
	 *****************************************************************************/
	public void fillHex(int i, int j, int n, Graphics2D g2) {
		char c='o';
		int x = i * (s+t);
		int y = j * h + (i%2) * h/2;
		if (n == 99) {
			g2.setColor(new Color(0,191,255));
			g2.fillPolygon(hex(x,y));
		}
		else if (n == 98) {
			g2.setColor(new Color(255,0,0));
			g2.fillPolygon(hex(x,y));
		}
		else if ( n == 97 ) {
			g2.setColor(new Color(50,205,50));
			g2.fillPolygon(hex(x,y));
		}
		else {
			if (n < 0) {
				g2.setColor(ThreePlayerHexGame.COLOURONE);
				g2.fillPolygon(hex(x,y));
				g2.setColor(ThreePlayerHexGame.COLOURONETXT);
			}
			if (n > 0) {
				g2.setColor(ThreePlayerHexGame.COLOURTWO);
				g2.fillPolygon(hex(x,y));
				g2.setColor(ThreePlayerHexGame.COLOURTWOTXT);
				c = (char)n;
				g2.drawString(""+c, x+r+BORDERS, y+r+BORDERS+4); // handle XYVertex
				//g2.drawString(i+","+j, x+r+BORDERS, y+r+BORDERS+4);
			}
		}
	}

	//This function changes pixel location from a mouse click to a hex grid location
	/*****************************************************************************
	 * Name: pxtoHex (pixel to hex)
	 * Parameters: mx, my. These are the co-ordinates of mouse click.
	 * Returns: point. A point containing the coordinates of the hex that is clicked in.
           If the point clicked is not a valid hex (ie. on the borders of the board, (-1,-1) is returned.
	 * Function: This only works for hexes in the FLAT orientation. The POINTY orientation would require
            a whole other function (different math).
            It takes into account the size of borders.
            It also works with XYVertex being True or False.
	 *****************************************************************************/
	public Point pxtoHex(int mx, int my) {
		Point p = new Point(-1,-1);

		//correction for BORDERS and XYVertex
		mx -= BORDERS;
		my -= BORDERS;
		if (XYVertex) mx += t;

		int x = (int) (mx / (s+t)); //this gives a quick value for x. It works only on odd cols and doesn't handle the triangle sections. It assumes that the hexagon is a rectangle with width s+t (=1.5*s).
		int y = (int) ((my - (x%2)*r)/h); //this gives the row easily. It needs to be offset by h/2 (=r)if it is in an even column

		/******FIX for clicking in the triangle spaces (on the left side only)*******/
		//dx,dy are the number of pixels from the hex boundary. (ie. relative to the hex clicked in)
		int dx = mx - x*(s+t);
		int dy = my - y*h;

		if (my - (x%2)*r < 0) return p; // prevent clicking in the open halfhexes at the top of the screen

		//System.out.println("dx=" + dx + " dy=" + dy + "  > " + dx*r/t + " <");

		//even columns
		if (x%2==0) {
			if (dy > r) {	//bottom half of hexes
				if (dx * r /t < dy - r) {
					x--;
				}
			}
			if (dy < r) {	//top half of hexes
				if ((t - dx)*r/t > dy ) {
					x--;
					y--;
				}
			}
		} else {  // odd columns
			if (dy > h) {	//bottom half of hexes
				if (dx * r/t < dy - h) {
					x--;
					y++;
				}
			}
			if (dy < h) {	//top half of hexes
				//System.out.println("" + (t- dx)*r/t +  " " + (dy - r));
				if ((t - dx)*r/t > dy - r) {
					x--;
				}
			}
		}
		p.x=x;
		p.y=y;
		return p;
	}
}




