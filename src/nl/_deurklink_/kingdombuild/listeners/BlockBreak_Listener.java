package nl._deurklink_.kingdombuild.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import nl._deurklink_.kingdombuild.Main;

public class BlockBreak_Listener implements Listener 
{
	@EventHandler
	public void blockBreak(BlockBreakEvent e)
	{
		if(e.isCancelled()) return;
		if(Main.r.checkIfPlayerCanBuild(e.getPlayer(), 1, e.getBlock()))
		{
			e.getPlayer().sendMessage(ChatColor.RED + "Je hebt te weinig influence hiervoor.");
			e.setCancelled(true);
		}
		if(e.getBlock().getY() == 1 && !e.getPlayer().hasPermission("kingdom.buttombuild")) e.setCancelled(true);
	}
}