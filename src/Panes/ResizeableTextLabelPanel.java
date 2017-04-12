package Panes;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Resizeable panel that contains labels with text.
 *
 */
public class ResizeableTextLabelPanel extends ResizeablePanel{
	
	private static final long serialVersionUID = 1L;
	private ArrayList<String> textFields;
	
	public ResizeableTextLabelPanel(ArrayList<String> textFields, int fontConfig, JFrame frame) {
		super(textFields.size(), fontConfig, frame);
		this.textFields = textFields;
		updateFontSize();
		addComponents();
	}

	protected void addComponents() {
		GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout());
        c.gridx = 0;
		for(int i = 0; i < textFields.size(); i++){
			c.gridy = i;
			String text = textFields.get(i);
			JLabel newLabel = new JLabel();
			newLabel.setText(text);
			newLabel.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
			components.add(newLabel);
			add(newLabel, c);
		}
		updateFonts();
	}

}
