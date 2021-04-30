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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iMiniGames.Arenas.SpleefArena;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.ConfigMaker;
import imu.iMiniGames.Other.SpleefDataCard;

public class SpleefManager 
{
	Main _main;
	
	HashMap<Integer, SpleefArena> _spleefArenas = new HashMap<>();
	HashMap<UUID, SpleefDataCard> _player_DataCards = new HashMap<>();
	HashMap<PotionEffectType, Boolean> _potionEffects_positive_enabled = new HashMap<>();
	
	String text_arena_yml="Arenas_Spleef";
	
	int _maximum_best_of = 5;

	public SpleefManager(Main main) 
	{
		_main = main;
		addPotionEffects();
	}
	
	public void onEnable()
	{
		loadArenas();
	}
	
	public void onDisable()
	{
		//saveAllArenas();
	}
	
	public void clearPlayerDataCards()
	{
		_player_DataCards.clear();
	}
	
	public void savePlayerDataCard(Player p, SpleefDataCard card)
	{
		_player_DataCards.put(p.getUniqueId(), card);
	}
	
	public SpleefDataCard getPlayerDataCard(Player p)
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
		_potionEffects_positive_enabled.put(PotionEffectType.FAST_DIGGING,false);
		_potionEffects_positive_enabled.put(PotionEffectType.HEAL,false);
		_potionEffects_positive_enabled.put(PotionEffectType.ABSORPTION,false);
		_potionEffects_positive_enabled.put(PotionEffectType.UNLUCK,false);
		_potionEffects_positive_enabled.put(PotionEffectType.CONDUIT_POWER,false);
		_potionEffects_positive_enabled.put(PotionEffectType.DOLPHINS_GRACE,false);
		_potionEffects_positive_enabled.put(PotionEffectType.BAD_OMEN,false);
		_potionEffects_positive_enabled.put(PotionEffectType.HERO_OF_THE_VILLAGE,false);
		_potionEffects_positive_enabled.put(PotionEffectType.HARM,false);
		_potionEffects_positive_enabled.put(PotionEffectType.SATURATION,false);
		_potionEffects_positive_enabled.put(PotionEffectType.HUNGER,false);
		_potionEffects_positive_enabled.put(PotionEffectType.WATER_BREATHING,false);
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
	
	public void createSpleefArena(String name)
	{
		addArena(new SpleefArena(name));
	}
	
	void addArena(SpleefArena arena)
	{
		_spleefArenas.put(_spleefArenas.size(), arena);
	}
	public SpleefArena getArena(String arena_name)
	{
		for(Entry<Integer, SpleefArena> entry : _spleefArenas.entrySet())
		{
			if(entry.getValue().get_name().equalsIgnoreCase(arena_name))
			{
				return entry.getValue();
			}
		}

		return null;
	}
	
	void removeArenaHash(String arena_name)
	{
		int key = -1;
		for(Entry<Integer, SpleefArena> entry : _spleefArenas.entrySet())
		{
			if(entry.getValue().get_name().equalsIgnoreCase(arena_name))
			{
				key = entry.getKey();
				break;
			}
		}
		
		if(key != -1)
		{
			_spleefArenas.remove(key);
		}

	}
	
	public ArrayList<SpleefArena> getArenas()
	{
		ArrayList<SpleefArena> ar = new ArrayList<SpleefArena>();
		for(Entry<Integer, SpleefArena> entry : _spleefArenas.entrySet())
		{
			ar.add(entry.getValue());
		}
		return ar;
	}
	public SpleefArena getArena(int idx)
	{
		return _spleefArenas.get(idx);
	}
	
	
	public boolean setupPlatformCorners(Player p, Location loc)
	{		
		return false;
	}
	
	void saveAllArenas()
	{
		if(_spleefArenas.isEmpty())
			return;
		
		for(Entry<Integer, SpleefArena> entry : _spleefArenas.entrySet())
		{
			saveArena(entry.getValue());
		}
	}
	
	public void saveArena(SpleefArena arena)
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
				config.set("CornerLoc1", arena.getPlatformCorner(0));
				config.set("CornerLoc2", arena.getPlatformCorner(1));
				config.set("LobbyLoc", arena.get_spectator_lobby());
				
				for(int i = 0; i < arena.getTotalSpawnPositions(); ++i)
				{
					config.set("spawn_pos"+i, arena.getSpawnpointLoc(i));
				}
				
				cm.saveConfig();
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	public void removeArena(SpleefArena arena)
	{
		removeArenaHash(arena.get_name());
		ConfigMaker cm = new ConfigMaker(_main, text_arena_yml+"/"+arena.get_name()+".yml");
		if(cm.isExists())
		{
			cm.removeConfig();
		}
		
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
						Location loc1 = config.getLocation("CornerLoc1");
						Location loc2 = config.getLocation("CornerLoc2");
						Location lobby_loc = config.getLocation("LobbyLoc");
						
						SpleefArena arena = new SpleefArena(name);
						arena.set_maxPlayers(max_p);
						arena.setPlatformCorner(0, loc1);
						arena.setPlatformCorner(1, loc2);
						arena.set_spectator_lobby(lobby_loc);
						arena.set_description(desc);
						
						for(int i = 0; i < max_p; ++i)
						{
							arena.addSpawnPosition(config.getLocation("spawn_pos"+i));
						}
						
						addArena(arena);
						System.out.println("Arena added: "+name);
					}
				} 
				catch (Exception e) 
				{
					System.out.println("imusMiniGames: Didn't find any created spleef arenas");
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
				ConfigMaker cm = new ConfigMaker(_main, "Spleef/Enabled_PotionEffects.yml");
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
		