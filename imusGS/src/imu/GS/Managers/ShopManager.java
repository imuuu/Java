package imu.GS.Managers;


import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import imu.GS.ENUMs.ShopItemType;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopNormal;
import imu.GS.ShopUtl.ItemPrice.PriceOwn;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.GS.ShopUtl.ShopItems.ShopItemUnique;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.ImusTabCompleter;

public class ShopManager 
{
	Main _main;
	
	ArrayList<ShopBase> _shops;
	HashMap<Material, Double> _material_prices = new HashMap<>();
	BukkitTask RunnableAsyncTask;
	public ShopManager(Main main)
	{
		_main = main;
		_shops = new ArrayList<>();
		
	}
	
	public void Init()
	{
		if(_main.GetSQL() == null)
			return;
		
		PreparedStatement ps;
		try 
		{
			System.out.println("===LOADING TABLES===");
			
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
			
			System.out.println("==> shops");
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shopItems ("
					+ "uuid VARCHAR(50), "
					+ "shop_name VARCHAR(100) NOT NULL, "
					+ "type VARCHAR(200) NOT NULL, "
					+ "item_display_name VARCHAR(100),"
					+ "amount INT(100),"
					+ "own_price FLOAT(20),"
					+ "page INT(100), "
					+ "slot INT(27),"
					+ "type_data TEXT(16000),"
					+ "itemstack TEXT(16000),"
					+ "PRIMARY KEY(uuid))");
//					+ "FOREIGN KEY(shop_name) REFERENCES shops(name),"
//					+ "FOREIGN KEY(custom_recipe_price) REFERENCES custom_prices_recipes(uuid))");
			ps.executeUpdate();
			
			System.out.println("===> shopItems");
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS material_prices ("
					+ "material VARCHAR(50), "
					+ "price FLOAT(20), "
					+ "PRIMARY KEY(material)"
					+ ")");
			ps.executeUpdate();
			
			System.out.println("====> material_prices");	
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS tags ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid_or_materialName VARCHAR(50) NOT NULL, "
					+ "tagName VARCHAR(30),"
					+ "PRIMARY KEY(id))");
			ps.executeUpdate();
			System.out.println("=====> tags");
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS custom_prices ("
					+ "uuid VARCHAR(50) NOT NULL, "
					+ "amount INT(100),"
	//				+ "price FLOAT(20), "
					+ "item_display_name VARCHAR(100),"
					+ "itemstack VARCHAR(16000),"
					+ "PRIMARY KEY(uuid))");
			ps.executeUpdate();
			System.out.println("=====> custom_prices");
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		System.out.println("===TABLE LOADING FINNISHED===");
		LoadMaterialPrices();
		LoadShops();
		
		RunnableAsync();
	}
	
	void RunnableAsync()
	{
		RunnableAsyncTask = new BukkitRunnable() 
		{			
			@Override
			public void run() 
			{
				//System.out.println("Check expires");
				for(ShopBase shop : _shops)
				{
					if(shop.HasExpired()) // && 
					{
						if(!shop.HasCustomers())
						{
							boolean locked = shop.HasLocked();
							shop.SetLocked(true);
							
							ReduceShopItemAmount(shop);
							
							shop.SetNewExpire();
							shop.SetLocked(locked);
						}						
					}
				}			
			}
		}.runTaskTimerAsynchronously(_main, 20 * 60, 20 * 60);
		
		
	}
	void ReduceShopItemAmount(ShopBase sBase)
	{

		for(ShopItemSeller[] siss : sBase.get_items())
		{
			for(ShopItemSeller sis : siss)
			{
				if(sis == null)
					continue;
				
				if(sis instanceof ShopItemStockable)
				{
					if(((ShopItemStockable)sis).AbleToFill())
					{
						((ShopItemStockable)sis).Fill();
						((ShopItemStockable)sis).RefreshFillCD();
					}
					return;
				}
				
				int amount = sis.Get_amount();
				int newAmount = (int)Math.floor(amount * (1.0 - sBase.get_expire_percent()));
				if(newAmount < 0)
					newAmount = 0;
				
				sis.Set_amount(newAmount);
				//sis.UpdateItem();
			}
		}
	}
	public double GetMaterialPrice(Material material)
	{
		return _material_prices.get(material);
	}
	
	public boolean OpenShop(Player p,String name)
	{
		ShopBase shop = GetShop(name);
		if(shop != null)
		{			
			if(shop.HasLocked())
			{
				p.sendMessage(ImusAPI._metods.msgC("&2Shop has closed. Please come laiter"));
				return false;
			}
			shop.AddNewCustomer(p);
			return true;
		}
		return false;
	}
	
