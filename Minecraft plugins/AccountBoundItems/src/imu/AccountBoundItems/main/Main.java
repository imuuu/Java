package imu.AccountBoundItems.main;


import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import imu.AccountBoundItems.Commands.ExampleCmd;
import imu.AccountBoundItems.Events.EventClass;
import imu.AccountBoundItems.Handlers.CommandHandler;
import imu.AccountBoundItems.SubCommands.ArgsCmd;


public class Main extends JavaPlugin
{

    public void registerCommands() 
    {
 
        CommandHandler handler = new CommandHandler();

        handler.registerCmd("acc", new ExampleCmd());       
        handler.registerSubCmd("acc", "aRgs", new ArgsCmd());
        handler.setPermissionOnLastCmd("accountbounditems.acc");
        
        
        getCommand("acc").setExecutor(handler);
 
        //TODO not implemented yet examples
        //expamle player <> give ..
        //expamle player <> get
        //expamle player <> take ..
    }
	
	@Override
	public void onEnable() 
	{
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" AccountBoundItems has been activated!");
		getServer().getPluginManager().registerEvents(new EventClass(), this);
	}

}
