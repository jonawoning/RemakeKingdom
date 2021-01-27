package nl._deurklink_.kingdombuild;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Crate 
{
	private String name;
	private String title;
	private ArrayList<String> lore = new ArrayList<String>();
	
	public Crate(String name)
	{
		if(Main.r.c.contains("crate." + name))
		{
			this.name = name;
			//TODO
			this.title = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("crate." + name + ".title"));
			for(String str : Main.r.c.getStringList("crate." + name + ".lore"))
			{
				lore.add(ChatColor.translateAlternateColorCodes('&', str));
			}
		}
	}
	public void giveCrate(Player p, int amount)
	{
		ItemStack i = new ItemStack(Material.CHEST, amount);
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(title);
		m.setLore(lore);
		i.setItemMeta(m);
		
		p.getInventory().addItem(i);
	}
	public String getTitle()
	{
		return title;
	}
	public void giveReward(Player p)
	{
		removeInventoryItems(p, Material.CHEST, title, 1);
		Random ra = new Random();
		
		List<String> list = Main.r.c.getStringList("crate." + name + ".rewards");
		
		String reward = list.get(ra.nextInt(list.size()));
		
		ItemStack i = new ItemStack(Material.valueOf(Main.r.c.getString("crate." + name + ".reward." + reward + ".type").toUpperCase()), Main.r.c.getInt("crate." + name + ".reward." + reward + ".amount"), (byte) Main.r.c.getInt("crate." + name + ".reward." + reward + ".data"));
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("crate." + name + ".reward." + reward + ".title")));
		
		ArrayList<String> lore = new ArrayList<String>();
		
		for(String str : Main.r.c.getStringList("crate." + name + ".reward." + reward + ".lore"))
		{
			lore.add(ChatColor.translateAlternateColorCodes('&', str));
		}
		
		m.setLore(lore);
		i.setItemMeta(m);
		
		p.getInventory().addItem(i);
	}
	private void removeInventoryItems(Player p, Material type, String title, int amount) 
    {
        for (ItemStack is : p.getInventory().getContents()) 
        {
            if (is != null && is.getType() == type && is.hasItemMeta() && is.getItemMeta().getDisplayName().equals(title)) 
            {
            	if(is.getAmount() > amount)
            	{
            		is.setAmount(is.getAmount()-amount);
            		p.updateInventory();
            		break;
            	}
            	if(is.getAmount() == amount)
            	{
            		p.getInventory().remove(is);
            		break;
            	}
            	if(is.getAmount() < amount)
            	{
            		amount = amount-is.getAmount();
            		p.getInventory().remove(is);
            	}
            }
        }
    }
}
