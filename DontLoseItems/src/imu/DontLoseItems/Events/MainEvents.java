package imu.DontLoseItems.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.ConfigMaker;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;


public class MainEvents implements Listener
{
		
	HashMap<UUID, ItemStack[]> saved_items = new HashMap<UUID, ItemStack[]>();
	HashMap<UUID, Boolean> died_pvp = new HashMap<UUID,Boolean>();

	final String[] tools = {"PICKAXE", "AXE", "HOE", "SHOVEL","ROD","ELYTRA"};
	final String[] weapons = {"SWORD","BOW","TRIDENT","SHIELD","CROSSBOW"};
	
	boolean saveArmor = true;
	boolean saveHotBar = true;
	boolean saveTools = false;
	boolean saveWeapons = false;
	
	double durability_penalty_pve = 0.1;
	double durability_penalty_pvp = 0.2;
	double durability_penalty_mob = 0.05;
	double _mendNerf = 0.4f;  // => 60%
	Plugin _plugin;
	Metods _itemM = null;
	Cooldowns _cd;
	
	String _cd_in_combat_dmg = "entity_combat_";
	int _cd_in_combat_cooldown = 5;
	
	HashMap<Player, ArrayList<EntityType>> _player_combat_with = new HashMap<>();
	HashMap<UUID, Double> _player_combat_penalty_join = new HashMap<>();
	
	public MainEvents(Plugin plugin)
	{
		_plugin = plugin;
		_itemM = ImusAPI._metods;
		getSettings();
		_cd = new Cooldowns();
		runnable();
	}
	
	
//	@EventHandler
//	public void ProjectileLaunch(ProjectileLaunchEvent e)
//	{
//		//System.out.println("projectile launched: "+e.getEntity().getShooter() );
//		if(e.getEntity().getShooter() instanceof Shulker)
//		{
//			System.out.println("its shulker");
//			Projectile prot = e.getEntity();
//			double mult = 100.0;
//			prot.setGlowing(true);
//			ShulkerBullet bullet = (ShulkerBullet)prot;
//			bullet.setVelocity(bullet.getVelocity().multiply(mult));
//			//bullet.getVelocity().multiply(10);
//		}
//	}
	
	@EventHandler
	public void ProjectileLaunch(ProjectileHitEvent e)
	{
		//System.out.println("projectile launched: "+e.getEntity().getShooter() );
		if(e.getEntity().getShooter() instanceof Shulker)
		{
			//System.out.println("its shulker HIT");
			if(e.getHitEntity() instanceof Player)
			{
				
				Player player = (Player)e.getHitEntity();
				
				if(player.getGameMode() != GameMode.SURVIVAL ) return;
				
				if(player.isBlocking() && !player.hasCooldown(Material.SHIELD)) 
				{
					player.setCooldown(Material.SHIELD, 60);
					return;
				}
				
				int protectionLevel = ImusAPI._metods.GetArmorSlotEnchantCount(player, Enchantment.PROTECTION_ENVIRONMENTAL);
				int protectileLevel = ImusAPI._metods.GetArmorSlotEnchantCount(player, Enchantment.PROTECTION_PROJECTILE);
//				System.out.println("prot lvl: "+ protectionLevel + " proj: "+protectileLevel);
				//System.out.println("player blocking: "+player.isBlocking());
				double toughnest = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
				
				if(toughnest < 1) toughnest = 1;
//				System.out.println("damage: " + (16.0 * 	(17.0 / (1.0+0.2*protectionLevel+0.4*protectileLevel) )   /  ( toughnest * 2)));
				double health = player.getHealth()-(16.0 * (17.0 / (1.0+0.2*protectionLevel+0.4*protectileLevel) ) / (toughnest * 2));			
//				System.out.println(player.getAttribute(Attribute.GENERIC_ARMOR).getValue());
//				System.out.println(player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue());
				
				if(health < 0) health = 0;
				
				player.setHealth(health);
			}
		}
	}
	
