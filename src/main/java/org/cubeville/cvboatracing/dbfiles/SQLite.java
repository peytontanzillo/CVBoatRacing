package org.cubeville.cvboatracing.dbfiles;
import org.cubeville.cvboatracing.CVBoatRacing;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite {
	private CVBoatRacing boatRacing;
	private Connection connection;
	private Statement statement;

	public SQLite(CVBoatRacing boatRacing) {
		this.boatRacing = boatRacing;
	}

	public void connect() {
		connection = null;

		try {
			File dbFile = new File(boatRacing.getDataFolder(), "scores.db");
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
