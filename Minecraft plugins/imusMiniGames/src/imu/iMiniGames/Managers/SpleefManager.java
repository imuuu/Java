package imu.iMiniGames.Managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import imu.iMiniGames.Arenas.SpleefArena;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.ConfigMaker;

public class SpleefManager 
{
	Main _main;
	
	HashMap<String, SpleefArena> _spleefArenas = new HashMap<>();
	String text_arena_yml="Arenas_Spleef";
	
	public SpleefManager(Main main) 
	{
		_main = main;
	}
	
	public void onEnable()
	{
		loadArenas();
	}
	
	public void onDisable()
	{
		//saveAllArenas();
	}
	public void createSpleefArena(String name)
	{
		addArena(new SpleefArena(name));
	}
	
	void addArena(SpleefArena arena)
	{
		_spleefArenas.put(arena.get_name().toLowerCase(), arena);
	}
	public SpleefArena getArena(String arena_name)
	{
		arena_name = arena_name.toLowerCase();
		if(_spleefArenas.containsKey(arena_name))
		{
			return _spleefArenas.get(arena_name);
		}
		return null;
	}
	
	public SpleefArena getArena(int idx)
	{
		File[] f = new File(_main.getDataFolder().getAbsoluteFile()+File.separator + text_arena_yml).listFiles();
		if(f.length != 0)
		{
			return getArena(f[idx].getName().replace(".yml", ""));
		}
		return null;
	}
	
	
	public boolean setupPlatformCorners(Player p, Location loc)
	{		
		return false;
	}
	
	void saveAllArenas()
	{
		if(_spleefArenas.isEmpty())
			return;
		
		for(Entry<String, SpleefArena> entry : _spleefArenas.entrySet())
		{
			saveArena(entry.getValue());
		}
	}
	
	public void saveArena(SpleefArena arena)
	{
		ConfigMaker cm = new ConfigMaker(_main, text_arena_yml+"/"+arena.get_name().toLowerCase()+".yml");
		FileConfiguration config = cm.getConfig();
		
		config.set("Name", arena.get_name());
		config.set("DisplayName", arena.get_displayName());		
		config.set("MaxPlayers",arena.get_maxPlayers());
		config.set("CornerLoc1", arena.getPlatformCorner(0));
		config.set("CornerLoc2", arena.getPlatformCorner(1));
		
		for(int i = 0; i < arena.getTotalSpawnPositions(); ++i)
		{
			config.set("spawn_pos"+i, arena.getSpawnpointLoc(i));
		}
		
		cm.saveConfig();
	}
	
	public void removeArena(SpleefArena arena)
	{
		ConfigMaker cm = new ConfigMaker(_main, text_arena_yml+"/"+arena.get_name()+".yml");
		cm.removeConfig();
	}
	public void loadArenas()
	{
	
		for(File file : new File(_main.getDataFolder().getAbsoluteFile()+File.separator + text_arena_yml).listFiles())
		{
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			String name = config.getString("Name");
			String d_name = config.getString("DisplayName");
			Integer max_p = config.getInt("MaxPlayers");
			Location loc1 = config.getLocation("CornerLoc1");
			Location loc2 = config.getLocation("CornerLoc2");
			
			SpleefArena arena = new SpleefArena(name);
			arena.set_displayName(d_name);
			arena.set_maxPlayers(max_p);
			arena.setPlatformCorner(0, loc1);
			arena.setPlatformCorner(1, loc2);
			
			for(int i = 0; i < max_p; ++i)
			{
				arena.addSpawnPosition(config.getLocation("spawn_pos"+i));
			}
			
			addArena(arena);
			System.out.println("Arena added: "+name);
		}
		
	}
}
		