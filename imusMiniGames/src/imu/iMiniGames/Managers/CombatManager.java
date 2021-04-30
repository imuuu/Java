package imu.iMiniGames.Managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Arenas.Arena;
import imu.iMiniGames.Arenas.CombatArena;
import imu.iMiniGames.Leaderbords.CombatLeaderBoard;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.ArenaKit;
import imu.iMiniGames.Other.CombatDataCard;
import imu.iMiniGames.Other.ConfigMaker;

public class CombatManager 
{
	Main _main;
	
	HashMap<Integer, CombatArena> _combatArenas = new HashMap<>();
	HashMap<UUID, CombatDataCard> _player_DataCards = new HashMap<>();
	
	HashMap<PotionEffectType, Boolean> _potionEffects_positive_enabled = new HashMap<>();
	
	ArrayList<ArenaKit> arena_kits = new ArrayList<>();
	
	String text_arena_yml="Arenas_Combat";
	String text_kits_yml="Combat/Kits";
	
	int _maximum_best_of = 5;
	
	CombatLeaderBoard _leaderboard;
	
	public CombatManager(Main main) 
	{
		_main = main;
		addPotionEffects();
		_leaderboard = new CombatLeaderBoard(main, "CombatLeaderBoards");
		
	}
	
	public void onEnable()
	{
		loadArenas();
		loadKits();
		
		new BukkitRunnable() 
		{			
			@Override
			public void run() 
			{
				_leaderboard.loadFromFile();
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	public void onDisable()
	{
		_leaderboard.saveToFile();
		//saveAllArenas();
	}
		
	public CombatLeaderBoard getLeaderBoard()
	{
		return _leaderboard;
	}
	
	public void clearPlayerDataCards()
	{
		_player_DataCards.clear();
	}
	public ArrayList<ArenaKit> getArena_kits() {
		return arena_kits;
	}
	
	public ArenaKit getKit(String name)
	{
		for(int i = 0 ; i < arena_kits.size(); ++i)
		{
			if(arena_kits.get(i).get_kitName().equalsIgnoreCase(name))
			{
				return arena_kits.get(i);
			}
		}
		return null;
	}
		
	
	public void addKit(String name, ItemStack[] _kitInv)
	{
		ArenaKit kit = new ArenaKit(name, _kitInv);
		for(int i = 0 ; i < arena_kits.size(); ++i)
		{
			if(arena_kits.get(i).get_kitName().equalsIgnoreCase(name))
			{
				arena_kits.set(i, kit);
				saveKit(kit);
				return;
			}
				
		}
		
		arena_kits.add(kit);
		saveKit(kit);
	}
	void saveKit(ArenaKit kit)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				ConfigMaker cm = new ConfigMaker(_main, text_kits_yml+"/kit_"+kit.get_kitName()+".yml");
				FileConfiguration config = cm.getConfig();
				config.set("Kit_Name", kit.get_kitName());
				
				ItemStack[] content = kit.get_kitInv();
				for(int i = 0; i <content.length; ++i)
				{
					ItemStack s = content[i];
					if(s != null)
					{
						config.set("KitContent."+i, s );
					}else
					{
						config.set("KitContent."+i, "null");
					}
				}
				cm.saveConfig();
			}
		}.runTaskAsynchronously(_main);
	}
	public void savePlayerDataCard(Player p, CombatDataCard card)
	{
		_player_DataCards.put(p.getUniqueId(), card);
	}
	
	public CombatDataCard getPlayerDataCard(Player p)
	{
		return _player_DataCards.get(p.getUniqueId());
	}
	
	public boolean hasPlayerDataCard(Player p)
	{
		return _player_DataCards.containsKey(p.getUniqueId());
	}
	
	void addPotionEffects()
	{
		for(PotionEffectType t : PotionEffectType.values())
		{
			_potionEffects_positive_enabled.put(t, true);
		}
		_potionEffects_positive_enabled.put(PotionEffectType.LUCK, false);
		_potionEffects_positive_enabled.put(PotionEffectType.LEVITATION,false);
		_potionEffects_positive_enabled.put(PotionEffectType.UNLUCK,false);
		_potionEffects_positive_enabled.put(PotionEffectType.CONDUIT_POWER,false);
		_potionEffects_positive_enabled.put(PotionEffectType.DOLPHINS_GRACE,false);
		_potionEffects_positive_enabled.put(PotionEffectType.BAD_OMEN,false);
		_potionEffects_positive_enabled.put(PotionEffectType.HERO_OF_THE_VILLAGE,false);
		_potionEffects_positive_enabled.put(PotionEffectType.HARM,false);
		_potionEffects_positive_enabled.put(PotionEffectType.WITHER,false);
		

	}
	
	public HashMap<PotionEffectType, Boolean> getPotionEffects()
	{
		return _potionEffects_positive_enabled;
	}
	
	public int get_maximum_best_of() {
		return _maximum_best_of;
	}

	public void set_maximum_best_of(int _maximum_best_of) {
		this._maximum_best_of = _maximum_best_of;
	}
	
	public void createArena(String name)
	{
		addArena(new CombatArena(name));
	}
	
	void addArena(CombatArena arena)
	{
		_combatArenas.put(_combatArenas.size(), arena);
	}
	public CombatArena getArena(String arena_name)
	{
		for(Entry<Integer, CombatArena> entry : _combatArenas.entrySet())
		{
			if(entry.getValue().get_name().toLowerCase().contains(arena_name.toLowerCase()))
			{
				return entry.getValue();
			}
		}

		return null;
	}
	
