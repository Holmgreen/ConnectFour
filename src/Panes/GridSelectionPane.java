package Panes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextField;
import Handlers.PaneChangeHandler;

/**
 * Panel for selecting the board size, i.e. number of rows and columns for the
 * game grid. It contains two text fields and two text labels.
 */
public class GridSelectionPane extends BasicPane {
	private static final long serialVersionUID = 1L;
	private JTextField[] fields; // Contains the text fields

	// For visual reasons, restrict the grid size.
	private final int MIN_ROWS = 4;
	private final int MAX_ROWS = 30;
	private final int MIN_COLS = 4;
	private final int MAX_COLS = 30;

	private final int NUM_PANELS = 3;
	private final int TOP = 2;
	private final int CENTER = 1;
	private final int BOTTOM = 0;

	public GridSelectionPane(GUI gui) {
		super(gui);
		panels = new ResizeablePanel[NUM_PANELS];
		addTopPanel();
		addCenterPanel();
		addBottomPanel();
		setDefaultForeground();
	}

	public void addTopPanel() {
		// Header
		ArrayList<String> textFields = new ArrayList<String>();
		textFields.add("Grid Selection");
		panels[TOP] = new ResizeableTextLabelPanel(textFields,
				ResizeablePanel.HEADER, gui);
		add(panels[TOP], BorderLayout.NORTH);
	}

	public void addCenterPanel() {
		// Text fields and labels
		String[] texts = new String[2];
		texts[0] = "Rows (" + MIN_ROWS + "-" + MAX_ROWS + ")";
		texts[1] = "Columns (" + MIN_ROWS + "-" + MAX_ROWS + ")";
		fields = new JTextField[2];
		fields[0] = new JTextField(2);
		fields[1] = new JTextField(2);
		fields[0].setText("6");
		fields[1].setText("7");

		panels[CENTER] = new ResizeableInputPanel(fields, texts,
				ResizeablePanel.MEDIUM, gui);
		add(panels[CENTER], BorderLayout.CENTER);
	}

	public void addBottomPanel() {
		// Continue and Back buttons
		HashMap<String, ActionListener> btnMap = new HashMap<String, ActionListener>();
		btnMap.put("Back", new PaneChangeHandler(gui, GUI.MAIN_MENU));
		btnMap.put("Continue", new ContinueHandler());
		panels[BOTTOM] = new ResizeableButtonPanel(btnMap,
				ResizeablePanel.SMALL, gui);
		add(panels[BOTTOM], BorderLayout.SOUTH);
	}

	/**
	 * Handles button clicks on the Continue button. Prepares the board manager
	 * for start of the game and changes the pane through the GUI.
	 * 
	 */
	class ContinueHandler implements ActionListener {

		public ContinueHandler() {
		}

		public void actionPerformed(ActionEvent e) {
			try {
				int rows = Integer.parseInt(fields[0].getText());
				int cols = Integer.parseInt(fields[1].getText());

				if (rows >= MIN_ROWS && rows <= MAX_ROWS && cols >= MIN_COLS
						&& cols <= MAX_COLS) {
					gui.prepareBoardDimension(rows, cols);
					gui.setPane(GUI.PLAYER_SELECTION);
				} else {
					// Do nothing
				}
			} catch (NumberFormatException nfe) {
				// Do nothing
			}
		}

	}

}
