package Panes;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * The basic panel for the panels that are implemented in the GUI of this
 * system. It is possible to update size and set the default foreground.
 *
 */
public class BasicPane extends JPanel {
	private static final long serialVersionUID = 1L;
	protected ResizeablePanel[] panels; // Array of panels that are components of this panel
	protected GUI gui;

	public BasicPane(GUI gui) {
		super();
		this.gui = gui;
		setLayout(new BorderLayout());
	}

	public void updateSize() {
		for (int i = 0; i < panels.length; i++) {
			panels[i].updateSize();
		}
	}

	protected void setDefaultForeground() {
		for (int i = 0; i < panels.length; i++) {
			panels[i].setDefaultForeground();
		}
	}

}
