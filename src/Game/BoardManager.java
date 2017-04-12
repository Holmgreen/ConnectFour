package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;

import Database.Database;
import Entity.AIPlayer;
import Entity.Coin;
import Entity.ColorFactory;
import Entity.Player;
import Formatters.TextLabelFormatter;
import Panes.GameBoardPane;

/**
 * Manages the logic of the game. Handles input and controls the dynamic panes
 * of the game.
 *
 */
public class BoardManager {
	private ArrayList<String[]> log; // Each String[] element contains String
										// playerName, String turn, String row,
										// String col of a move
	private int[][] board; // Matrix representation of the board negative for
							// empty slot

	private ArrayList<Player> players; // Players active in the game
	private int rows;
	private int cols;
	private int turn; // Number of moves that has been made during a session
	private int numPlayers;
	private int currentPlayer; // The index of the player to make a move

	private int state; // Current state of the game, positive values correspond
						// to a player index
	public final static int DRAW = -2;
	public final static int CONTINUE = -1;

	private int boardWidth; // Relative to the screen
	private int boardHeight; // Relative to the screen

	private boolean allowAction = true; // Allow player action
	private boolean resized = false; // Controls if the graphical contents should be resized

	private Area boardArea; // Area object that represents the board
	private Rectangle background; // Rectangle which is used to create the area
									// behind the board
	private Coin[][] coinContainers; // Containers for the coins
	private ArrayList<JLabel> infoLabels; // Dynamic info labels which take the
											// form of messages to the user
	private ArrayList<JLabel> playerList; // List of players
	private Database db;

	public BoardManager(Database db, int rows, int cols, ArrayList<Player> players, ArrayList<JLabel> infoLabels, ArrayList<JLabel> playerList) {
		this.db = db;
		this.rows = rows;
		this.cols = cols;
		this.players = players;
		this.infoLabels = infoLabels;
		this.playerList = playerList;
		init();
	}

	private void init(){
		resized = false;
		numPlayers = players.size();
		coinContainers = new Coin[rows][cols];
		
		// Create the board matrix
		board = new int[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				board[i][j] = -1; // All initially unclaimed
			}
		}
		
		log = new ArrayList<String[]>();
		state = CONTINUE;
		currentPlayer = 0;
		turn = 0;
		allowAction = true;
		
