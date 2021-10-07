package imu.GS.Managers;


import java.util.ArrayList;

import org.bukkit.entity.Player;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopNormal;
import imu.iAPI.Other.ImusTabCompleter;

public class ShopManager 
{
	Main _main;
	
	ArrayList<ShopBase> _shops;
	public ShopManager(Main main)
	{
		_main = main;
		_shops = new ArrayList<>();
		CreateShop("test");
	}
	
	public boolean OpenShop(Player p,String name)
	{
		ShopBase shop = GetShop(name);
		if(shop != null)
		{			
			shop.AddNewCustomer(p);
			return true;
		}
		return false;
	}
	
	public void onDisabled()
	{	
		closeShopInvs();
	}
	
	void closeShopInvs()
	{
		for(ShopBase sb : _shops)
		{
			sb.RemoveCustomerALL();
		}
		
	}
	
	public void CreateShop(String name)
	{
		ShopBase shop = new ShopNormal(_main, name);
		_shops.add(shop);
		//UpdateTabCompliters();
	}
	
	public void RemoveShop(String name)
	{
		ShopBase s = GetShop(name);
		s.RemoveCustomerALL();
		_shops.remove(s);
	}
	
	public ShopBase GetShop(String name)
	{
		for(ShopBase s : _shops)
		{
			if(s._name.toLowerCase().contains(name.toLowerCase()))
				return s;
		}
		return null;
	}
	
//	public void SaveShop(String name,boolean async)
//	{
//		Shop s = getShop(name);
//		if(s != null)
//			s.saveShop(async);
//	}
	
	public void UpdateTabCompliters()
	{
		
		String[] shopNames = new String[_shops.size()];
		for(int i = 0; i < _shops.size(); ++i)
		{
			shopNames[i] = _shops.get(i)._name;
		}
		ImusTabCompleter tab = _main.get_tab_cmd1();
		tab.setArgumenrs("shop", shopNames);
	}
	
//	public void loadShopsAsync()
//	{
//		//TODO shops kansioo ei luoda jos sitä ei oo olemas!
//		for(File file : new File(_main.getDataFolder().getAbsoluteFile()+File.separator + "Shops").listFiles())
//		{
//
//			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
//			
//			Shop shop = new Shop(_main, "");
//			shop.set_name(config.getString("Name"));
//			shop.set_sellM(config.getDouble("Sell_Multiplier"));
//			shop.set_buyM(config.getDouble("Buy_Multiplier"));
//			shop.set_expire_percent(config.getDouble("Expire_percent"));
//			shop.set_expire_cooldown_m(config.getDouble("Expire_Cooldown"));
//
//			_shops.add(shop);
//			System.out.println("Shop added: "+shop.get_name());
//		}
//	}
}
