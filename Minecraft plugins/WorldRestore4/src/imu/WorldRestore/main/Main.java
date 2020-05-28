package imu.WorldRestore.main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.WorldGuard;

import imu.WorldRestore.Commands.WorldRestoreCmd;
import imu.WorldRestore.Events.Event1;
import imu.WorldRestore.Handlers.CommandHandler;
import imu.WorldRestore.Managers.ChunkManager;
import imu.WorldRestore.Other.ChunkFileHandler;
import imu.WorldRestore.Other.ChunkHandler;
import imu.WorldRestore.Other.ConfigMaker;
import imu.WorldRestore.Other.ItemMetods;
import imu.WorldRestore.Other.PlayerData;
import imu.WorldRestore.SubCommands.subWrFixAll;
import imu.WorldRestore.SubCommands.subCmd3;
import imu.WorldRestore.SubCommands.subTeleportCmd;
import imu.WorldRestore.SubCommands.subWrAreaCmd;

public class Main extends JavaPlugin
{
	final Main main = this;
	WorldGuard wg = null;
	ItemMetods itemM = null;
	
	ChunkManager _chunkManager = null;
	ChunkHandler _chunkHandler = null;
	ChunkFileHandler _chunkFileHandler = null;
	PlayerData _playerData = null;
	
	
	int _chunkHandler_maxChunkFixSize = 2;
	int _chunkHandler_delay = 10; //s
	

	public void registerCommands() 
    {  	
        CommandHandler handler = new CommandHandler();

        String cmd1="wr";
        handler.registerCmd(cmd1, new WorldRestoreCmd(this));  
        handler.setPermissionOnLastCmd("wr");
        handler.registerSubCmd(cmd1, "area", new subWrAreaCmd(this));
        handler.registerSubCmd(cmd1, "test2", new subWrFixAll(this));
        handler.registerSubCmd(cmd1, "test3", new subCmd3(this));
        handler.registerSubCmd(cmd1, "tp", new subTeleportCmd(this));
       
        
        getCommand(cmd1).setExecutor(handler);
        
    }
	
	@Override
	public void onEnable() 
	{		
		wg = WorldGuard.getInstance();
		itemM = new ItemMetods(this);
		_chunkManager = new ChunkManager(this);
		_chunkHandler = new ChunkHandler(this);
		_chunkFileHandler = new ChunkFileHandler(this);
		_chunkManager.SetupHandlers();
		_chunkHandler.SetupHanddlers();
		_playerData = new PlayerData(this);
		
		ConfigsSetup();

		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" World Restore Active!");
		getServer().getPluginManager().registerEvents(new Event1(this), this);

	}
		
	@Override
	public void onDisable()
	{
		getChunkManager().saveAllChunks();
	}
	public Main getInstance()
	{
		return main;
	}
	public WorldGuard getWorldGuard()
	{
		return wg;
	}
		
	public void ConfigsSetup()
	{
		//makeSettingsConfig(false);

	}
	
	public ChunkManager getChunkManager()
	{
		return _chunkManager;
	}
	
	public ChunkFileHandler getChunkFileHandler()
	{
		return _chunkFileHandler;
	}
	public ChunkHandler getChunkHandler()
	{
		return _chunkHandler;
	}
	
	public ItemMetods getItemM() 
	{
		return itemM;
	}
	
	public PlayerData getPlayerData()
	{
		return _playerData;
	}
	
	public void makeSettingsConfig(boolean refresh)
	{
		ConfigMaker cm = new ConfigMaker(this, "settings.yml");
		if(refresh)
		{
			cm.clearConfig();
		}
		try 
		{
			//default_prices[0] = cm.addDefault("DefaultMinPrice", default_prices[0],"Minprice: item will not sold lower than this each epoch");			
			//cm.addComments();
			
		} catch (Exception e) 
		{
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Something got wrong World Restore fileNamed: "+cm.getFileName());
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Maybe you casted some value as Integer When it should be Double?");
		}
		
		
	}
}
