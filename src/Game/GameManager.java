package Game;

import java.util.ArrayList;

import javax.swing.JLabel;

import Database.Database;
import Entity.Player;
import Panes.GUI;

/**
 * This class contains the key objects of the system.
 */
public class GameManager {
	private Database db;
	private BoardManager boardManager; // The board manager that handles game logic
	public static final int MAX_PLAYERS = 4; // Max number of players that can play the game
	public static final int WIN_NEIGHBOURS = 4; // Number of coins in a row needed to win
	
	// Board grid of the game
	private int rows;
	private int cols;
	
	// List of players in the game
	private ArrayList<Player> players;
	
	private ArrayList<JLabel> boardInfoLabels;
	private ArrayList<JLabel> boardPlayerList;
	
	public GameManager() {
		db = new Database();
		db.openConnection("4inrow.db");
		new GUI(this, "Connect Four");
	}
	
	public Database getDatabase() {
		return db;
	}
	
	public BoardManager getBoardManager(){
		return boardManager;
	}
	
	/**
	 * Set the board grid.
	 */
	public void setDimensions(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
	}

	/**
	 * Set the player list
	 */
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public int getNumPlayers(){
		if(players == null) return 0;
		return players.size();
	}
	
	/**
	 * Create the boardManager object
	 */
	public void initiateBoardManager(){
		boardManager = new BoardManager(db, rows, cols, players, boardInfoLabels, boardPlayerList);
	}

	public void setInfoLabel(ArrayList<JLabel> westLabels) {
		boardInfoLabels = westLabels;
	}

	public void setPlayerList(ArrayList<JLabel> playerList) {
		boardPlayerList = playerList;
	}

}
