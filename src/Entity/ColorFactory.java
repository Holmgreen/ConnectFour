package Entity;
import java.awt.Color;

/**
 * Contains different colours used, or not used, in the program.
 */
public class ColorFactory {
	public final static Color BOARD_BORDER = Color.decode("#51BBFE");
	public final static Color BOARD_BACKGROUND = Color.WHITE;
	public final static Color[] PLAYER_COINS = {
		Color.decode("#F4E76E"),
		Color.decode("#F87666"),
		Color.decode("#95A78D"),
		Color.decode("#E6EBE0"),
		Color.decode("#071108"),
		Color.decode("#364652"),
		Color.decode("#C2F9BB"),
		Color.decode("#CC3363")
	};
	public static final Color REGULAR = Color.decode("#3C3C3B");
	public static final Color PANEL_BACKGROUND = BOARD_BACKGROUND;
	public static final Color BUTTON_REGULAR = REGULAR;
	public static final Color BUTTON_BACKGROUND = REGULAR;
	public static final Color BUTTON_BORDER = Color.decode("#F7FFF7");
}
