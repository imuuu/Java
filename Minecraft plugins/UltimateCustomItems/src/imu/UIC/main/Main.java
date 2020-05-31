package imu.UIC.main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import imu.UIC.Commands.UICCmd;
import imu.UIC.Events.Event1;
import imu.UIC.Handlers.CommandHandler;
import imu.UIC.Managers.MainMenuManager;
import imu.UIC.Managers.SetMenuManager;
import imu.UIC.Other.ConfigMaker;
import imu.UIC.Other.ItemMetods;
import imu.UIC.SubCommands.subUICmenuCmd;

public class Main extends JavaPlugin
{
	final Main _main = this;
	final MainMenuManager _mainMenuManager = new MainMenuManager(this);
	final SetMenuManager _setMenuManager = new SetMenuManager(this);
	
	ItemMetods _itemM = new ItemMetods(this);
	
	
	public void registerCommands() 
    {  	
        CommandHandler handler = new CommandHandler();

        String cmd1="uic";
        handler.registerCmd(cmd1, new UICCmd(this));  
        handler.setPermissionOnLastCmd(cmd1);
        handler.registerSubCmd(cmd1, "menu", new subUICmenuCmd(this));
 
       
        
        getCommand(cmd1).setExecutor(handler);
        
    }
	
	@Override
	public void onEnable() 
	{		
		ConfigsSetup();
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" World Restore Active!");
		getServer().getPluginManager().registerEvents(new Event1(this), this);

	}
		
	@Override
	public void onDisable()
	{
	}
	public Main getInstance()
	{
		return _main;
	}
	
	public MainMenuManager getMainMenuManager()
	{
		return _mainMenuManager;
	}
	public SetMenuManager getSetMenuManager()
	{
		return _setMenuManager;
	}
	public ItemMetods getItemM()
	{
		return _itemM;
	}

	void ConfigsSetup()
	{
		//makeSettingsConfig();
	}
	
	void makeSettingsConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, "settings.yml");

		try 
		{	//_playWorld = cm.addDefault("PlayWorldName", _playWorld, "World where player play and what will be fixed.. Changing this requires restart.. remember after changing this clear chunk.yml and chunk_being_fixed.yml");
			
			//cm.addComments();
			
		} catch (Exception e) 
		{
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Something got wrong UIC fileNamed: "+cm.getFileName());
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Maybe you casted some value as Integer When it should be Double?");
		}
		
		
	}
}
