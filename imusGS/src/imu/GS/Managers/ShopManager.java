package imu.GS.Managers;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		Init();
	}
	
	void Init()
	{
		if(_main.GetSQL() == null)
			return;
		
		PreparedStatement ps;
		try 
		{
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shops ("
					+ "name VARCHAR(100) NOT NULL, "
					+ "display_name VARCHAR(100), "
					+ "pages INT(100), "
					+ "shop_type INT(100), "
					+ "sellM FLOAT(20), "
					+ "buyM FLOAT(20), "
					+ "expire_percent FLOAT(20),"
					+ "expire_cooldown INT(100), PRIMARY KEY(name))");
			ps.executeUpdate();
			
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shopItems ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "shop_name VARCHAR(100) NOT NULL, "
					+ "item_display_name VARCHAR(100),"
					+ "amount INT(100),"
					+ "own_price INT(100),"
					+ "page INT(100), "
					+ "slot INT(27),"
					+ "custom_recipe_price VARCHAR(100),"
					+ "itemstack VARCHAR(16000),"
					+ "PRIMARY KEY(id))");
//					+ "FOREIGN KEY(shop_name) REFERENCES shops(name),"
//					+ "FOREIGN KEY(custom_recipe_price) REFERENCES custom_prices_recipes(uuid))");
			ps.executeUpdate();
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS material_prices ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "material VARCHAR(50), "
					+ "price FLOAT(20), "
					+ "PRIMARY KEY(id)"
					+ ")");
			ps.executeUpdate();
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS custom_prices_recipes ("
					+ "uuid VARCHAR(50) NOT NULL, "
					+ "recipe_uuids VARCHAR(10000),"
					+ "PRIMARY KEY(uuid))");
			ps.executeUpdate();
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS custom_prices ("
					+ "uuid VARCHAR(50) NOT NULL, "
					+ "amount INT(100),"
					+ "item_display_name VARCHAR(100),"
					+ "itemstack VARCHAR(16000),"

					+ "PRIMARY KEY(uuid))");
			ps.executeUpdate();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		LoadShops();
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
		SaveShop(name);
		UpdateTabCompliters();
	}
	
	public void RemoveShop(String name)
	{
		ShopBase s = GetShop(name);
		if(s == null)
			return;
		
		s.RemoveCustomerALL();
		_shops.remove(s);

		if(_main.GetSQL() == null)
			return;
		
		PreparedStatement ps;
		try 
		{		
			ps = _main.GetSQL().GetConnection().prepareStatement(""
					+ "DELETE FROM shops WHERE name='"+s._name+"'");
			
			ps.executeUpdate();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public ShopBase GetShop(String name)
	{
		String searchName = _main.GetMetods().StripColor(name);
		for(ShopBase s : _shops)
		{
			if(s._name.toLowerCase().contains(searchName.toLowerCase()))
				return s;
		}
		return null;
	}
	
	public void SaveShop(String name)
	{
		if(_main.GetSQL() == null)
			return;
		
		ShopBase sBase = GetShop(name);
		
		if(sBase == null)
			return;
		
		PreparedStatement ps;
		try 
		{
			ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO shops "
					+ "(name, display_name, pages,shop_type, sellM, buyM, expire_percent, expire_cooldown)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, sBase._name);
			ps.setString(2, sBase._displayName);
			ps.setInt	(3, sBase.get_items().size());
			ps.setString(4, "1");
			ps.setFloat	(5, (float)sBase.get_sellM());
			ps.setFloat	(6, (float)sBase.get_buyM());
			ps.setFloat	(7, (float)sBase.get_expire_percent());
			ps.setInt	(8, sBase.get_expire_cooldown_m());

			
			ps.executeUpdate();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void LoadShops()
	{
		if(_main.GetSQL() == null)
			return;
		
		PreparedStatement ps;
		try {
			ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM shops");
			ResultSet rs = ps.executeQuery();			
			while(rs.next())
			{
				int i = 1;
				String name = rs.getString(i++);
				String _displayName = rs.getString(i++);
				int pages = rs.getInt(i++);
				int type  = rs.getInt(i++);
				float sellM = rs.getFloat(i++);
				float buyM = rs.getFloat(i++);
				float expirePrercent = rs.getFloat(i++);
				int expireCooldown = rs.getInt(i++);
				ShopBase shop = new ShopNormal(_main, _displayName);
				shop.set_sellM(sellM);
				shop.set_buyM(buyM);
				shop.set_expire_percent(expirePrercent);
				shop.set_expire_cooldown_m(expireCooldown);
				_shops.add(shop);
				System.out.println("sql load name: "+ name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
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
	

}
