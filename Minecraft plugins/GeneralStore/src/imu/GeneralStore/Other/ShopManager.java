package imu.GeneralStore.Other;

import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import imu.GeneralStore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class ShopManager 
{
	Main _main = Main.getInstance();
	HashMap<String,Shop> shops = new HashMap<>();
	
	String shopYAML ="shopNames.yml";
	
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
