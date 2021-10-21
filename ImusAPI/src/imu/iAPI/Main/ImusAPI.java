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
	}
	
	

	
}
