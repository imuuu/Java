package imu.DontLoseItems.main;


import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import imu.DontLoseItems.Commands.ExampleCmd;
import imu.DontLoseItems.Events.DotEvents;
import imu.DontLoseItems.Events.MainEvents;
import imu.DontLoseItems.Events.NetherEvents;
import imu.iAPI.Handelers.CommandHandler;


public class DontLoseItems extends JavaPlugin
{
	public static DontLoseItems Instance;
    public void registerCommands() 
    {
 
        CommandHandler handler = new CommandHandler(this);
        handler.registerCmd("drop", new ExampleCmd());       
        handler.setPermissionOnLastCmd("dontloseitems.drop");
              
        //getCommand("drop").setExecutor(handler);
   
        //TODO not implemented yet examples
        //expamle player <> give ..
        //expamle player <> get
        //expamle player <> take ..
        //asd
    }
	
	@Override
	public void onEnable() 
	{
		Instance = this;
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" Dont lose your items has been activated!");
		getServer().getPluginManager().registerEvents(new MainEvents(this), this);
		getServer().getPluginManager().registerEvents(new DotEvents(), this);
		getServer().getPluginManager().registerEvents(new NetherEvents(), this);
		//getServer().getPluginManager().registerEvents(new FishingEvent(this), this);
	}
	
	

}
