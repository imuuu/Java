package me.def.Plate;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import Commands.DefaultCommands;
import Commands.ExampleCmd;
import Commands.MoiCmd;
import Handlers.CommandHandler;
import SubCommands.ArgsCmd;
import SubCommands.TestSubCmd;
import imu.def.Events.EventClass;


public class Main extends JavaPlugin
{

    public void registerCommands() 
    {
 
        CommandHandler handler = new CommandHandler();

        handler.registerCmd("example", new ExampleCmd());       
        handler.registerSubCmd("example", "aRgs", new ArgsCmd());
        handler.setPermissionOnLastCmd("lol.lol");
        
        handler.registerCmd("moi", new MoiCmd());
        handler.registerSubCmd("moi", "test", new TestSubCmd());
        
        getCommand("example").setExecutor(handler);
        getCommand("moi").setExecutor(handler);
        
        
        //TODO not implemented yet examples
        //expamle player <> give ..
        //expamle player <> get
        //expamle player <> take ..
    }
	
	@Override
	public void onEnable() 
	{
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" Default plate has been activated!");
		getServer().getPluginManager().registerEvents(new EventClass(), this);
	}

}
