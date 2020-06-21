package me.DevTec.GUI;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.common.collect.Maps;

import me.DevTec.ConfigAPI;

public class GUIID {
	private Inventory i;
	private GUICreatorAPI p;
	private ConfigAPI d;
	private String id;

	public GUIID(GUICreatorAPI s, ConfigAPI g) {
		p = s;
		this.d = g;
		for (int i = 0; i > -1; ++i) {
			if (!g.existPath("guis." + i)) {
				id = i + "";
				break;
			}
		}
	}

	public void setInv(Inventory iv) {
		i = iv;
	}

	public Inventory getInventory() {
		return i;
	}

	public List<Player> getPlayers() {
		return p.getPlayers();
	}

	public String getID() {
		return id;
	}

	public static enum GRunnable {
		RUNNABLE, RUNNABLE_LEFT_CLICK, RUNNABLE_RIGHT_CLICK, RUNNABLE_SHIFT_WITH_LEFT_CLICK,
		RUNNABLE_SHIFT_WITH_RIGHT_CLICK, RUNNABLE_MIDDLE_CLICK, RUNNABLE_ON_INV_CLOSE;
	}

	public void setRunnable(GRunnable r, int slot, Runnable e) {
		if (GRunnable.RUNNABLE_ON_INV_CLOSE == r) {
			close = e;
		} else {
			HashMap<Integer, Runnable> d = run.containsKey(r)?run.get(r):Maps.newHashMap();
			d.put(slot, e);
			run.put(r, d);
		}
	}

	private HashMap<GRunnable, HashMap<Integer, Runnable>> run = new HashMap<GRunnable, HashMap<Integer, Runnable>>();
	Runnable close;

	public Runnable getRunnable(GRunnable r, int slot) {
		if (GRunnable.RUNNABLE_ON_INV_CLOSE == r)
			return close;
		else if (run.containsKey(r) && run.get(r).containsKey(slot))
			return run.get(r).get(slot);
		return null;
	}

	public void runRunnable(GRunnable r, int slot) {
		if (GRunnable.RUNNABLE_ON_INV_CLOSE == r) {
			if (close != null)
				close.run();
		} else if (run.containsKey(r) && run.get(r).containsKey(slot))
			run.get(r).get(slot).run();
	}

	public void clear() {
		run.clear();
		close = null;
		d.set("guis." + id, null);
		d.save();
	}

	public void closeAndClear() {
		clear();
		p.close();
	}

	public void close() {
		p.close();
	}

	public GUICreatorAPI getGUI() {
		return p;
	}
}