	public void onDisabled()
	{	
		closeShopInvs();
		if(RunnableAsyncTask != null)
			RunnableAsyncTask.cancel();
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
		ShopBase shop = new ShopNormal(_main, name, 1);
		_shops.add(shop);
		SaveShop(name, true);
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
	
	public void SaveShop(String name, boolean dontsavedatabase)
	{
		if(_main.GetSQL() == null || dontsavedatabase)
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
			
			int page = 0;
			for (ShopItemSeller[] siss : sBase.get_items()) 
			{
				for(int slot = 0; slot < siss.length; ++slot)
				{
					ShopItemSeller sis = siss[slot];
					ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM shopitems WHERE page="+page+" AND slot="+slot+" AND shop_name='"+sBase._name+"';");
					ps.executeUpdate();	
					if(sis == null)
					{												
						continue;
					}

					String displayName = sis.GetRealItem().getType().name();
					if(sis.GetRealItem().hasItemMeta())
						displayName = sis.GetRealItem().getItemMeta().getDisplayName();
					
					int i = 1;
					
					ps = _main.GetSQL().GetConnection().prepareStatement("INSERT INTO shopitems "
							+ "(uuid, shop_name, type ,item_display_name, amount, own_price, page, slot, type_data,itemstack) "
							+ "VALUES (?,?,?,?,?,?,?,?,?,?)");
					ps.setString(i++, sis.GetUUID().toString());
					ps.setString(i++, sBase._name);
					ps.setString(i++, sis.GetItemType().toString());
					ps.setString(i++, displayName);
					ps.setInt(i++, sis.Get_amount());
					ps.setFloat(i++, (sis.GetItemPrice() instanceof PriceOwn) ? (float)((PriceOwn)sis.GetItemPrice()).GetPrice() : -1.0f);
					ps.setInt(i++, page);
					ps.setInt(i++, slot);
					ps.setString(i++, new Gson().toJson(sis.GetJsonData()));
					//ps.setString(i++, "Make custom price");
					ps.setString(i++, _main.GetMetods().EncodeItemStack(sis.GetRealItem()));
					
					ps.executeUpdate();
				}
				page++;
			}
			
		} catch (Exception e) 
		{
			System.out.println("Couldnt save shop data, probably SQL's shops table is missing");
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
			if(!rs.isBeforeFirst())
			{
				//NO DATA
				System.out.println("No shops found!");
				return;
			}
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
				ShopBase shop = new ShopNormal(_main, _displayName, pages);
				shop.set_sellM(sellM);
				shop.set_buyM(buyM);
				shop.set_expire_percent(expirePrercent);
				shop.set_expire_cooldown_m(expireCooldown);
			
				
				System.out.println("Shop loaded from sql: "+ name);
				GetShopItemsFromSQL(shop);
				_shops.add(shop);
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	void GetShopItemsFromSQL(ShopBase shop) throws SQLException
	{
		PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM shopitems WHERE shop_name='"+shop._name+"';");
		ResultSet rs2 = ps.executeQuery();
		
		if(rs2.isBeforeFirst())
		{
			while(rs2.next())
			{
				int i = 1;
				String uuid = rs2.getString(i++);
				String shopName = rs2.getString(i++);
				ShopItemType siType = ShopItemType.valueOf(rs2.getString(i++));
				String displayName = rs2.getString(i++);
				int amount = rs2.getInt(i++);
				float own_price = rs2.getFloat(i++);
				int page = rs2.getInt(i++);
				int slot = rs2.getInt(i++);
				String typeData = rs2.getString(i++);
				//String custom_recipe_price = rs2.getString(i++);
				ItemStack stack = ImusAPI._metods.DecodeItemStack(rs2.getString(i++));					
				ShopItemSeller sis = null;
				switch (siType) 
				{
				case NORMAL:
					sis = new ShopItemSeller(_main,shop, stack, amount);
					break;
				case UNIQUE:
					sis = new ShopItemUnique(_main,shop, stack, amount);
					break;
				case STOCKABLE:
					sis = new ShopItemStockable(_main, shop, stack, amount);
					break;
				default:
					sis = new ShopItemSeller(_main,shop, stack, amount);
					break;
					}

				
				if(own_price != -1)
				{
					System.out.println("Has own price");
					sis.SetItemPrice(new PriceOwn().SetPrice(own_price));
				}
				
				if(!Strings.isNullOrEmpty(typeData))
					sis.ParseJsonData(new JsonParser().parse(typeData).getAsJsonObject());
			
				shop.SetItem(sis, page, slot);
				
			}
		}
		
	}
	
	
	
	void LoadMaterialPrices()
	{
		PreparedStatement ps;
		
		try {
			ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM material_prices;");
			ResultSet rs = ps.executeQuery();
			int i = 1;
			if(!rs.isBeforeFirst())
			{
				//empty!
				for(Material mat : Material.values())
				{
					i = 1;
					ps = _main.GetSQL().GetConnection().prepareStatement("INSERT INTO material_prices (material, price) VALUES(?,?);");
					ps.setString(i++, mat.name());
					ps.setFloat(i++, 0.0f);
					ps.executeUpdate();
					_material_prices.put(mat, 0.0);
				}
			}else
			{
				while(rs.next())
				{
					i = 1;
					String mat_name = rs.getString(i++);
					
//					if(mat_name.equalsIgnoreCase("air"))
//						continue;
//					System.out.println("Mat: "+mat_name);
					_material_prices.put(Material.getMaterial(mat_name), (double)rs.getFloat(i++));
				}
				System.out.println("Material prices loaded");
			}
		} catch (SQLException e) 
		{
			System.out.println("Couldnt add materials!");
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
