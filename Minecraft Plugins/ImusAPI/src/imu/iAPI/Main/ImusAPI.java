package imu.iAPI.Main;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.DateParser;
import imu.iAPI.Other.Metods;
import imu.iAPI.Other.MySQLHelper;
import imu.iAPI.Other.ProtocolLibUtil;


public class ImusAPI extends JavaPlugin
{
	public static ImusAPI _instance;
	public static Metods _metods;
	public static MySQLHelper _sqlHelper;
	private HashMap<UUID, CustomInvLayout> _openedInvs = new HashMap<>();
	private ProtocolManager _protocolManager;
	private ProtocolLibUtil _protocolLibUtil;
	
	@Override
	public void onEnable() 
	{
		_instance = this;
		_metods = new Metods(this);
		_sqlHelper = new MySQLHelper();

		_protocolManager = ProtocolLibrary.getProtocolManager();
		//setup();
		_protocolLibUtil = new ProtocolLibUtil();
		
		
		
		
		
	}
	
	@Override
	public void onDisable() 
	{
		for(CustomInvLayout inv : _openedInvs.values())
		{
			inv.GetPlayer().closeInventory();
		}
		_openedInvs.clear();
	}
	
	public void RegisterInvOpen(CustomInvLayout inv)
	{
		_openedInvs.put(inv.GetPlayer().getUniqueId(),inv);
	}
	
	public void UnregisterInv(CustomInvLayout inv)
	{
		_openedInvs.remove(inv.GetPlayer().getUniqueId());
	}
	
	public CustomInvLayout GetOpenedInv(Player player)
	{
		return _openedInvs.get(player.getUniqueId());
	}
	
	public ProtocolManager GetProtocolManager()
	{
		return _protocolManager;
	}
	
	public ProtocolLibUtil GetProtocolLibUtil()
	{
		return _protocolLibUtil;
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
