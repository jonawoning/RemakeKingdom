package nl._deurklink_.kingdombuild.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import nl._deurklink_.kingdombuild.Koth;
import nl._deurklink_.kingdombuild.Main;
import nl._deurklink_.kingdombuild.events.AttackMenu;
import nl._deurklink_.kingdombuild.events.CustomScoreBoard;

public class Movemend_Listener implements Listener
{
	@EventHandler
	public void move(PlayerMoveEvent e)
	{
		if(e.getTo().getBlockX() != e.getFrom().getBlockX() || e.getTo().getBlockZ() != e.getFrom().getBlockZ())
		{
				for(Koth k : Main.r.Koths.values())
				{
					if(k.getCapper() == null)
					{
						if(k.isInKoth(e.getPlayer()))
						{
							k.claim(e.getPlayer());
							return;
						}
					}
					else if(k.getCapper() == e.getPlayer())
					{
						if(!k.isInKoth(e.getPlayer()))
						{
							k.reset();
						}
					}
				}
				
			if(!Main.r.getInWitchKingdomAPlayerIs(e.getFrom().getBlock()).equals(Main.r.getInWitchKingdomAPlayerIs(e.getTo().getBlock())) && Main.r.war == false)
			{
				Player p = e.getPlayer();
				new CustomScoreBoard().setScoreboard(p, e.getTo());
				new AttackMenu().check(p, e.getTo());
			}
			
			if(Main.r.pvpProtection.contains(e.getPlayer().getUniqueId()))
			{
				boolean match = true;
				for(ProtectedRegion r : WGBukkit.getRegionManager(e.getPlayer().getWorld()).getApplicableRegions(e.getPlayer().getLocation())) 
				{
					if(r.getId().equalsIgnoreCase(ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(e.getPlayer()))))
					{
						match = false;
					}
				}
				if(match)
				{
					e.getPlayer().sendMessage(ChatColor.RED + "Met spawn protectie mag je de spawn niet verlaten! typ /kingdom pvpenable.");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn "+e.getPlayer().getName());
					e.setCancelled(true);
				}
			}
		}
	}
}
