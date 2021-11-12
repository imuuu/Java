package imu.GS.Main;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import imu.GS.CMDs.Cmd;
import imu.GS.Managers.ShopManager;
import imu.GS.Other.CmdHelper;
import imu.GS.SubCmds.SubAddStockableCMD;
import imu.GS.SubCmds.SubCreateUniqueCMD;
import imu.GS.SubCmds.SubModifyShopCMD;
import imu.GS.SubCmds.SubModifyUniqueCMD;
import imu.GS.SubCmds.SubShopCreateCMD;
import imu.GS.SubCmds.SubShopDeleteCMD;
import imu.GS.SubCmds.SubShopOpenCMD;
import imu.iAPI.Handelers.CommandHandler;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.ImusTabCompleter;
import imu.iAPI.Other.Metods;
import imu.iAPI.Other.MySQL;
import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin
{
	ShopManager _shopManager;
	

	CmdHelper _cmdHelper;
	Economy _econ = null;
	ImusAPI _imusAPI;
	MySQL _SQL;
	ImusTabCompleter _tab_cmd1;
	
	HashMap<UUID, CustomInvLayout> _opendInvs = new HashMap<>();
	
	@Override
	public void onEnable() 
	{
		ConnectDataBase();
		
		setupImusApi();
		setupEconomy();
		_cmdHelper = new CmdHelper(this);
		
		// MANAGERS
		_shopManager = new ShopManager(this);
		_shopManager.Init();
		//_shopManager.loadShopsAsync();
		 
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" [imusGS] has been activated!");
		
		registerCommands();
		
		
	}
	
	@Override
	 public void onDisable()
	{
		for(CustomInvLayout inv : _opendInvs.values()) { inv.GetPlayer().closeInventory();}
		_opendInvs.clear();
		
		if(_shopManager != null)
			_shopManager.onDisabled();
		
		
		
		if(_SQL != null)
			_SQL.Disconnect();
		
		
	}
	
	void ConnectDataBase()
	{
		_SQL = new MySQL(this, "imusGS");
		try {
			_SQL.Connect();
			Bukkit.getLogger().info(ChatColor.GREEN +"[imusGS] Database Connected!");
		} 
		catch (ClassNotFoundException | SQLException e) {

			Bukkit.getLogger().info(ChatColor.RED +"[imusGS] Database not connected");
		}
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
	    handler.registerSubCmd(cmd1, cmd1_sub1, new SubShopCreateCMD(this, _cmdHelper.getCmdData(full_sub1)));
	    
	    String cmd1_sub2 = "open shop";
	    String full_sub2 = cmd1+" "+cmd1_sub2;
	    _cmdHelper.setCmd(full_sub2, "Open the Shop", full_sub2 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub2, new SubShopOpenCMD(this, _cmdHelper.getCmdData(full_sub2)));
	    
	    String cmd1_sub3 ="delete shop";
	    String full_sub3 =cmd1+" "+cmd1_sub3;
	    _cmdHelper.setCmd(full_sub3, "Delete the Shop", full_sub3 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub3, new SubShopDeleteCMD(this, _cmdHelper.getCmdData(full_sub2)));
	    
	    String cmd1_sub4="add shop";
	    String full_sub4=cmd1+" "+cmd1_sub4;
	    _cmdHelper.setCmd(full_sub4, "Add Stockable to shop", cmd1_sub4 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub4, new SubAddStockableCMD(this, _cmdHelper.getCmdData(full_sub4)));
	    
	    String cmd1_sub5="create unique";
	    String full_sub5=cmd1+" "+cmd1_sub5;
	    _cmdHelper.setCmd(full_sub5, "Create Unique Item", cmd1_sub5 + " {price}");
	    handler.registerSubCmd(cmd1, cmd1_sub5, new SubCreateUniqueCMD(this, _cmdHelper.getCmdData(full_sub5)));
	    
	    String cmd1_sub6="modify uniques";
	    String full_sub6=cmd1+" "+cmd1_sub6;
	    _cmdHelper.setCmd(full_sub6, "Modify Uniques", cmd1_sub6);
	    handler.registerSubCmd(cmd1, cmd1_sub6, new SubModifyUniqueCMD(this, _cmdHelper.getCmdData(full_sub6)));
	    
	    String cmd1_sub7="modify shop";
	    String full_sub7=cmd1+" "+cmd1_sub7;
	    _cmdHelper.setCmd(full_sub7, "Modify Shop", cmd1_sub7);
	    handler.registerSubCmd(cmd1, cmd1_sub7, new SubModifyShopCMD(this, _cmdHelper.getCmdData(full_sub7)));
	    
	     
	    
	    cmd1AndArguments.put(cmd1, new String[] {"create","open", "delete","add", "modify"});
	    cmd1AndArguments.put("open", new String[] {"shop"});
	    cmd1AndArguments.put("delete", new String[] {"shop"});
	    cmd1AndArguments.put("create", new String[] {"shop","unique"});
	    cmd1AndArguments.put("add", new String[] {"shop"});
	    cmd1AndArguments.put("modify", new String[] {"shop", "uniques"});
	
	    
	    getCommand(cmd1).setExecutor(handler);
	    _tab_cmd1 = new ImusTabCompleter(cmd1, cmd1AndArguments);
	    getCommand(cmd1).setTabCompleter(_tab_cmd1);
	    _shopManager.UpdateTabCompliters();
	    //_shopManager.CreateShop("test");
	    

	}	
		
	public MySQL GetSQL()
	{
		return _SQL;
	}
	
	public ImusAPI GetIAPI()
	{
		return _imusAPI;
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
	
	public void RegisterInv(CustomInvLayout inv)
	{
		_opendInvs.put(inv.GetPlayer().getUniqueId(), inv);
	}
	
	public void UnregisterInv(CustomInvLayout inv)
	{
		_opendInvs.remove(inv.GetPlayer().getUniqueId());
	}
}