		// Create coin objects for each position on the board
		coinContainers = new Coin[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				coinContainers[i][j] = new Coin();
			}
		}
		fillPlayerList(playerList);
		createBackground();
		createBoardArea();
	}
	
	/**
	 * Clear all parameters and objects and restart the game.
	 */
	public void restart() {
		if (players.get(currentPlayer) instanceof AIPlayer) {
			Point aiMove = ((AIPlayer) players.get(currentPlayer)).getNextMove(
					board, numPlayers, turn, currentPlayer);
			move((int) aiMove.getX(), (int) aiMove.getY());
		}
		
	}
	
	/**
	 * Is called once the state of the game might have changed. Writes to
	 * database if the game is over, otherwise continue the game and check if
	 * the next player is an AI.
	 */
	public void update() {
		if(allowAction == false){
			return;
		}
		if(resized){
			// Recreate the background and board graphics
			createBackground();
			createBoardArea();
			resized = false;
		}
		if (state == DRAW) {
			// Draw
			showMessage(DRAW);
			allowAction = false;
			writeToDatabase();
		} else if (state >= 0) {
			// Player with index state won
			showMessage(state);
			allowAction = false;
			writeToDatabase();
		} else {
			// Game continues
			showMessage(CONTINUE);
			if (players.get(currentPlayer) instanceof AIPlayer) {
				Point aiMove = ((AIPlayer) players.get(currentPlayer))
						.getNextMove(board, numPlayers, turn, currentPlayer);
				move((int) aiMove.getX(), (int) aiMove.getY());
			}
		}
	}
	
	/**
	 * Draw the board on the 2D Graphics object g
	 * 
	 * @param g
	 *            Graphics object to be drawn on
	 * @param boardWidth
	 *            the new board width after resize
	 * @param boardHeight
	 *            the new board height after resize
	 */
	public void drawBoard(Graphics2D g, int boardWidth, int boardHeight) {
		if (boardArea == null || boardWidth != this.boardWidth) {
			updateSize(boardWidth, boardHeight);
		}

		g.setColor(ColorFactory.BOARD_BACKGROUND);
		g.fill(background);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int index = board[i][j];
				if (index >= 0) {
					g.setColor(ColorFactory.PLAYER_COINS[index]);
					coinContainers[i][j].draw(g);
				}
			}
		}

		g.setColor(ColorFactory.BOARD_BORDER);
		g.fill(boardArea);
	}

	public void createBackground() {
		background = new Rectangle(boardWidth, boardHeight);
	}

	/**
	 * Creates the graphical representation of the board and the containers for
	 * the coins.
	 */
	public void createBoardArea() {
		Rectangle rect = new Rectangle(boardWidth, boardHeight);
		boardArea = new Area(rect);
		int containerWidth = boardWidth / cols;
		int containerHeight = boardHeight / rows;
		int margin = 2;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Coin newContainer = coinContainers[i][j];
				newContainer.setTarget(i * containerHeight);
				newContainer.updateFrame(j * containerWidth, i * containerHeight,
						containerWidth - margin, containerHeight - margin);
				Area circleArea = new Area(newContainer);
				boardArea.subtract(circleArea);
				coinContainers[i][j] = newContainer;
			}
		}

	}

	/**
	 * Checks if row, col is outside the board matrix
	 */
	private boolean isOutside(int row, int col) {
		if (row >= rows || row < 0 || col >= cols || col < 0)
			return true;
		return false;
	}

	/**
	 * Takes a point from the game panel and evaluates whether the move is
	 * legitimate or not.
	 * 
	 * @param screenX
	 *            the x-coordinate of the point on the panel
	 * @param screenY
	 *            the y-coordinate of the point on the panel
	 */
	public boolean requestMove(int screenX, int screenY) {
		if (!allowAction)
			return false;

		Point p = getBoardPoint(screenX, screenY); // Converts the point from
													// the panel to a row and
													// column on the board
		if (p == null)
			return false;

		int row = (int) p.getX();
		int col = (int) p.getY();
		if (isOutside(row, col) || board[row][col] != -1) {
			return false; // Outside the board or place already taken
		}

		if (row + 1 >= rows) {
			// First row is free
			move(row, col);
			return true;
		} else if (board[row + 1][col] > -1) {
			// Supporting coin below
			move(row, col);
			return true;
		}

		return false;
	}

	/**
	 * Converts and returns a row and column from a point on the screen if it
	 * was contained in one of the coin containers
	 */
	private Point getBoardPoint(int x, int y) {
		int potCol = (int) (x / (boardWidth / cols)); // Potential column
		int potRow = (int) (y / (boardHeight / rows)); // Potential row
		if (isOutside(potRow, potCol))
			return null;
		if (coinContainers[potRow][potCol].contains(new Point(x, y))) {
			return new Point(potRow, potCol);
		} else
			return null;
	}

	/**
	 * Makes a move on the board the board. This method should only be called
	 * once it is certain that row, col is a legitimate point for a coin to be
	 * placed in the existing board matrix.
	 */
	private void move(int row, int col) {
		board[row][col] = currentPlayer;
		turn++;
		String[] moveTexts = { players.get(currentPlayer).toString(),
				"" + turn, "" + row, "" + col };
		log.add(moveTexts);

		if (hasConsecutiveNeighbours(row, col, currentPlayer,
				GameManager.WIN_NEIGHBOURS - 1)) {
			state = currentPlayer; // This was the winning move
		} else if (turn >= rows * cols) {
			state = DRAW; // No more available moves
		}
		if (currentPlayer + 1 >= numPlayers) {
			currentPlayer = 0;
		} else {
			currentPlayer++;
		}

		showMessage(CONTINUE);
		coinContainers[row][col].startFall();
	}

	/**
	 * Returns true if a point on the board has a certain number of neighbours
	 * of the same type (color)
	 * 
	 * @param row
	 * @param col
	 * @param type
	 *            the index to be investigated
	 * @param amount
	 *            the number of neighbours required for returning true
	 */
	private boolean hasConsecutiveNeighbours(int row, int col, int type,
			int amount) {
		// Check all directions for neighbours of index type
		if (numNeighbours(row, col, 0, 1, type) + numNeighbours(row, col, 0, -1, type) >= amount)
			return true;
		if (numNeighbours(row, col, 1, 1, type) + numNeighbours(row, col, -1, -1, type)>= amount)
			return true;
		if (numNeighbours(row, col, 1, 0, type) + numNeighbours(row, col, -1, 0, type) >= amount)
			return true;
		if (numNeighbours(row, col, 1, -1, type) + numNeighbours(row, col, -1, 1, type) >= amount)
			return true;
		return false;
	}

	/**
	 * Returns the number of consecutive neighbours of the same type in a
	 * certain direction
	 * 
	 * @param row
	 * @param col
	 * @param dirrow
	 *            the x-direction to look for neighbours
	 * @param dircol
	 *            the y-direction to look for neighbours
	 * @param type
	 *            the index to be investigated
	 */
	private int numNeighbours(int row, int col, int dirrow, int dircol, int type) {
		int newrow = row + dirrow;
		int newcol = col + dircol;
		if (isOutside(newrow, newcol))
			return 0;
		if (board[newrow][newcol] == type) {
			return (1 + numNeighbours(newrow, newcol, dirrow, dircol, type));
		} else {
			return 0;
		}
	}

	public int getGameState() {
		return state;
	}

	/**
	 * Writes the log from the game to the database.
	 */
	private void writeToDatabase() {
		String[] playerNames = new String[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			playerNames[i] = players.get(i).toString();
			db.insertNewPlayer(playerNames[i]);
		}
		db.insertGameLog(log, playerNames, state);
	}

	/**
	 * Updates the graphical size of the board. This function is needed to keep
	 * the painted board to be of the same relative size if the window is
	 * resized.
	 * 
	 * @param boardWidth
	 *            the new board width after resize
	 * @param boardHeight
	 *            the new board height after resize
	 */
	public void updateSize(int boardWidth, int boardHeight) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		createBackground();
		createBoardArea();
	}

	public void setAllowAction(boolean b) {
		allowAction = b;
	}

	public Player getPlayer(int state) {
		return players.get(state);
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public Player getCurrentPlayer() {
		return players.get(currentPlayer);
	}

	/**
	 * Shows a message which corresponds to a certain state in the game on the
	 * info label
	 * 
	 * @param gameState
	 *            the current state of the game
	 */
	public void showMessage(int gameState) {
		if (gameState == DRAW) {
			setMessageText("Draw!", "Play Again?", ColorFactory.REGULAR);
		} else if (gameState == CONTINUE) {
			setMessageText("Next move:", players.get(currentPlayer).toString(),
					ColorFactory.PLAYER_COINS[currentPlayer]);
		} else {
			setMessageText(players.get(state).toString() + " won!",
					"Play Again?", ColorFactory.REGULAR);
		}
	}

	private void setMessageText(String header, String info, Color color) {
		infoLabels.get(0).setText(
				TextLabelFormatter.getFixedString(header,
						GameBoardPane.LABEL_MARGIN));
		infoLabels.get(1).setText(
				TextLabelFormatter.getFixedString(info,
						GameBoardPane.LABEL_MARGIN));
		if (color == null) {
			// Keep color
		} else {
			JLabel label = infoLabels.get(1);
			label.setForeground(color);
		}
	}

	public void setInfoLabel(ArrayList<JLabel> infoLabels) {
		this.infoLabels = infoLabels;
	}

	/**
	 * Fill the graphical list of active players in the session
	 * 
	 * @param playerList
	 */
	public void fillPlayerList(ArrayList<JLabel> playerList) {
		((JLabel) playerList.get(0)).setForeground(ColorFactory.REGULAR);
		for (int i = 0; i < numPlayers; i++) {
			JComponent comp = playerList.get(i + 1);
			if (comp instanceof JLabel) {
				JLabel label = (JLabel) comp;
				label.setForeground(ColorFactory.PLAYER_COINS[i]);
				label.setText(TextLabelFormatter.getFixedString(players.get(i)
						.toString(), GameBoardPane.LABEL_MARGIN));
			}
		}
	}
}
