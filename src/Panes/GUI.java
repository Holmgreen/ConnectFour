package Panes;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Entity.Player;
import Game.GameManager;

/**
 * The graphical user interface of the system. Handles the switching and initialization between panes.
 *
 */
public class GUI extends JFrame implements ComponentListener{
	
	private static final long serialVersionUID = 1L;
	private BasicPane[] panes; // The array of panes that this GUI handles
	private int currentPane; // The currently shown pane
	private int previousPane; // The previously shown pane
	private GameManager gm; 
	
	public static final int NUM_PANES = 5;
	public static final int MAIN_MENU = 0;
	public static final int GRID_SELECTION = 1;
	public static final int PLAYER_SELECTION = 2;
	public static final int GAME_BOARD = 3;
	public static final int HIGHSCORE = 4;
	
	public GUI(GameManager gm, String text){
		super(text);
		this.gm = gm;
		
		// Starting dimensions
		int startWidth = 360*3; 
		int startHeight = 240*3;
		
		panes = new BasicPane[NUM_PANES];
		
		// Start at the main menu
		currentPane = MAIN_MENU;
		previousPane = -1;
		loadPane(currentPane);
		
		setLocationByPlatform(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		setSize(startWidth,startHeight);
		addComponentListener(this);
	}
	
	/**
	 * Loads a new pane and updates the current content of the GUI.
	 */
	private void loadPane(int pane) {
		if (pane == MAIN_MENU){
			updateContent(new MainMenuPane(this));
		} else if (pane == GRID_SELECTION) {
			updateContent(new GridSelectionPane(this));
		} else if (pane == PLAYER_SELECTION) {
			updateContent(new PlayerSelectionPane(this));
		} else if (pane == GAME_BOARD) {
			updateContent(new GameBoardPane(this, gm));
		} else if (pane == HIGHSCORE) {
			updateContent(new HighscorePane(this));
		}
	}
	
	public void prepareBoardDimension(int rows, int cols){
		gm.setDimensions(rows, cols);
	}
	
	public void prepareBoardPlayers(ArrayList<Player> players){
		gm.setPlayers(players);
	}
	
	private void updateContent(BasicPane newPane){
		getContentPane().removeAll();
		panes[currentPane] = newPane;
		setContentPane(panes[currentPane]);
	}
	
	/**
	 * Sets the content of the GUI to the specified pane. 
	 */
	public void setPane(int pane) {
		previousPane = currentPane;
		currentPane = pane;
		loadPane(currentPane);
		resetWindow();
	}

	private void resetWindow(){
		setVisible(true);
	}
	
	public int getPreviousState() {
		return previousPane;
	}

	public JPanel getPreviousPane() {
		return panes[previousPane];
	}

	public JPanel getCurrentPane() {
		return panes[currentPane];
	}

	public GameManager getGameManager(){
		return gm;
	}
	
	public void componentHidden(ComponentEvent e) {}

	public void componentMoved(ComponentEvent e) {}

	public void componentResized(ComponentEvent e) {
		panes[currentPane].updateSize();
		setVisible(true);
	}

	public void componentShown(ComponentEvent e) {}
}
