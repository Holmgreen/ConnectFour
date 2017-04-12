package Database;

import java.sql.*;
import java.util.*;

public class Database {
	private Connection conn;

	public Database() {
		conn = null;
	}

	public boolean openConnection(String filename) {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
		} catch (SQLException e) {
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}
	}

	public boolean isConnected() {
		return conn != null;
	}

	/**
	 * Insert the game log, containing all information there is to know about a
	 * game session
	 * 
	 * @param log
	 *            a list of information of each move that was made during the
	 *            game in the following order: (playerName, turn, row, col)
	 * @param players
	 *            an array of all players active in the game session
	 * @param winIndex
	 *            the index of the player who won, if there was a draw it should
	 *            be negative
	 */
	public void insertGameLog(ArrayList<String[]> log, String[] players,
			int winIndex) {
		insertPlayedGame();
		String gameId = getLastGameId();
		insertGameOutcome(gameId, players, winIndex);
		insertActions(gameId, log);
	}

	/**
	 * Returns the maximum value of all game ID's
	 */
	public String getLastGameId() {
		return getDistinctValue("select MAX(gameId) from PlayedGames");
	}

	/**
	 * Inserts a played game to the database
	 */
	public void insertPlayedGame() {
		insertTransaction("insert into PlayedGames values(NULL, datetime())");
	}

	/**
	 * Inserts all actions from a game session to the database
	 * 
	 * @param gameId
	 *            the ID of the played game
	 * @param log
	 *            a list of information of each move that was made during the
	 *            game in the following order: (playerName, turn, row, col)
	 */
	public void insertActions(String gameId, ArrayList<String[]> log) {
		ArrayList<String> sql = new ArrayList<String>();
		for (int i = 0; i < log.size(); i++) {
			sql.add("(" + gameId + ", '" + log.get(i)[0] + "', "
					+ log.get(i)[1] + ", " + log.get(i)[2] + ", "
					+ log.get(i)[3] + ")");
		}
		insertTransactions("Actions", sql);
	}

	public void insertAction(String gameId, String playerName, String turn,
			String row, String col) {
		String values = "(" + gameId + ",'" + playerName + "'," + turn + ","
				+ row + "," + col + ")";
		insertTransaction("insert into Actions values" + values);
	}

	/**
	 * Insert the outcome of the game, i.e. Win, Draw or Lose for each of the
	 * players in the game
	 * 
	 * @param gameId
	 *            the ID of the game
	 * @param players
	 *            the players that were active in the game session
	 * @param winIndex
	 *            the index of the player who won, if there was a draw it should
	 *            be negative
	 */
	public void insertGameOutcome(String gameId, String[] players, int winIndex) {
		if (winIndex < 0) {
			for (int i = 0; i < players.length; i++) {
				String values = "(" + gameId + ",'" + players[i] + "', 'Draw')";
				insertTransaction("insert into GameHistories values " + values);
			}
		} else {
			for (int i = 0; i < players.length; i++) {
				String playerName = players[i];

				String outcome;
				if (i == winIndex) {
					outcome = "'Won'";
				} else
					outcome = "'Lost'";
				String values = "(" + gameId + ",'" + playerName + "',"
						+ outcome + ")";
				insertTransaction("insert into GameHistories values " + values);
			}
		}
	}

	/**
	 * Inserts a new player into the database
	 * 
	 * @param playerName
	 *            the name of the player
	 */
	public void insertNewPlayer(String playerName) {
		if (getDistinctValue("select playerName from Players where playerName = '"
				+ playerName + "'") == null) {
			String values = "('" + playerName + "')";
			insertTransaction("insert into Players values " + values);
		}
	}

	/**
	 * Returns a value that is known to be unique from the database
	 * 
	 * @param sql
	 *            query
	 * @return answer of query and null if query failed
	 */
	public String getDistinctValue(String sql) {
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			String string = rs.getString(1);
			ps.close();
			rs.close();
			return string;
		} catch (SQLException e) {
			return null;
		}
	}

	/**
	 * Insert several values to the database
	 * 
	 * @param values
	 *            the list of all values to be inserted on the form (value1, ...
	 *            , valueN)
	 * @param table
	 *            the table to insert the values to s
	 */
	public void insertTransactions(String table, ArrayList<String> values) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into " + table + " values ");
			for (int i = 0; i < values.size() - 1; i++) {
				sql.append(values.get(i) + ", ");
			}
			sql.append(values.get(values.size() - 1));
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * Make a single insertion to the database
	 * 
	 * @param sql
	 *            query to be sent
	 */
	public void insertTransaction(String sql) {
		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/**
	 * Returns a list of the best performing players in form of number of wins, ordered from most to least.
	 */
	public ArrayList<String> getHighscores() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String sql = "select playerName, COUNT(outcome) count from GameHistories where outcome = 'Won' group by playerName order by count DESC";

			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(rs.getString(1) + " - " + rs.getString(2) + " wins");
			}

			ps.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				return null;
			}
		}
		return list;
	}
}
