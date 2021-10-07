package imu.GS.Main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import imu.GS.CMDs.Cmd;

import imu.GS.Managers.ShopManager;
import imu.GS.Other.CmdHelper;

import imu.GS.SubCmds.subShopCreateCMD;
import imu.GS.SubCmds.subShopOpenCMD;
import imu.iAPI.Handelers.CommandHandler;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.ImusTabCompleter;
import imu.iAPI.Other.Metods;
import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin
{
	ShopManager _shopManager;
	

	CmdHelper _cmdHelper;
	Economy _econ = null;
	ImusAPI _imusAPI;
	
	ImusTabCompleter _tab_cmd1;
	
	@Override
	public void onEnable() 
	{
		setupImusApi();
		setupEconomy();
		_cmdHelper = new CmdHelper(this);
		
		// MANAGERS
		_shopManager = new ShopManager(this);
		//_shopManager.loadShopsAsync();
		 
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" [imusGS] has been activated!");
		
		registerCommands();
		
		
	}
	
	@Override
	 public void onDisable()
	{
		if(_shopManager != null)
			_shopManager.onDisabled();
	}
	
	public void registerCommands() 
	{
		HashMap<String, String[]> cmd1AndArguments = new HashMap<>();
		CommandHandler handler = new CommandHandler(this);
		String cmd1="gs";
	    handler.registerCmd(cmd1, new Cmd(this));
	     	     
	    String cmd1_sub1 = "create";
	    String full_sub1 = cmd1+" "+cmd1_sub1;
	    _cmdHelper.setCmd(full_sub1, "Create Shop", full_sub1 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub1, new subShopCreateCMD(this, _cmdHelper.getCmdData(full_sub1)));
	    
	    String cmd1_sub2 = "open shop";
	    String full_sub2 = cmd1+" "+cmd1_sub2;
	    _cmdHelper.setCmd(full_sub2, "Open the Shop", full_sub2 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub2, new subShopOpenCMD(this, _cmdHelper.getCmdData(full_sub2)));
	     
	    
	    cmd1AndArguments.put(cmd1, new String[] {"create","open"});
	    cmd1AndArguments.put("open", new String[] {"shop"});
	    cmd1AndArguments.put("create", new String[] {"shop"});
	    
	    getCommand(cmd1).setExecutor(handler);
	    _tab_cmd1 = new ImusTabCompleter(cmd1, cmd1AndArguments);
	    getCommand(cmd1).setTabCompleter(_tab_cmd1);
	    _shopManager.UpdateTabCompliters();

	    

	}	
	
	public ImusTabCompleter get_tab_cmd1() {
		return _tab_cmd1;
	}
	
	public Economy get_econ() {
			return _econ;
		}

	boolean setupImusApi()
	{
		if(Bukkit.getPluginManager().getPlugin("imusAPI") != null)
		{
			_imusAPI = (ImusAPI) Bukkit.getPluginManager().getPlugin("imusAPI");
			return true;
		}
		return false;
	}
	
	public ImusAPI getImusAPI()
	{
		return _imusAPI;
	}

	public Metods GetMetods()
	{
		return ImusAPI._metods;
	}
	
	public ShopManager get_shopManager() {
		return _shopManager;
	}
	
	boolean setupEconomy() 
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
        	System.out.println("Vault not found");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        
        if (rsp == null) {
            return false;
        }
        _econ = rsp.getProvider();
        return _econ != null;
    }
}
