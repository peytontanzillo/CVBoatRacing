package org.cubeville.cvboatracing;

import org.cubeville.cvboatracing.dbfiles.BoatRacingDB;
import org.cubeville.cvboatracing.models.Score;
import org.cubeville.cvboatracing.models.Track;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class ScoreManager {

	private static HashMap<Integer, Score> importScoreManager = new HashMap<>(); // database id -> score
	private static HashMap<UUID, HashMap<String, Score>> scoreManager = new HashMap<>(); // player uuid -> track id -> score
	private static BoatRacingDB database;

	public static void importDataFromDatabase(BoatRacingDB db) {
		database = db;
		importScoresFromDB(db);
		importSplitsFromDB(db);
		for (Score score : importScoreManager.values()) {
			addScoreToManager(score);
		}
		TrackManager.sortTrackScores();
	}

	private static void importScoresFromDB(BoatRacingDB db) {
		try {
			ResultSet scoresSet = db.getAllScores();
			if (scoresSet == null) { return; }
			while (scoresSet.next()) {
				int scoreID = scoresSet.getInt("score_id");
				Track track = TrackManager.getTrack(scoresSet.getString("track_id"));
				UUID playerUUID = UUID.fromString(scoresSet.getString("uuid"));
				Score s = new Score(
					scoresSet.getLong("time"),
					track,
					playerUUID
					);
				track.addScore(s);
				importScoreManager.put(scoreID, s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void importSplitsFromDB(BoatRacingDB db) {
		try {
			ResultSet splitSet = db.getAllSplits();
			if (splitSet == null) { return; }
			while (splitSet.next()) {
				importScoreManager.get(splitSet.getInt("score_id"))
					.addSplit(splitSet.getInt("cp_id"), splitSet.getLong("time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static Score getScore(UUID uuid, Track track) {
		HashMap<String, Score> playerMap = scoreManager.get(uuid);
		if (playerMap != null) {
			return playerMap.get(track.getName());
		}
		return null;
	}

	public static Score addScore(UUID uuid, Track track, long finalTime, HashMap<Integer, Long> splits) {
		Score s = new Score(finalTime, track, uuid);
		HashMap<String, Score> oldMap = scoreManager.get(uuid);
		if (oldMap == null) {
			oldMap = new HashMap<>();
		}
		s.setSplits(splits);
		oldMap.put(track.getName(), s);
		scoreManager.put(uuid, oldMap);
		return s;
	}

	public static void addScoreToManager(Score score) {
		HashMap<String, Score> oldMap = scoreManager.get(score.getPlayerUUID());
		if (oldMap == null) {
			oldMap = new HashMap<>();
		}
		score.setSplits(score.getSplits());
		oldMap.put(score.getTrack().getName(), score);
		scoreManager.put(score.getPlayerUUID(), oldMap);
	}

	public static void setNewPB(UUID uuid, Track track, long finalTime, HashMap<Integer, Long> splits) {
		Score s = getScore(uuid, track);
		if (s == null) {
			Score newScore = addScore(uuid, track, finalTime, splits);
			database.addScore(newScore);
			track.addScore(newScore);
		} else {
			// update the score that is currently in the db
			s.setFinalTime(finalTime);
			s.setSplits(splits);
			database.updateScore(s);
		}
		track.sortScores();
	}
}
