package Panes;

import javax.swing.*;

import Entity.ColorFactory;

import java.awt.*;

/**
 * Resizeable panel containing input text fields and text fields in front.
 * @author Holmgren
 *
 */
public class ResizeableInputPanel extends ResizeablePanel {

    private static final long serialVersionUID = 1;
    private JTextField[] fields; // Text fields for user input
    private String[] texts; // Text label in front
    
    public ResizeableInputPanel(JTextField[] fields, String[] texts, int fontConfig, JFrame frame) {
    	super(texts.length, fontConfig, frame);
    	this.fields = fields;
    	this.texts = texts;
    	updateFontSize();
        addComponents();
    }

	protected void addComponents() {
		// Add texts to the left of the text fields
		JPanel left = new JPanel();
        left.setLayout(new GridLayout(texts.length, 1));
        for (int i = 0; i < texts.length; i++) {
            JLabel label = new JLabel(texts[i] + "      ", JLabel.RIGHT);
            left.add(label);
            label.setFont(new Font("Comic Sans MS", label.getFont().getStyle(), fontSize));
            components.add(label);
        }
        
        left.setBackground(ColorFactory.PANEL_BACKGROUND);

        // Add text fields to the right
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(fields.length, 1));
        for (int i = 0; i < fields.length; i++) {
            right.add(fields[i]);
            components.add(fields[i]);
        }

        add(left);
        add(right);
        updateFonts();
	}
}