	void removeArenaHash(String arena_name)
	{
		int key = -1;
		for(Entry<Integer, CombatArena> entry : _combatArenas.entrySet())
		{
			if(entry.getValue().get_name().equalsIgnoreCase(arena_name))
			{
				key = entry.getKey();
				break;
			}
		}
		
		if(key != -1)
		{
			_combatArenas.remove(key);
		}

	}
	
	public ArrayList<CombatArena> getArenas()
	{
		ArrayList<CombatArena> ar = new ArrayList<>();
		for(Entry<Integer, CombatArena> entry : _combatArenas.entrySet())
		{
			ar.add(entry.getValue());
		}
		return ar;
	}
	public CombatArena getArena(int idx)
	{
		return _combatArenas.get(idx);
	}
	

	void saveAllArenas()
	{
		if(_combatArenas.isEmpty())
			return;
		
		for(Entry<Integer, CombatArena> entry : _combatArenas.entrySet())
		{
			saveArena(entry.getValue());
		}
	}
	
	public void saveArena(CombatArena arena)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				ConfigMaker cm = new ConfigMaker(_main, text_arena_yml+"/"+arena.get_name().toLowerCase()+".yml");
				FileConfiguration config = cm.getConfig();
				
				config.set("Name", arena.get_name().toString());
				config.set("Desc", arena.get_description());
				config.set("MaxPlayers",arena.get_maxPlayers());
				config.set("MiddleLoc", arena.getArenas_middleloc());
				config.set("LobbyLoc", arena.get_spectator_lobby());
				config.set("Max_Radius", arena.getArena_radius());
				
				for(int i = 0; i < arena.getTotalSpawnPositions(); ++i)
				{
					config.set("spawn_pos"+i, arena.getSpawnpointLoc(i));
				}
				
				cm.saveConfig();
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	public void removeArena(Arena arena)
	{
		removeArenaHash(arena.get_name());
		ConfigMaker cm = new ConfigMaker(_main, text_arena_yml+"/"+arena.get_name()+".yml");
		if(cm.isExists())
		{
			cm.removeConfig();
		}
		
	}
	
	public void loadKits()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				try 
				{
					for(File file : new File(_main.getDataFolder().getAbsoluteFile()+File.separator + text_kits_yml).listFiles())
					{
						if(!file.exists())
							continue;
						
						FileConfiguration config = YamlConfiguration.loadConfiguration(file);
						
						
						String name = config.getString("Kit_Name");
						
						ItemStack[] stacks = new ItemStack[41];
						
						for (String key : config.getConfigurationSection("KitContent.").getKeys(false)) 
						{
							if(config.getString("KitContent."+key).equalsIgnoreCase("null"))
							{
								stacks[Integer.parseInt(key)] = null;
							}
							else
							{
								stacks[Integer.parseInt(key)] = config.getItemStack("KitContent."+key);
							}
							
						}
						
						arena_kits.add(new ArenaKit(name, stacks));
						System.out.println("Combat kit added: "+name);
					}
									
				} 
				catch (Exception e) 
				{
					System.out.println("imusMiniGames: Didn't find any created combat arenas");
				}
				
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void loadArenas()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
//				ConfigMaker cm = new ConfigMaker(_main, text_arena_yml+"/1temp1.yml");
//				cm.saveConfig();
//				cm.removeConfig();
				try 
				{
					for(File file : new File(_main.getDataFolder().getAbsoluteFile()+File.separator + text_arena_yml).listFiles())
					{
						if(!file.exists())
							continue;
						
						FileConfiguration config = YamlConfiguration.loadConfiguration(file);
						
						
						String name = config.getString("Name");
						String desc = config.getString("Desc");
						Integer max_p = config.getInt("MaxPlayers");
						Location lobby_loc = config.getLocation("LobbyLoc");
						Location middle_loc = config.getLocation("MiddleLoc");
						Integer max_radius = config.getInt("Max_Radius");
						
						CombatArena arena = new CombatArena(name);
						arena.set_maxPlayers(max_p);
						arena.set_spectator_lobby(lobby_loc);
						arena.set_description(desc);
						arena.setArenas_middleloc(middle_loc);
						arena.setArena_radius(max_radius);
						
						for(int i = 0; i < max_p; ++i)
						{
							arena.addSpawnPosition(config.getLocation("spawn_pos"+i));
						}
						
						addArena(arena);
						System.out.println("Combat arena added: "+name);
					}
				} 
				catch (Exception e) 
				{
					System.out.println("imusMiniGames: Didn't find any created combat arenas");
				}
				
			}
		}.runTaskAsynchronously(_main);
				
	}
	
	public void loadPotionsConfig()
	{
		new BukkitRunnable() 
		{		
			@Override
			public void run() 
			{
				ConfigMaker cm = new ConfigMaker(_main, "Combat/Enabled_PotionEffects.yml");
				FileConfiguration config = cm.getConfig();
				
				if(!cm.isExists())
				{
					for(Entry<PotionEffectType, Boolean> entry : getPotionEffects().entrySet())
					{
						config.set(entry.getKey().getName(), entry.getValue());
					}
				}
				else
				{
					getPotionEffects().clear();
					for(PotionEffectType t : PotionEffectType.values())
					{
						Boolean value = config.getBoolean(t.getName());
						getPotionEffects().put(t, value);
					}
				}
				
				cm.saveConfig();
			}
			
		}.runTaskAsynchronously(_main);
	}
}
