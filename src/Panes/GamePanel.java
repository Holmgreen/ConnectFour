package Panes;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;

import Game.BoardManager;
import Game.GameManager;

/**
 * Panel containing the game board and all the graphics connected to it. It
 * communicates with the board manager.
 *
 */
public class GamePanel extends ResizeablePanel implements Runnable,
		MouseListener {

	private static final long serialVersionUID = 1L;

	// Game thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;

	private BoardManager boardManager;
	private GameManager gm;

	// Image to draw all graphics on
	private BufferedImage image;
	private Graphics2D g;

	// Sizes of the game panel
	private int gameWidth;
	private int gameHeight;


	private GUI gui;

	public GamePanel(GUI gui, GameManager gm, ArrayList<JLabel> playerList,
			ArrayList<JLabel> westLabels) {
		super(0, ResizeablePanel.MEDIUM, gui);
		gm.setInfoLabel(westLabels);
		gm.setPlayerList(playerList);
		this.gui = gui;
		this.gm = gm;
	}

	/**
	 * Start the thread. Called upon creation.
	 */
	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			addMouseListener(this);
			thread.start();
		}
	}

	/**
	 * Initiate all parameters to their starting state.
	 */
	public void init() {
		if (gameWidth <= 0 || gameHeight <= 0) {
			// Panel does not know its own size before it is filled. Initiate with arbitrary dimension.
			gameWidth = gui.getWidth();
			gameHeight = gui.getHeight();
		} else {
			gameWidth = getWidth();
			gameHeight = getHeight();
		}
		image = new BufferedImage(gameWidth, gameHeight,
				BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		running = true;
		gm.initiateBoardManager();
		boardManager = gm.getBoardManager();
	}

	public void run() {
		init();
		long start;
		long elapsed;
		long wait;

		// Game loop
		while (running) {

			start = System.nanoTime();

			update();
			draw();
			drawToScreen();

			elapsed = System.nanoTime() - start;

			wait = targetTime - elapsed / 1000000;
			if (wait < 0)
				wait = 5;

			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Updates the size of the game panel if any resizing has taken place. This
	 * method is called by this class, whereas updateSize() is called elsewhere
	 * when the window is resized.
	 */
	private void update() {
		gameWidth = getWidth();
		gameHeight = getHeight();
		boardManager.update();
	}

	/**
	 * Draw the board.
	 */
	private void draw() {
		if (gameWidth > 0 && gameHeight > 0) {
			updateSize();
		}
		// Draw the board on the graphics object
		boardManager.drawBoard(g, gameWidth, gameHeight);
	}

	private void drawToScreen() {
		Graphics g2 = getGraphics();

		// Draw the image onto the screen
		if (image == null)
			System.out.println("null");
		g2.drawImage(image, 0, 0, gameWidth, gameHeight, null);
		g2.dispose();
	}

	protected void addComponents() {
		// No JComponents on the game panel
	}

	/**
	 * Update sizes and also create a new image that fits the new dimensions.
	 */
	public void updateSize() {
		gameWidth = getWidth();
		gameHeight = getHeight();
		image = new BufferedImage(gameWidth, gameHeight,
				BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		boardManager.updateSize(gameWidth, gameHeight);
	}

	/**
	 * Pause and continue the game loop.
	 */
	public void setRunnable(boolean running) {
		this.running = running;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void mouseClicked(MouseEvent me) {
	}

	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
	}

	/**
	 * Handles mouse clicks on release. Requests a move to the point on the
	 * board on which the user clicked through the board manager.
	 */
	public void mouseReleased(MouseEvent me) {
		boardManager.requestMove(me.getX(), me.getY());
	}
}