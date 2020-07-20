package me.DevTec.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJumpEvent extends Event implements Cancellable {
	Player s;

	public PlayerJumpEvent(Player p, Location from, Location to, double jump) {
		s = p;
		f = from;
		t = to;
		i = jump;
	}

	double i;
	Location t;
	Location f;

	public double getJump() {
		return i;
	}

	public Location getTo() {
		return t;
	}

	public Location getFrom() {
		return f;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	boolean cancel;

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public Player getPlayer() {
		return s;
	}

	private static final HandlerList cs = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return cs;
	}

	public static HandlerList getHandlerList() {
		return cs;
	}
}