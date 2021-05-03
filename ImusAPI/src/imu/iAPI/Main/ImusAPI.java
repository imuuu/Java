package imu.iAPI.Main;

import org.bukkit.plugin.java.JavaPlugin;

import imu.iAPI.Other.Metods;


public class ImusAPI extends JavaPlugin
{
	public Metods _metods;
	@Override
	public void onEnable() 
	{
		_metods = new Metods(this);
	}

	
}
