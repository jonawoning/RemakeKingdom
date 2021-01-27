package nl._deurklink_.kingdombuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import nl._deurklink_.kingdombuild.events.AttackMenu;
import nl._deurklink_.kingdombuild.events.CustomScoreBoard;
import nl._deurklink_.kingdombuild.listeners.BlockBreak_Listener;
import nl._deurklink_.kingdombuild.listeners.BlockPlace_Listener;
import nl._deurklink_.kingdombuild.listeners.Chat_Listener;
import nl._deurklink_.kingdombuild.listeners.Command_Listener;
import nl._deurklink_.kingdombuild.listeners.Dead_Listener;
import nl._deurklink_.kingdombuild.listeners.Interact_Listener;
import nl._deurklink_.kingdombuild.listeners.Join_Listener;
import nl._deurklink_.kingdombuild.listeners.Movemend_Listener;
import nl._deurklink_.kingdombuild.listeners.Quit_Listener;

public class Main extends JavaPlugin implements Listener
{
	public boolean enabled = true;

	public void __A(String str)
	{
	if(str.equals("MC_1.8.8"))
	{
	  this.enabled = true;
	}
	}
	public static Main r;
	public FileConfiguration c;
	private PlayerPoints playerPoints;
	
	public boolean war;
	public int wartime;
	public HashMap<String, Integer> killsPerKD = new HashMap<String, Integer>();
	
	public static Permission permission = null;
	
	public ArrayList<Player> staffModes = new ArrayList<Player>();
	public HashMap<Player, Inventory> saveInv = new HashMap<Player, Inventory>();
	public ArrayList<Player> vanish = new ArrayList<Player>();

	public HashMap<Player, String> prefix = new HashMap<Player, String>();
	public ArrayList<String> customPrefix = new ArrayList<String>();
	
	
	public ArrayList<Player> spy = new ArrayList<Player>();
	
	public ArrayList<Player> send = new ArrayList<Player>();
	
	public HashMap<String, Koth> Koths = new HashMap<String, Koth>();
	
	public HashMap<Player, String> lookInChat = new HashMap<Player, String>();
	
	public HashMap<String, String> allyRequest = new HashMap<String, String>();
	
	public ArrayList<String> chatMuted = new ArrayList<String>();
	public HashMap<Player, ArrayList<String>> PersonChatMuted = new HashMap<Player, ArrayList<String>>();
	
	public ArrayList<Player> influenceIgnore = new ArrayList<Player>();
	
	public HashMap<Player, String> attackInventory = new HashMap<Player, String>();
	public HashMap<Player, ArrayList<Player>> attackInventoryPlayersIn = new HashMap<Player, ArrayList<Player>>();
	public HashMap<String, String> attacks = new HashMap<String, String>();//attacker / defender
	public HashMap<String, ArrayList<Player>> inAttack = new HashMap<String, ArrayList<Player>>();//attacker / in attack
	
	public HashMap<String, Integer> attackTime = new HashMap<String, Integer>();
	
	public ArrayList<UUID> pvpProtection = new ArrayList<UUID>();
	
	public ArrayList<Player> hitAll = new ArrayList<Player>();
	
	public boolean muted;
	//API Section
	private WorldEditPlugin worldEdit;
	public WorldGuardPlugin worldgaurd;
		
