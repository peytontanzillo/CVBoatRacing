package org.cubeville.cvracing.dbfiles;
import org.cubeville.cvracing.CVRacing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

public class SQLite {
	private CVRacing racing;
	private Connection connection;
	private Statement statement;

	public SQLite(CVRacing racing) {
		this.racing = racing;
	}

	public void connect() {
		connection = null;

		try {
			if (!racing.getDataFolder().exists()) {
				racing.saveDefaultConfig();
			}
			File dbFile = new File(racing.getDataFolder(), "scores.db");

			if (!dbFile.exists()) {
				dbFile.createNewFile();
			}
			String url = "jdbc:sqlite:" + dbFile.getPath();

			connection = DriverManager.getConnection(url);
			statement = connection.createStatement();

		} catch (IOException | SQLException exception) {
			exception.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	public void update(String sql) {
		try {
			statement.execute(sql);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}

	public ResultSet getResult(String sql) {
		try {
			return statement.executeQuery(sql);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}
}
