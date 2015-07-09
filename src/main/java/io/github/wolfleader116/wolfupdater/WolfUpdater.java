package io.github.wolfleader116.wolfupdater;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

public class WolfUpdater extends JavaPlugin implements Listener {
	
	public static int updatesfound = 0;
	
	public static boolean restartnoplayers = true;
	public static boolean loginfail = false;
	
	public static WolfUpdater plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		this.saveDefaultConfig();
		if (this.getConfig().getInt("Version") != 1) {
			File config = new File(this.getDataFolder(), "config.yml");
			config.delete();
			this.saveDefaultConfig();
		}
		if (this.getConfig().getBoolean("CheckOnStartup")) {
			updateCheck(true);
		}
	}
	
	@Override
	public void onDisable() {
		plugin = null;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent e) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				if (Bukkit.getServer().getOnlinePlayers().size() == 0) {
					restart(true);
				}
			}
		}, 20);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent e) {
		if (loginfail) {
			e.disallow(Result.KICK_OTHER, "§cServer is restarting soon! Please come back in a few minutes.");
		}
	}
	
	public static void startUpdateCheckLoop() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				updateCheck(true);
			}
		}, Long.valueOf(plugin.getConfig().getInt("UpdateCheckTime")));
	}
	
	public static void update(Plugin plugin) {
		File file = new File(Bukkit.getServer().getUpdateFolder() + "/..");
		try {
			FileUtils.copyURLToFile(new URL("https://drone.io/github.com/WolfLeader116/" + plugin.getName() + "/files/target/" + plugin.getName() + ".jar"), file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateSelf() {
		File file = new File(Bukkit.getServer().getUpdateFolder());
		try {
			FileUtils.copyURLToFile(new URL("https://drone.io/github.com/WolfLeader116/WolfUpdater/files/target/WolfUpdater.jar"), file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void restart(boolean noplayers) {
		if (noplayers == false) {
			if (plugin.getConfig().getBoolean("RestartNoPlayers") && Bukkit.getServer().getOnlinePlayers().size() != 0) {
				restartnoplayers = true;
			} else {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast &c&lSERVER RESTARTING IN 1 MINUTE TO PERFORM UPDATES!");
				loginfail = true;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast &c&lSERVER RESTARTING NOW TO PERFORM UPDATES!");
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								for (Player p : Bukkit.getOnlinePlayers()) {
									p.kickPlayer("§cServer is restarting! Please come back in a few minutes.");
								}
								Bukkit.getServer().shutdown();
							}
						}, 20);
					}
				}, 1200);
			}
		} else {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast &c&lSERVER RESTARTING NOW TO PERFORM UPDATES!");
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.kickPlayer("§cServer is restarting! Please come back in a few minutes.");
					}
					Bukkit.getServer().shutdown();
				}
			}, 20);
		}
	}
	
	public static void reload() {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "broadcast &c&lSERVER RELOADING NOW TO PERFORM UPDATES!");
		Bukkit.getServer().reload();
	}
	
	public static void updateCheckComplete() {
		if (plugin.getConfig().getString("Action").equalsIgnoreCase("restart")) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.isOp() || p.hasPermission("wolfupdater.notify") && plugin.getConfig().getBoolean("UpdateNotify")) {
					if (updatesfound == 0) {
						p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Server has completed checking for plugin updates. There were no updates found.");
					} else if (updatesfound > 0) {
						p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Server has completed checking for plugin updates. " + String.valueOf(updatesfound) + " updates were found and downloaded. Server will now restart.");
					}
				}
			}
			if (updatesfound > 0) {
				restart(false);
			}
		} else if (plugin.getConfig().getString("Action").equalsIgnoreCase("reload")) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.isOp() || p.hasPermission("wolfupdater.notify") && plugin.getConfig().getBoolean("UpdateNotify")) {
					if (updatesfound == 0) {
						p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Server has completed checking for plugin updates. There were no updates found.");
					} else if (updatesfound > 0) {
						p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Server has completed checking for plugin updates. " + String.valueOf(updatesfound) + " updates were found and downloaded. Server will now reload.");
					}
				}
			}
			if (updatesfound > 0) {
				reload();
			}
		} else if (plugin.getConfig().getString("Action").equalsIgnoreCase("none")) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.isOp() || p.hasPermission("wolfupdater.notify") && plugin.getConfig().getBoolean("UpdateNotify")) {
					if (updatesfound == 0) {
						p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Server has completed checking for plugin updates. There were no updates found.");
					} else if (updatesfound > 0) {
						p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Server has completed checking for plugin updates. " + String.valueOf(updatesfound) + " updates were found and downloaded.");
					}
				}
			}
		}
	}
	
	public static void updateCheck(boolean automatic) {
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.isOp() || p.hasPermission("wolfupdater.notify") && plugin.getConfig().getBoolean("UpdateNotify")) {
				p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Server has started checking for updates.");
			}
		}
		Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
		for(int i = 0; i < plugins.length; i++) {
			if (plugins[i].getClass().getCanonicalName().startsWith("io.github.wolfleader116") && plugins[i].getName() != "WolfUpdater") {
				String version = plugins[i].getDescription().getVersion();
				JSONObject json = JsonReader.readJsonFromUrl("https://api.github.com/repos/WolfLeader116/"+ plugins[i].getDescription().getName() + "/releases/latest");
				String ver;
				try {
					ver = json.getString("tag_name");
				} catch (Exception e) {
					ver = "0";
					e.printStackTrace();
				}
				String[] versions = version.split(".");
				String[] vers = ver.split(".");
				if (vers[0] != null && versions[0] != null && Integer.valueOf(vers[0]) > Integer.valueOf(versions[0])) {
					update(plugins[i]);
				} else if (vers[0] != null && versions[0] == null) {
					update(plugins[i]);
				} else {
					if (vers[1] != null && versions[1] != null && Integer.valueOf(vers[1]) > Integer.valueOf(versions[1])) {
						update(plugins[i]);
					} else if (vers[1] != null && versions[1] == null) {
						update(plugins[i]);
					} else {
						if (vers[2] != null && versions[2] != null && Integer.valueOf(vers[2]) > Integer.valueOf(versions[2])) {
							update(plugins[i]);
						} else if (vers[2] != null && versions[1] == null) {
							update(plugins[i]);
						} else {
							if (vers[3] != null && versions[3] != null && Integer.valueOf(vers[3]) > Integer.valueOf(versions[3])) {
								update(plugins[i]);
							} else if (vers[3] != null && versions[3] == null) {
								update(plugins[i]);
							} else {
								if (vers[4] != null && versions[4] != null && Integer.valueOf(vers[4]) > Integer.valueOf(versions[4])) {
									update(plugins[i]);
								} else if (vers[4] != null && versions[4] == null) {
									update(plugins[i]);
								} else {
									if (vers[5] != null && versions[5] != null && Integer.valueOf(vers[5]) > Integer.valueOf(versions[5])) {
										update(plugins[i]);
									} else if (vers[5] != null && versions[5] == null) {
										update(plugins[i]);
									} else {
										if (vers[6] != null && versions[6] != null && Integer.valueOf(vers[6]) > Integer.valueOf(versions[6])) {
											update(plugins[i]);
										} else if (vers[6] != null && versions[6] == null) {
											update(plugins[i]);
										} else {
											update(plugins[i]);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		String version = plugin.getDescription().getVersion();
		JSONObject json = JsonReader.readJsonFromUrl("https://api.github.com/repos/WolfLeader116/WolfUpdater/releases/latest");
		String ver;
		try {
			ver = json.getString("tag_name");
		} catch (Exception e) {
			ver = "0";
			e.printStackTrace();
		}
		String[] versions = version.split(".");
		String[] vers = ver.split(".");
		if (vers[0] != null && versions[0] != null && Integer.valueOf(vers[0]) > Integer.valueOf(versions[0])) {
			updateSelf();
		} else if (vers[0] != null && versions[0] == null) {
			updateSelf();
		} else {
			if (vers[1] != null && versions[1] != null && Integer.valueOf(vers[1]) > Integer.valueOf(versions[1])) {
				updateSelf();
			} else if (vers[1] != null && versions[1] == null) {
				updateSelf();
			} else {
				if (vers[2] != null && versions[2] != null && Integer.valueOf(vers[2]) > Integer.valueOf(versions[2])) {
					updateSelf();
				} else if (vers[2] != null && versions[1] == null) {
					updateSelf();
				} else {
					if (vers[3] != null && versions[3] != null && Integer.valueOf(vers[3]) > Integer.valueOf(versions[3])) {
						updateSelf();
					} else if (vers[3] != null && versions[3] == null) {
						updateSelf();
					} else {
						if (vers[4] != null && versions[4] != null && Integer.valueOf(vers[4]) > Integer.valueOf(versions[4])) {
							updateSelf();
						} else if (vers[4] != null && versions[4] == null) {
							updateSelf();
						} else {
							if (vers[5] != null && versions[5] != null && Integer.valueOf(vers[5]) > Integer.valueOf(versions[5])) {
								updateSelf();
							} else if (vers[5] != null && versions[5] == null) {
								updateSelf();
							} else {
								if (vers[6] != null && versions[6] != null && Integer.valueOf(vers[6]) > Integer.valueOf(versions[6])) {
									updateSelf();
								} else if (vers[6] != null && versions[6] == null) {
									updateSelf();
								} else {
									updateSelf();
								}
							}
						}
					}
				}
			}
		}
		if (automatic && plugin.getConfig().getBoolean("AutoCheck")) {
			startUpdateCheckLoop();
		}
		updateCheckComplete();
	}
	
}