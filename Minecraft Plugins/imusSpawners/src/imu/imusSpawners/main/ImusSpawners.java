package imu.imusSpawners.main;


import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import imu.iAPI.Handelers.CommandHandler;
import imu.imusSpawners.Commands.ExampleCmd;
import imu.imusSpawners.Events.MainEvents;


public class ImusSpawners extends JavaPlugin
{
	public static ImusSpawners Instance;

    public void registerCommands() 
    {
    	
        CommandHandler handler = new CommandHandler(this);
        handler.registerCmd("drop", new ExampleCmd());       
        handler.setPermissionOnLastCmd("dontloseitems.drop");
              

    }
	
	@Override
	public void onEnable() 
	{
		Instance = this;

		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" Imus Spawners is Activated");
		getServer().getPluginManager().registerEvents(new MainEvents(), this);
		//getServer().getPluginManager().registerEvents(new FishingEvent(this), this);
	}
	
	
}
