package me.DevTec.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.DevTec.GUI.GUIID;
import me.DevTec.Other.LoaderClass;

public class GUIClickEvent extends Event implements Cancellable {

	public GUIClickEvent(Player player, Inventory inv, String title, int slot, ItemStack item) {
		this.player = player;
		this.inv = inv;
		this.title = title;
		this.slot = slot;
		this.item = item;
	}

	private ItemStack item;
	private int slot;
	private String title;
	private Player player;
	private Inventory inv;
	private boolean cancel;

	/**
	 * 
	 * @return Clicked slot
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * 
	 * @return Clicked item (Can be null)
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * @return is event cancelled
	 */
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	/**
	 * 
	 * @return Title of GUI
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Cancel GUI click event
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	private static final HandlerList cs = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return cs;
	}

	public static HandlerList getHandlerList() {
		return cs;
	}

	/**
	 * 
	 * @return Item is null
	 */
	public boolean isItemNull() {
		return getItem() == null;
	}

	/**
	 * 
	 * @return ID of GUI
	 */
	public String getID() {
		String id = null;
	for(GUIID d : LoaderClass.plugin.gui)
		if(d.getPlayers().contains(player)) {
			id=d.getID();
			break;
		}
		return id;
	}

	public boolean isPlayerInventory() {
		return getGUI().getType().equals(InventoryType.PLAYER);
	}

	/**
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * 
	 * @return Opened GUI
	 */
	public Inventory getGUI() {
		return inv;
	}

}
