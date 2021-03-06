package me.DevTec.TheAPI.Utils.TheAPIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.APIs.MemoryAPI;
import me.DevTec.TheAPI.APIs.PluginManagerAPI;
import me.DevTec.TheAPI.BossBar.BossBar;
import me.DevTec.TheAPI.ConfigAPI.Config;
import me.DevTec.TheAPI.EconomyAPI.EconomyAPI;
import me.DevTec.TheAPI.GUIAPI.GUI;
import me.DevTec.TheAPI.PlaceholderAPI.PlaceholderAPI;
import me.DevTec.TheAPI.PlaceholderAPI.PlaceholderPreRegister;
import me.DevTec.TheAPI.PlaceholderAPI.ThePlaceholder;
import me.DevTec.TheAPI.PlaceholderAPI.ThePlaceholderAPI;
import me.DevTec.TheAPI.Scheduler.Scheduler;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.ScoreboardAPI.ScoreboardAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.DataType;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;
import me.DevTec.TheAPI.Utils.PacketListenerAPI.PacketHandler;
import me.DevTec.TheAPI.Utils.PacketListenerAPI.PacketHandler_New;
import me.DevTec.TheAPI.Utils.PacketListenerAPI.PacketHandler_Old;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Command.TheAPICommand;
import me.DevTec.TheAPI.WorldsAPI.WorldsAPI;
import me.DevTec.TheVault.Bank;
import me.DevTec.TheVault.TheVault;
import net.milkbowl.vault.economy.Economy;

@SuppressWarnings("restriction")
public class LoaderClass extends JavaPlugin {
	//Scoreboards
	public final Map<Integer, ScoreboardAPI> scoreboard = new HashMap<>();
	public final MultiMap<Integer, Integer, Object> map = new MultiMap<>();
	//GUIs
	public final Map<String, GUI> gui = new HashMap<>();
	//BossBars
	public final List<BossBar> bars = new ArrayList<>();
	//TheAPI
	public static LoaderClass plugin;
	public static Config config= new Config("TheAPI/Config.yml"), data= new Config("TheAPI/Data.yml");
	public String motd;
	public int max;
	//EconomyAPI
	public boolean e, tve, tbank;
	public Economy economy;
	public me.DevTec.TheVault.Economy tveeconomy;
	public Bank bank;
	private boolean oa = true;
	public Object air = Ref.invoke(Ref.getNulled(Ref.field(Ref.nms("Block"), "AIR")), "getBlockData");

	public static sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref.getNulled(Ref.field(sun.misc.Unsafe.class,"theUnsafe"));
	
	@SuppressWarnings("unchecked")
	@Override
	public void onLoad() {
		plugin = this;
		new TheAPI();
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eLoading plugin..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		createConfig();
		if(TheAPI.isOlder1_9())
		new Thread(new Runnable() {
			public void run() {
				while(oa){
			        for(BossBar s : bars)
			        	s.move();
			        try {
			            Thread.sleep(1000);
			        } catch (InterruptedException e) {
			        }
				}
			}
		}).start();
		if(TheAPI.isNewerThan(7) || Ref.getClass("net.minecraft.util.io.netty.channel.ChannelInitializer")==null)
		handler = new PacketHandler_New();
		else
			handler = new PacketHandler_Old();
		for(Player s : TheAPI.getOnlinePlayers()) {
			if (!handler.hasInjected(handler.getChannel(s)))
				handler.injectPlayer(s);
		}
	}
	
