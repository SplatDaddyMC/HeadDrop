package me.rrs.headdrop.commands;

import me.rrs.headdrop.HeadDrop;
import me.rrs.headdrop.listener.GUI;
import me.rrs.headdrop.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Lang lang = new Lang();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
	        sender.sendMessage("HeadDrop" + ChatColor.RESET + " plugin by RRS " +
	            ChatColor.of(new Color(255, 255, 255)) + "(Forked for " + 
	            ChatColor.of(new Color(255, 85, 85)) + "" + ChatColor.BOLD + "S" + 
	            ChatColor.of(new Color(239, 191, 4)) + ChatColor.BOLD + "traight " + 
	            ChatColor.of(new Color(255, 85, 85)) + "" + ChatColor.BOLD + "U" + 
	            ChatColor.of(new Color(239, 191, 4)) + ChatColor.BOLD + "p" + 
	            ChatColor.RESET + ChatColor.of(new Color(255, 255, 255)) + ")\n" + 
	            ChatColor.of(new Color(255, 85, 85)) + "> " + ChatColor.of(new Color(239, 191, 4)) + 
	            "/headdrop leaderboard" + ChatColor.of(new Color(255, 85, 85)) + " -> " + ChatColor.RESET + "view the kill leaderboard."
	        );
        } else {
            switch (args[0].toLowerCase()) {
                case "help":
                    sendHelpMessage(sender);
                    break;
                case "reload":
                    reloadConfigAndLang(sender);
                    break;
                case "leaderboard":
                    showLeaderboard(sender);
                    break;
                case "debug":
                    generateDebugFile(sender);
                    break;
                /*case "gui":
                    openGUI(sender);
                    break;*/
            }
        }
        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        if (sender instanceof Player player) {
            // Convert standard color names to RGB equivalents:
            // DARK_GREEN -> (0, 170, 0)
            // AQUA -> (85, 255, 255)
            // LIGHT_PURPLE -> (255, 85, 255)
            // RESET remains as provided by ChatColor.RESET
            player.sendMessage(
	            "HeadDrop" + ChatColor.RESET + " plugin by RRS " +
	            ChatColor.of(new Color(255, 255, 255)) + "(Forked for " + 
	            ChatColor.of(new Color(255, 85, 85)) + "" + ChatColor.BOLD + "S" + 
	            ChatColor.of(new Color(239, 191, 4)) + ChatColor.BOLD + "traight " + 
	            ChatColor.of(new Color(255, 85, 85)) + "" + ChatColor.BOLD + "U" + 
	            ChatColor.of(new Color(239, 191, 4)) + ChatColor.BOLD + "p" + 
	            ChatColor.RESET + ChatColor.of(new Color(255, 255, 255)) + ")\n" + 
	            ChatColor.of(new Color(255, 85, 85)) + "> " + ChatColor.of(new Color(239, 191, 4)) + 
	            "/headdrop leaderboard" + ChatColor.of(new Color(255, 85, 85)) + " -> " + ChatColor.RESET + "view the kill leaderboard."
                //ChatColor.of(new Color(85, 255, 255)) + "> " + ChatColor.of(new Color(255, 85, 255)) + "/headdrop help" + ChatColor.RESET + " -> you already discovered it!",
                //ChatColor.of(new Color(85, 255, 255)) + "> " + ChatColor.of(new Color(255, 85, 255)) + "/headdrop reload" + ChatColor.RESET + " -> reload plugin config.",
                //ChatColor.of(new Color(85, 255, 255)) + "> " + ChatColor.of(new Color(255, 85, 255)) + "/myhead" + ChatColor.RESET + " -> Get your head.",
                //ChatColor.of(new Color(85, 255, 255)) + "> " + ChatColor.of(new Color(255, 85, 255)) + "/head <player Name>" + ChatColor.RESET + " -> Get another player head"
            );
        }
    }

    private void reloadConfigAndLang(CommandSender sender) {
        if (sender instanceof Player player) {
            if (player.hasPermission("headdrop.reload")) {
                try {
                    HeadDrop.getInstance().getLang().reload();
                    HeadDrop.getInstance().getConfiguration().reload();
                    // GREEN -> (85, 255, 85)
                    lang.msg(ChatColor.of(new Color(85, 255, 85)) + "[HeadDrop] " + ChatColor.RESET, "Reload", player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                lang.noPerm(player);
            }
        } else {
            try {
                HeadDrop.getInstance().getConfiguration().reload();
                HeadDrop.getInstance().getLang().reload();
                Bukkit.getLogger().info(HeadDrop.getInstance().getLang().getString("Reload"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

		private void showLeaderboard(CommandSender sender) {
		    if (!HeadDrop.getInstance().getConfiguration().getBoolean("Database.Enable")) {
		        Bukkit.getLogger().severe("[HeadDrop] Enable database on config!");
		        if (sender instanceof Player) sender.sendMessage("[HeadDrop] Check console log!");
		        return;
		    }
		
		    Map<String, Integer> playerData = HeadDrop.getInstance().getDatabase().getPlayerData();
		    List<Map.Entry<String, Integer>> sortedData = new ArrayList<>(playerData.entrySet());
		    sortedData.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
		
		    sender.sendMessage(ChatColor.of(new Color(255, 85, 85)) + "--- Top Head Hunters ---");
		
		    for (int i = 0; i < Math.min(sortedData.size(), 10); i++) {
		        Map.Entry<String, Integer> entry = sortedData.get(i);
		
		        ChatColor numberColor;
		        ChatColor rankColor;
		
		        switch (i) {
		            case 0:
		                numberColor = ChatColor.of(new Color(239, 191, 4));
		                rankColor = ChatColor.of(new Color(239, 191, 4));
		                break;
		            case 1:
		                numberColor = ChatColor.of(new Color(191, 201, 202));
		                rankColor = ChatColor.of(new Color(191, 201, 202));
		                break;
		            case 2:
		                numberColor = ChatColor.of(new Color(206, 137, 70));
		                rankColor = ChatColor.of(new Color(206, 137, 70));
		                break;
		            default:
		                numberColor = ChatColor.of(new Color(255, 255, 85));
		                rankColor = ChatColor.of(new Color(255, 255, 255));
		                break;
		        }
		
		        sender.sendMessage(numberColor.toString() + (i + 1) + ". " + rankColor.toString() + entry.getKey()
		                + " - " + entry.getValue() + " Kills");
		    }
		
		    sender.sendMessage(ChatColor.of(new Color(255, 85, 85)) + "-----------------------");
		}

    private void generateDebugFile(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            try {
                File debugFile = new File(HeadDrop.getInstance().getDataFolder().getAbsolutePath() + File.separator + "debug.txt");
                if (debugFile.exists()) {
                    debugFile.delete();
                }
                debugFile.createNewFile();

                try (FileWriter writer = new FileWriter(debugFile)) {
                    writer.write("Server Name: " + Bukkit.getServer().getName() + "\n");
                    writer.write("Server Version: " + Bukkit.getServer().getVersion() + "\n");
                    writer.write("Plugin Version: " + HeadDrop.getInstance().getDescription().getVersion() + "\n");
                    writer.write("Java Version: " + System.getProperty("java.version") + "\n");
                    writer.write("Operating System: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n");
                    writer.write("\n");
                    writer.write("Require-Killer-Player: " + HeadDrop.getInstance().getConfiguration().getBoolean("Config.Require-Killer-Player") + "\n");
                    writer.write("Killer-Require-Permission: " + HeadDrop.getInstance().getConfiguration().getBoolean("Config.Killer-Require-Permission") + "\n");
                    writer.write("Enable-Looting: " + HeadDrop.getInstance().getConfiguration().getBoolean("Config.Enable-Looting") + "\n");
                    writer.write("Enable-Perm-Chance: " + HeadDrop.getInstance().getConfiguration().getBoolean("Config.Enable-Perm-Chance") + "\n");
                    writer.write("Database: " + HeadDrop.getInstance().getConfiguration().getBoolean("Database.Online") + "\n");
                    writer.write("Premium: True\n");
                }
                Bukkit.getLogger().info("[HeadDrop-Debug] debug.txt file created!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openGUI(CommandSender sender) {
        if (sender instanceof Player player) {
            GUI gui = new GUI();
            player.openInventory(gui.getInventory());
        } else {
            lang.pcmd();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equals("headdrop") && args.length == 1) {
            //return Arrays.asList("help", "reload", "leaderboard", "gui");
			return Arrays.asList("help", "reload", "leaderboard");
        }
        return Collections.emptyList();
    }
}
