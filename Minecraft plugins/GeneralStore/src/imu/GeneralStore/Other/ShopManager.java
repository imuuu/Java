package imu.GeneralStore.Other;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.datafixers.util.Pair;

import imu.GeneralStore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class ShopManager 
{
	Main _main = Main.getInstance();
	public HashMap<Material,Double[]> smart_prices = new HashMap<>();
	
	HashMap<String,Shop> shops = new HashMap<>();
	
	ArrayList<Pair<Date, String> > selledItems = new ArrayList<>();
	
	String shopYAML ="shopNames.yml";
	
	boolean calculationReady = true;
	public ShopManager()
	{
		
		if(!calculationReady)
		{
			System.out.println("Loading SMART PRICES");
			new BukkitRunnable() {
				
				@Override
				public void run() 
				{
					Shop tempShop =new Shop("asddd",false);
							//_main.shopManager.addShop("temp294736232192_Shop");
					
					ConfigMaker cm = new ConfigMaker(_main,"smartcal.yml");
					FileConfiguration config=cm.getConfig();
					for(Material m : Material.values())
					{
						ItemStack stack = new ItemStack(m);
						Double[] test2 = {0.0,0.0,0.0,0.0};
						ArrayList<Double[]> test = new ArrayList<>();
						test.add(test2);
						test = tempShop.materialTreePrices(stack, test);
						Double[] values = tempShop.materialPricesDecoder(stack,test);
						
						if(values[0] != 0 && values[1] != 0 && values[2] != 0)
						{
							smart_prices.put(m, values);
							config.set(m.toString(), values[1]);					
						}
						
					}
					cm.saveConfig();
					System.out.println("CALS READY");
					calculationReady = true;
					//removeShop("temp294736232192_Shop");
				}
			}.runTaskAsynchronously(_main);
		}
		
		
		
	}
	
	public Shop addShop(String name)
	{
		Shop shop = new Shop(name,true);
		shops.put(name, shop);
		
		ConfigMaker cm = new ConfigMaker(_main, shopYAML);
		FileConfiguration config = cm.getConfig();
		config.set(name, true);
		cm.saveConfig();
		
		return shop;
	}
	
	public boolean isExists(String name)
	{
		if(getShop(name) != null)
		{
			return true;
		}
		return false;
	}
	public void removeShop(String name)
	{
		Shop shop = getShop(name);
		if(shop == null)
		{
			System.out.println("Try to remove shop but didnt find with that name: " + name);
			return;
		}
		shops.remove(shop._displayName);
		
		ConfigMaker cm = new ConfigMaker(_main, shopYAML);
		FileConfiguration config = cm.getConfig();
		config.set(name, null);
		cm.saveConfig();
		
		String[] fileNames = {shop._fileNameShopYML,shop._fileNameSelledMaterialCount};
		for(String fName : fileNames)
		{
			ConfigMaker cm2 = new ConfigMaker(_main, fName);
			cm2.removeConfig();
		}
		
	}
	
	public void addSmartPrice(Material material, Double[] prices)
	{
		if(!smart_prices.containsKey(material))
		{
			smart_prices.put(material, prices);
		}
	}
	public Shop getShop(String name)
	{
		Shop shop = shops.get(name);
		
		if(shop == null)
		{
			name = name.replace(" ", "");
			for(Shop s : shops.values())
			{
				if(name.equalsIgnoreCase(s.getName().replace(" ", "")))
				{
					shop = s;
					break;
				}
			}
		}
		
		return shop;
	}

	
	public void openShop(Player player, String shopName)
	{
		if(!calculationReady)
		{
			return;
		}
		
		Shop shop = getShop(shopName);
		if(shop == null)
		{
			player.sendMessage(ChatColor.RED + "Shop wasn't found with that name");
			return;
		}
		
		shop.openShopInv(player);
	}
	
	public void saveShopsContent()
	{
		for(Shop shop : shops.values())
		{
			shop.configSaveContent();		
		}
	}
	public void closeShopsInvs()
	{
		for(Shop shop : shops.values())
		{
			shop.closeShopInvs();	
		}
		
	}
	
	public void makeShopsConfig() 
	{
		ConfigMaker cm = new ConfigMaker(_main, shopYAML);
		FileConfiguration config = cm.getConfig();
		
		if(!cm.isExists())
		{
			config.set(ChatColor.DARK_RED + "General Store", true);
			cm.saveConfig();
		}
		
		for (String key : config.getConfigurationSection("").getKeys(false)) 
		{
			String shopName = key;
			System.out.println("Shop added: "+shopName);
			addShop(shopName);
		}		
	}
	
	public void logAddSold(Pair<Date, String> value)
	{
		selledItems.add(value);
	}
	
	public void checkIfAbleToSaveData()
	{
		if(selledItems.size() <= 0 )
		{
			return;
		}
		
		for(Shop shop : shops.values())
		{
			if(shop.isShopsOpened())
			{
				return;
			}
		}
		
	
		System.out.println("SAVING sell LOG data");
		ConfigMaker cm = new ConfigMaker(_main, "Sold_Log.yml");
		FileConfiguration config = cm.getConfig();
		
		if(!cm.isExists())
		{
			cm.saveConfig();
		}
		
		for(Pair<Date, String> values : selledItems)
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss z");
			String date = formatter.format(values.getFirst());
			String str = values.getSecond();
			if(config.contains(date))
			{
				int count =  Integer.parseInt(str.split(":")[3]) + Integer.parseInt(config.getString(date).split(":")[3]);
				str = str.substring(0, str.lastIndexOf(":"))+":"+count;
			}
			config.set(date, str);
		}
		cm.saveConfig();
		selledItems.clear();
		
	}
}
