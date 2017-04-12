package Panes;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import Handlers.PaneChangeHandler;

/**
 * The main menu pane which consists of buttons which direct to other panes. 
 *
 */
public class MainMenuPane extends BasicPane {
	private static final long serialVersionUID = 1L;
	private final int NUM_PANELS = 3;
	private final int TOP = 0;
	private final int CENTER = 1;
	private final int BOTTOM = 2;
	
	// The text fields in the center
	private ArrayList<String> textFields;

	public MainMenuPane(GUI gui) {
		super(gui);
		panels = new ResizeablePanel[NUM_PANELS];
		addHeader();
		addCenterPanel();
		addBottomPanel();
		setDefaultForeground();
	}

	private void addHeader(){
		// Header
		textFields = new ArrayList<String>();
		textFields.add("Main Menu");
		panels[TOP] = new ResizeableTextLabelPanel(textFields, ResizeablePanel.HEADER,  gui);
		add(panels[TOP], BorderLayout.NORTH);
	}
	
	private void addCenterPanel() {
		// Game name
		textFields = new ArrayList<String>();
		textFields.add("Connect");
		textFields.add("Four");
		panels[CENTER] = new ResizeableTextLabelPanel(textFields, ResizeablePanel.BIG, gui);
		add(panels[CENTER], BorderLayout.CENTER);
	}

	private void addBottomPanel() {
		// Play and Highscore buttons
		HashMap<String, ActionListener> btnMap = new HashMap<String, ActionListener>();
		btnMap.put("Play", new PaneChangeHandler(gui,
				GUI.GRID_SELECTION));
		btnMap.put("Highscores", new PaneChangeHandler(gui,
				GUI.HIGHSCORE));
		panels[BOTTOM] = new ResizeableButtonPanel(btnMap, ResizeablePanel.SMALL, gui);

		add(panels[BOTTOM], BorderLayout.SOUTH);
	}

}
