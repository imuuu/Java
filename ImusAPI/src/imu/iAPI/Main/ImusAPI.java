package imu.iAPI.Main;

import org.bukkit.plugin.java.JavaPlugin;

import imu.iAPI.Other.Metods;
import imu.iAPI.Other.MySQLHelper;


public class ImusAPI extends JavaPlugin
{
	public static Metods _metods;
	public static MySQLHelper _sqlHelper;
	
	@Override
	public void onEnable() 
	{
		_metods = new Metods(this);
		_sqlHelper = new MySQLHelper();
		//setup();
	}
	
	
//	boolean setup()
//	{
//		if(Bukkit.getPluginManager().getPlugin("imusAPI") != null)
//		{
//			ChestCleaner cc = (ChestCleaner) Bukkit.getPluginManager().getPlugin("ChestCleaner");
//			System.out.println("LOADING CHESTCLEANER");
//			
//			
//			return true;
//		}
//		return false;
//	}
	
//	boolean setupImusApi()
//	{
//		if(Bukkit.getPluginManager().getPlugin("imusAPI") != null)
//		{
//			_imusAPI = (ImusAPI) Bukkit.getPluginManager().getPlugin("imusAPI");
//			return true;
//		}
//		return false;
//	}
	
}