	@Override
	public void onEnable() {
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eEnabling plugin, creating config and registering economy..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		loadWorlds();
		new Tasker() {
			
			@Override
			public void run() {
		loadPlaceholders();
		if(PlaceholderAPI.isEnabledPlaceholderAPI()) {
			/* TheAPI placeholder extension for PAPI
			 * BRIDGE:
			 * 
			 * PAPI -> THEAPI : %papi_placeholder_here%
			 * PAPI <- THEAPI : %theapi_{theapi_placeholder_here}%
			 */
			new PlaceholderPreRegister("TheAPI", "DevTec", getDescription().getVersion()) {
				Pattern finder = Pattern.compile("\\{(.*?)\\}"),
						math = Pattern.compile("\\{math\\{((?:\\{??[^A-Za-z\\{][ 0-9+*/^%()~.-]*))\\}\\}");
				@Override
				public String onRequest(Player player, String params) {
					String text = params;
					while(true) {
						Matcher m = math.matcher(text);
						int v = 0;
						while(m.find()) {
							++v;
							String w = m.group();
							text=text.replace(w, StringUtils.calculate(w.substring(6,w.length()-2)).toString());
						}
						if(v!=0)continue;
						else break;
					}
					Matcher found = finder.matcher(text);
					while(found.find()) {
						String g = found.group();
						String find = g.substring(1,g.length()-1);
						int v = 0;
						for(Iterator<ThePlaceholder> r = ThePlaceholderAPI.getPlaceholders().iterator(); r.hasNext();) {
							++v;
							ThePlaceholder get = r.next();
							String toReplace = get.onPlaceholderRequest(player, find);
							if(toReplace!=null)
								text=text.replace("{"+find+"}", toReplace);
						}
						if(v!=0)continue;
						else break;
					}
					return text.equals(params)?null:text;
				}
			}.register();
		}
		Tasks.load();
		Bukkit.getPluginManager().registerEvents(new Events(), LoaderClass.this);
		TheAPI.createAndRegisterCommand("TheAPI", null, new TheAPICommand());
		if (PluginManagerAPI.getPlugin("TheVault") != null)
			TheVaultHooking();
		if (PluginManagerAPI.getPlugin("Vault") == null) {
			TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
			TheAPI.msg("&cTheAPI&7: &ePlugin not found Vault, EconomyAPI is disabled.", TheAPI.getConsole());
			TheAPI.msg("&cTheAPI&7: &eYou can enabled EconomyAPI by set custom Economy in EconomyAPI.",
					TheAPI.getConsole());
			TheAPI.msg("&cTheAPI&7: &e *TheAPI will still normally work without problems*", TheAPI.getConsole());
			TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		} else
			vaultHooking();
		new Tasker() {
			@Override
			public void run() {
				if (getTheAPIsPlugins().size() == 0)return;
				String end = getTheAPIsPlugins().size() != 1?"s":"";
				TheAPI.msg("&cTheAPI&7: &eTheAPI using &6" + getTheAPIsPlugins().size() + " &eplugin" + end,TheAPI.getConsole());
			}
		}.runLater(200);
		int removed = 0;
		for(UUID u : TheAPI.getUsers()) {
			if(TheAPI.getUser(u).getKeys().isEmpty()) {
				TheAPI.getUser(u).delete();
				++removed;
			}
		}
		if(removed!=0)
		TheAPI.msg("&cTheAPI&7: &eTheAPI deleted &6" + removed + " &eunused user files", TheAPI.getConsole());
		TheAPI.clearCache();
		}}.runTask();
	}
	
	@SuppressWarnings("rawtypes")
	public PacketHandler handler;
	
	@Override
	public void onDisable() {
		oa=false;
		handler.close();
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eDisabling plugin, saving configs and stopping runnables..",
				TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		Scheduler.cancelAll();
		for (String p : gui.keySet())
			gui.get(p).clear();
		gui.clear();
		main.unregister();
	}

