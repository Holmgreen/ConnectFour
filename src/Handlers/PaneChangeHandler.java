package Handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Panes.GUI;

/**
 * Action listener that uses the GUI to change the pane on notice.
 *
 */
public class PaneChangeHandler implements ActionListener{
	private GUI gui;
	private int targetPane;
	
	public PaneChangeHandler(GUI gui, int targetPane){
		this.targetPane = targetPane;
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent e) {
		gui.setPane(targetPane);
	}

}
