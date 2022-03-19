package org.cubeville.cvracing.dbfiles;

import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.CVRacing;
import org.cubeville.cvracing.models.Score;
import org.cubeville.cvracing.models.Track;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RacingDB extends SQLite {

	public RacingDB(CVRacing racing) {
		super(racing);
	}

	public String SQLiteCreateScoresTable = "CREATE TABLE IF NOT EXISTS scores (" +
		"`score_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
		"`uuid` varchar(32) NOT NULL," +
		"`time` BIGINT NOT NULL," +
		"`track_id` varchar(32) NOT NULL," +
		"`timestamp` BIGINT NOT NULL" +
		");";

	public String SQLiteCreateSplitsTable = "CREATE TABLE IF NOT EXISTS splits (" +
		"`split_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
		"`time` BIGINT NOT NULL," +
		"`cp_id` INTEGER NOT NULL," +
		"`score_id` INTEGER NOT NULL," +
		"FOREIGN KEY (`score_id`) REFERENCES scores(`score_id`) ON DELETE CASCADE" +
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

	public void createBackup(JavaPlugin plugin) throws IOException {
		File dbFile = new File(plugin.getDataFolder(), "scores.db");
		if (dbFile.exists()) {
			Path source = dbFile.toPath();
			Path destination = plugin.getDataFolder().toPath();
			Files.copy(source, destination.resolve("scores-backup.db"), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void addScore(Score s) {
		// add score to database
		update("INSERT INTO `scores` (uuid, track_id, time, timestamp) " +
			"VALUES(\"" + s.getPlayerUUID().toString() + "\", \"" + s.getTrack().getName() + "\", " + s.getFinalTime() + ", " + s.getTimestamp() + ");"
		);

		Integer scoreId = getScoreID(s.getPlayerUUID(), s.getTrack());
		for (int checkpoint : s.getSplits().keySet()) {
			// add splits to database
			update("INSERT INTO `splits` (score_id, cp_id, time) " +
				"VALUES(" + scoreId + ", " + checkpoint + ", " + s.getSplit(checkpoint) + ");"
			);
		}
	}

	public void updateScore(Score s) {
		update("UPDATE `scores`" +
			" SET time = " + s.getFinalTime() + ", timestamp = " + s.getTimestamp()
			+ scoreConditionString(s.getPlayerUUID(), s.getTrack())
		);
		Integer scoreId = getScoreID(s.getPlayerUUID(), s.getTrack());
		for (int checkpoint : s.getSplits().keySet()) {
			update("UPDATE `splits`" +
				" SET time = " + s.getSplit(checkpoint)
				+ splitConditionString(checkpoint, scoreId)
			);
		}
	}

	public void deleteScore(Score score) {
		update("DELETE FROM splits where score_id in ( SELECT score_id from scores" + scoreConditionString(score.getPlayerUUID(), score.getTrack()) + ")");
		update("DELETE FROM `scores`" + scoreConditionString(score.getPlayerUUID(), score.getTrack()));

	}

	public void deleteTrackScores(Track track) {
		update("DELETE FROM `scores` WHERE `track_id` = \"" + track.getName() + "\"");
	}

	public void deleteSplitsAtScore(int scoreID) {
		update("DELETE FROM `splits` WHERE `score_id` = " + scoreID);
	}

	public void deleteTrackSplits(Track track) {
		update("DELETE from splits where score_id in ( SELECT score_id from scores WHERE track_id = \""+ track.getName() + "\")");
	}

	public void deletePlayerScores(UUID uuid) {
		update("DELETE FROM `scores` WHERE `uuid` = \"" + uuid.toString() + "\"");
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
