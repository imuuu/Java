package imu.GeneralStore.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import imu.GeneralStore.Commands.GeneralStoreCmd;
import imu.GeneralStore.Events.InventoriesClass;
import imu.GeneralStore.Handlers.CommandHandler;
import imu.GeneralStore.Other.ConfigMaker;
import imu.GeneralStore.Other.Shop;
import imu.GeneralStore.SubCommands.subStoreCmd;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin
{

	public HashMap<Player, ItemStack[]> playerInvContent = new HashMap<>();
	public HashMap<Material, Double[]> materialPrices = new HashMap<>(); //min, max, sell prosent each item
		
	static Main instance;
	static Economy econ = null;
	
	ArrayList<Shop> shops = new ArrayList<>();
	public Shop shop1;
	
	public int default_expireTime = 60*60*24; // 1d
	public Double[] default_prices= {0.1,1.0,2.0};
    
	public void registerCommands() 
    {
    	
        CommandHandler handler = new CommandHandler();

        String cmd1="gs";
        handler.registerCmd(cmd1, new GeneralStoreCmd());       
        handler.registerSubCmd(cmd1, "shop", new subStoreCmd());
        
        
        getCommand(cmd1).setExecutor(handler);
        
    }
	
	@Override
	public void onEnable() 
	{
		instance = this;
		shop1 = new Shop(ChatColor.DARK_RED + "General Store");
		shops.add(shop1);
		setupEconomy();
		ConfigsSetup();
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" General Store has been activated!");
		getServer().getPluginManager().registerEvents(new InventoriesClass(), this);
	}
	
	@Override
	 public void onDisable()
	{
		saveShopsContent();
	}
	
	public static Main getInstance()
	{
		return instance;
	}
	
	public static Economy getEconomy() 
	{
        return econ;
    }
	
	public void ConfigsSetup()
	{
		makeSettingsConfig();
		makeMaterialConfig();
	}
	
	boolean setupEconomy() 
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	
	void saveShopsContent()
	{
		for(Shop shop : shops)
		{
			shop.configSaveContent();
			shop.closeShopInvs();
		}
	}
	
	void makeSettingsConfig()
	{
		String dmi = "defaultMinPrice";
		String dma="defaultMaxPrice";
		String dp="defaultProsent";
		String de="defaultExpireTimeInSeconds";
		ConfigMaker cm = new ConfigMaker(this, "settings.yml");
		FileConfiguration config = cm.getConfig();
		
		if(!cm.isExists())
		{
			config.set(de, default_expireTime);
			config.set(dmi, default_prices[0]);
			config.set(dma,default_prices[1]);
			config.set(dp,default_prices[2]);
			cm.saveConfig();
		}
		else
		{
			if(!config.contains(de))
			{
				config.set(de, default_expireTime);
			}
			
			if(!config.contains(dmi))
			{
				config.set(dmi,default_prices[0]);
			}
			
			if(!config.contains(dma))
			{
				config.set(dma,default_prices[1]);
			}
			
			if(!config.contains(dp))
			{
				config.set(dp, default_prices[2]);
			}
			
			
			cm.saveConfig();
			
			default_expireTime = config.getInt(de);
			default_prices[0] = config.getDouble(dmi);
			default_prices[1] = config.getDouble(dma);
			default_prices[2] = config.getDouble(dp);
			
		
			
		}
	}
	
	void makeMaterialConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, "material_prices.yml");
		FileConfiguration config = cm.getConfig();
		String minPrice=".minPrice";
		String maxPrice=".maxPrice";
		String prosent=".proEachSell";
		if(!cm.isExists())
		{
			config.options().header("minPrice = price will not drop below this each sells\n"
					+ "maxPrice = price where calculation starts  => maxPrice * (1.0-proEachSell)^epoch\n"
					+ "proEachSell = how much price go down from max price each selled item");
		
			for(Material m : Material.values())
			{
				config.set(m.name()+minPrice, 0);
				config.set(m.name()+maxPrice, 0);
				config.set(m.name()+prosent, 0);
			}
			cm.saveConfig();
		}
		else
		{
			materialPrices.clear();
			for (String key : config.getConfigurationSection("").getKeys(false)) 
			{
				Material material = Material.getMaterial(key);
				double mi = config.getDouble(key+minPrice);
				double ma = config.getDouble(key+maxPrice);
				double pr = config.getDouble(key+prosent);
				if(mi != 0 || ma != 0 || pr != 0)
				{
					Double[] values = {mi,ma,pr};
					materialPrices.put(material,values);
				}
				
			}
			
		}
	}
	
}
