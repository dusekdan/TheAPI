package me.DevTec.Utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import me.DevTec.TheAPI;
import me.DevTec.Events.EntityMoveEvent;
import me.DevTec.Other.LoaderClass;
import me.DevTec.Other.Ref;
import me.DevTec.Other.StringUtils;
import me.DevTec.Scheduler.Scheduler;
import me.DevTec.Scheduler.Tasker;

public class Tasks {
	private static StringUtils f;
	private static boolean load;
	private static int task;
	private static me.DevTec.NMS.PacketListeners.Listener l=null;

	public static void load() {
		f = TheAPI.getStringUtils();
		FileConfiguration c = LoaderClass.config.getConfig();
		FileConfiguration v = LoaderClass.unused.getConfig();
		if (load)
			return;
		load = true;
		if(l==null)
		l=new me.DevTec.NMS.PacketListeners.Listener() {
			@Override
			public void PacketPlayOut(Player player, Object packet) {
				if(packet.toString().contains("PacketStatusOutServerInfo")) {
					Object w = Ref.invoke(Ref.server(),"getServerPing");
					Object sd = Ref.newInstance(Ref.constructor(Ref.nms("ServerPing$ServerPingPlayerSample"), int.class, int.class), LoaderClass.plugin.max>-1?LoaderClass.plugin.max:Bukkit.getMaxPlayers(),LoaderClass.plugin.fakeOnline>-1?LoaderClass.plugin.fakeOnline:TheAPI.getOnlinePlayers().size());
					if(LoaderClass.plugin.onlineText!=null && !LoaderClass.plugin.onlineText.isEmpty()) {
					GameProfile[] texts = new GameProfile[LoaderClass.plugin.onlineText.size()];
					int i = 0;
					for(String s : LoaderClass.plugin.onlineText) {
						texts[i]=new GameProfile(UUID.randomUUID(), TheAPI.colorize(TheAPI.getPlaceholderAPI().setPlaceholders(null, s)));
						++i;
					}
					Ref.set(sd, "c", texts);
					}else {
						GameProfile[] texts = new GameProfile[TheAPI.getPlayers().size()];
						int i = 0;
						for(Player s : TheAPI.getPlayers()) {
							if(!TheAPI.isVanished(s))
							texts[i]=new GameProfile(UUID.randomUUID(), s.getName());
							++i;
						}
						Ref.set(sd, "c", texts);
					}
					Ref.set(w, "b", sd);
					if(LoaderClass.plugin.motd!=null)
					Ref.set(w, "a", Ref.IChatBaseComponent(TheAPI.colorize(TheAPI.getPlaceholderAPI().setPlaceholders(null, LoaderClass.plugin.motd))));
					Ref.set(packet, "b", w);
				}
			}
			
			@Override
			public void PacketPlayIn(Player player, Object packet) {
				
			}
		};
		l.register();
		if (c.getBoolean("Options.EntityMoveEvent.Enabled"))
			task=new Tasker() {
				public void run() {
					for (World w : Bukkit.getWorlds()) {
						for (Entity d : w.getEntities()) {
							if (d.getType() == EntityType.DROPPED_ITEM)
								continue;
							if (d instanceof LivingEntity) {
								Location a = d.getLocation();
								LivingEntity e = (LivingEntity) d;
								Location old = (v.getString("entities." + e.getUniqueId()) != null
										? f.getLocationFromString(v.getString("entities." + e.getUniqueId()))
										: a);
								if (v.getString("entities." + e.getUniqueId()) != null
										&& f.getLocationFromString(v.getString("entities." + e.getUniqueId())) != a) {
									EntityMoveEvent event = new EntityMoveEvent(e, old, a);
									Bukkit.getPluginManager().callEvent(event);
									if (event.isCancelled())
										e.teleport(old);
									else
										LoaderClass.unused.getConfig().set("entities." + e.getUniqueId(),
												f.getLocationAsString(a));
								}
							}
						}
					}
				}
			}.repeating(0, c.getInt("Options.EntityMoveEvent.Reflesh"));
	}

	public static void unload() {
		load = false;
		if(l!=null)
		l.unregister();
		Scheduler.cancelTask(task);
	}
}
