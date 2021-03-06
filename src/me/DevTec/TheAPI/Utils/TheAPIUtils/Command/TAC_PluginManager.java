package me.DevTec.TheAPI.Utils.TheAPIUtils.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.APIs.PluginManagerAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class TAC_PluginManager {

	public TAC_PluginManager(CommandSender s, String[] args) {
		if (args.length == 1) {
			TheAPI.msg("&e/TheAPI PluginManager Enable <plugin>", s);
			TheAPI.msg("&e/TheAPI PluginManager Disable <plugin>", s);
			TheAPI.msg("&e/TheAPI PluginManager Load <plugin>", s);
			TheAPI.msg("&e/TheAPI PluginManager Unload <plugin>", s);
			TheAPI.msg("&e/TheAPI PluginManager Reload <plugin>", s);
			TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
			TheAPI.msg("&e/TheAPI PluginManager EnableAll", s);
			TheAPI.msg("&e/TheAPI PluginManager DisableAll", s);
			TheAPI.msg("&e/TheAPI PluginManager LoadAll", s);
			TheAPI.msg("&e/TheAPI PluginManager UnloadAll", s);
			TheAPI.msg("&e/TheAPI PluginManager ReloadAll", s);
			TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
			TheAPI.msg("&e/TheAPI PluginManager Files", s);
			List<Plugin> pl =  PluginManagerAPI.getPlugins();
			String sd = "";
			for (Plugin w :pl)
				sd+=(sd.equals("")?"":"&7, ")+getPlugin(w)+" &6v"+w.getDescription().getVersion();
			TheAPI.msg("&7Plugins ("+pl.size()+"): "+sd, s);
			return;
		}
		if (args[1].equalsIgnoreCase("Files")||args[1].equalsIgnoreCase("notloaded")||args[1].equalsIgnoreCase( "toload")||args[1].equalsIgnoreCase( "unloaded")) {
			HashMap<String, String> d = PluginManagerAPI.getPluginsToLoadWithNames();
			if(d.isEmpty()) {
				TheAPI.msg("&eNo plugin to load.", s);
				return;
			}
			TheAPI.msg("&ePlugins to load:", s);
			for(String a : d.keySet()) {
				String text = " &7- &e"+d.get(a)+" &7(&e"+a+"&7)";
			TheAPI.msg(text, s);
			}
			return;
		}
		if (args[1].equalsIgnoreCase("EnableAll")) {
			List<Plugin> ad = PluginManagerAPI.getPlugins();
			ad.remove(PluginManagerAPI.getPlugin(LoaderClass.plugin.getName()));
			if(ad.isEmpty()) {
				TheAPI.msg("&eNo plugin to enable.", s);
				return;
			}
			for(Plugin a : ad) {
				TheAPI.msg("&eEnabling plugin "+a.getName()+"..", s);
				PluginManagerAPI.enablePlugin(a);
				TheAPI.msg("&ePlugin "+a.getName()+" enabled.", s);
			}
			return;
		}
		if (args[1].equalsIgnoreCase("DisableAll")) {
			List<Plugin> ad = PluginManagerAPI.getPlugins();
			ad.remove(PluginManagerAPI.getPlugin(LoaderClass.plugin.getName()));
			if(ad.isEmpty()) {
				TheAPI.msg("&eNo plugin to disable.", s);
				return;
			}
			for(Plugin a : ad) {
				TheAPI.msg("&eDisabling plugin "+a.getName()+"..", s);
				PluginManagerAPI.disablePlugin(a);
				TheAPI.msg("&ePlugin "+a.getName()+" disabled.", s);
			}
			return;
		}
		if (args[1].equalsIgnoreCase("ReloadAll")) {
			if(PluginManagerAPI.getPlugins().isEmpty()) {
				TheAPI.msg("&eNo plugin to reload.", s);
				return;
			}
			for(Plugin a : PluginManagerAPI.getPlugins()) {
				TheAPI.msg("&eReloading plugin "+a.getName()+"..", s);
				PluginManagerAPI.reloadPlugin(a);
				TheAPI.msg("&ePlugin "+a.getName()+" reloaded.", s);
			}
			return;
		}
		if (args[1].equalsIgnoreCase("LoadAll")) {
			if(PluginManagerAPI.getPluginsToLoad().isEmpty()) {
				TheAPI.msg("&eNo plugin to load.", s);
				return;
			}
			for(String a : PluginManagerAPI.getPluginsToLoad()) {
				if(a.equals("TheAPI"))continue;
				TheAPI.msg("&eLoading plugin "+a+"..", s);
				PluginManagerAPI.loadPlugin(a);
				TheAPI.msg("&ePlugin "+a+" loaded.", s);
			}
			return;
		}
		if (args[1].equalsIgnoreCase("UnloadAll")) {
			List<Plugin> ad = PluginManagerAPI.getPlugins();
			ad.remove(PluginManagerAPI.getPlugin(LoaderClass.plugin.getName()));
			if(ad.isEmpty()) {
				TheAPI.msg("&eNo plugin to unload.", s);
				return;
			}
			for(Plugin a : ad) {
				TheAPI.msg("&eUnloading plugin "+a.getName()+"..", s);
				PluginManagerAPI.unloadPlugin(a);
				TheAPI.msg("&ePlugin "+a.getName()+" unloaded.", s);
			}
			return;
		}
		if (args[1].equalsIgnoreCase("Enable")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI PluginManager Enable <plugin>", s);
				return;
			}
			if(args[2].equals("TheAPI")) {
				TheAPI.msg("&eYou can't enable TheAPI.", s);
				return;
			}
			int f=0;
			for(Plugin a : PluginManagerAPI.getPlugins())
				if(a.getName().equals(args[2])) {
					if(a.isEnabled())f=1;
					else f = 2;
					break;
				}
			if(f==1) {
				TheAPI.msg("&7Plugin "+args[2]+" is already enabled.", s);
				return;
			}
			if(f==0) {
				TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
				return;
			}
			TheAPI.msg("&eEnabling plugin "+args[2]+"..", s);
			PluginManagerAPI.enablePlugin(args[2]);
			TheAPI.msg("&ePlugin "+args[2]+" enabled.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("Disable")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI PluginManager Disable <plugin>", s);
				return;
			}
			if(args[2].equals("TheAPI")) {
				TheAPI.msg("&eYou can't disable TheAPI.", s);
				return;
			}
			int f=0;
			for(Plugin a : PluginManagerAPI.getPlugins())
				if(a.getName().equals(args[2])) {
					if(a.isEnabled())f=1;
					else f = 2;
					break;
				}
			if(f==2) {
				TheAPI.msg("&7Plugin "+args[2]+" is already disabled.", s);
				return;
			}
			if(f==0) {
				TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
				return;
			}
			TheAPI.msg("&eDisabling plugin "+args[2]+"..", s);
			PluginManagerAPI.disablePlugin(args[2]);
			TheAPI.msg("&ePlugin "+args[2]+" disabled.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("Load")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI PluginManager Load <plugin>", s);
				return;
			}
			String pluginName = args[2];
			if(PluginManagerAPI.getRawPluginsToLoad().contains(pluginName)||PluginManagerAPI.getRawPluginsToLoad().contains(pluginName+".jar")
					||PluginManagerAPI.getPluginsToLoad().contains(pluginName)||PluginManagerAPI.getPluginsToLoad().contains(pluginName+".jar")) {
			if(pluginName.equals("TheAPI")) {
				TheAPI.msg("&eYou can't load TheAPI.", s);
				return;
			}
			int f=0;
			String real = null;
			for(Plugin a : PluginManagerAPI.getPlugins())
				if(a.getName().equals(pluginName)||PluginManagerAPI.getFileOfPlugin(a).getName().equals(pluginName)
						||PluginManagerAPI.getFileOfPlugin(a).getName().equals(pluginName+".jar")) {
					if(a.isEnabled())f=1;
					else f = 2;
					real=a.getName();
					break;
				}
			if(f==2) {
				TheAPI.msg("&7Plugin "+real+" is already loaded, but disabled.", s);
				return;
			}
			if(f==1) {
				TheAPI.msg("&7Plugin "+real+" is already loaded.", s);
				return;
			}
			real = PluginManagerAPI.getPluginNameByFile(pluginName);
			TheAPI.msg("&eLoading plugin "+real+" ("+pluginName+")..", s);
			PluginManagerAPI.loadPlugin(pluginName);
			TheAPI.msg("&ePlugin "+real+" ("+pluginName+") loaded & enabled.", s);
			return;
			}
			if (pluginName.endsWith(".jar"))
				pluginName = pluginName.substring(0, pluginName.length() - 4);
			TheAPI.msg("&7Plugin "+pluginName+" not found.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("Unload")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI PluginManager Unload <plugin>", s);
				return;
			}
			if(args[2].equals("TheAPI")) {
				TheAPI.msg("&eYou can't unload TheAPI.", s);
				return;
			}
			int f=0;
			for(Plugin a : PluginManagerAPI.getPlugins()) {
				if(a.getName().equals(args[2])) {
					if(a.isEnabled())f=1;
					else f = 2;
					break;
				}}
			if(f==0) {
				TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
				return;
			}
			TheAPI.msg("&eUnloading plugin "+args[2]+"..", s);
			PluginManagerAPI.unloadPlugin(args[2]);
			TheAPI.msg("&ePlugin "+args[2]+" unloaded.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("Reload")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI PluginManager Reload <plugin>", s);
				return;
			}
			int i = 0;
			for(Plugin a : PluginManagerAPI.getPlugins())
				if(a.getName().equals(args[2])) {
					if(a.isEnabled())i=1;
					else i = 2;
					break;
				}
			if(i==0) {
				TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
				return;
			}
			TheAPI.msg("&eReloading plugin "+args[2]+"..", s);
			PluginManagerAPI.reloadPlugin(args[2]);
			TheAPI.msg("&ePlugin "+args[2]+" reloaded.", s);
			return;
		}
		if (args[1].equalsIgnoreCase("Info")||args[1].equalsIgnoreCase("information")||args[1].equalsIgnoreCase("informations")) {
			if (args.length == 2) {
				TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
				return;
			}
			int i = 0;
			for(Plugin a : PluginManagerAPI.getPlugins())
				if(a.getName().equals(args[2])) {
					if(a.isEnabled())i=1;
					else i = 2;
					break;
				}
			if(i==0) {
				TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
				return;
			}
			TheAPI.msg("&7Name: &e"+args[2], s);
			TheAPI.msg("&7State: &e"+(i==1?"Enabled":"Disabled"), s);
			if (PluginManagerAPI.getCommands(args[2]).size() != 0) {
				TheAPI.msg("&7Commands:", s);
				for (String a : PluginManagerAPI.getCommands(args[2]))
					TheAPI.msg("&7 - &e" + a, s);
			}
			if (PluginManagerAPI.getPermissions(args[2]).size() != 0) {
				TheAPI.msg("&7Permissions:", s);
				for (Permission a : PluginManagerAPI.getPermissions(args[2])) {
					TheAPI.msg("&7 » &e" + a.getName()+"&7:", s);
					Map<String, Boolean> c = a.getChildren();
					if(c.isEmpty()==false)
						for(String d : c.keySet())
				TheAPI.msg("&7   - "+(c.get(d) ? "&a" : "&c") + d, s);
				}
			}
			if (PluginManagerAPI.getVersion(args[2]) != null)
				TheAPI.msg("&7Version: &e"+PluginManagerAPI.getVersion(args[2]), s);
			if (PluginManagerAPI.getWebsite(args[2]) != null)
				TheAPI.msg("&7Website: &e"+PluginManagerAPI.getWebsite(args[2]), s);
			if (PluginManagerAPI.getMainClass(args[2]) != null)
				TheAPI.msg("&7MainClass: &e"+PluginManagerAPI.getMainClass(args[2]), s);
			if (!PluginManagerAPI.getAuthor(args[2]).isEmpty())
				TheAPI.msg("&7Author(s): &e"+StringUtils.join(PluginManagerAPI.getAuthor(args[2]),", "), s);
			if (!PluginManagerAPI.getSoftDepend(args[2]).isEmpty())
				TheAPI.msg("&7SoftDepend(s): &e"+StringUtils.join(PluginManagerAPI.getSoftDepend(args[2]),", "), s);
			if (!PluginManagerAPI.getDepend(args[2]).isEmpty())
				TheAPI.msg("&7Depend(s): &e"+StringUtils.join(PluginManagerAPI.getDepend(args[2]),", "), s);
			return;
		}
		TheAPI.msg("&e/TheAPI PluginManager Enable <plugin>", s);
		TheAPI.msg("&e/TheAPI PluginManager Disable <plugin>", s);
		TheAPI.msg("&e/TheAPI PluginManager Load <plugin>", s);
		TheAPI.msg("&e/TheAPI PluginManager Unload <plugin>", s);
		TheAPI.msg("&e/TheAPI PluginManager Reload <plugin>", s);
		TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
		TheAPI.msg("&e/TheAPI PluginManager EnableAll", s);
		TheAPI.msg("&e/TheAPI PluginManager DisableAll", s);
		TheAPI.msg("&e/TheAPI PluginManager LoadAll", s);
		TheAPI.msg("&e/TheAPI PluginManager UnloadAll", s);
		TheAPI.msg("&e/TheAPI PluginManager ReloadAll", s);
		TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
		TheAPI.msg("&e/TheAPI PluginManager Files", s);
		List<Plugin> pl =  PluginManagerAPI.getPlugins();
		String sd = "";
		for (Plugin w :pl)
			sd+=(sd.equals("")?"":"&7, ")+getPlugin(w)+" &6v"+w.getDescription().getVersion();
		TheAPI.msg("&7Plugins ("+pl.size()+"): "+sd, s);
		return;
	}

	public static String getPlugin(Plugin a) {
		if (a.isEnabled())
			return "&a" + a.getName();
		return "&c" + a.getName();
	}

}
