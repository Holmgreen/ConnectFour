package Panes;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import Database.Database;
import Handlers.PaneChangeHandler;

/**
 * This panel shows the highscores from players who have won the most games.
 * This information is collected from the database. The panel has a header, a
 * list of players with the most wins, and a Back button.
 * 
 */
public class HighscorePane extends BasicPane {
	private static final long serialVersionUID = 1L;
	
	private Database db;
	
	// The highscore list containing the player and his/her wins.
	private ArrayList<String> playerWins;
	
	// List no more than this number of players
	private final int MAX_LISTED_PLAYERS = 5;

	private final int NUM_PANELS = 3;
	private final int TOP = 0;
	private final int CENTER = 1;
	private final int BOTTOM = 2;

	public HighscorePane(GUI gui) {
		super(gui);
		db = gui.getGameManager().getDatabase();
		panels = new ResizeablePanel[NUM_PANELS];
		addTopPanel();
		addCenterPanel();
		createBottomPanel();
		setDefaultForeground();
	}

	public void addTopPanel() {
		// Header
		ArrayList<String> textFields = new ArrayList<String>();
		textFields.add("Highscores - Top " + MAX_LISTED_PLAYERS);
		panels[TOP] = new ResizeableTextLabelPanel(textFields,
				ResizeablePanel.HEADER, gui);
		add(panels[TOP], BorderLayout.NORTH);
	}

	public void addCenterPanel() {
		// Request highscore list from database
		playerWins = db.getHighscores();
		
		if (playerWins.size() == 0) {
			playerWins = new ArrayList<String>();
			playerWins.add("No player has won any games yet.");
		} else {
			if (playerWins.size() > MAX_LISTED_PLAYERS) {
				// Remove the last player names from the list
				for (int i = playerWins.size() - 1; i >= MAX_LISTED_PLAYERS; i--) {
					playerWins.remove(i);
				}
			}
		}

		// Add the highscore list to the panel
		panels[CENTER] = new ResizeableTextLabelPanel(playerWins,
				ResizeablePanel.MEDIUM, gui);

		add(panels[CENTER], BorderLayout.CENTER);
	}

	public void createBottomPanel() {
		// Back button that changes the pane back to the main menu on click
		HashMap<String, ActionListener> btnMap = new HashMap<String, ActionListener>();
		btnMap.put("Back", new PaneChangeHandler(gui, GUI.MAIN_MENU));
		panels[BOTTOM] = new ResizeableButtonPanel(btnMap,
				ResizeablePanel.SMALL, gui);

		add(panels[BOTTOM], BorderLayout.SOUTH);
	}

}
