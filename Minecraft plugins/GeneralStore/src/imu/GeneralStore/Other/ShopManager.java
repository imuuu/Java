package imu.GeneralStore.Other;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GeneralStore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class ShopManager 
{
	Main _main = Main.getInstance();
	public HashMap<Material,Double[]> smart_prices = new HashMap<>();
	
	HashMap<String,Shop> shops = new HashMap<>();
	
	String shopYAML ="shopNames.yml";
	
	boolean calculationReady = true;
	public ShopManager()
	{
		System.out.println("DO SHIT");
		if(!calculationReady)
		{
			new BukkitRunnable() {
				
				@Override
				public void run() 
				{
					Shop tempShop = new Shop("temp");
					
					
					ConfigMaker cm = new ConfigMaker(_main,"smartcal.yml");
					FileConfiguration config=cm.getConfig();
					for(Material m : Material.values())
					{
						ItemStack stack = new ItemStack(m);
						Double[] test2 = {0.0,0.0,0.0,0.0};
						ArrayList<Double[]> test = new ArrayList<>();
						test.add(test2);
						test = tempShop. materialPrices(stack, test);
						Double[] values = tempShop.materialPricesDecoder(stack,test);
						if(values[0] != 0 && values[1] != 0 && values[2] != 0)
						{
							smart_prices.put(m, values);
							//config.set(m.toString(), values[0]);					
						}
						
					}
					cm.saveConfig();
					System.out.println("CALS READY");
					calculationReady = true;
				}
			}.runTaskAsynchronously(_main);
		}
		
		
		
	}
	
	public void addShop(String name)
	{
		Shop shop = new Shop(name);
		shops.put(name, shop);
		
		ConfigMaker cm = new ConfigMaker(_main, shopYAML);
		FileConfiguration config = cm.getConfig();
		config.set(name, true);
		cm.saveConfig();
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
	}
	
	public Shop getShop(String name)
	{
		Shop shop = shops.get(name);
		name = name.replace(" ", "");
		if(shop == null)
		{
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
}
