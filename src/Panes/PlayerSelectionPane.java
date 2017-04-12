package Panes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Entity.AIPlayer;
import Entity.Player;
import Handlers.PaneChangeHandler;
import Game.GameManager;

/**
 * This is a pane where the user chooses what players that will join the game.
 * The center pane consists of a 2x2 grid with a list of added players, info
 * message panel, text fields for entering player names and buttons for removing
 * and adding players.
 */
public class PlayerSelectionPane extends BasicPane {

	private static final long serialVersionUID = 1L;
	private final int NUM_PANELS = 3;
	private final int TOP = 0;
	private final int CENTER = 1;
	private final int BOTTOM = 2;

	private ArrayList<JLabel> addedPlayers; // The panel containing a list of
											// the currently added players
	private ArrayList<JLabel> infoFields; // The panel containing information
											// for the user

	private JTextField[] fields;
	private ArrayList<Player> players;

	public PlayerSelectionPane(GUI gui) {
		super(gui);
		panels = new ResizeablePanel[NUM_PANELS];
		players = new ArrayList<Player>();
		addHeader();
		addCenterPanel();
		addBottomPanel();
		setDefaultForeground();
	}

	private void addHeader() {
		ArrayList<String> textFields = new ArrayList<String>();
		textFields.add("Player Selection");
		panels[TOP] = new ResizeableTextLabelPanel(textFields,
				ResizeablePanel.HEADER, gui);
		add(panels[TOP], BorderLayout.NORTH);
	}

	private void addCenterPanel() {
		ArrayList<JButton[]> buttons = new ArrayList<JButton[]>();
		JButton[] removeButtons = new JButton[GameManager.MAX_PLAYERS];
		JButton[] addButtons = new JButton[GameManager.MAX_PLAYERS];
		JButton[] mediumButtons = new JButton[GameManager.MAX_PLAYERS];
		JButton[] hardButtons = new JButton[GameManager.MAX_PLAYERS];

		// Create the text fields
		fields = new JTextField[GameManager.MAX_PLAYERS];
		for (int i = 0; i < GameManager.MAX_PLAYERS; i++) {
			fields[i] = new JTextField();
			fields[i].setText("");
		}

		// Add all the buttons
		for (int i = 0; i < GameManager.MAX_PLAYERS; i++) {
			removeButtons[i] = new JButton("Remove");
			removeButtons[i].addActionListener(new RemoveHandler(i));
		}
		buttons.add(removeButtons);
		for (int i = 0; i < GameManager.MAX_PLAYERS; i++) {
			addButtons[i] = new JButton("Add Player");
			addButtons[i].addActionListener(new AddHandler(i));
		}
		buttons.add(addButtons);
		for (int i = 0; i < GameManager.MAX_PLAYERS; i++) {
			mediumButtons[i] = new JButton("Medium AI");
			mediumButtons[i].addActionListener(new AddAIHandler(i,
					AIPlayer.MEDIUM));
		}
		buttons.add(mediumButtons);
		for (int i = 0; i < GameManager.MAX_PLAYERS; i++) {
			hardButtons[i] = new JButton("Hard AI");
			hardButtons[i]
					.addActionListener(new AddAIHandler(i, AIPlayer.HARD));
		}
		buttons.add(hardButtons);

		panels[CENTER] = new ResizeableSeveralButtonsAndInputPanel(fields,
				buttons, ResizeablePanel.SMALL, gui);
		add(panels[CENTER], BorderLayout.CENTER); // Add the buttons and text
													// fields to the center
													// panel

		// Added player list
		addedPlayers = new ArrayList<JLabel>();
		ArrayList<String> initStrings = new ArrayList<String>();
		initStrings.add("Players Added:");
		for (int i = 1; i < GameManager.MAX_PLAYERS + 1; i++) {
			initStrings.add("-");
		}
		ResizeableTextLabelPanel playerListPanel = new ResizeableTextLabelPanel(
				initStrings, ResizeablePanel.SMALL, gui);
		addedPlayers = playerListPanel.getLabels();
		addedPlayers.remove(0); // Remove the header, which is not supposed to
								// be a dynamic text field
		panels[CENTER].setLayout(new GridLayout(2, 2));
		panels[CENTER].add(playerListPanel);

		// Info fields with user messages
		infoFields = new ArrayList<JLabel>();
		initStrings = new ArrayList<String>();
		initStrings.add("Add players to participate in the game!"); // Initial
																	// message
		ResizeableTextLabelPanel infoFieldPanel = new ResizeableTextLabelPanel(
				initStrings, ResizeablePanel.SMALL, gui);
		infoFields = infoFieldPanel.getLabels();
		panels[CENTER].add(infoFieldPanel);

	}

