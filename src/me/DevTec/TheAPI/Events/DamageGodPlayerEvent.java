package me.DevTec.TheAPI.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageGodPlayerEvent extends Event implements Cancellable {
	private Player s;
	private DamageCause cause;
	private boolean cancel = true;

	public DamageGodPlayerEvent(Player p, double dam, DamageCause cau) {
		s = p;
		damage = dam;
		cause = cau;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	double damage;

	public double getDamage() {
		return damage;
	}

	public void setDamage(double value) {
		damage = value;
	}

	public DamageCause getDamageCause() {
		return cause;
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
