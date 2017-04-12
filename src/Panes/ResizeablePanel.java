package Panes;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import Entity.ColorFactory;

/**
 * Panel which is able to maintain a constant size in relation to the frame
 * size.
 */
public abstract class ResizeablePanel extends JPanel {
	protected static final long serialVersionUID = 1L;

	protected ArrayList<JComponent> components; // The components of this panel
	protected int widthFraction; // Fraction of panel width
	protected int heightFraction; // Fraction of panel height
	protected int numComponents;
	protected Dimension frameDim; // Dimension of the GUI
	protected Dimension componentDim; // Dimension of this panel
	protected JFrame frame;

	// Font configurations
	protected static final int[] fontConfigs = { 10000, 20000, 30000, 60000 }; // Determines
																				// font
																				// size
																				// relative
																				// to
																				// frame
																				// size.
	protected static final int HEADER = 0;
	protected static final int BIG = 1;
	protected static final int MEDIUM = 2;
	protected static final int SMALL = 3;
	protected int fontSize; // Dynamic font size
	protected int fontConfig;
	protected int minFontSize = 12; // The font size can not get lower than this

	public ResizeablePanel(int numComponents, int fontConfig, JFrame frame) {
		super();
		this.frame = frame;
		this.fontConfig = fontConfig;
		this.numComponents = numComponents;
		defineFractions();

		frameDim = frame.getSize();
		components = new ArrayList<JComponent>();
		componentDim = getComponentDim();
		setBackground(ColorFactory.PANEL_BACKGROUND);
	}

	// Subclasses adds components onto this panel
	protected abstract void addComponents();

	protected void defineFractions() {
		widthFraction = numComponents + 2;
		heightFraction = numComponents + 3;
	}

	protected Dimension getComponentDim() {
		int w = (int) frameDim.getWidth() / widthFraction;
		int h = (int) frameDim.getHeight() / heightFraction;
		return new Dimension(w, h);
	}

	/**
	 * Updates the component dimension in case of window resizing.
	 */
	protected void updateComponentDim() {
		int w = (int) frameDim.getWidth() / widthFraction;
		int h = (int) frameDim.getHeight() / heightFraction;
		componentDim.setSize(w, h);
	}

	protected void updateFrameDim() {
		frameDim = frame.getSize();
	}

	/**
	 * Update the fonts of components that contain text to correspond to the new
	 * fontSize.
	 */
	protected void updateFonts() {
		for (int i = 0; i < components.size(); i++) {
			JComponent comp = components.get(i);
			if (comp instanceof JButton) {
				JButton btn = (JButton) comp;
				Font btnFont = btn.getFont();
				btn.setFont(new Font(btnFont.getName(), btnFont.getStyle(),
						fontSize));
			} else if (comp instanceof JLabel) {
				JLabel label = (JLabel) comp;
				Font labelFont = label.getFont();
				label.setFont(new Font(labelFont.getName(), labelFont
						.getStyle(), fontSize));
			} else if (comp instanceof JTextField) {
				JTextField textField = (JTextField) comp;
				Font labelFont = textField.getFont();
				textField.setFont(new Font(labelFont.getName(), labelFont
						.getStyle(), fontSize));
			}
		}
	}

	/**
	 * Sets the foreground of buttons and labels
	 */
	protected void setDefaultForeground() {
		for (int i = 0; i < components.size(); i++) {
			JComponent comp = components.get(i);
			if (comp instanceof JLabel) {
				JLabel label = (JLabel) comp;
				label.setForeground(ColorFactory.REGULAR);
			} else if (comp instanceof JButton) {
				JButton btn = (JButton) comp;
				btn.setForeground(ColorFactory.BUTTON_REGULAR);
				btn.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
				btn.setFocusable(false);
			}
		}
	}

	/**
	 * Update the font sizes in case of window resizing.
	 */
	protected void updateFontSize() {
		fontSize = (int) (frameDim.getWidth() * frameDim.getHeight() / fontConfigs[fontConfig]);
		if (fontSize < minFontSize)
			fontSize = minFontSize;
	}

	/**
	 * Update all graphics in this panel.
	 */
	public void updateSize() {
		updateFrameDim();
		updateComponentDim();
		for (int i = 0; i < components.size(); i++) {
			components.get(i).setSize(componentDim);
		}
		updateFontSize();
		updateFonts();
	}

	/**
	 * Returns an ArrayList of all components that are instances of JLabel.
	 */
	public ArrayList<JLabel> getLabels() {
		ArrayList<JLabel> labels = new ArrayList<JLabel>();
		for (int i = 0; i < components.size(); i++) {
			JComponent comp = components.get(i);
			if (comp instanceof JLabel)
				labels.add((JLabel) comp);
		}
		return labels;
	}
}
