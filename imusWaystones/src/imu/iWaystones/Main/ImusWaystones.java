package imu.iWaystones.Main;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import imu.iAPI.Handelers.CommandHandler;
import imu.iAPI.Other.ImusTabCompleter;
import imu.iAPI.Other.MySQL;
import imu.iWaystones.Managers.WaystoneManager;
import imu.iWaystones.Managers.WaystoneManagerSQL;
import imu.iWaystones.Other.CmdHelper;
import imu.iWaystones.SubCmds.CMD;
import imu.iWaystones.SubCmds.SubWaystoneConfirmationCmd;
import imus.iWaystones.Events.WaystoneEvents;

public class ImusWaystones extends JavaPlugin
{
	public static ImusWaystones _instance;
	MySQL _SQL;
	ImusTabCompleter _tab_cmd1;
	CmdHelper _cmdHelper;
	final private String _pluginName = "[imusWaystones]";
	
	private WaystoneManager _waystoneManagers;
	
	@Override
	public void onEnable() 
	{
		_instance = this;	
		if(!ConnectDataBase())
		{
			getServer().getConsoleSender().sendMessage(ChatColor.RED +_pluginName+" has been disabled due to unable to connect to database!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +_pluginName+" Plugin folder you can adjust database settings");
			return;
		}
		
		_waystoneManagers = new WaystoneManager();
		
		
		_cmdHelper = new CmdHelper();
		
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +_pluginName+" has been activated!");
		
		registerCommands();
		
		new WaystoneEvents();
		
		_waystoneManagers.Init();
	}
	

	@Override
	 public void onDisable()
	{
				
		if(_SQL != null)
			_SQL.Disconnect();
		
		
	}
	
	boolean ConnectDataBase()
	{
		_SQL = new MySQL(this, "imusWaystones");
		try {
			_SQL.Connect();
			Bukkit.getLogger().info(ChatColor.GREEN +_pluginName+" Database Connected!");
			return true;
		} 
		catch (ClassNotFoundException | SQLException e) {

			Bukkit.getLogger().info(ChatColor.GREEN +_pluginName+" Database not connected");
		}
		
		return false;
	}
	
	public void registerCommands() 
	{
		HashMap<String, String[]> cmd1AndArguments = new HashMap<>();
		CommandHandler handler = new CommandHandler(this);
		String cmd1="iw";
	    handler.registerCmd(cmd1, new CMD());
	    
	    String cmd1_sub1 = "confirm";
	    String full_sub1 = cmd1+" "+cmd1_sub1;
	    _cmdHelper.setCmd(full_sub1, "Confirm waystone", full_sub1 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub1, new SubWaystoneConfirmationCmd(_cmdHelper.getCmdData(full_sub1)));
	    
	    getCommand(cmd1).setExecutor(handler);
	}	
		
	
	public WaystoneManager GetWaystoneManager()
	{
		return _waystoneManagers;
	}
	
	public WaystoneManagerSQL GetWaystoneManagerSQL()
	{
		return _waystoneManagers.GetWaystoneManagerSQL();
	}
	
	public MySQL GetSQL()
	{
		return _SQL;
	}

	public ImusTabCompleter get_tab_cmd1() {
		return _tab_cmd1;
	}
	
}
