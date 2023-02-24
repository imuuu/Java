package imu.DontLoseItems.main;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import imu.DontLoseItems.Commands.ExampleCmd;
import imu.DontLoseItems.CustomEnd.EndEvents;
import imu.DontLoseItems.CustomEnd.UnstableEnd;
import imu.DontLoseItems.CustomItems.Manager_HellTools;
import imu.DontLoseItems.Events.ChestLootEvents;
import imu.DontLoseItems.Events.DotEvents;
import imu.DontLoseItems.Events.MainEvents;
import imu.DontLoseItems.Events.NetherEvents;
import imu.DontLoseItems.other.Manager_HellArmor;
import imu.iAPI.Handelers.CommandHandler;


public class DontLoseItems extends JavaPlugin
{
	public static DontLoseItems Instance;
   
	
  
	@Override
	public void onEnable() 
	{
		Instance = this;
		
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" Dont lose your items has been activated!");
		getServer().getPluginManager().registerEvents(new MainEvents(this), this);
		getServer().getPluginManager().registerEvents(new DotEvents(), this);
		getServer().getPluginManager().registerEvents(new NetherEvents(), this);
		getServer().getPluginManager().registerEvents(new EndEvents(), this);
		
		getServer().getPluginManager().registerEvents(new Manager_HellArmor(), this);
		getServer().getPluginManager().registerEvents(new Manager_HellTools(), this);
		getServer().getPluginManager().registerEvents(new ChestLootEvents(), this);
		//getServer().getPluginManager().registerEvents(new FishingEvent(this), this);
	}
	
	@Override
	public void onDisable() 
	{
		NetherEvents.Instance.OnDisabled();
		Manager_HellTools.Instance.OnDisable();
		EndEvents.Instance.OnDisabled();
	}
	
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


	@SuppressWarnings("unused")
	public static boolean IsEnd(World world)
	{
		if(world == null) return false;
		
		return world.getEnvironment() == Environment.THE_END;
	}
	@SuppressWarnings("unused")
	public static boolean IsEnd(Entity entity)
	{
		if(entity == null) return false;
		
		return IsEnd(entity.getWorld());
	}
	@SuppressWarnings("unused")
	public static boolean IsEnd(Block block)
	{
		if(block == null) return false;
		
		return IsEnd(block.getWorld());
	}
	@SuppressWarnings("unused")
	public static boolean IsEnd(Location loc)
	{
		if(loc == null) return false;
		
		return IsEnd(loc.getWorld());
	}

	
	

}
