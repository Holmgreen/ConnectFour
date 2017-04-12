package Panes;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * A resizeable panel containing buttons.
 *
 */
public class ResizeableButtonPanel extends ResizeablePanel {

	private static final long serialVersionUID = 1L;
	private HashMap<String, ActionListener> btnMap; // Button name corresponding
													// to its action listener

	public ResizeableButtonPanel(HashMap<String, ActionListener> btnMap,
			int fontConfig, JFrame frame) {
		super(btnMap.size(), fontConfig, frame);
		this.btnMap = btnMap;
		updateFontSize();
		addComponents();
	}

	protected void addComponents() {
		// Add the buttons of this panel
		Set<String> btnStrings = btnMap.keySet();
		Iterator<String> it = btnStrings.iterator();
		while (it.hasNext()) {
			String btnString = it.next();
			JButton btn = new JButton(btnString);
			btn.setPreferredSize(componentDim);
			components.add(btn);
			btn.addActionListener(btnMap.get(btnString));
			add(btn);
		}
		updateFonts();
	}
}
