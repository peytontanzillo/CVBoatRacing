package org.cubeville.cvboatracing.dbfiles;

import org.cubeville.cvboatracing.CVBoatRacing;
import org.cubeville.cvboatracing.models.Score;
import org.cubeville.cvboatracing.models.Track;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BoatRacingDB extends SQLite {

	public BoatRacingDB(CVBoatRacing boatRacing) {
		super(boatRacing);
	}

	public String SQLiteCreateScoresTable = "CREATE TABLE IF NOT EXISTS scores (" + // make sure to put your table name in here too.
		"`score_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
		"`uuid` varchar(32) NOT NULL," +
		"`time` BIGINT NOT NULL," +
		"`track_id` varchar(32) NOT NULL" +
		");";

	public String SQLiteCreateSplitsTable = "CREATE TABLE IF NOT EXISTS splits (" + // make sure to put your table name in here too.
		"`split_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
		"`time` BIGINT NOT NULL," +
		"`cp_id` INTEGER NOT NULL," +
		"`score_id` INTEGER NOT NULL," +
		"FOREIGN KEY (`score_id`) REFERENCES scores(`score_id`)" +
		");";

	public void load() {
		connect();
		update(SQLiteCreateScoresTable);
		update(SQLiteCreateSplitsTable);
	}

	public ResultSet getAllScores() {
		return getResult("SELECT * FROM `scores`");
	}

	public ResultSet getAllSplits() {
		return getResult("SELECT * FROM `splits`");
	}

	public void addScore(Score s) {
		// add score to database
		update("INSERT INTO `scores` (uuid, track_id, time) " +
			"VALUES(\"" + s.getPlayerUUID().toString() + "\", \"" + s.getTrack().getName() + "\", " + s.getFinalTime() + ");"
		);

		Integer scoreId = getScoreID(s.getPlayerUUID(), s.getTrack());
		for (int checkpoint : s.getSplits().keySet()) {
			// add splits to database
			update("INSERT INTO `splits` (score_id, cp_id, time) " +
				"VALUES(" + scoreId + ", " + checkpoint + ", " + s.getSplit(checkpoint) + ");"
			);
		}
		System.out.println("addscore");
	}

	public void updateScore(Score s) {
		update("UPDATE `scores`" +
			" SET time = " + s.getFinalTime()
			+ scoreConditionString(s.getPlayerUUID(), s.getTrack())
		);
		Integer scoreId = getScoreID(s.getPlayerUUID(), s.getTrack());
		for (int checkpoint : s.getSplits().keySet()) {
			update("UPDATE `splits`" +
				" SET time = " + s.getSplit(checkpoint)
				+ splitConditionString(checkpoint, scoreId)
			);
		}
		System.out.println("updatescore");
	}

	private Integer getScoreID(UUID uuid, Track track) {
		try {
			return getResult("SELECT * FROM `scores`" + scoreConditionString(uuid, track)).getInt("score_id");
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	private String scoreConditionString(UUID uuid, Track track) {
		return 	" WHERE `uuid` = \"" + uuid.toString() + "\"" +
			" AND `track_id` = \"" + track.getName() + "\"";
	}

	private String splitConditionString(int cpID, int scoreID) {
		return 	" WHERE `cp_id` = " + cpID +
			" AND `score_id` = " + scoreID;
	}
}