	private void addBottomPanel() {
		HashMap<String, ActionListener> btnMap = new HashMap<String, ActionListener>();
		btnMap.put("Start", new StartHandler());
		btnMap.put("Back", new PaneChangeHandler(gui, GUI.GRID_SELECTION));
		panels[BOTTOM] = new ResizeableButtonPanel(btnMap,
				ResizeablePanel.SMALL, gui);

		add(panels[BOTTOM], BorderLayout.SOUTH);
	}

	/**
	 * Show a message on the info label
	 * 
	 * @param text
	 *            the String to show
	 */
	private void showMessage(String text) {
		infoFields.get(0).setText(text);
	}

	class RemoveHandler implements ActionListener {
		private int fieldNbr;

		public RemoveHandler(int fieldNbr) {
			this.fieldNbr = fieldNbr;
		}

		public void actionPerformed(ActionEvent e) {
			if (!fields[fieldNbr].isEditable()) {
				for (int i = 0; i < players.size(); i++) {
					// Check if the player to be removed exists
					if (players.get(i).toString()
							.equals(fields[fieldNbr].getText())) {
						fields[fieldNbr].setText("");
						fields[fieldNbr].setEditable(true);
						addedPlayers.get(fieldNbr).setText("-");
						showMessage("Removed " + players.get(i).toString());
						players.remove(i);
					}
				}
			} else {
				showMessage("That slot has not yet been added");
			}
		}

	}

	/**
	 * Handles button clicks on the add button. Adds player if the name is unique among the other players.
	 *
	 */
	class AddHandler implements ActionListener {
		private int fieldNbr;

		public AddHandler(int fieldNbr) {
			this.fieldNbr = fieldNbr;
		}

		public void actionPerformed(ActionEvent e) {
			if (fields[fieldNbr].getText().equals("")) {
				showMessage("Name the players!");
				return;
			}
			if (fields[fieldNbr].isEditable()) {
				// Slot not locked
				String[] aiNames = AIPlayer.NAMES;
				for (int i = 0; i < players.size(); i++) {
					String pName = players.get(i).toString();
					if (pName.equals(fields[fieldNbr].getText())) {
						// pName already exist in another field
						showMessage("Player names needs to be unique");
						return;
					}
				}
				
				// Check that the given player name is not an AI name
				for(int j = 0; j < aiNames.length; j++){
					if(fields[fieldNbr].getText().contains(aiNames[j])){
						showMessage("Players can not have AI names.");
						return;
					}
				}
				players.add(new Player(fields[fieldNbr].getText()));
				fields[fieldNbr].setEditable(false);
				addedPlayers.get(fieldNbr).setText(fields[fieldNbr].getText());
				showMessage("Added " + fields[fieldNbr].getText());
			} else {
				showMessage("That slot has already been taken!");
			}
		}
	}

	/**
	 * Handles button clicks on the add AI buttons. Adds a unique AI player.
	 *
	 */
	class AddAIHandler implements ActionListener {
		private int fieldNbr;
		private int level;

		public AddAIHandler(int fieldNbr, int level) {
			this.fieldNbr = fieldNbr;
			this.level = level;
		}

		public void actionPerformed(ActionEvent e) {
			if (fields[fieldNbr].isEditable()) {
				AIPlayer ai = new AIPlayer(level, fieldNbr);
				players.add(ai);
				fields[fieldNbr].setText(ai.toString());
				fields[fieldNbr].setEditable(false);
				addedPlayers.get(fieldNbr).setText(ai.toString());
				showMessage("Added " + ai.toString());
			} else {
				showMessage("That slot has already been taken!");
			}
		}

	}

	/**
	 * Handles button clicks on the start button and start the game if at least
	 * on real players has been added.
	 *
	 */
	class StartHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!players.isEmpty()) {
				boolean anyRealPlayer = false;
				for (int i = 0; i < players.size(); i++) {
					if (!(players.get(i) instanceof AIPlayer)) {
						anyRealPlayer = true;
						break;
					}
				}
				if (anyRealPlayer) { // Needs to have at least one real player
										// for the game to make sense.
					gui.prepareBoardPlayers(players);
					gui.setPane(GUI.GAME_BOARD);
				} else {
					showMessage("AI wars not allowed for security reasons..");
				}
			}
		}

	}

}
