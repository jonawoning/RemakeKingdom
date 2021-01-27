package nl._deurklink_.kingdombuild.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import nl._deurklink_.kingdombuild.Main;
import nl._deurklink_.kingdombuild.events.AttackMenu;

public class Dead_Listener implements Listener 
{
	private HashMap<Player, Player> lastDamage = new HashMap<Player, Player>();
	
	
	@EventHandler
	public void dead(EntityDamageEvent e)
	{
		if(e.getEntity() instanceof Player)
		{
			Player p = ((Player)e.getEntity());
			String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
			if(pKD.equals("default"))
			{
				e.setCancelled(true);
				return;
			}
		}
	}
	@EventHandler
	public void dead(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			Player p = ((Player)e.getEntity());
			Player damager = (Player)e.getDamager();
			
			if(Main.r.war == false && !Main.r.hitAll.contains(damager))
			{
				String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
				String dKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager));
				
				if(pKD.equals("default") || dKD.equals("default"))
				{
					e.setCancelled(true);
					return;
				}
				
				if(Main.r.c.getStringList("allies." + dKD).contains(pKD))
				{
					List<String> not = Main.r.c.getStringList("no_ally_regions");
					boolean allow = true;
					for(ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) 
					{
						if(not.contains(r.getId()))
						{
							allow = false;
							break;
						}
					}
					if(allow)
					{
						damager.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("cant_hit_because_ally")
							.replace("%player%", p.getName())));
						e.setCancelled(true);
						return;
					}
				}
				String walkInKD = Main.r.getInWitchKingdomAPlayerIs(damager.getLocation().getBlock());
				if(!walkInKD.equals("-") && !walkInKD.equals(dKD))
				{
					List<String> dAllies = Main.r.c.getStringList("allies." + dKD);
					
					if((walkInKD.equals(pKD) && Main.r.attacks.containsKey(dKD) && Main.r.attacks.get(dKD).equals(pKD))){}
					else
					{
						boolean check1 = false;
						
						for(String ally : dAllies)
						{
							if(Main.r.attacks.containsKey(ally))
							{
								if(Main.r.attacks.get(ally).equals(pKD))
								{
									check1 = true;
									break;
								}
							}
						}
						
						if(walkInKD.equals(pKD) && check1){}
						else
						{
							e.setCancelled(true);
							damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan op dit gebied.");
							return;
						}
					}
				}
			}
			
			if(Main.r.pvpProtection.contains(p.getUniqueId()))
			{
				e.setCancelled(true);
				damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan omdat hij pvp protectie aan heeft staan.");
				return;
			}
			
			
			if(!Main.r.getInWitchKingdomAPlayerIs(p).equals(Main.r.getInWitchKingdomAPlayerIs(damager)))
			{
				lastDamage.put(p, damager);
			}
			else
			{
				if(Main.r.c.getStringList("friendly-fire").contains(ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager))))
				{
					lastDamage.put(p, damager);
				}
				else
				{
					e.setCancelled(true);
				}
			}
		}
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Arrow && ((Arrow)e.getDamager()).getShooter() instanceof Player)
		{
			Player damager = (Player) ((Arrow)e.getDamager()).getShooter();
			Player p = (Player)e.getEntity();
			
			if(Main.r.war == false && !Main.r.hitAll.contains(damager))
			{
				String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
				String dKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager));
				
				if(pKD.equals("default") || dKD.equals("default"))
				{
					e.setCancelled(true);
					return;
				}
				
				if(Main.r.c.getStringList("allies." + dKD).contains(pKD))
				{
					List<String> not = Main.r.c.getStringList("no_ally_regions");
					boolean allow = true;
					for(ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) 
					{
						if(not.contains(r.getId()))
						{
							allow = false;
							break;
						}
					}
					if(allow)
					{
						damager.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("cant_hit_because_ally")
							.replace("%player%", p.getName())));
						e.setCancelled(true);
						return;
					}
				}
				String walkInKD = Main.r.getInWitchKingdomAPlayerIs(damager.getLocation().getBlock());
				if(!walkInKD.equals("-") && !walkInKD.equals(dKD))
				{
					List<String> dAllies = Main.r.c.getStringList("allies." + dKD);
					
					if((walkInKD.equals(pKD) && Main.r.attacks.containsKey(dKD) && Main.r.attacks.get(dKD).equals(pKD))){}
					else
					{
						boolean check1 = false;
						
						for(String ally : dAllies)
						{
							if(Main.r.attacks.containsKey(ally))
							{
								if(Main.r.attacks.get(ally).equals(pKD))
								{
									check1 = true;
									break;
								}
							}
						}
						
						if(walkInKD.equals(pKD) && check1){}
						else
						{
							e.setCancelled(true);
							damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet neerschieten op dit gebied.");
							return;
						}
					}
				}
			}
			
			if(Main.r.pvpProtection.contains(p.getUniqueId()))
			{
				e.setCancelled(true);
				damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan omdat hij pvp protectie aan heeft staan.");
				return;
			}
			
			if(!Main.r.getInWitchKingdomAPlayerIs(p).equals(Main.r.getInWitchKingdomAPlayerIs(damager)))
			{
				lastDamage.put(p, damager);
			}
			else
			{
				if(Main.r.c.getStringList("friendly-fire").contains(ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager))))
				{
					lastDamage.put(p, damager);
				}
				else
				{
					e.setCancelled(true);
				}
			}
		}
		if(e.getEntity() instanceof Player && e.getDamager() instanceof FishHook)
		{
			Player damager = (Player) ((FishHook)e.getDamager()).getShooter();
			Player p = (Player)e.getEntity();
			
			if(Main.r.war == false && !Main.r.hitAll.contains(damager))
			{
				String pKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
				String dKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager));
				
				if(pKD.equals("default") || dKD.equals("default"))
				{
					e.setCancelled(true);
					return;
				}
				
				if(Main.r.c.getStringList("allies." + dKD).contains(pKD))
				{
					List<String> not = Main.r.c.getStringList("no_ally_regions");
					boolean allow = true;
					for(ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation())) 
					{
						if(not.contains(r.getId()))
						{
							allow = false;
							break;
						}
					}
					if(allow)
					{
						damager.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("cant_hit_because_ally")
							.replace("%player%", p.getName())));
						e.setCancelled(true);
						return;
					}
				}
				String walkInKD = Main.r.getInWitchKingdomAPlayerIs(damager.getLocation().getBlock());
				if(!walkInKD.equals("-") && !walkInKD.equals(dKD))
				{
					List<String> dAllies = Main.r.c.getStringList("allies." + dKD);
					
					if((walkInKD.equals(pKD) && Main.r.attacks.containsKey(dKD) && Main.r.attacks.get(dKD).equals(pKD))){}
					else
					{
						boolean check1 = false;
						
						for(String ally : dAllies)
						{
							if(Main.r.attacks.containsKey(ally))
							{
								if(Main.r.attacks.get(ally).equals(pKD))
								{
									check1 = true;
									break;
								}
							}
						}
						
						if(walkInKD.equals(pKD) && check1){}
						else
						{
							e.setCancelled(true);
							damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan op dit gebied.");
							return;
						}
					}
				}
			}
			
			if(Main.r.pvpProtection.contains(p.getUniqueId()))
			{
				e.setCancelled(true);
				damager.sendMessage(ChatColor.RED + "Je kan " + p.getName() + " niet slaan omdat hij pvp protectie aan heeft staan.");
				return;
			}
			
			if(!Main.r.getInWitchKingdomAPlayerIs(p).equals(Main.r.getInWitchKingdomAPlayerIs(damager)))
			{
				lastDamage.put(p, damager);
			}
			else
			{
				if(Main.r.c.getStringList("friendly-fire").contains(ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(damager))))
				{
					lastDamage.put(p, damager);
				}
				else
				{
					e.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void deadEvent(PlayerDeathEvent e)
	{
		Player p = ((Player)e.getEntity());
		
		Main.r.pvpProtection.add(p.getUniqueId());
		
		String inKD = ChatColor.stripColor(Main.r.getInWitchKingdomAPlayerIs(p));
		ArrayList<Player> in = (Main.r.inAttack.containsKey(inKD) ? Main.r.inAttack.get(inKD) : new ArrayList<Player>());
		if(in.contains(p))
		{
			in.remove(p);
			Main.r.inAttack.put(inKD, in);
			new AttackMenu().check(p);
		}
		
		Main.r.c.set("users."  + p.getUniqueId().toString() + ".deaths", (Main.r.c.contains("users."  + p.getUniqueId().toString() + ".deaths") ? Main.r.c.getInt("users."  + p.getUniqueId().toString() + ".deaths")+1 : 1));
		Main.r.saveConfig();
		
		if(lastDamage.containsKey(p) && Main.r.war == true)
		{
			Player killer = lastDamage.get(p);
			e.setDeathMessage(null);
		
			Main.r.c.set("users."  + killer.getUniqueId().toString() + ".kills", (Main.r.c.contains("users."  + killer.getUniqueId().toString() + ".kills") ? Main.r.c.getInt("users."  + killer.getUniqueId().toString() + ".kills")+1 : 1));
			Main.r.saveConfig();
			
			String kingdomKiller = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(killer)));
			Main.r.c.set("leaderboard.kills." + kingdomKiller, (Main.r.c.contains("leaderboard.kills." + kingdomKiller) ? Main.r.c.getInt("leaderboard.kills." + kingdomKiller)+1 : 1));
			Main.r.saveConfig();
			
			String RankK = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + Main.r.c.getStringList("ranks").get(Main.r.c.getInt("users." + killer.getUniqueId().toString() + ".rank")) + ".prefix"));
			RankK = (ChatColor.stripColor(RankK).length() == 0 ? RankK : RankK + " ");
			
			String RankP = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + Main.r.c.getStringList("ranks").get(Main.r.c.getInt("users." + p.getUniqueId().toString() + ".rank")) + ".prefix"));
			RankP = (ChatColor.stripColor(RankP).length() == 0 ? RankP : RankP + " ");
			
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("messages.kill")
					.replace("%player_rank%", RankP)
					.replace("%player_kingdom%", Main.r.getInWitchKingdomAPlayerIs(p))
					.replace("%player%", (p).getName())
					.replace("%killer_rank%", RankK)
					.replace("%killer_kingdom%", Main.r.getInWitchKingdomAPlayerIs(killer))
					.replace("%killer%", (killer).getName())
					));
			Main.r.killsPerKD.put(Main.r.getInWitchKingdomAPlayerIs(killer), (Main.r.killsPerKD.containsKey(Main.r.getInWitchKingdomAPlayerIs(killer)) ? Main.r.killsPerKD.get(Main.r.getInWitchKingdomAPlayerIs(killer)) : 0)+1);
			lastDamage.remove(p);
		}
		else if(lastDamage.containsKey(p))
		{
			Player killer = lastDamage.get(p);
			e.setDeathMessage(null);
			
			Main.r.c.set("users."  + killer.getUniqueId().toString() + ".kills", (Main.r.c.contains("users."  + killer.getUniqueId().toString() + ".kills") ? Main.r.c.getInt("users."  + killer.getUniqueId().toString() + ".kills")+1 : 1));
			Main.r.saveConfig();
			
			String kingdomKiller = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.r.getInWitchKingdomAPlayerIs(killer)));
			Main.r.c.set("leaderboard.kills." + kingdomKiller, (Main.r.c.contains("leaderboard.kills." + kingdomKiller) ? Main.r.c.getInt("leaderboard.kills." + kingdomKiller)+1 : 1));
			Main.r.saveConfig();
			
			String RankK = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + Main.r.c.getStringList("ranks").get(Main.r.c.getInt("users." + killer.getUniqueId().toString() + ".rank")) + ".prefix"));
			RankK = (ChatColor.stripColor(RankK).length() == 0 ? RankK : RankK + " ");
			
			String RankP = ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("rank." + Main.r.c.getStringList("ranks").get(Main.r.c.getInt("users." + p.getUniqueId().toString() + ".rank")) + ".prefix"));
			RankP = (ChatColor.stripColor(RankP).length() == 0 ? RankP : RankP + " ");
			
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.r.c.getString("messages.kill")
					.replace("%player_rank%", RankP)
					.replace("%player_kingdom%", Main.r.getInWitchKingdomAPlayerIs(p))
					.replace("%player%", (p).getName())
					.replace("%killer_rank%", RankK)
					.replace("%killer_kingdom%", Main.r.getInWitchKingdomAPlayerIs(killer))
					.replace("%killer%", (killer).getName())
					));
		}
	}
}
