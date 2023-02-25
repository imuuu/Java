package imu.DontLoseItems.Events;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import imu.DontLoseItems.other.MinecraftJokes;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.ConfigMaker;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.DateParser;
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
	
	double durability_penalty_pve = 0.25;
	double durability_penalty_pvp = 0.6;
	double durability_penalty_mob = 0.1;
	double _mendNerf = 1.0f;  // => 60%
	Plugin _plugin;
	Metods _itemM = null;
	Cooldowns _cd;
	
	String _cd_in_combat_dmg = "entity_combat_";
	int _cd_in_combat_cooldown = 5;
	
	HashMap<Player, ArrayList<EntityType>> _player_combat_with = new HashMap<>();
	HashMap<UUID, Double> _player_combat_penalty_join = new HashMap<>();
	
	private MinecraftJokes _joker;
	private int _totemJokeChance = 20;
	
	private Date _netherOpenDate;
	private Date _endOpenDate;
	private boolean _enableGolemDieOnWither = true;
	private Random _rand;
	public MainEvents(Plugin plugin)
	{

		_netherOpenDate = DateParser.ParseDate("10/2/2023/18:00");
		_endOpenDate = DateParser.ParseDate("24/2/2023/18:00");
		
		_plugin = plugin;
		_itemM = ImusAPI._metods;
		getSettings();
		_cd = new Cooldowns();
		runnable();
		
		_joker = new MinecraftJokes();
		_rand = new Random();
//		try
//		{
//			Date date = DateParser.parseDate(_testDate);
//			
//			System.out.println("date: "+date + " is date passed or current"+DateParser.IsDateNowOrPassed(date));
//		} catch (ParseException e)
//		{
//			// TODO Auto-generated catch block
//			System.out.println("error has happend");
//			//e.printStackTrace();
//		}
	}
	@EventHandler
	public void OnEntityDamage(EntityDamageEvent e)
	{

		if(!(e.getEntity() instanceof Player)) return;
		
		ItemStack stack = ((Player)e.getEntity()).getInventory().getItemInOffHand();
		
		if(stack == null || stack.getType() != Material.TOTEM_OF_UNDYING) return;
		

		if(_rand.nextInt(100) >= _totemJokeChance) return;
		
		((Player)e.getEntity()).sendMessage(" ");
		((Player)e.getEntity()).sendMessage(ChatColor.BLUE+_joker.GetTotemJoke());
	}
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
	public void CancelPortals(PlayerPortalEvent e)
	{
		if(e.getPlayer().isOp()) return;

		String id = "portal."+e.getPlayer().getUniqueId().toString();
		final float portalCd = 2f;

		Location to = e.getTo();
		if(to == null) return;

		World world = to.getWorld();
		if(world == null) return;

		switch (world.getName()) {
			case "world_nether" -> {
				if (!IsNetherAllowed()) {
					if (!_cd.isCooldownReady(id)) {
						e.setCancelled(true);
						return;
					}

					System.out.println("date: " + _netherOpenDate + " is nether allowed: " + IsNetherAllowed() + " " + e.getPlayer().getName());
					e.getPlayer().sendMessage(ChatColor.RED + "Nether isn't opened yet!");
					e.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Nether opens in " + DateParser.GetTimeDifference(_netherOpenDate));
					e.setCancelled(true);

					_cd.addCooldownInSeconds(id, portalCd);
				}
			}
			case "world_the_end" -> {
				if (!IsEndAllowed()) {
					if (!_cd.isCooldownReady(id)) {
						e.setCancelled(true);
						return;
					}

					System.out.println("date: " + _endOpenDate + " is end allowed: " + IsEndAllowed() + " " + e.getPlayer().getName());
					e.getPlayer().sendMessage(ChatColor.RED + "End isn't opened yet!");
					e.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "End opens in " + DateParser.GetTimeDifference(_endOpenDate));
					e.setCancelled(true);

					_cd.addCooldownInSeconds(id, portalCd);
				}
			}
		}
	}
	
//	@EventHandler
//	public void OnBlockPlace(BlockPlaceEvent e)
//	{
//		
//		if(e.getBlock().getType() != Material.SPAWNER) return;
//		
//		CreatureSpawner spawner = (CreatureSpawner)e.getBlock().getState();
//		spawner.setSpawnedType(EntityType.CREEPER);
//		spawner.update();
//	}
	
	public boolean IsNetherAllowed()
	{
		return DateParser.IsDateNowOrPassed(_netherOpenDate);
	}
	public boolean IsEndAllowed()
	{
		return DateParser.IsDateNowOrPassed(_endOpenDate);
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
		if(event.isCancelled()) return;
		
		if(event.getDamager() instanceof IronGolem && event.getEntity() instanceof Wither)
		{
			if(!_enableGolemDieOnWither) return;
			IronGolem golem = (IronGolem) event.getDamager();
			golem.setHealth(0);
			event.setCancelled(true);
			return;
			
		}
		
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
			//p.sendMessage(ChatColor.RED + "You are in combat with new "+ChatColor.AQUA +"Mob!");				
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
						//p.sendMessage(ChatColor.GREEN +"" +ChatColor.BOLD+"Your combat with mob has ended!");
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
				if(Metods._ins.HasEnchant(stack, Enchantment.VANISHING_CURSE))
				{
					continue;
				}
				
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
				
		String endOpenDate_path = "settings.end_open_date";
		String netherOpenDate_path = "settings.nether_open_date";
		
		
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
//			
	
			config.set(endOpenDate_path, DateParser.FormatDate(_endOpenDate));
			config.set(netherOpenDate_path, DateParser.FormatDate(_netherOpenDate));
			
			
			
			cm.saveConfig();
			return;
		}
		if(!config.contains(endOpenDate_path)) 
		{
			config.set(endOpenDate_path, DateParser.FormatDate(_endOpenDate));
		}
		if(!config.contains(netherOpenDate_path)) 
		{
			config.set(netherOpenDate_path, DateParser.FormatDate(_netherOpenDate));
		}
		cm.saveConfig();
		String jokeTotemChance = "jokes.jokeTotemChance";
		
		if(!config.contains("jokes.")) 
		{
			config.set(jokeTotemChance, _totemJokeChance);
			cm.saveConfig();
		}
		
		
		saveArmor = config.getBoolean(a_path);
		saveTools = config.getBoolean(t_path);
		saveWeapons = config.getBoolean(w_path);
		saveHotBar = config.getBoolean(h_path);
		durability_penalty_pvp = config.getDouble(dpvp_path);
		durability_penalty_pve = config.getDouble(dpve_path);
		durability_penalty_mob = config.getDouble(dmob_path);
		_cd_in_combat_cooldown = config.getInt(dcombat_cd_path);
		

		_endOpenDate = DateParser.ParseDate(config.getString(endOpenDate_path));
		_netherOpenDate = DateParser.ParseDate(config.getString(netherOpenDate_path));
		
		_totemJokeChance = config.getInt(jokeTotemChance);
			
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