	public List<Plugin> getTheAPIsPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin all : PluginManagerAPI.getPlugins())
			if (PluginManagerAPI.getDepend(all.getName()).contains("TheAPI")
					|| PluginManagerAPI.getSoftDepend(all.getName()).contains("TheAPI"))
				a.add(all);
		return a;
	}
	
	private void createConfig() {
		config.addDefault("Options.HideErrors", false); //hide only TheAPI errors
		config.setComments("Options.HideErrors", Arrays.asList("","# If you enable this option, errors from TheAPI will dissapear","# defaulty: false"));
		config.addDefault("Options.Cache.User.Use", true); //Require memory, but loading of User.class is faster (only from TheAPI.class)
		config.setComments("Options.Cache", Arrays.asList(""));
		config.setComments("Options.Cache.User.Use", Arrays.asList("# Cache Users to memory for faster loading","# defaulty: true"));
		config.addDefault("Options.Cache.User.RemoveOnQuit", true); //Remove cached player from cache on PlayerQuitEvent
		config.setComments("Options.Cache.User.RemoveOnQuit", Arrays.asList("# Remove cache of User from memory","# defaulty: true"));
		config.addDefault("Options.User-SavingType", DataType.YAML.name());
		config.setComments("Options.User-SavingType", Arrays.asList("","# Saving type of User data","# Types: YAML, JSON, BYTE, DATA","# defaulty: YAML"));
		config.addDefault("Options.AntiBot.Use", false);
		config.setComments("Options.AntiBot", Arrays.asList(""));
		config.setComments("Options.AntiBot.Use", Arrays.asList("# If you enable this, TheAPI will set time between player can't connect to the server","# defaulty: false"));
		config.addDefault("Options.AntiBot.TimeBetweenPlayer", 10); //10 milis
		config.setComments("Options.AntiBot.TimeBetweenPlayer", Arrays.asList("# Time between player can't connect to the server","# defaulty: 10"));
		config.setComments("Options.Optimize", Arrays.asList("","# TheAPI's optimizers for server"));
		config.addDefault("Options.Optimize.TNT.Use", true);
		config.setComments("Options.Optimize.TNT", Arrays.asList("# TNT optimizer"));
		config.setComments("Options.Optimize.TNT.Use", Arrays.asList("# If you disable this, TNT optimizer will be disabled","# defaulty: true"));
		config.addDefault("Options.Optimize.TNT.Particles.Use", false);
		config.setComments("Options.Optimize.TNT.Particles.Use", Arrays.asList("# If you enable this, TNT will have particles","# defaulty: false"));
		config.addDefault("Options.Optimize.TNT.Particles.Type", "EXPLOSION_LARGE");
		config.setComments("Options.Optimize.TNT.Particles.Type", Arrays.asList("# Particle types are available on https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html","# defaulty: EXPLOSION_LARGE"));
		config.addDefault("Options.Optimize.TNT.LiquidCancelExplosion", true);
		config.setComments("Options.Optimize.TNT.LiquidCancelExplosion", Arrays.asList("# Can water/lava cancel TNT explosion","# defaulty: true"));
		config.addDefault("Options.Optimize.TNT.DestroyBlocks", true);
		config.setComments("Options.Optimize.TNT.DestroyBlocks", Arrays.asList("# Can TNT explosion destroy blocks","# defaulty: true"));
		config.addDefault("Options.Optimize.TNT.DamageEntities", true);
		config.setComments("Options.Optimize.TNT.DamageEntities", Arrays.asList("# Can TNT explosion damage entities around","# defaulty: true"));
		config.addDefault("Options.Optimize.TNT.Power", 1);
		config.setComments("Options.Optimize.TNT.Power", Arrays.asList("# Power of TNT explosion","# defaulty: 1"));
		config.addDefault("Options.Optimize.TNT.Drops.Allowed", true);
		config.setComments("Options.Optimize.TNT.Drops.Allowed", Arrays.asList("# Drop TNT explosion drops from destroyed blocks","# defaulty: true"));
		config.addDefault("Options.Optimize.TNT.Drops.InSingleLocation", true);
		config.setComments("Options.Optimize.TNT.Drops.InSingleLocation", Arrays.asList("# Drop TNT explosion drops to TNT's location","# defaulty: true"));
		config.addDefault("Options.Optimize.TNT.Drops.InFirstTNTLocation", false);
		config.setComments("Options.Optimize.TNT.Drops.InFirstTNTLocation", Arrays.asList("# Drop TNT explosion drops to first TNT's location","# This can be used for mining system","# defaulty: false"));
		config.addDefault("Options.Optimize.TNT.CollidingTNT.Use", true);
		config.setComments("Options.Optimize.TNT.CollidingTNT.Use", Arrays.asList("# Can TNT explosion explode colliding TNTs","# defaulty: true"));
		config.addDefault("Options.Optimize.TNT.CollidingTNT.IgniteTime", 3); // 0 is ultra fast, but with ultra lag
		config.setComments("Options.Optimize.TNT.CollidingTNT.IgniteTime", Arrays.asList("# Time to ignite TNT","# defaulty: 3"));
		config.addDefault("Options.Optimize.TNT.Action.LowMememory", "# WAIT");
		config.setComments("Options.Optimize.TNT.Action", Arrays.asList("# Action types: WAIT, DROP"));
		config.setComments("Options.Optimize.TNT.Action.LowMememory", Arrays.asList("# What TheAPI have to do when is low memory (ex.: free 100mb)","# defaulty: WAIT"));
		config.addDefault("Options.Optimize.TNT.Action.LowTPS", "WAIT");
		config.setComments("Options.Optimize.TNT.Action.LowTPS", Arrays.asList("# What TheAPI have to do when is low tps (ex.: 15 TPS)","# defaulty: WAIT"));
		config.addDefault("Options.Optimize.TNT.SpawnTNT", false); //defaulty false, more friendly to server
		config.setComments("Options.Optimize.TNT.SpawnTNT", Arrays.asList("# If you enable this, TheAPI will spawn ignite TNT entities","# Big amount of ignite TNT entities server can be laggy","# defaulty: false"));
		config.addDefault("Options.EntityMoveEvent.Enabled", true);
		config.setComments("Options.EntityMoveEvent.Enabled", Arrays.asList("# Enable EntityMoveEvent event","# defaulty: true"));
		config.addDefault("Options.EntityMoveEvent.Reflesh", 3);
		config.setComments("Options.EntityMoveEvent.Reflesh", Arrays.asList("# Ticks to look for entity move action","# defaulty: 3"));
		config.addDefault("Options.FakeEconomyAPI.Symbol", "$");
		config.setComments("Options.FakeEconomyAPI", Arrays.asList(""));
		config.setComments("Options.FakeEconomyAPI.Symbol", Arrays.asList("# Economy symbol of FakeEconomyAPI","# defaulty: $"));
		config.addDefault("Options.FakeEconomyAPI.Format", "$%money%");
		config.setComments("Options.FakeEconomyAPI.Format", Arrays.asList("# Economy format of FakeEconomyAPI","# defaulty: $%money%"));
		config.save();
		max=Bukkit.getMaxPlayers();
		motd=Bukkit.getMotd();
	}
	
	private static ThePlaceholder main;
	public void loadPlaceholders() {
		main = new ThePlaceholder("TheAPI") {
			@SuppressWarnings("deprecation")
			@Override
			public String onRequest(Player player, String placeholder) {
				if(player!=null) {
				if(placeholder.equalsIgnoreCase("player_money"))
					return ""+EconomyAPI.getBalance(player);
				if(placeholder.equalsIgnoreCase("player_formated_money"))
					return EconomyAPI.format(EconomyAPI.getBalance(player));
				if(placeholder.equalsIgnoreCase("player_displayname"))
					return player.getDisplayName();
				if(placeholder.equalsIgnoreCase("player_customname"))
					return player.getCustomName();
				if(placeholder.equalsIgnoreCase("player_name"))
					return player.getName();
				if(placeholder.equalsIgnoreCase("player_gamemode"))
					return player.getGameMode().name();
				if(placeholder.equalsIgnoreCase("player_uuid"))
					return player.getUniqueId().toString();
				if(placeholder.equalsIgnoreCase("player_health"))
					return ""+((Damageable)player).getHealth();
				if(placeholder.equalsIgnoreCase("player_food"))
					return ""+player.getFoodLevel();
				if(placeholder.equalsIgnoreCase("player_exp"))
					return ""+player.getExp();
				if(placeholder.equalsIgnoreCase("player_ping"))
					return ""+TheAPI.getPlayerPing(player);
				if(placeholder.equalsIgnoreCase("player_level"))
					return ""+player.getLevel();
				if(placeholder.equalsIgnoreCase("player_maxhealth"))
					return ""+((Damageable)player).getMaxHealth();
				if(placeholder.equalsIgnoreCase("player_world"))
					return ""+player.getWorld().getName();
				if(placeholder.equalsIgnoreCase("player_air"))
					return ""+player.getRemainingAir();
				if(placeholder.equalsIgnoreCase("player_statistic_play_one_tick"))
					return ""+player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK"));
				if(placeholder.equalsIgnoreCase("player_statistic_play_one_minue"))
					return ""+player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"));
				if(placeholder.equalsIgnoreCase("player_statistic_kills"))
					return ""+player.getStatistic(Statistic.PLAYER_KILLS);
				if(placeholder.equalsIgnoreCase("player_statistic_deaths"))
					return ""+player.getStatistic(Statistic.DEATHS);
				if(placeholder.equalsIgnoreCase("player_statistic_jump"))
					return ""+player.getStatistic(Statistic.JUMP);
				if(placeholder.equalsIgnoreCase("player_statistic_entity_kill"))
					return ""+player.getStatistic(Statistic.KILL_ENTITY);
				if(placeholder.equalsIgnoreCase("player_statistic_sneak_time"))
					return ""+player.getStatistic(Statistic.valueOf("SNEAK_TIME"));
			}
				if(placeholder.equalsIgnoreCase("server_time"))
					return ""+new SimpleDateFormat("HH:mm:ss").format(new Date());
				if(placeholder.equalsIgnoreCase("server_date"))
					return ""+new SimpleDateFormat("dd.MM.yyyy").format(new Date());
				if(placeholder.equalsIgnoreCase("server_online"))
					return ""+TheAPI.getOnlinePlayers().size();
				if(placeholder.equalsIgnoreCase("server_maxonline"))
					return ""+TheAPI.getMaxPlayers();
				if(placeholder.equalsIgnoreCase("server_version"))
					return Bukkit.getBukkitVersion();
				if(placeholder.equalsIgnoreCase("server_motd"))
					return motd!=null?motd:"";
				if(placeholder.equalsIgnoreCase("server_worlds"))
					return ""+Bukkit.getWorlds().size();
				if(placeholder.equalsIgnoreCase("server_tps"))
					return ""+TheAPI.getServerTPS();
				if(placeholder.equalsIgnoreCase("server_memory_max"))
					return ""+MemoryAPI.getMaxMemory();
				if(placeholder.equalsIgnoreCase("server_memory_used"))
					return ""+MemoryAPI.getUsedMemory(false);
				if(placeholder.equalsIgnoreCase("server_memory_free"))
					return ""+MemoryAPI.getFreeMemory(false);
				return null;
			}
		};
		main.register();
	}

	public void loadWorlds() {
		if (config.exists("Worlds")) {
			if (!config.getStringList("Worlds").isEmpty()) {
				TheAPI.msg("&cTheAPI&7: &8********************",TheAPI.getConsole());
				TheAPI.msg("&cTheAPI&7: &6Action: &eLoading worlds..",TheAPI.getConsole());
				TheAPI.msg("&cTheAPI&7: &8********************",TheAPI.getConsole());
				for (String s : config.getStringList("Worlds")) {
					String type = "Default";
					for (String w : Arrays.asList("Default", "Normal", "Nether", "The_End", "End", "The_Void", "Void",
							"Empty", "Flat")) {
						if (config.exists("WorldsSetting." + s)) {
							if (config.getString("WorldsSetting." + s + ".Generator").equalsIgnoreCase(w)) {
								if (w.equalsIgnoreCase("Flat"))
									type = "Flat";
								if (w.equalsIgnoreCase("Nether"))
									type = "Nether";
								if (w.equalsIgnoreCase("The_End") || w.equalsIgnoreCase("End"))
									type = "The_End";
								if (w.equalsIgnoreCase("The_Void") || w.equalsIgnoreCase("Void")
										|| w.equalsIgnoreCase("Empty"))
									type = "The_Void";
								break;
							}
						} else
							break;
					}
					Environment env = Environment.NORMAL;
					WorldType wt = WorldType.NORMAL;
					if (type.equals("Flat"))
						wt = WorldType.FLAT;
					if (type.equals("The_Void"))
						env = null;
					if (type.equals("The_End")) {
						try {
							env = Environment.valueOf("THE_END");
						} catch (Exception e) {
							env = Environment.valueOf("END");
						}
					}
					if (type.equals("Nether"))
						env = Environment.NETHER;
					boolean f = true;
					if (config.exists("WorldsSetting." + s + ".GenerateStructures"))
						f = config.getBoolean("WorldsSetting." + s + ".GenerateStructures");
					WorldsAPI.create(s, env, wt, f, 0);
					TheAPI.msg("&bTheAPI&7: &eWorld with name '&6" + s + "&e' loaded.", TheAPI.getConsole());
				}
			}
		}
	}

	private boolean getVaultEconomy() {
		try {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();

			}
			return economy != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void vaultHooking() {
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eLooking for Vault Economy..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		new Tasker() {
			@Override
			public void run() {
				if (getVaultEconomy()) {
					e = true;
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &eFound Vault Economy", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
					cancel();
				}
			}
		}.runTimer(0, 20, 15);
	}

	public void TheVaultHooking() {
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eLooking for TheVault Economy and Bank system..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		new Tasker() {
			boolean as = false, b = false;
			public void run() {
				if (TheVault.getEconomy() != null && !as) {
					as = true;
					tveeconomy = TheVault.getEconomy();
					tve = true;
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &eFound TheVault Economy", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
				}
				if (TheVault.getBank() != null && !b) {
					b = true;
					bank = TheVault.getBank();
					tbank = true;
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &eFound TheVault Bank system", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
				}
				if (as && b)
					cancel();
			}
		}.runTimer(0, 20, 15);
	}
}
