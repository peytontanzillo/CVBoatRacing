package org.cubeville.cvracing.models;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.RaceSignType;
import org.cubeville.cvracing.TrackStatus;

public class RaceSign {

	private final Sign sign;
	private final Track track;
	private RaceSignType type;

	public RaceSign(Sign sign, Track track, RaceSignType type) {
		this.sign = sign;
		this.track = track;
		this.sign.setLine(1, track.getName());
		this.setType(type);
		this.displayQueue();
		this.displayStatus(track.getStatus());
	}

	public void displayType() {
		String typeText = "";
		switch (type) {
			case TRIALS:
				typeText += "§b";
				break;
			case VERSUS:
				typeText += "§d";
				break;
			case SPECTATE:
				typeText += "§7";
				break;
			case EXIT:
			case ERROR:
				typeText += "§c";
				break;
		}
		typeText += "§l[";
		typeText += type;
		typeText += "]";
		this.sign.setLine(0, typeText);
	}

	public void displayQueue() {
		switch (type) {
			case TRIALS:
				if (track.getQueue().size() > 0) {
					this.sign.setLine(3, "§6§o" + track.getQueue().size() + " in queue");
				} else {
					this.sign.setLine(3, "");
				}
				break;
			case VERSUS:
				int playerFill = 0;
				if (track.getVersusRace() != null) {
					playerFill = track.getVersusRace().playerSize();
				}
				this.sign.setLine(3, playerFill + " / 4");
				break;
			default:
				this.sign.setLine(3, "");
				break;
		}
		this.sign.update();
	}

	public void displayStatus(TrackStatus status) {
		if (type != RaceSignType.TRIALS && type != RaceSignType.VERSUS) {
			this.sign.setLine(2, "");
		} else {
			switch (status) {
				case OPEN:
					this.sign.setLine(2, "§a§lOPEN");
					break;
				case IN_USE:
					this.sign.setLine(2, "§7§lIN USE");
					break;
				case CLOSED:
					this.sign.setLine(2, "§c§lCLOSED");
					break;
				case IN_LOBBY:
					this.sign.setLine(2, "§e§lIN LOBBY");
					break;
			}
		}
		this.sign.update();
	}

	public void setType(RaceSignType type) {
		this.type = type;
		this.displayType();
		this.displayStatus(track.getStatus());
	}

	public Sign getSign() {
		return sign;
	}

	public Track getTrack() {
		return track;
	}

	public void onRightClick(Player p) {
		this.track.onRightClick(p, type);
	}
}
