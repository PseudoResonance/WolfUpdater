package io.github.wolfleader116.wolfupdater.commands;

import io.github.wolfleader116.wolfupdater.WolfUpdater;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WolfUpdaterC implements CommandExecutor {

	private static final Logger log = Logger.getLogger("Minecraft");

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("wolfupdater")) {
			File configFile = new File(WolfUpdater.plugin.getDataFolder(), "config.yml");
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					log.info("Use /wolfupdater help for a list of commands.");
					log.info("WolfUpdater plugin created by WolfLeader116");
					log.info("===---WolfUpdater Info---===");
				} else if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("help")) {
						log.info("/wolfupdater update Checks for plugin updates.");
						log.info("/wolfupdater reload Reloads the config.");
						log.info("/wolfupdater reset Resets the config.");
						log.info("/wolfupdater help Shows this message.");
						log.info("/wolfupdater Shows the info page.");
						log.info("===---WolfUpdater Help---===");
					} else if (args[0].equalsIgnoreCase("reset")) {
						configFile.delete();
						WolfUpdater.plugin.saveDefaultConfig();
						log.info("Reset the config!");
					} else if (args[0].equalsIgnoreCase("reload")) {
						WolfUpdater.plugin.reloadConfig();
						log.info("Reloaded the config!");
					} else if (args[0].equalsIgnoreCase("update")) {
						WolfUpdater.updateCheck(false);
						log.info("Started plugin update check.");
					} else {
						log.info("Unknown subcommand!");
					}
				}
			} else {
				Player p = (Player) sender;
				if (args.length == 0) {
					sender.sendMessage(ChatColor.DARK_AQUA + "===---" + ChatColor.GOLD + "WolfUpdater Info" + ChatColor.DARK_AQUA + "---===");
					sender.sendMessage(ChatColor.AQUA + "WolfUpdater plugin created by WolfLeader116");
					sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.RED + "/wolfupdater help " + ChatColor.AQUA + "for a list of commands.");
				} else if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("help")) {
						sender.sendMessage(ChatColor.DARK_AQUA + "===---" + ChatColor.GOLD + "WolfUpdater Help" + ChatColor.DARK_AQUA + "---===");
						sender.sendMessage(ChatColor.RED + "/wolfupdater " + ChatColor.AQUA + "Shows the info page.");
						sender.sendMessage(ChatColor.RED + "/wolfupdater help " + ChatColor.AQUA + "Shows this message.");
						if (sender.hasPermission("wolfupdater.reset")) {
							sender.sendMessage(ChatColor.RED + "/wolfupdater reset " + ChatColor.AQUA + "Resets the config.");
						}
						if (sender.hasPermission("wolfupdater.reload")) {
							sender.sendMessage(ChatColor.RED + "/wolfupdater reload " + ChatColor.AQUA + "Reloads the config.");
						}
						if (sender.hasPermission("wolfupdater.update")) {
							sender.sendMessage(ChatColor.RED + "/wolfupdater update " + ChatColor.AQUA + "Checks for plugin updates.");
						}
					} else if (args[0].equalsIgnoreCase("reset")) {
						if (sender.hasPermission("wolfupdater.reset")) {
							configFile.delete();
							WolfUpdater.plugin.saveDefaultConfig();
							p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Reset the config!");
						} else {
							p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.RED + "You do not have permission to do this!");
						}
					} else if (args[0].equalsIgnoreCase("reload")) {
						if (sender.hasPermission("wolfupdater.reload")) {
							WolfUpdater.plugin.reloadConfig();
							p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Reloaded the config!");
						} else {
							p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.RED + "You do not have permission to do this!");
						}
					} else if (args[0].equalsIgnoreCase("update")) {
						if (sender.hasPermission("wolfupdater.update")) {
							WolfUpdater.updateCheck(false);
							p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Started plugin update check.");
						} else {
							p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.RED + "You do not have permission to do this!");
						}
					} else {
						p.sendMessage(ChatColor.BLUE + "WolfUpdater> " + ChatColor.GREEN + "Unknown subcommand!");
					}
				}
			}
		}
		return false;
	}
}
