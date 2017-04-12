package Entity;

import java.awt.Point;
import java.util.Random;

/**
 * Player for which the moves are calculated and determined by the computer.
 */
public class AIPlayer extends Player {

	public static final String[] NAMES = { "Hard AI", "Medium AI", "Easy AI" }; // These
																				// names
																				// represent
																				// the
																				// AI
	public static final int HARD = 0;
	public static final int MEDIUM = 1;
	public static final int EASY = 2;

	private int level; // Defines the level of the algorithm that the AI uses to
						// make a move
	private int[][] board;
	private int rows;
	private int cols;
	private int index; // The index this AI has in relation to other players.

	public AIPlayer(int level, int nameAdd) {
		super(NAMES[level] + " " + (nameAdd + 1));
		this.level = level;
	}

	/**
	 * Returns a point containing a row and a column for where the next move
	 * should be made, depending on the level of the AI.
	 */
	public Point getNextMove(int[][] board, int numPlayers, int turn, int index) {
		this.index = index;
		if (level == HARD) {
			return getNextMoveHard(board, numPlayers);
		} else if (level == MEDIUM) {
			return getNextMoveMedium(board, numPlayers);
		} else {
			return getNextMoveEasy(board, numPlayers);
		}
	}

	private Point getNextMoveEasy(int[][] board, int numPlayers) {
		Random rand = new Random();
		rows = board.length;
		cols = board[0].length;
		int[] triedCols = new int[cols]; // 0 for not tried, 1 for tried.
		for (int j = 0; j < cols; j++) {
			triedCols[j] = 0;
		}

		for (int j = 0; j < cols; j++) {

			// Generate random column that has not already been tried
			int randCol = rand.nextInt(cols);
			while (triedCols[randCol] != 0) {
				randCol = rand.nextInt(cols);
			}

			// Find which row is eligible to make the move on
			for (int i = rows - 1; i >= 0; i--) {
				if (board[i][randCol] < 0) {
					return new Point(i, randCol);
				}
			}
			triedCols[randCol] = 1;
		}

		return null;
	}

	private Point getNextMoveMedium(int[][] board, int numPlayers) {
		return getNextMoveEasy(board, numPlayers);

	}

	private Point getNextMoveHard(int[][] board, int numPlayers) {
		this.board = board;
		rows = board.length;
		cols = board[0].length;
		int[] potNeighbours = new int[cols]; // Number of potential neighbours
												// if the column corresponding
												// to an index in this array is
												// chosen as a move
		for (int i = 0; i < cols; i++) {
			potNeighbours[i] = 0;
		}
		int bestCol = (int) cols / 2;
		int row = rows - 1; // Best move to start in middle
		int maxBlocks = 0;
		for (int i = 0; i < cols; i++) {
			for (int j = rows - 1; j >= 0; j--) {
				if (board[j][i] < 0) {
					potNeighbours[i] = numConsecutiveNeighbours(j, i, index);
					if (potNeighbours[i] >= 3) {
						return new Point(j, i);
					} else if (maxBlocks >= 3) {
						// The only move that is better than this is if the AI
						// can get 4 in a row, no need to check.
						break;
					}

					// Check how many potential blocks this move will create for
					// the opponents.
					int blocks = 0;
					for (int k = 0; k < numPlayers; k++) {
						int temp = 0;
						if (index != k) {
							temp = numConsecutiveNeighbours(j, i, k);
							if (temp > blocks)
								blocks = temp;
						}
					}

					// Sets this to the best move yet if blocks or potential own
					// neighbouring coins are higher than before. The algorithm
					// slightly favours having more neighbours itself.
					if (blocks > maxBlocks) {
						maxBlocks = blocks;
					}

					if (potNeighbours[i] > potNeighbours[bestCol]
							|| blocks > potNeighbours[bestCol]) {
						bestCol = i;
						row = j;
					}
					break;
				}
			}
		}
		return new Point(row, bestCol);
	}

	public int getLevel() {
		return level;
	}

	/**
	 * Returns the number of neighbours of the same type (color) in any
	 * direction.
	 * 
	 * @param row
	 * @param col
	 * @param type
	 *            the index to be investigated
	 * @param amount
	 *            the number of neighbours required for returning true
	 */
	private int numConsecutiveNeighbours(int row, int col, int type) {
		// Check all directions for neighbours of index type
		int[] values = new int[4];
		values[0] = numNeighbours(row, col, 0, 1, type)
				+ numNeighbours(row, col, 0, -1, type);
		values[1] = numNeighbours(row, col, 1, 1, type)
				+ numNeighbours(row, col, -1, -1, type);
		values[2] = numNeighbours(row, col, 1, 0, type)
				+ numNeighbours(row, col, -1, 0, type);
		values[3] = numNeighbours(row, col, 1, -1, type)
				+ numNeighbours(row, col, -1, 1, type);

		int max = -1;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;

	}

	/**
	 * Returns the number of consecutive neighbours of the same type in a
	 * certain direction
	 * 
	 * @param row
	 * @param col
	 * @param dirrow
	 *            the x-direction to look for neighbours
	 * @param dircol
	 *            the y-direction to look for neighbours
	 * @param type
	 *            the index to be investigated
	 */
	private int numNeighbours(int row, int col, int dirrow, int dircol, int type) {
		int newrow = row + dirrow;
		int newcol = col + dircol;
		if (isOutside(newrow, newcol))
			return 0;
		if (board[newrow][newcol] == type) {
			return (1 + numNeighbours(newrow, newcol, dirrow, dircol, type));
		} else {
			return 0;
		}
	}

	private boolean isOutside(int row, int col) {
		if (row >= rows || row < 0 || col >= cols || col < 0)
			return true;
		return false;
	}
}
