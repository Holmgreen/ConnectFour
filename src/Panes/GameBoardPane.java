package Panes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;

import Game.GameManager;
import Formatters.TextLabelFormatter;

/**
 * The pane containing the game board, info messages, header, buttons and
 * coloured player list.
 */
public class GameBoardPane extends BasicPane {
	private static final long serialVersionUID = 1L;

	private final int NUM_PANELS = 5;
	private final int TOP = 0;
	private final int EAST = 1;
	private final int WEST = 2;
	private final int CENTER = 3;
	private final int BOTTOM = 4;
	public final static int LABEL_MARGIN = 20; // The margin to the center label
												// of the east and west labels

	private GamePanel gamePanel; // The panel containing the actual game and its
									// running loop
	private GameManager gm;
	private ArrayList<JLabel> playerList;
	private ArrayList<JLabel> westLabels;

	public GameBoardPane(GUI gui, GameManager gm) {
		super(gui);
		this.gm = gm;
		panels = new ResizeablePanel[NUM_PANELS];

		// Add panes
		addBottomPane();
		addEastPane();
		addWestPane();
		addTopPane();
		addGamePane();
		setDefaultForeground();
	}

	public void addBottomPane() {
		// Buttons
		HashMap<String, ActionListener> btnMap = new HashMap<String, ActionListener>();
		btnMap.put("Quit", new QuitHandler());
		btnMap.put("Restart", new RestartHandler());
		panels[BOTTOM] = new ResizeableButtonPanel(btnMap,
				ResizeablePanel.SMALL, gui);
		add(panels[BOTTOM], BorderLayout.SOUTH);
	}

	public void addEastPane() {
		// Coloured player list
		ArrayList<String> textFields = new ArrayList<String>();
		textFields.add(TextLabelFormatter.getFixedString("Players:",
				GameBoardPane.LABEL_MARGIN));
		int numPlayers = gm.getNumPlayers();
		for (int i = 0; i < numPlayers; i++) {
			textFields.add("");
		}
		panels[EAST] = new ResizeableTextLabelPanel(textFields,
				ResizeablePanel.MEDIUM, gui);
		add(panels[EAST], BorderLayout.EAST);
		playerList = panels[EAST].getLabels();
	}

	public void addWestPane() {
		// Info messages to the user
		ArrayList<String> textFields = new ArrayList<String>();
		textFields.add(TextLabelFormatter.getFixedString("To move:",
				GameBoardPane.LABEL_MARGIN));
		textFields.add(TextLabelFormatter.getFixedString("-",
				GameBoardPane.LABEL_MARGIN));
		panels[WEST] = new ResizeableTextLabelPanel(textFields,
				ResizeablePanel.MEDIUM, gui);
		add(panels[WEST], BorderLayout.WEST);
		westLabels = panels[WEST].getLabels();
	}

	public void addTopPane() {
		// Header
		ArrayList<String> textFields = new ArrayList<String>();
		textFields.add("Connect Four");
		panels[TOP] = new ResizeableTextLabelPanel(textFields,
				ResizeablePanel.HEADER, gui);
		add(panels[TOP], BorderLayout.NORTH);
	}

	public void addGamePane() {
		// The game panel
		gamePanel = new GamePanel(gui, gm, playerList, westLabels);
		panels[CENTER] = gamePanel;
		add(panels[CENTER], BorderLayout.CENTER);
	}

	/**
	 * Handles button clicks on the Quit button. Changes the pane through the
	 * GUI and pauses the game loop.
	 *
	 */
	class QuitHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gamePanel.setRunnable(false);
			gui.setPane(GUI.PLAYER_SELECTION);
		}
	}

	/**
	 * Handles button clicks on the Restart button. Restarts the game by
	 * clearing the board and reseting parameters.
	 *
	 */
	class RestartHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gamePanel.init();
		}

	}

}