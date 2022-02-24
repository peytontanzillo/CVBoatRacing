package org.cubeville.cvracing.models;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
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
		this.displayStatus(track.getStatus());
		this.displayQueue(track.getQueue().size());
		this.setType(type);
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

	public void displayQueue(int queueLength) {
		if (queueLength > 0) {
			this.sign.setLine(3, "§6§o" + queueLength + " in queue");
		} else {
			this.sign.setLine(3, "");
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