	@EventHandler
	public void MendingXp(PlayerItemMendEvent e)
	{
		e.setCancelled(true);
		ImusAPI._metods.giveDamage(e.getItem(), (int)(e.getRepairAmount() * -1 * _mendNerf), false);
		
	}
	
	
	@EventHandler
	public void onInteract(EntityDamageByEntityEvent event)
	{
		
		if(event.getDamager() instanceof Player && !(event.getEntity() instanceof Player) && event.getEntity() instanceof Monster)
		{
			Player p =(Player) event.getDamager();
			setInCombat(p, event.getEntity());
			
		}
		
		if(event.getDamager() instanceof Monster  && event.getEntity() instanceof Player)
		{
			Player p =(Player) event.getEntity();
			setInCombat(p, event.getDamager());
		}
		
		if(event.getDamager() instanceof Projectile && event.getEntity() instanceof Player)
		{
			Projectile pr =(Projectile) event.getDamager();
			if(pr.getShooter() instanceof Player)
			{
				return;
			}
			Player p =(Player) event.getEntity();
			setInCombat(p, event.getDamager());
		}
		
	}

	void setInCombat(Player p, Entity with_mob)
	{
		_cd.setCooldownInSeconds(_cd_in_combat_dmg+p.getName(), _cd_in_combat_cooldown);
		
		boolean send_smg = true;
		boolean send_smg2 = true;
		if(_player_combat_with.containsKey(p))
		{
			send_smg = false;
			send_smg2 = false;
			if(!_player_combat_with.get(p).contains(with_mob.getType()))
			{
				send_smg = true;
				_player_combat_with.get(p).add(with_mob.getType());
			}
			
			
		}
		else
		{
			ArrayList<EntityType> arr = new ArrayList<>();
			arr.add(with_mob.getType());
			_player_combat_with.put(p, arr);
			
		}
		if(send_smg)
		{
			p.sendMessage(ChatColor.RED + "You are in combat with new "+ChatColor.AQUA +"Mob!");				
		}
		if(send_smg2)
		{
			if(new Random().nextInt(10)  <= 1) p.sendMessage(ChatColor.RED + "If you log out, in combat you will lose durability from all items! ");
			
		}
	}
	
	void runnable()
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				ArrayList<Player> removeThesePlayers = new ArrayList<>();
				for(Map.Entry<Player,ArrayList<EntityType>> entry : _player_combat_with.entrySet())
				{
					Player p = entry.getKey();
					if(_cd.isCooldownReady(_cd_in_combat_dmg+p.getName()))
					{
						p.sendMessage(ChatColor.GREEN +"" +ChatColor.BOLD+"Your combat with mob has ended!");
						removeThesePlayers.add(p);
					}
				}
				
