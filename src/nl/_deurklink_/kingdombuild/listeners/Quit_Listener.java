package nl._deurklink_.kingdombuild.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import nl._deurklink_.kingdombuild.Main;
import nl._deurklink_.kingdombuild.events.AttackMenu;
import nl._deurklink_.kingdombuild.events.CustomScoreBoard;

public class Quit_Listener implements Listener
{
	@EventHandler
	public void quit(PlayerQuitEvent e)
	{
		new CustomScoreBoard().delPrefix(e.getPlayer());
		Player p = e.getPlayer();
		p.setGameMode(GameMode.SURVIVAL);
		if(Main.r.staffModes.contains(p))
		{
			Main.r.staffModes.remove(p);
			if(Main.r.vanish.contains(p))
			{ 
				Main.r.vanish.remove(p);
				p.sendMessage(ChatColor.GRAY + "Iedereen ziet jouw nu.");
				for(Player wp : Bukkit.getOnlinePlayers())
				{
					wp.showPlayer(p);
				}
			}
			if(Main.r.saveInv.containsKey(p))
			{
				p.getInventory().clear();
				Inventory inv = Main.r.saveInv.get(p);
				for(int i = 0; i < inv.getSize(); i++)
				{
					p.getInventory().setItem(i, inv.getItem(i));
				}
			}
		}
		String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
		ArrayList<Player> in = (Main.r.inAttack.containsKey(inKD) ? Main.r.inAttack.get(inKD) : new ArrayList<Player>());
		if(in.contains(p))
		{
			in.remove(p);
			Main.r.inAttack.put(inKD, in);
			new AttackMenu().check(p);
		}
	}
}
