package imu.AccountBoundItems.main;


import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import imu.AccountBoundItems.Commands.BoundCommand;
import imu.AccountBoundItems.Events.DropAndPickup;
import imu.AccountBoundItems.Events.OnDamage;
import imu.AccountBoundItems.Events.OnPlayerInteract;
import imu.AccountBoundItems.Handlers.CommandHandler;
import imu.AccountBoundItems.Other.ConfigMaker;
import imu.AccountBoundItems.SubCommands.subBindOnUseWaitCmd;
import imu.AccountBoundItems.SubCommands.subBindWaitCmd;
import imu.AccountBoundItems.SubCommands.subBoundCmd;
import imu.AccountBoundItems.SubCommands.subBrokenCmd;
import imu.AccountBoundItems.SubCommands.subRepairAllCmd;
import imu.AccountBoundItems.SubCommands.subRepairCmd;
import imu.AccountBoundItems.SubCommands.subUnBoundCmd;


public class Main extends JavaPlugin
{
	public HashMap<String, String> loreNames = new HashMap<String, String>();
	public HashMap<String, String> keyNames = new HashMap<String, String>();
	
	public HashMap<Enchantment, Double> enchPrices = new HashMap<Enchantment, Double>();
	public HashMap<String, Double> lorePrices = new HashMap<String, Double>();
	public HashMap<Material, Double> materialPrices = new HashMap<Material, Double>();
	
	static Main instance;
	
    void registerCommands() 
    {
 
        CommandHandler handler = new CommandHandler();
        String cmd1 = "abi";
        handler.registerCmd(cmd1, new BoundCommand());       
        handler.setPermissionOnLastCmd("abi");
        handler.registerSubCmd(cmd1, "bind", new subBoundCmd());
        handler.registerSubCmd(cmd1, "bind wait", new subBindWaitCmd());
        handler.registerSubCmd(cmd1, "bind use", new subBindOnUseWaitCmd());
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
	
    void registerkeyNames()
    {
    	keyNames.put("name","abi.Name");
    	keyNames.put("uuid","abi.Uuid");
    	keyNames.put("price","abi.Price");
    	keyNames.put("overrideprice","abi.Overrideprice");
    	keyNames.put("broken","abi.Broken");
    	keyNames.put("bound","abi.Bound");
    	keyNames.put("wait","abi.WaitBind");
    	keyNames.put("onuse","abi.OnUseBind");
    }
    
    void registerLoreNames()
    {
    	loreNames.put("bound", ChatColor.GRAY + "BOUND: ");
    	//loreNames.put("broken",ChatColor.RED + "" + ChatColor.BOLD + "BROKEN");
    	loreNames.put("broken","BROKEN ");
    }
    
	@Override
	public void onEnable() 
	{
		ConfigsSetup();
		instance = this;
		registerLoreNames();
		registerkeyNames();
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
	
	public void ConfigsSetup()
	{
		
		makeEnchantConfig();
		makeLoreConfig();
		makeMaterialConfig();
	}
	
	void makeMaterialConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, "material_prices.yml");
		FileConfiguration config = cm.getConfig();
		if(!cm.isExists())
		{
			
			for(Material m : Material.values())
			{
				config.set(m.name(), 0);
			}
			cm.saveConfig();
		}
		else
		{
			for (String key : config.getConfigurationSection("").getKeys(false)) 
			{
				Material material = Material.getMaterial(key);
				double value = config.getDouble(key);
				if(value != 0)
				{
					materialPrices.put(material,value);
				}
				
			}
			
		}
	}
	
	void makeLoreConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, "lore_prices.yml");
		FileConfiguration config = cm.getConfig();
		if(!cm.isExists())
		{
			config.set("lore",0);
			cm.saveConfig();
		}
		else
		{
			for (String key : config.getConfigurationSection("").getKeys(false)) 
			{
				String lore = key;
				double value = config.getDouble(key);
				lorePrices.put(lore,value);
			}
			
		}
	}
	
	void makeEnchantConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, "enchant_prices.yml");
		FileConfiguration config = cm.getConfig();
		if(!cm.isExists())
		{
			for(Enchantment ench : Enchantment.values())
			{

				config.set(ench.getKey().toString().split(":")[1], 0);
				
			}
			cm.saveConfig();
		}
		else
		{
			for (String key : config.getConfigurationSection("").getKeys(false)) 
			{
				Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(key));
				double value = config.getDouble(key);
				if(value != 0)
				{
					enchPrices.put(ench,value);
				}
				
			}
			System.out.println("Ench size:" + enchPrices.size());
			
		}
	}

}
