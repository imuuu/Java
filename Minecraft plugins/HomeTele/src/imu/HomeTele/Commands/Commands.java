package imu.HomeTele.Commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import imu.HomeTele.Other.ConfigMaker;
import imu.HomeTele.Other.Cooldowns;
import imu.HomeTele.Other.TeleChecks;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;


public class Commands implements CommandExecutor
{

	public String[] cmds = {"sethome","home","homelist"};
	
	public HashMap<Player, Location> playerHomes = new HashMap<Player, Location>();
	public HashMap<Player, TeleChecks> playerChecks = new HashMap<Player, TeleChecks>();
	
	Plugin _plugin;
	
	HashMap<Player, Cooldowns> player_cds = new HashMap<Player, Cooldowns>();
	HashMap<String, Location> allHomes = new HashMap<String, Location>();
	
	double teleport_castTime = 15;
	double teleport_cooldown = 60*15; // 15min
	double setHome_cooldown = 60*60; // 1h
	
	
	public Commands(Plugin plugin)
	{
		_plugin = plugin;
		checkPlayerHomes();
		runnable_checkTeles();
		getSettings();
		setAllHomes();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) 
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			
			if(cmd.getName().equalsIgnoreCase(cmds[0])) //sethome
			{				
				settingHome(player);
				
			}else if(cmd.getName().equalsIgnoreCase(cmds[1])) //home
			{
				if(arg.length == 0)
				{
					if(hasHome(player))
					{
						if(!isCd(player, "home", teleport_cooldown))
							playerChecks.put(player, new TeleChecks(player,_plugin));
					}
				}
				if(arg.length == 1 && player.isOp())
				{
					Player target_player = _plugin.getServer().getPlayer(arg[0]);
					if(target_player != null)
					{
						teleportHome(player, target_player);
					}
				}
				
				
			}else if(cmd.getName().equalsIgnoreCase(cmds[2])) //homelist
			{
				homeList(player);
				
			}else
			{
				return false;
			}
			
			return true;
		}else
		{
			sender.sendMessage(ChatColor.RED +"Only player can use this command!");
			return true;
		}
				
	}
	
	void homeList(Player player)
	{		
		for(Map.Entry<String,Location> entry : allHomes.entrySet())
		{	
			String player_name = entry.getKey();

			BaseComponent message = new TextComponent("Teleport home of ");	
			message.setColor( net.md_5.bungee.api.ChatColor.GRAY );
			TextComponent pName = new TextComponent(player_name);
			pName.setBold( true );
			pName.setColor(net.md_5.bungee.api.ChatColor.AQUA);
			message.addExtra(pName);
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home "+player_name));
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Teleport").create()));
			player.spigot().sendMessage(message);
		}
		
	}
	
	void getSettings()
	{
		ConfigMaker cm = new ConfigMaker(_plugin, "settings.yml");
		FileConfiguration config=cm.getConfig();
		if(!config.contains("settings.")) 
		{
			//default values
			System.out.println("home teles: Default config made!");			
			config.set("settings.teleCastTime",teleport_castTime);
			config.set("settings.teleCooldown",teleport_cooldown);
			config.set("settings.setHomeCooldown",setHome_cooldown);		
			cm.saveConfig();
			return;
		}
		
		teleport_castTime = config.getDouble("settings.teleCastTime");
		teleport_cooldown = config.getDouble("settings.teleCooldown");
		setHome_cooldown = config.getDouble("settings.setHomeCooldown");
				
	}
	
	public void cancelTeleport(Player p)
	{
		p.sendMessage("Teleport canceled!");
		playerChecks.remove(p);
	}
	
	void checkPlayerHomes()
	{
		for (Player p : _plugin.getServer().getOnlinePlayers())
		{
			setHomeToHash(p);
		}
	}
	
	public void setAllHomes()
	{
		Location loc;
		ConfigMaker conM = new ConfigMaker(_plugin, "homes.yml");
		FileConfiguration customConfig = conM.getConfig();
		
		for(final String uuid : customConfig.getConfigurationSection("homes.").getKeys(false))
		{

			loc = customConfig.getLocation("homes."+ uuid +".loc");
			String player_name = customConfig.getString("homes."+ uuid +".name");
			allHomes.put(player_name, loc);
		}
		
	}
	
	public Location getPlayerHomeFromConfig(Player player)
	{
		ConfigMaker conM = new ConfigMaker(_plugin, "homes.yml");
		FileConfiguration customConfig = conM.getConfig();
		Location loc = customConfig.getLocation("homes."+ player.getUniqueId()+".loc");
		
		if(loc == null)
		{
			System.out.println("not home found in config");
			return null;
		}
		return loc;
	}
	public void setHomeToHash(Player p)
	{		
		Location loc = getPlayerHomeFromConfig(p);
		if(loc != null)
		{
			playerHomes.put(p, loc);
			allHomes.put(p.getName(),loc);
		}
			
		
	}
	
	boolean hasHome(Player p)
	{
		Location homeLoc = allHomes.get(p.getName());
		if(homeLoc == null)
		{
			p.sendMessage("You don't have home!");
			return false;
		}
		return true;
	}
	
	void teleportHome(Player p, Player target_p)
	{
		if(!hasHome(p) || !hasHome(target_p))
			return;
		
		p.sendMessage("You have teleported to home!");
		p.teleport(allHomes.get(target_p.getName()));
	}
	boolean isCd(Player p , String cd_name, double cd)
	{
		Cooldowns cds = player_cds.get(p);
		if(cds == null)
		{
			cds = new Cooldowns();
			cds.addCooldownInSeconds(cd_name, cd);
			player_cds.put(p, cds);
		}
		else
		{
			if(!cds.isCooldownReady(cd_name))
			{
				p.sendMessage("You have "+cds.GetCdInSeconds(cd_name)/60+"m cooldown left!");
				return true;
			}else
			{
				cds.addCooldownInSeconds(cd_name, cd);
			}
		}
		
		return false;
	}
	void settingHome(Player p)
	{
		if(isCd(p, "sethome", setHome_cooldown))
			return;
		
		p.sendMessage("Home has been set!");
		
		
		ConfigMaker conM = new ConfigMaker(_plugin, "homes.yml");
		FileConfiguration customConfig = conM.getConfig();
			
		customConfig.set("homes." + p.getUniqueId()+".loc", p.getLocation());
		customConfig.set("homes." + p.getUniqueId()+".name", p.getName());
		conM.saveConfig();
		playerHomes.put(p, p.getLocation());
		
	}
	
	void runnable_checkTeles()
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				if(playerChecks.size() == 0)
					return;
				
				Player player;
				for(Map.Entry<Player,TeleChecks> entry : playerChecks.entrySet())
				{	
					player = entry.getKey();
					if(!entry.getValue().canTeleport())
					{
						cancelTeleport(player);
						player_cds.get(player).removeCooldown("home");
						continue;
					}
					
					if(entry.getValue().drawAnimation(teleport_castTime))
					{
						teleportHome(player,player);
						playerChecks.remove(player);
					}
										
				}
				
			}
		}.runTaskTimer(_plugin, 0, 1);
	}

}