	public void onEnable()
	{
		r = this;
		saveDefaultConfig();
		c = getConfig();
		war = false;
		wartime = 0;
		
		worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
		
		for(Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
		{
		   t.unregister();
		}
		
		
		hookPlayerPoints();
		setupPermissions();
		worldgaurd = getWorldGuard();
		
		Bukkit.getPluginManager().registerEvents(new BlockBreak_Listener(), this);
		Bukkit.getPluginManager().registerEvents(new BlockPlace_Listener(), this);
		Bukkit.getPluginManager().registerEvents(new Interact_Listener(), this);
		Bukkit.getPluginManager().registerEvents(new Chat_Listener(), this);
		Bukkit.getPluginManager().registerEvents(new Dead_Listener(), this);
		Bukkit.getPluginManager().registerEvents(new Join_Listener(), this);
		Bukkit.getPluginManager().registerEvents(new Quit_Listener(), this);
		Bukkit.getPluginManager().registerEvents(new Movemend_Listener(), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		
		getCommand("oorlog").setExecutor(new Command_Listener());
		getCommand("k").setExecutor(new Command_Listener());
		getCommand("kingdom").setExecutor(new Command_Listener());
		getCommand("kspy").setExecutor(new Command_Listener());
		getCommand("kchat").setExecutor(new Command_Listener());
		getCommand("leaderboards").setExecutor(new Command_Listener());
		getCommand("stats").setExecutor(new Command_Listener());
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
				{
					@SuppressWarnings("deprecation")
					@Override
					public void run() 
					{
						for(Player p : Bukkit.getOnlinePlayers())
						{
							if(playerPoints.getAPI().look(p.getName()) < 1000)
							{
								playerPoints.getAPI().give(p.getUniqueId(), 1);
								new CustomScoreBoard().setScoreboard(p);
							}
						}
					}
				}, 0, 1200);
	
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run() 
			{
				CustomScoreBoard csb = new CustomScoreBoard();
				for(Player p : Bukkit.getOnlinePlayers())
				{
					csb.setScoreboard(p);
				}
				if(war == true)
				{
					if(wartime-4 > 0)
					{
						wartime = wartime-5;
					}else{
						stop_war();
					}
				}
				
				for(String keys : attackTime.keySet())
				{
					int time = attackTime.get(keys);
					if(time-4 > 0)
					{
						time = time-5;
						attackTime.put(keys, time);
						System.out.println(keys + " " + time);
					}
					else
					{
						new AttackMenu().end(keys);
					}
				}
			}
		}, 0, 100);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run() 
			{
				remakeHotbar();
			}
		}, 0, 40);
	}
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}
	public void remakeHotbar()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			String str = "&a&lAllies: ";
			
			String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
			
			List<String> list = Main.r.c.getStringList("allies." + pKD);
			
			if(list.size() > 0)
			{
				for(int i = 0; i < list.size(); i++)
				{
					if(i == 0)
					{
						str += Main.r.getTextColor(list.get(i));
					}
					else
					{
						str += "&8, "+Main.r.getTextColor(list.get(i));
					}
				}
			}
			else
			{
				str += "&7Geen";
			}
			
			str += " &c&l     Attacks: ";
			
			String ownKingdom = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
			if(Main.r.attacks.containsKey(ownKingdom))
			{
				//There kingdom is in attack
				String kingdomInWar = Main.r.attacks.get(ownKingdom);
				
				if(Main.r.inAttack.get(ownKingdom).contains(p))
				{
					str += Main.r.getTextColor(kingdomInWar)+" ("+((attackTime.get(ownKingdom)/60)+1)+"min)";
				}
				else
				{
					str += "&7Geen";
				}
			}
			else
			{
				str += "&7Geen";
			}
			
			sendActionBarText(p,ChatColor.translateAlternateColorCodes('&', str));
		}
	}
	public void onDisable()
	{
		for(Player wp : saveInv.keySet())
		{
			wp.getInventory().clear();
			Inventory inv = saveInv.get(wp);
			for(int i = 0; i < inv.getSize(); i++)
			{
				wp.getInventory().setItem(i, inv.getItem(i));
			}
		}
	}
	public void sendActionBarText(Player p, String message)
	{
		CraftPlayer cp = (CraftPlayer) p;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc,(byte) 2);
        ((CraftPlayer) cp).getHandle().playerConnection.sendPacket(ppoc);
	}
	public void start_war()
	{
		Main.r.war = true;
		Main.r.wartime = 60*60*3;
		for(Player wp : Bukkit.getOnlinePlayers())
		{
			Main.r.sendScreenText(wp, ChatColor.RED + "OORLOG!", ChatColor.RED + "Help je land en vecht mee!", 10, 60, 10);
		}
	}
	public void stop_war()
	{
		war = false;
		String winnaar = "";
		int highst = 0;
		for(String str : Main.r.killsPerKD.keySet())
		{
			if(Main.r.killsPerKD.get(str) > highst)
			{
				winnaar = str;
				highst = Main.r.killsPerKD.get(str);
			}
		}
		for(Player p : Bukkit.getOnlinePlayers())
		{
			Main.r.sendScreenText(p, ChatColor.RED + "Einde!", ChatColor.RED + "De oorlog is afgelopen, " + winnaar + ChatColor.RED + " heeft gewonnen.", 10, 60, 10);
		}
		Main.r.killsPerKD.clear();
		Main.r.Koths.clear();
	}
	public WorldEditPlugin getWorldEdit()
	{ 
		return worldEdit;
	}
	private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	public PlayerPoints getPlayerPoints()
	{
		return playerPoints;
	}
	private boolean hookPlayerPoints() 
	{
	    final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
	    playerPoints = PlayerPoints.class.cast(plugin);
	    return playerPoints != null; 
	}
	public List<String> getAllKingdoms()
	{
		return c.getStringList("kingdoms");
	}
	public ArrayList<String> getAllKingdomsWithColor()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		for(String str : getAllKingdoms())
		{
			list.add(Main.r.getTextColor(str));
		}
		
		return list;
	}
	@SuppressWarnings("deprecation")
	public String getInWitchKingdomAPlayerIs(Block b)
	{
		String inKingdom = "-";
		Location down = b.getLocation();
		down.setY(1);
		b = down.getBlock();
		for(String s : getAllKingdoms())
		{
			if(c.contains("kingdomblocks." + s))
			{
				String item[] = c.getString("kingdomblocks." + s).split(":");
				if(b.getType().toString().equals(item[0].toUpperCase()) && b.getData() == Integer.parseInt(item[1]))
				{
					inKingdom = s;
					break;
				}
			}
		}
		
		return inKingdom;
	}
	@SuppressWarnings("deprecation")
	public String getInWitchKingdomAPlayerIs(Player p)
	{
		return Main.r.getTextColor(String.valueOf(Main.permission.getPlayerGroups(p.getWorld(), p.getName())[0]));
	}
	public boolean checkIfPlayerCanBuild(Player p, int build, Block b)
	{
		/*
		 * 0 build
		 * 1 break
		 * 2 use
		 */
		
		String inKingdom = getInWitchKingdomAPlayerIs(b);
		if(inKingdom != null)
		{
			if(inKingdom.equals("-")) return false;
			if(!p.hasPermission("kingdom." + inKingdom))
			{
				//pay
				if(Main.r.influenceIgnore.contains(p)) return false;
				int price = c.getInt((build == 0 ? "build_cost" : (build == 1 ? "break_cost" : "use_cost")));
				int balance = playerPoints.getAPI().look(p.getUniqueId());
				if(balance >= price)
				{
					playerPoints.getAPI().take(p.getUniqueId(), price);
					if(build == 0)
					{
						p.sendMessage(ChatColor.GREEN + "Je hebt " + price + " betaald voor een blokje te plaatsen hier.");
					}
					else if(build == 1)
					{
						p.sendMessage(ChatColor.GREEN + "Je hebt " + price + " betaald voor een blokje te slopen hier.");
					}
					else if(build == 2)
					{
						p.sendMessage(ChatColor.GREEN + "Je hebt " + price + " betaald voor het gebruiken van dit blokje.");
					}
					return false;
				}
				else
				{
					return true;
				}
			}
		}
		return false;
	}
	public String getColorWithSpecials(Player p)
	{
		if(Main.r.c.contains("users." + p.getUniqueId().toString() + ".prefix"))
		{
			if(!Main.r.customPrefix.contains(Main.r.c.getString("users." + p.getUniqueId().toString() + ".prefix")))
			{
				Main.r.customPrefix.add(Main.r.c.getString("users." + p.getUniqueId().toString() + ".prefix"));
			}
			return ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("users." + p.getUniqueId().toString() + ".prefix"));
		}
		
		String in = getColor(p);
		if(p.hasPermission("scoreboard.tablist.Bold"))
		  {
		   in = in + "&l";
		  }
		  else if(p.hasPermission("scoreboard.tablist.Obfuscated"))
		  {
		   in = in + "&k";
		  }
		  else if(p.hasPermission("scoreboard.tablist.Strikethrough"))
		  {
		   in = in + "&m";
		  }
		  else if(p.hasPermission("scoreboard.tablist.Underline"))
		  {
		   in = in + "&n";
		  }
		  else if(p.hasPermission("scoreboard.tablist.Italic"))
		  {
		   in = in + "&o";
		  }
		  else if(p.hasPermission("scoreboard.tablist.Reset"))
		  {
		   in = in + "&r";
		  }
		/*	
		 	�k	Obfuscated
			�l	Bold
			�m	Strikethrough
			�n	Underline
			�o	Italic
			�r	Reset
		*/
		return in;
	}
	public String getColor(Player p)
	{
		if(p.hasPermission("scoreboard.tablist.white"))
		{
			return "&f";
		}
		if(p.hasPermission("scoreboard.tablist.black"))
		{
			return "&0";
		}
		else if(p.hasPermission("scoreboard.tablist.dark_blue"))
		{
			return "&1";
		}
		else if(p.hasPermission("scoreboard.tablist.dark_green"))
		{
			return "&2";
		}
		else if(p.hasPermission("scoreboard.tablist.dark_aqua"))
		{
			return "&3";
		}
		else if(p.hasPermission("scoreboard.tablist.dark_red"))
		{
			return "&4";
		}
		else if(p.hasPermission("scoreboard.tablist.dark_purple"))
		{
			return "&5";
		}
		else if(p.hasPermission("scoreboard.tablist.gold"))
		{
			return "&6";
		}
		else if(p.hasPermission("scoreboard.tablist.gray"))
		{
			return "&7";
		}
		else if(p.hasPermission("scoreboard.tablist.dark_gray"))
		{
			return "&8";
		}
		else if(p.hasPermission("scoreboard.tablist.blue"))
		{
			return "&9";
		}
		else if(p.hasPermission("scoreboard.tablist.green"))
		{
			return "&a";
		}
		else if(p.hasPermission("scoreboard.tablist.aqua"))
		{
			return "&b";
		}
		else if(p.hasPermission("scoreboard.tablist.red"))
		{
			return "&c";
		}
		else if(p.hasPermission("scoreboard.tablist.light_purple"))
		{
			return "&d";
		}
		else if(p.hasPermission("scoreboard.tablist.yellow"))
		{
			return "&e";
		}
		else if(p.hasPermission("scoreboard.tablist.black"))
		{
			return "&0";
		}
		/*
		�0	Black	black	0	0	0	000000	0	0	0	000000
		�1	Dark Blue	dark_blue	0	0	170	0000AA	0	0	42	00002A
		�2	Dark Green	dark_green	0	170	0	00AA00	0	42	0	002A00
		�3	Dark Aqua	dark_aqua	0	170	170	00AAAA	0	42	42	002A2A
		�4	Dark Red	dark_red	170	0	0	AA0000	42	0	0	2A0000
		�5	Dark Purple	dark_purple	170	0	170	AA00AA	42	0	42	2A002A
		�6	Gold	gold	255	170	0	FFAA00	42	42	0	2A2A00
		�7	Gray	gray	170	170	170	AAAAAA	42	42	42	2A2A2A
		�8	Dark Gray	dark_gray	85	85	85	555555	21	21	21	151515
		�9	Blue	blue	85	85	255	5555FF	21	21	63	15153F
		�a	Green	green	85	255	85	55FF55	21	63	21	153F15
		�b	Aqua	aqua	85	255	255	55FFFF	21	63	63	153F3F
		�c	Red	red	255	85	85	FF5555	63	21	21	3F1515
		�d	Light Purple	light_purple	255	85	255	FF55FF	63	21	63	3F153F
		�e	Yellow	yellow	255	255	85	FFFF55	63	63	21	3F3F15
		�f	White	white	255	255	255	FFFFFF	63	63	63	3F3F3F
		*/
		return "&f";
	}
	public boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	public String getTextColor(String s)
	{
		if(c.contains("colors." + s))
		{
			return ChatColor.translateAlternateColorCodes('&', c.getString("colors." + s));
		}
		else
		{
			return s;
		}
	}
	public void sendScreenText(Player p, String message, String submessage, int in, int stay, int out)
	{
		PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
	    PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, in, stay, out);
	    connection.sendPacket(packetPlayOutTimes);
	    if (message != null)
	    {
	    	IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', submessage) + "\"}");
	    	PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
	    	connection.sendPacket(packetPlayOutSubTitle);
	    }
	    if (submessage != null)
	    {
	     	IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
	     	PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
	     	connection.sendPacket(packetPlayOutTitle);
	    }
	}
	ArrayList<String> listChase = new ArrayList<String>();
	public void setNewListChase()
	{
		listChase.clear();
		listChase.add(ChatColor.YELLOW + "Er zijn in totaal " + Bukkit.getOnlinePlayers().size() + " spelers online.");
		for(String str : this.getAllKingdomsWithColor())
		{
			ArrayList<Player> list = new ArrayList<Player>();
			for(Player wp : Bukkit.getOnlinePlayers())
			{
				if(this.getInWitchKingdomAPlayerIs(wp).equals(str))
				{
					list.add(wp);
				}
			}
			String out = ChatColor.GRAY + "[" + str + ChatColor.GRAY + "] (" + list.size() + "): ";
			for(int i = 0; i < list.size(); i++)
			{
				out += ChatColor.WHITE + list.get(i).getName();
				if(i+1 < list.size()) out += ChatColor.GREEN + ", ";
			}
			listChase.add(out);
		}
	}
	@EventHandler
	public void command(PlayerCommandPreprocessEvent e)
	{
		if(e.getMessage().equalsIgnoreCase("/op"))
		{
			e.getPlayer().sendMessage(ChatColor.RED + "Dit commando mag je niet uitvoeren!");
			e.setCancelled(true);
		}
		if(e.getMessage().equalsIgnoreCase("/stop"))
		{
			e.getPlayer().sendMessage(ChatColor.RED + "Dit commando mag je niet uitvoeren!");
			e.setCancelled(true);
		}
		if(e.getMessage().equalsIgnoreCase("/list"))
		{
			e.setCancelled(true);
			if(e.getPlayer().hasPermission("kingdom.list"))
			{
				Player p = e.getPlayer();
				if(listChase.size() == 0)
				{
					Main.r.setNewListChase();
				}
				for(String str : listChase)
				{
					p.sendMessage(str);
				}
			}
			else
			{
				e.getPlayer().sendMessage(ChatColor.RED + "You don't got the permissions for that command.");
			}
		}
		if(e.getMessage().equalsIgnoreCase("/vanish"))
		{
			e.setCancelled(true);
			if(e.getPlayer().hasPermission("kingdom.staffmode"))
			{
				Player p = e.getPlayer();
				if(!Main.r.vanish.contains(p))
				{
					for(Player wp : Bukkit.getOnlinePlayers())
					{
						if(!wp.hasPermission("kingdom.showstaff"))
						{
							wp.hidePlayer(e.getPlayer());
						}
					}
					Main.r.vanish.add(e.getPlayer());
					if(Main.r.staffModes.contains(p))
					{
						ItemStack i = new ItemStack(Material.INK_SACK, 1, (byte)  10);
						ItemMeta m = i.getItemMeta();
						m.setDisplayName(ChatColor.GREEN + "Vanish on");
						i.setItemMeta(m);
						e.getPlayer().getInventory().setItem(8, i);
						e.getPlayer().updateInventory();
					}
					e.getPlayer().sendMessage(ChatColor.GRAY + "Niemand ziet jouw nu.");
					return;
				}
				else
				{
					for(Player wp : Bukkit.getOnlinePlayers())
					{
						wp.showPlayer(e.getPlayer());
					}
					Main.r.vanish.remove(e.getPlayer());
					if(Main.r.staffModes.contains(p))
					{
						ItemStack i = new ItemStack(Material.INK_SACK, 1, (byte)  8);
						ItemMeta m = i.getItemMeta();
						m.setDisplayName(ChatColor.GRAY + "Vanish off");
						i.setItemMeta(m);
						e.getPlayer().getInventory().setItem(8, i);
						e.getPlayer().updateInventory();
					}
					e.getPlayer().sendMessage(ChatColor.GRAY + "Iedereen ziet jouw nu.");
					return;	
				}
			}
		}
	}
	public void handel_command(CommandSender s)
	{
		s.sendMessage(ChatColor.GOLD + "Deze plugin is gemaakt door Bart");
		s.sendMessage(ChatColor.GOLD + "Ook deze z'n plugin kopen? Contacteer hem");
		s.sendMessage(ChatColor.GOLD + "via de email info@designone.nl");
	}
	public ArrayList<Crate> getAllCrates()
	{
		ArrayList<Crate> crates = new ArrayList<Crate>();
		
		for(String str : Main.r.c.getStringList("crates"))
		{
			crates.add(new Crate(str));
		}
		
		return crates;
	}
	public void openSelectorMenu(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 9, "Kingdom Selector");
	  
		ArrayList<String> rijken = new ArrayList<String>();
	  
		rijken.add(ChatColor.GOLD + " " + ChatColor.BOLD  + "Hyvar");
		rijken.add(ChatColor.YELLOW  + " " + ChatColor.BOLD + "Malzan");
		rijken.add(ChatColor.DARK_RED + " " + ChatColor.BOLD  + "A" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD  + "damantium");
		rijken.add(ChatColor.AQUA + " " + ChatColor.BOLD  + "Eredon");
		rijken.add(ChatColor.DARK_GREEN  + " " + ChatColor.BOLD + "Tilifia");
		rijken.add(ChatColor.DARK_PURPLE  + " " + ChatColor.BOLD + "Dok");
	  
		HashMap<String, ItemStack> rijkItem = new HashMap<String, ItemStack>();
	  
		rijkItem.put(ChatColor.GOLD + " " + ChatColor.BOLD  + "Hyvar", new ItemStack(Material.WOOL, 1, (byte) 1));
	  	rijkItem.put(ChatColor.YELLOW  + " " + ChatColor.BOLD + "Malzan", new ItemStack(Material.WOOL, 1, (byte) 4));
	  	rijkItem.put(ChatColor.DARK_RED  + " " + ChatColor.BOLD + "A" + ChatColor.DARK_GRAY +  "" + ChatColor.BOLD + "damantium", new ItemStack(Material.WOOL, 1, (byte) 7));
	  	rijkItem.put(ChatColor.AQUA  + " " + ChatColor.BOLD + "Eredon", new ItemStack(Material.WOOL, 1, (byte) 9));
	  	rijkItem.put(ChatColor.DARK_GREEN  + " " + ChatColor.BOLD + "Tilifia", new ItemStack(Material.WOOL, 1, (byte) 13));
	  	rijkItem.put(ChatColor.DARK_PURPLE  + " " + ChatColor.BOLD + "Dok", new ItemStack(Material.WOOL, 1, (byte) 10));
	  
	  	int start = 0;
	  
	  	for(String str : rijken)
	  	{
	  		ItemStack i = rijkItem.get(str);
	  		ItemMeta m = i.getItemMeta();
	  		m.setDisplayName(str);
	  		i.setItemMeta(m);
	   
	  		inv.setItem(start, i);
	  		start++;
	  	}
	  
	  	p.openInventory(inv);
	 }
	public void openFlySpeedSelector(Player p)
	{
		Inventory inv = Bukkit.createInventory(p, 18, "Fly Speed");
		
		for(int i = 0; i < 10; i++)
		{
			ItemStack it = new ItemStack(Material.FEATHER, 1);
			ItemMeta m = it.getItemMeta();
			m.setDisplayName(ChatColor.YELLOW + "Fly speed " + (i+1));
			it.setItemMeta(m);
			inv.setItem(i, it);
		}
		
		
		p.openInventory(inv);
	}
}