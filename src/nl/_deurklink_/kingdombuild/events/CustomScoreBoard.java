package nl._deurklink_.kingdombuild.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import nl._deurklink_.kingdombuild.Koth;
import nl._deurklink_.kingdombuild.Main;

public class CustomScoreBoard
{
	public void setScoreboard(Player p)
	{
		setScoreboard(p, p.getLocation());
	}
	public void setScoreboard(Player p, Location loc)
	{
		if(Main.r.staffModes.contains(p))
		{
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard board = manager.getNewScoreboard();
			Objective objective = board.registerNewObjective(p.getName(), "dummy");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	    
			objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.r.getConfig().getString("scoreboard_staff.title")));
	    
			int onlineStaff=0;
			for(Player wp : Bukkit.getOnlinePlayers())
			{
				if(wp.hasPermission("kingdom.ikbenstaff"))
				{
					onlineStaff++;
				}
			}
			
			List<String> Board = Main.r.getConfig().getStringList("scoreboard_staff.board");
			for (int i = 0; i < Board.size(); i++) 
			{
				objective.getScore(ChatColor.translateAlternateColorCodes('&', ((String)Board.get(i))
						.replace("%zichtbaarheid%", (Main.r.vanish.contains(p) ? "Hidden" : "Visible"))
						.replace("%staff_c%", onlineStaff+"")
						.replace("%flyspeed%", ((int)(p.getFlySpeed()*10) + "")))).setScore(Board.size() - i);
			}
			setPrefix(p, Main.r.getColorWithSpecials(p));
			
			for(String str : Main.r.customPrefix)
			{
				if(board.getTeam(str) == null)
				{
					board.registerNewTeam(str);
				}
			}
			
