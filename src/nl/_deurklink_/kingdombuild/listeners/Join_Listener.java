package nl._deurklink_.kingdombuild.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import nl._deurklink_.kingdombuild.Main;
import nl._deurklink_.kingdombuild.events.CustomScoreBoard;

public class Join_Listener implements Listener
{
	@EventHandler
	public void join(PlayerJoinEvent e)
	{
		CustomScoreBoard csb = new CustomScoreBoard();
		csb.setScoreboard(e.getPlayer());
		
		if(!Main.r.c.contains("users." + e.getPlayer().getUniqueId().toString()))
		{
			Main.r.c.set("users."  + e.getPlayer().getUniqueId().toString() + ".rank", 0);
			Main.r.saveConfig();
		}
		Main.r.setNewListChase();
		
		
		if(Main.r.getInWitchKingdomAPlayerIs(e.getPlayer()).equals("default"))
		{
			e.getPlayer().getInventory().setItem(4, new ItemStack(Material.NETHER_STAR, 1));
		}
		
		
		if(!e.getPlayer().hasPermission("kingdom.showstaff"))
		{
			for(Player wp : Main.r.vanish)
			{
				e.getPlayer().hidePlayer(wp);
			}
		}
	}
}