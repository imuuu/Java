package imu.AccountBoundItems.main;


import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import imu.AccountBoundItems.Commands.BoundCommand;
import imu.AccountBoundItems.Events.DropAndPickup;
import imu.AccountBoundItems.Events.OnDamage;
import imu.AccountBoundItems.Events.OnPlayerInteract;
import imu.AccountBoundItems.Handlers.CommandHandler;
import imu.AccountBoundItems.SubCommands.subBoundCmd;
import imu.AccountBoundItems.SubCommands.subBrokenCmd;
import imu.AccountBoundItems.SubCommands.subRepairAllCmd;
import imu.AccountBoundItems.SubCommands.subRepairCmd;
import imu.AccountBoundItems.SubCommands.subUnBoundCmd;


public class Main extends JavaPlugin
{
	public HashMap<String, String> loreNames = new HashMap<String, String>();
	
	static Main instance;
	
    void registerCommands() 
    {
 
        CommandHandler handler = new CommandHandler();
        String cmd1 = "abi";
        handler.registerCmd(cmd1, new BoundCommand());       
        handler.setPermissionOnLastCmd("abi");
        handler.registerSubCmd(cmd1, "bind", new subBoundCmd());
        handler.registerSubCmd(cmd1, "broken", new subBrokenCmd());
        handler.registerSubCmd(cmd1, "unbind", new subUnBoundCmd());
        handler.registerSubCmd(cmd1, "repair", new subRepairCmd());
        handler.registerSubCmd(cmd1, "repair all", new subRepairAllCmd());
        //handler.setPermissionOnLastCmd(cmd1+".bound");
        
        
        getCommand(cmd1).setExecutor(handler);
 
        //TODO not implemented yet examples
        //expamle player <> give ..
        //expamle player <> get
        //expamle player <> take ..
    }
	
    void registerLoreNames()
    {
    	loreNames.put("bound", ChatColor.GRAY + "BOUND: ");
    	loreNames.put("broken",ChatColor.RED + "" + ChatColor.BOLD + "BROKEN");
    }
    
	@Override
	public void onEnable() 
	{
		instance = this;
		registerLoreNames();
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" AccountBoundItems has been activated!");
		getServer().getPluginManager().registerEvents(new OnDamage(), this);
		getServer().getPluginManager().registerEvents(new DropAndPickup(), this);
		getServer().getPluginManager().registerEvents(new OnPlayerInteract(), this);
	}
	
	public static Main getInstance()
	{
		return instance;
	}

}
