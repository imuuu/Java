package imu.GS.Main;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.CMDs.Cmd;
import imu.GS.CMDs.Cmd2;
import imu.GS.ENUMs.Cmd_add_options;
import imu.GS.ENUMs.TagSubCmds;
import imu.GS.Managers.ShopManager;
import imu.GS.Managers.ShopManagerSQL;
import imu.GS.Managers.TagManager;
import imu.GS.Other.CmdHelper;
import imu.GS.Other.DenizenScriptCreator;
import imu.GS.SubCmds.SubAddStockableCMD;
import imu.GS.SubCmds.SubAssingToNpcCMD;
import imu.GS.SubCmds.SubCreateUniqueCMD;
import imu.GS.SubCmds.SubGetPlayerPriceCMD;
import imu.GS.SubCmds.SubModifyShopCMD;
import imu.GS.SubCmds.SubModifyUniqueCMD;
import imu.GS.SubCmds.SubSetMaterialPriceCMD;
import imu.GS.SubCmds.SubShopCreateCMD;
import imu.GS.SubCmds.SubShopOpenCMD;
import imu.GS.SubCmds.SubTagMaterialCMD;
import imu.iAPI.Handelers.CommandHandler;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.ImusTabCompleter;
import imu.iAPI.Other.MySQL;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin
{
	private ShopManager _shopManager;
	private TagManager _tagManager;
	private DenizenScriptCreator _denizenScriptCreator;
	

	private CmdHelper _cmdHelper;
	Economy _econ = null;
	private MySQL _SQL;
	private ImusTabCompleter _tab_cmd1;
	
	private HashMap<UUID, CustomInvLayout> _opendInvs = new HashMap<>();
	
	@Override
	public void onEnable() 
	{
		ConnectDataBase();
		
		//setupImusApi();
		setupEconomy();
		_cmdHelper = new CmdHelper(this);
		
		// MANAGERS
		_shopManager = new ShopManager(this);
		_shopManager.Init();
		_denizenScriptCreator = new DenizenScriptCreator();
		
		_tagManager = new TagManager(this);
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
	   
	     	     
	    String cmd1_sub1 = "create shop";
	    String full_sub1 = cmd1+" "+cmd1_sub1;
	    _cmdHelper.setCmd(full_sub1, "Create Shop", full_sub1 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub1, new SubShopCreateCMD(this, _cmdHelper.getCmdData(full_sub1)));
	    handler.setPermissionOnLastCmd("gs.create.shop");
	    
	    String cmd1_sub2 = "open shop";
	    String full_sub2 = cmd1+" "+cmd1_sub2;
	    _cmdHelper.setCmd(full_sub2, "Open the Shop", full_sub2 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub2, new SubShopOpenCMD(this, _cmdHelper.getCmdData(full_sub2)));
	    handler.setPermissionOnLastCmd("gs.open.shop");
	    
//	    String cmd1_sub3 ="delete shop";
//	    String full_sub3 =cmd1+" "+cmd1_sub3;
//	    _cmdHelper.setCmd(full_sub3, "Delete the Shop", full_sub3 + " [ShopName]");
//	    handler.registerSubCmd(cmd1, cmd1_sub3, new SubShopDeleteCMD(this, _cmdHelper.getCmdData(full_sub2)));
	    
	    String cmd1_sub4="add";
	    String full_sub4=cmd1+" "+cmd1_sub4;
	    _cmdHelper.setCmd(full_sub4, "Add Stockable to shop", cmd1_sub4 + " [ShopName]");
	    handler.registerSubCmd(cmd1, cmd1_sub4, new SubAddStockableCMD(this, _cmdHelper.getCmdData(full_sub4)));
	    handler.setPermissionOnLastCmd("gs.add");
	    
	    String cmd1_sub5="create unique";
	    String full_sub5=cmd1+" "+cmd1_sub5;
	    _cmdHelper.setCmd(full_sub5, "Create Unique Item", cmd1_sub5 + " {price}");
	    handler.registerSubCmd(cmd1, cmd1_sub5, new SubCreateUniqueCMD(this, _cmdHelper.getCmdData(full_sub5)));
	    handler.setPermissionOnLastCmd("gs.create.uniques");
	    
	    String cmd1_sub6="modify uniques";
	    String full_sub6=cmd1+" "+cmd1_sub6;
	    _cmdHelper.setCmd(full_sub6, "Modify Uniques", cmd1_sub6);
	    handler.registerSubCmd(cmd1, cmd1_sub6, new SubModifyUniqueCMD(this, _cmdHelper.getCmdData(full_sub6)));
	    handler.setPermissionOnLastCmd("gs.modify.uniques");
	    
	    String cmd1_sub7="modify shop";
	    String full_sub7=cmd1+" "+cmd1_sub7;
	    _cmdHelper.setCmd(full_sub7, "Modify Shop", cmd1_sub7);
	    handler.registerSubCmd(cmd1, cmd1_sub7, new SubModifyShopCMD(this, _cmdHelper.getCmdData(full_sub7)));
	    handler.setPermissionOnLastCmd("gs.modify.shop");
	    
	    String cmd1_sub8="assign";
	    String full_sub8=cmd1+" "+cmd1_sub8;
	    _cmdHelper.setCmd(full_sub8, "assign shop to npc", cmd1_sub8);
	    handler.registerSubCmd(cmd1, cmd1_sub8, new SubAssingToNpcCMD(this, _cmdHelper.getCmdData(full_sub8)));
	    handler.setPermissionOnLastCmd("gs.assing");
	    
	    String cmd1_sub9="setprice";
	    String full_sub9=cmd1+" "+cmd1_sub9;
	    _cmdHelper.setCmd(full_sub9, "Setting material price", cmd1_sub9);
	    handler.registerSubCmd(cmd1, cmd1_sub9, new SubSetMaterialPriceCMD(this, _cmdHelper.getCmdData(full_sub9)));
	    handler.setPermissionOnLastCmd("gs.setprice");
	    
	    String cmd1_sub10="tag";
	    String full_sub10=cmd1+" "+cmd1_sub10;
	    _cmdHelper.setCmd(full_sub10, "Set tag for materials", cmd1_sub10);
	    handler.registerSubCmd(cmd1, cmd1_sub10, new SubTagMaterialCMD(this, _cmdHelper.getCmdData(full_sub10)));
	    handler.setPermissionOnLastCmd("gs.tag");
	    
	    
	    
	    String[] one_hotbar_inv = new String[] {Cmd_add_options.inventory.toString(),Cmd_add_options.hotbar.toString(),Cmd_add_options.hand.toString()};
	    
	    cmd1AndArguments.put(cmd1, new String[] {"create","open","tag", "modify","assign","setprice","add"});
	    cmd1AndArguments.put("open", new String[] {"shop"});
	    //cmd1AndArguments.put("delete", new String[] {"shop"});
	    cmd1AndArguments.put("create", new String[] {"shop","unique"});
	    cmd1AndArguments.put("tag", new String[] {"materials","shopitems"}); //new String[] {"add","remove","remove_all_tags","set_price","increase_price"}
	    cmd1AndArguments.put("add", one_hotbar_inv);
	    
	    cmd1AndArguments.put("setprice", one_hotbar_inv);
	    cmd1AndArguments.put("modify", new String[] {"shop", "uniques"});
	
	    
	    getCommand(cmd1).setExecutor(handler);
	    _tab_cmd1 = new ImusTabCompleter(cmd1, cmd1AndArguments, "gs.tabcompleter");
	    getCommand(cmd1).setTabCompleter(_tab_cmd1);
	    _shopManager.UpdateTabCompliters();
	    //_shopManager.CreateShop("test");
	    
	    
	    //Stream.of(TagSubCmds.values()).map(TagSubCmds::name).toArray((String[]::new))
	    _tab_cmd1.SetRule("/gs tag materials", 3, Arrays.asList(Stream.of(TagSubCmds.values()).map(TagSubCmds::name).toArray((String[]::new))));
	    _tab_cmd1.SetRule("/gs tag materials add", 4, Arrays.asList(one_hotbar_inv));
	    _tab_cmd1.SetRule("/gs tag materials remove", 4, Arrays.asList(one_hotbar_inv));
	    _tab_cmd1.SetRule("/gs tag materials remove_all_tags", 4, Arrays.asList(one_hotbar_inv));
	    
	    _tab_cmd1.SetRule("/gs tag shopitems", 3, Arrays.asList(new String[] {TagSubCmds.set_price.toString(),TagSubCmds.increase_price.toString()}));
	    //
	    
	    
	    HashMap<String, String[]> cmd2AndArguments = new HashMap<>();
	    String cmd2="g";
	    handler.registerCmd(cmd2, new Cmd2(this));
	    
	    String cmd2_sub1 = "getprice";
	    String full2_sub1 = cmd2+" "+cmd2_sub1;
	    _cmdHelper.setCmd(full2_sub1, "Get price of item", full2_sub1);
	    handler.registerSubCmd(cmd2, cmd2_sub1, new SubGetPlayerPriceCMD(this, _cmdHelper.getCmdData(full2_sub1)));
	    //handler.setPermissionOnLastCmd("g.");
	    
	    cmd2AndArguments.put(cmd2, new String[] {"getprice"});
	    cmd2AndArguments.put("getprice", one_hotbar_inv);
	    getCommand(cmd2).setExecutor(handler);
	    ImusTabCompleter tab2 = new ImusTabCompleter(cmd2, cmd2AndArguments, null);
	    getCommand(cmd2).setTabCompleter(tab2);
	}	
		
	public void UpdateShopNames(String[] shopNames)
	{
		_tab_cmd1.SetRule("/"+"gs assign", 3, Arrays.asList(shopNames));
		_tab_cmd1.SetRule("/"+"gs setprice", 4, Arrays.asList(shopNames));
		_tab_cmd1.SetRule("/gs add", 3, Arrays.asList(shopNames));
		_tab_cmd1.setArgumenrs("shop", shopNames);
	}
	
	public MySQL GetSQL()
	{
		return _SQL;
	}
	
	
	
	public DenizenScriptCreator GetDenizenSCreator()
	{
		return _denizenScriptCreator;
	}
	
	public ImusTabCompleter get_tab_cmd1() {
		return _tab_cmd1;
	}
	
	public Economy get_econ() {
			return _econ;
		}

//	boolean setupImusApi()
//	{
//		if(Bukkit.getPluginManager().getPlugin("imusAPI") != null)
//		{
//			_imusAPI = (ImusAPI) Bukkit.getPluginManager().getPlugin("imusAPI");
//			return true;
//		}
//		return false;
//	}
	

	public TagManager GetTagManager()
	{
		return _tagManager;
	}
	public ShopManager get_shopManager() {
		return _shopManager;
	}
	
	public ShopManagerSQL GetShopManagerSQL()
	{
		return _shopManager.GetShopManagerSQL();
	}
	
	void setupEconomy() 
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				 if (getServer().getPluginManager().getPlugin("Vault") == null) 
			        {
			        	System.out.println("Vault not found");
			            return;
			        }
			        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			        //System.out.println("rsp: "+rsp);
			        if (rsp == null) {
			            return;
			        }
			        _econ = rsp.getProvider();
			        return;
			}
		}.runTaskLaterAsynchronously(this, 20);
       
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
