package Panes;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Resizeable panel which contains several buttons and an input panel.
 *
 */
public class ResizeableSeveralButtonsAndInputPanel extends ResizeablePanel {

	private static final long serialVersionUID = 1L;
	private JTextField[] fields;
	private ArrayList<JButton[]> buttons;
	private int numFields;
	private int numButtons;

	public ResizeableSeveralButtonsAndInputPanel(JTextField[] fields,
			ArrayList<JButton[]> buttons, int fontConfig, JFrame frame) {
		super(fields.length, fontConfig, frame);
		this.fields = fields;
		this.buttons = buttons;
		numFields = fields.length;
		numButtons = buttons.size();
		updateFontSize();
		addComponents();
	}

	protected void addComponents() {
		// Add buttons in front of text fields
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(numButtons, 1));
		for (int j = 0; j < numFields; j++) {
			for (int i = 0; i < numButtons; i++) {
				JButton btn = buttons.get(i)[j];
				leftPanel.add(btn);
				components.add(btn);
			}
		}

		// Add text fields
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(numButtons, 1));
		for (int i = 0; i < numFields; i++) {
			rightPanel.add(fields[i]);
			components.add(fields[i]);
		}

		add(leftPanel);
		add(rightPanel);
		updateFonts();
	}

}