			for (Player wp : Main.r.prefix.keySet())
			{
				Team team = board.getTeam(((String)Main.r.prefix.get(wp))) == null ? board.registerNewTeam(((String)Main.r.prefix.get(wp))) : board.getTeam(((String)Main.r.prefix.get(wp)));
	      
				team.addEntry(wp.getName());
				team.setPrefix(((String)Main.r.prefix.get(wp)).replace("&", "§"));
			}
			p.setScoreboard(board);
		}
		else
		{
			if(Main.r.war == true)
			{
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
		    
				String ping = (((CraftPlayer)p).getHandle().ping + "").replace("ping", "");
				String inKingdom = Main.r.getInWitchKingdomAPlayerIs(loc.getBlock());
				
				Objective objective = board.registerNewObjective(p.getName(), "dummy");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		    
				String title = Main.r.getConfig().getString("scoreboard_war.title");
				objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
		    
				int totalSecs = Main.r.wartime;
				if(totalSecs < 0) totalSecs = 0;
				int hours = totalSecs / 3600;
				int minutes = (totalSecs % 3600) / 60;
				int seconds = totalSecs % 60;
				int totalDeads = 0;
				
				for(int val : Main.r.killsPerKD.values())
				{
					totalDeads += val;
				}
				
				String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);	
				
				List<String> Board = Main.r.getConfig().getStringList("scoreboard_war.board");
				for (int i = 0; i < Board.size(); i++) 
				{
					objective.getScore(ChatColor.translateAlternateColorCodes('&', ((String)Board.get(i))
							.replace("%loc%", Main.r.getTextColor(inKingdom).replace("-", ChatColor.WHITE + "Onbekend"))
							.replace("%points%", (Main.r.influenceIgnore.contains(p) ? "&c" : "&f")+Main.r.getPlayerPoints().getAPI().look(p.getUniqueId()) + "")
							.replace("%ping%", ping)
							.replace("%duur%", timeString)
							.replace("%deelnemers%", Bukkit.getOnlinePlayers().size() +"")
							.replace("%gesneuveld%", totalDeads + "")
							.replace("%rank%", Main.r.c.getString("rank." + Main.r.c.getStringList("ranks").get(Main.r.c.getInt("users." + p.getUniqueId().toString() + ".rank")) + ".prefix"))
							.replace("%kingdomName%", Main.r.getInWitchKingdomAPlayerIs(p))
							.replace("%online%", Bukkit.getOnlinePlayers().size() + "")
							.replace("%channel%", (Main.r.lookInChat.containsKey(p) ? Main.r.getTextColor(Main.r.lookInChat.get(p)) : Main.r.getTextColor(Main.r.getInWitchKingdomAPlayerIs(p)))))).setScore(Board.size() + Main.r.getAllKingdoms().size() - i + Main.r.Koths.size()+2);
				}
				List<String> Kingdoms = Main.r.getAllKingdomsWithColor();
				for (int i = 0; i < Kingdoms.size(); i++) 
				{
					objective.getScore(ChatColor.translateAlternateColorCodes('&', Main.r.getConfig().getString("scoreboard_war.kills")
							.replace("%kingdom%", Kingdoms.get(i))
							.replace("%kills%", (!Main.r.killsPerKD.containsKey(Kingdoms.get(i)) ? "0" : Main.r.killsPerKD.get(Kingdoms.get(i))+"")))).setScore(Main.r.getAllKingdoms().size() - i + Main.r.Koths.size()+2);
				}
				
				
				
				objective.getScore(ChatColor.translateAlternateColorCodes('&', Main.r.getConfig().getString("scoreboard_war.kothl1"))).setScore(Main.r.Koths.size()+2);
				objective.getScore(ChatColor.translateAlternateColorCodes('&', Main.r.getConfig().getString("scoreboard_war.kothl2"))).setScore(Main.r.Koths.size()+1);
				
				
				int i = 0;
				for(Koth koth : Main.r.Koths.values())
				{
					objective.getScore(ChatColor.translateAlternateColorCodes('&', Main.r.getConfig().getString("scoreboard_war.koth")
							.replace("%koth%", koth.getName())
							.replace("%time%", koth.getTimer()+""))).setScore(Main.r.Koths.size() - i);
					i++;
				}
				setPrefix(p, Main.r.getColorWithSpecials(p));
				
				for(String str : Main.r.customPrefix)
				{
					if(board.getTeam(str) == null)
					{
						board.registerNewTeam(str);
					}
				}
				
				for (Player wp : Main.r.prefix.keySet())
				{
					Team team = board.getTeam(((String)Main.r.prefix.get(wp))) == null ? board.registerNewTeam(((String)Main.r.prefix.get(wp))) : board.getTeam(((String)Main.r.prefix.get(wp)));
		      
					team.addEntry(wp.getName());
					team.setPrefix(((String)Main.r.prefix.get(wp)).replace("&", "§"));
				}
				p.setScoreboard(board);
			}
			else if(Main.r.war == false)
			{
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
		    
				String ping = (((CraftPlayer)p).getHandle().ping + "").replace("ping", "");
				String inKingdom = Main.r.getInWitchKingdomAPlayerIs(loc.getBlock());
				
				Objective objective = board.registerNewObjective(p.getName(), "dummy");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		    
				objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.r.getConfig().getString("scoreboard.title")));
		    
				List<String> Board = Main.r.getConfig().getStringList("scoreboard.board");
				for (int i = 0; i < Board.size(); i++) 
				{
					objective.getScore(ChatColor.translateAlternateColorCodes('&', ((String)Board.get(i))
							.replace("%loc%", Main.r.getTextColor(inKingdom).replace("-", ChatColor.WHITE + "Onbekend"))
							.replace("%points%", Main.r.getPlayerPoints().getAPI().look(p.getUniqueId()) + "")
							.replace("%ping%", ping)
							.replace("%rank%", Main.r.c.getString("rank." + Main.r.c.getStringList("ranks").get(Main.r.c.getInt("users." + p.getUniqueId().toString() + ".rank")) + ".prefix"))
							.replace("%kingdomName%", Main.r.getInWitchKingdomAPlayerIs(p))
							.replace("%online%", Bukkit.getOnlinePlayers().size() + "")
							.replace("%channel%", (Main.r.lookInChat.containsKey(p) ? Main.r.getTextColor(Main.r.lookInChat.get(p)) : Main.r.getTextColor(Main.r.getInWitchKingdomAPlayerIs(p)))))).setScore(Board.size() - i);
				}
				setPrefix(p, Main.r.getColorWithSpecials(p));
				
				for(String str : Main.r.customPrefix)
				{
					if(board.getTeam(str) == null)
					{
						board.registerNewTeam(str);
					}
				}
				
				for (Player wp : Main.r.prefix.keySet())
				{
					Team team = board.getTeam(((String)Main.r.prefix.get(wp))) == null ? board.registerNewTeam(((String)Main.r.prefix.get(wp))) : board.getTeam(((String)Main.r.prefix.get(wp)));
		      
					team.addEntry(wp.getName());
					team.setPrefix(((String)Main.r.prefix.get(wp)).replace("&", "§"));
				}
				p.setScoreboard(board);
			}
		}
	}
  
	public void setPrefix(Player p, String color)
	{
		Main.r.prefix.put(p, color);
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
    
		Team team = board.getTeam(((String)Main.r.prefix.get(p))) == null ? board.registerNewTeam(((String)Main.r.prefix.get(p))) : board.getTeam(((String)Main.r.prefix.get(p)));
	      
		team.addEntry(p.getName());
		team.setPrefix(((String)Main.r.prefix.get(p)).replace("&", "§"));
	}
  
	public void delPrefix(Player p)
	{
		if (Main.r.prefix.containsKey(p)) 
		{
			Main.r.prefix.remove(p);
		}
	}
}