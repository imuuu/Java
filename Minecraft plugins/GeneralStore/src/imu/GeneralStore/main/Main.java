package imu.GeneralStore.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import imu.GeneralStore.Commands.GeneralStoreCmd;
import imu.GeneralStore.Events.InventoriesClass;
import imu.GeneralStore.Handlers.CommandHandler;
import imu.GeneralStore.Other.ConfigMaker;
import imu.GeneralStore.Other.ItemMetods;
import imu.GeneralStore.Other.ShopManager;
import imu.GeneralStore.SubCommands.subStoreAddCmd;
import imu.GeneralStore.SubCommands.subStoreCmd;
import imu.GeneralStore.SubCommands.subStoreCostCmd;
import imu.GeneralStore.SubCommands.subStoreCreateCmd;
import imu.GeneralStore.SubCommands.subStoreReloadCmd;
import imu.GeneralStore.SubCommands.subStoreRemoveCmd;
import imu.GeneralStore.SubCommands.subStoreRemoveINFCmd;
import imu.GeneralStore.SubCommands.subStoreSetPriceCmd;
import imu.GeneralStore.SubCommands.subStoreSetUniquePriceCmd;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin
{

	public HashMap<Material, Double[]> materialPrices = new HashMap<>(); //min, max, sell prosent each item
	public HashMap<Enchantment, Double[]> enchPrices = new HashMap<>(); // {minlvl,maxlvl,minPrice,maxPrice
	static Main instance;
	static Economy econ = null;
	
	ShopManager shopManager;
	
	public HashMap<String, ArrayList<Material>> materialCategorys=new HashMap<>();
	public HashMap<String, Double[]> materialCatPrices=new HashMap<>();
	
	int clickPerSecond=10;
	
	int expireTime = 60; 
	double expireProsent = 10; 
	int runnableDelay = 1;
	
	Double[] default_prices= {0.1, 1.0, 2.0};
	
	double sellProsent = 1.5;
	double durabilityCostMultiplier = 0.8;
	
    boolean enableSmartPrices = false; //TODO add config 
    boolean loadSmartPricesUpFront=false; //TODO add config 
	ItemMetods itemM = null;
	
	


	String materialPriceYML ="material_prices.yml";
	public void registerCommands() 
    {  	
        CommandHandler handler = new CommandHandler();

        String cmd1="gs";
        handler.registerCmd(cmd1, new GeneralStoreCmd());  
        handler.setPermissionOnLastCmd("gs");
        handler.registerSubCmd(cmd1, "shop", new subStoreCmd());
        handler.registerSubCmd(cmd1, "create", new subStoreCreateCmd());
        handler.registerSubCmd(cmd1, "remove", new subStoreRemoveCmd());
        handler.registerSubCmd(cmd1, "price", new subStoreSetPriceCmd());
        handler.registerSubCmd(cmd1, "cost", new subStoreCostCmd());
        handler.registerSubCmd(cmd1, "add", new subStoreAddCmd());
        handler.registerSubCmd(cmd1, "remove inf", new subStoreRemoveINFCmd());
        handler.registerSubCmd(cmd1, "reload", new subStoreReloadCmd(this));
        handler.registerSubCmd(cmd1, "unique", new subStoreSetUniquePriceCmd(this));
        
        
        getCommand(cmd1).setExecutor(handler);
        
    }
	
	@Override
	public void onEnable() 
	{		
		instance = this;
		itemM = new ItemMetods();
		setupEconomy();
	
		ConfigsSetup();
		
		registerCommands();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN +" General Store has been activated!");
		getServer().getPluginManager().registerEvents(new InventoriesClass(), this);
		
		shopManager  = new ShopManager(this);
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
	
	public ShopManager getShopManager()
	{
		return shopManager;
	}
	
	public int getClickPerSecond() {
		return clickPerSecond;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public double getExpireProsent() {
		return expireProsent;
	}

	public int getRunnableDelay() {
		return runnableDelay;
	}

	public Double[] getDefault_prices() {
		return default_prices;
	}


	public double getSellProsent() {
		return sellProsent;
	}

	public double getDurabilityCostMultiplier() {
		return durabilityCostMultiplier;
	}

	public boolean isEnableSmartPrices() {
		return enableSmartPrices;
	}

	public boolean isLoadSmartPricesUpFront() {
		return loadSmartPricesUpFront;
	}

	public String getMaterialPriceYML() {
		return materialPriceYML;
	}
	
	public ItemMetods getItemM() {
		return itemM;
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
			default_prices[0] = cm.addDefault("DefaultMinPrice", default_prices[0],"Minprice: item will not sold lower than this each epoch");
			default_prices[1] = cm.addDefault("DefaultMaxPrice", default_prices[1],"Maxprice: first item price if shop is not contain that item, buing from shop this is multiplied with sellprosent");
			default_prices[2] = cm.addDefault("DefaultPriceProsent", default_prices[2],"PriceProsent: how many prosents (from maxprice) it goes down in each item in shop");
			expireProsent = cm.addDefault("ExpireProsent", expireProsent,"ExpireProsent: How many prosent item amount in shop its go down every time expire ticks");
			expireTime = cm.addDefault("ExpireTime", expireTime,"ExpireTime: How often shop will remove items its shop");
			clickPerSecond = cm.addDefault("ClicksPerSecond", clickPerSecond,"ClicksPerSecond: How many clicks is allowed in shop menu. Prevents duplications, good is under 10-12 clicks");
			sellProsent = cm.addDefault("SellProsent", sellProsent,"SellProsent: How much item will be sold for player. price comes from maxprice * sellprosent");
			durabilityCostMultiplier = cm.addDefault("DurabilityCostMultiplier", durabilityCostMultiplier,"DurabilityCostMultiplier:(0.0-1.0) How much durability effects item price. If equals 1 it means same price but lowered, If 0 durability isnt effected at all");
			cm.addComments();
			
		} catch (Exception e) 
		{
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Something got wrong GeneralStore fileNamed: "+cm.getFileName());
			getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Maybe you casted some value as Integer When it should be Double?");
		}
		
	}
	
	void makeMaterialConfig()
	{
		ConfigMaker cm = new ConfigMaker(this, materialPriceYML);
		FileConfiguration config = cm.getConfig();
		String minPrice=".minPrice";
		String maxPrice=".maxPrice";
		String prosent=".proEachSell";
		if(!cm.isExists())
		{
			config.options().header("minPrice = price will not drop below this each sells\n"
					+ "maxPrice = price where calculation starts  => maxPrice * (1.0-proEachSell)^epoch\n"
					+ "proEachSell = how much price go down from max price each selled item");
		

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
				
			materialCatPrices.clear();
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
			enchPrices.clear();
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