				for(Player p : removeThesePlayers)
				{
					_player_combat_with.remove(p);
				}
				
			}
		}.runTaskTimer(_plugin, 0, 20);
	}
	
	void removeCooldownAndCombat(Player p)
	{
		_player_combat_with.remove(p);
		_cd.removeCooldown(_cd_in_combat_dmg+p.getName());
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		if(_player_combat_with.containsKey(e.getPlayer()))
		{
			//System.out.println("set durability penalty");
			_player_combat_penalty_join.put(e.getPlayer().getUniqueId(), durability_penalty_mob);
			removeCooldownAndCombat(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if(_player_combat_penalty_join.containsKey(e.getPlayer().getUniqueId()))
		{
			e.getPlayer().sendMessage(ChatColor.RED + "You have forfeited good fight! Next time do not leave!");
			setDurabilityPenalty(e.getPlayer(), _player_combat_penalty_join.get(e.getPlayer().getUniqueId()));
			_player_combat_penalty_join.remove(e.getPlayer().getUniqueId());
		}
	}
	
	void setDurabilityPenalty(Player p, double prosent)
	{
		PlayerInventory inv = p.getInventory();
		
		ItemStack[] s_items = inv.getContents();
		
		if(s_items == null)
		{
			return;
		}
			
		
		for(int i = 0; i < inv.getContents().length; ++i)
		{
			ItemStack item = s_items[i];
			if(item != null)
			{
				_itemM.giveDamage(item, (int)(item.getType().getMaxDurability() * prosent), false);
			
			inv.setItem(i, item);
			}
		}
		
		prosent = (int)(prosent * 100);
		if(prosent > 0)
		{
			p.sendMessage(ChatColor.GRAY + "All your items has lost durability: " + ChatColor.RED+ prosent +"%");
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		PlayerInventory inv = player.getInventory();
		
		removeCooldownAndCombat(player);
		
		ItemStack[] content = inv.getContents();
		ItemStack[] save_items = new ItemStack[content.length];

		if(player.getKiller() instanceof Player)
		{
			died_pvp.put(player.getUniqueId(), true);
		}else
		{
			died_pvp.put(player.getUniqueId(), false);
		}
		
		for(int l = 0 ; l < content.length; ++l)
		{
			ItemStack stack = content[l];
			if(stack != null)
			{
				ItemStack copy = new ItemStack(stack);
							
				if(saveHotBar && l > -1 && l < 9)
				{
					save_items[l] = copy;
					stack.setAmount(0);
				}
				else if(saveTools || saveWeapons)
				{
					if(saveTools & stringEndsWith(copy.getType().toString(), tools)) //Its a tool!
					{
						save_items[l]= copy;
						stack.setAmount(0);
					}
					
					if(saveWeapons & stringEndsWith(copy.getType().toString(), weapons)) // its a weapon
					{
						save_items[l]= copy;
						stack.setAmount(0);
					}
				}
				
				if(saveArmor && l > content.length-6) 
				{
					save_items[l] = copy;
					stack.setAmount(0);
				}
			}
			
		}
		saved_items.put(player.getUniqueId(), save_items);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		Player player = e.getPlayer();

		PlayerInventory inv = player.getInventory();
		
		ItemStack[] s_items = saved_items.get(player.getUniqueId());
		int prosent = 0;
		
		if(s_items == null)
		{
			return;
		}
			
		
		for(int i = 0; i < inv.getContents().length; ++i)
		{
			ItemStack item = s_items[i];
			if(item != null)
			{
				
				if(died_pvp.get(player.getUniqueId()))
				{
					_itemM.giveDamage(item, (int)(item.getType().getMaxDurability() * durability_penalty_pvp), false);
					prosent = (int)(durability_penalty_pvp * 100);
				}else
				{
					_itemM.giveDamage(item, (int)(item.getType().getMaxDurability() * durability_penalty_pve), false);
					prosent = (int)(durability_penalty_pve * 100);
				}
			
			inv.setItem(i, item);
			}
		}
		
		if(prosent > 0)
		{
			player.sendMessage(ChatColor.GRAY + "All your items has lost durability: " + ChatColor.RED+ prosent +"%");
		}
		saved_items.remove(player.getUniqueId());
		
	}
	
	
	void getSettings()
	{
		ConfigMaker cm = new ConfigMaker(_plugin, "settings.yml");
		FileConfiguration config=cm.getConfig();
		
		String a_path ="settings.saveArmor";
		String t_path = "settings.saveTools";
		String w_path = "settings.saveWeapons";
		String h_path = "settings.saveHotbar";
		String dpvp_path = "settings.pvp_damage_penalty";
		String dpve_path = "settings.pve_damage_penalty";
		String dmob_path = "settings.mob_damage_penalty";
		String dcombat_cd_path = "settings.mob_comabt_cd";
		
		if(!config.contains("settings.")) 
		{
			//default values
			_plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA +"DontLoseItems : Default config made!");		
			config.set(a_path,saveArmor);
			config.set(t_path,saveTools);
			config.set(w_path,saveWeapons);		
			config.set(h_path,saveHotBar);		
			config.set(dpvp_path,durability_penalty_pvp);		
			config.set(dpve_path,durability_penalty_pve);		
			config.set(dmob_path,durability_penalty_mob);		
			config.set(dcombat_cd_path,_cd_in_combat_cooldown);		
			cm.saveConfig();
			return;
		}
		
		saveArmor = config.getBoolean(a_path);
		saveTools = config.getBoolean(t_path);
		saveWeapons = config.getBoolean(w_path);
		saveHotBar = config.getBoolean(h_path);
		durability_penalty_pvp = config.getDouble(dpvp_path);
		durability_penalty_pve = config.getDouble(dpve_path);
		durability_penalty_mob = config.getDouble(dmob_path);
		_cd_in_combat_cooldown = config.getInt(dcombat_cd_path);
				
	}
	boolean stringEndsWith(String target, String[] array)
	{
		for(String str : array)
		{
			if(target.endsWith(str))
				return true;
		}
		
		return false;
	}
	
	
	
	
	
}
