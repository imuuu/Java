package imu.GeneralStore.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import imu.GeneralStore.Commands.GeneralStoreCmd;
import imu.GeneralStore.Events.InventoriesClass;
import imu.GeneralStore.Handlers.CommandHandler;
import imu.GeneralStore.Other.ConfigMaker;
import imu.GeneralStore.Other.ItemMetods;
import imu.GeneralStore.Other.ShopManager;
import imu.GeneralStore.SubCommands.subStoreCmd;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin
{

	public HashMap<Player, ItemStack[]> playerInvContent = new HashMap<>();
	public HashMap<Material, Double[]> materialPrices = new HashMap<>(); //min, max, sell prosent each item
	public HashMap<Enchantment, Double[]> enchPrices = new HashMap<>(); // {minlvl,maxlvl,minPrice,maxPrice
	static Main instance;
	static Economy econ = null;
	
	public ShopManager shopManager;
	
	public HashMap<String, ArrayList<Material>> materialCategorys=new HashMap<>();
	public HashMap<String, Double[]> materialCatPrices=new HashMap<>();
	
	public int clickPerSecond=10;
	
	public int expireTime = 10; // 1d
	public double expireProsent = 10; // 1d
	public int runnableDelay = 1;
	
	public Double[] default_prices= {0.1, 1.0, 2.0};
	
	public double sellProsent=1.5;
	
    
	ItemMetods itemM = new ItemMetods();
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
		setupEconomy();
		
		//shopManager.addShop(ChatColor.DARK_RED + "General Store");
		
		ConfigsSetup();
		
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" General Store has been activated!");
		getServer().getPluginManager().registerEvents(new InventoriesClass(), this);
		
		shopManager  = new ShopManager();
		shopManager.makeShopsConfig();
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
		makeEnchantExponentConfig();
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
		shopManager.closeShopsInvs();
		shopManager.saveShopsContent();
		shopManager.checkIfAbleToSaveData();
	}
	
	void makeSettingsConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, "settings.yml");

		try 
		{
			default_prices[0] = cm.addDefault("DefaultMinPrice", default_prices[0]);
			default_prices[1] = cm.addDefault("DefaultMaxPrice", default_prices[1]);
			default_prices[2] = cm.addDefault("DefaultPriceProsent", default_prices[2]);
			expireProsent = cm.addDefault("ExpireProsent", expireProsent);
			expireTime = cm.addDefault("ExpireTime", expireTime);
			clickPerSecond = cm.addDefault("ClicksPerSecond", clickPerSecond);
			sellProsent = cm.addDefault("SellProsent", sellProsent);
			
		} catch (Exception e) 
		{
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Something got wrong GeneralStore fileNamed: "+cm.getFileName());
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Maybe you casted some value as Integer When it should be Double?");
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
		
			//for(Material m : Material.values())
			//{
			//	config.set(m.name()+minPrice, 0);
			//	config.set(m.name()+maxPrice, 0);
			//	config.set(m.name()+prosent, 0);
			//}
			HashMap<String, ArrayList<Material>> gats=new HashMap<>();
			for(Material m : Material.values())
			{
				String name = itemM.getMaterialCategory(m);
				if(gats.containsKey(name))
				{
					gats.get(name).add(m);
				}else
				{
					ArrayList<Material> arr = new ArrayList<>();
					arr.add(m);
					gats.put(name,arr);
				}
			}
			
			for(Entry<String, ArrayList<Material>> entry :  gats.entrySet())
			{	 
				String key = entry.getKey();
				config.set(key+minPrice, 0);
				config.set(key+maxPrice, 0);
				config.set(key+prosent, 0);
				for(Material mat : entry.getValue())
				{					
					config.set(entry.getKey()+".belong."+mat.name()+minPrice, 0);
					config.set(entry.getKey()+".belong."+mat.name()+maxPrice, 0);
					config.set(entry.getKey()+".belong."+mat.name()+prosent, 0);
				}
			}
			cm.saveConfig();

		}
		else
		{
			materialPrices.clear();
			
			
			for (String key : config.getConfigurationSection("").getKeys(false)) 
			{
						
				String materialString = key+".belong";
				double mi = config.getDouble(key + minPrice);
				double ma = config.getDouble(key + maxPrice);
				double pr = config.getDouble(key + prosent);
				
				if(mi != 0 || ma != 0 || pr != 0)
				{
					Double[] values = {mi,ma,pr};
					materialCatPrices.put(key,values);
				}
				
				for (String key2 : config.getConfigurationSection(materialString).getKeys(false)) 
				{
					Material material = Material.getMaterial(key2);
					mi = config.getDouble(materialString+"."+key2 + minPrice);
					ma = config.getDouble(materialString+"."+key2 + maxPrice);
					pr = config.getDouble(materialString+"."+key2 + prosent);
					
					if(mi != 0 || ma != 0 || pr != 0)
					{
						Double[] values = {mi,ma,pr};
						materialPrices.put(material,values);
					}
				}
				
			}
			
			System.out.println("size: "+materialPrices.size());
			itemM.printHashMap(materialPrices);
			itemM.printHashMap(materialCatPrices);
			
		}
	}
	
	void makeEnchantExponentConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, "enchant_prices.yml");
		FileConfiguration config = cm.getConfig();
		if(!cm.isExists())
		{
			config.options().header("MinLevel doesnt effect anything yet.. always calculate 1-maxLevel");
			for(Enchantment ench : Enchantment.values())
			{

				config.set(ench.getKey().toString().split(":")[1]+".minLevel", ench.getStartLevel());
				config.set(ench.getKey().toString().split(":")[1]+".maxLevel", ench.getMaxLevel());
				config.set(ench.getKey().toString().split(":")[1]+".minPrice", 0);
				config.set(ench.getKey().toString().split(":")[1]+".maxPrice",0);
				
			}
			cm.saveConfig();
		}
		else
		{
			for (String key : config.getConfigurationSection("").getKeys(false)) 
			{
				Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(key));
				double minlvl = config.getDouble(key+".minLevel");
				double maxlvl = config.getDouble(key+".maxLevel");
				double minPrice = config.getDouble(key+".minPrice");
				double maxPrice = config.getDouble(key+".maxPrice");
				
				if(minPrice != 0 || maxPrice != 0)
				{
					Double[] array= {minlvl,maxlvl,minPrice,maxPrice};
					
					enchPrices.put(ench, array);
				}
				
			}
			
			
		}
	}
	
}
