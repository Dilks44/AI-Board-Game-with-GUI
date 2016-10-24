# AI-Board-Game-with-GUI
I've written an AI to play this special type of game as well as the GUI to visualize what is going on!
Screenshot: http://zetapsiumd.com/images/HexGame.png

Recently I've included a .jar file so that anyone who wishes to see what this project does can just open that file!
If you would like to run this project from eclipse to get a better understanding of what is going on, navigate to file->import-> General -> Existing project into workspace -> Select Archive file -> navigate to HexGame.zip included in this repo. Main is located in ThreePlayerHexGame.class


--ORIGINAL POST TO CLASS WEBPAGE--
Hello everyone,

I just wanted to share a graphical representation of our game that I found online and then repurposed for our uses.
I’m sharing this with all of you because I spent quite a bit of time on it and I thought you all might enjoy looking at something better than ASCII art. Below are instructions on how to set it up. This shouldn’t take more than 5 minutes of your time.

First import java.awt.Color inside your Game.java Class

Then, add DrawingTest.java to the same package that your Game.class is in.

DrawingTest.java:
https://gist.github.com/Dilks44/683bd3590b3655a90350

Next, replace the printBoard method in Game.java with the one I wrote

public void printBoard() {
	DrawingTest newDrawing = new DrawingTest();
	for(int i=0;i<board.length;i++) {
		for(int j=0;j<board[i].length;j++) {
			if (board[i][j] == 0) {
				newDrawing.drawLocation(i,j, Color.RED);
			}
			else if (board[i][j] == 1) {
				newDrawing.drawLocation(i,j, Color.GREEN);
			}
			else if (board[i][j] == 2) {
				newDrawing.drawLocation(i,j, Color.BLUE);
			}
			}
			}
			}


At this point you should be able to see the before and after picture given that you have already replaced P2_LastName in Game.java’s main method.
If you dont have any code for P2_LastName yet, use the HumanPlayer.class instead.

And that’s it. Let me know if you have any questions or you find bugs.

Source: Here is the original article where I found the unmodified code
http://www.quarkphysics.ca/scripsi/hexgrid/
