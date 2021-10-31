package imu.GS.Managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import imu.GS.ENUMs.ShopItemType;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemModData;
import imu.GS.ShopUtl.ShopNormal;
import imu.GS.ShopUtl.ItemPrice.PriceOwn;
import imu.GS.ShopUtl.ShopItems.ShopItemSeller;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.GS.ShopUtl.ShopItems.ShopItemUnique;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Tuple;

public class ShopManagerSQL 
{
	Main _main;
	ShopManager _shopManager;
	public ShopManagerSQL(Main main, ShopManager shopManager) 
	{
		_main = main;
		_shopManager = shopManager;
	}
	
	public void LoadTables()
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
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shopitems ("
					+ "uuid CHAR(36), "	
					+ "shop_name VARCHAR(100) NOT NULL, "
					+ "type VARCHAR(200) NOT NULL, "
					+ "item_display_name VARCHAR(100),"
					+ "amount INT(100),"
					+ "page INT(100), "
					+ "slot INT(27),"
					
					+ "own_price 		float(20),"
					+ "max_amount 		INT(20),"
					+ "fill_amount 		INT(20),"
					+ "fill_delay 		INT(20),"
					
					+ "selltime_start 	INT(20),"
					+ "selltime_end		INT(20),"
					
//					+ "distance_uuid	VARCHAR(50),"
//										
//					+ "permissions_uuid	VARCHAR(50),"
//					+ "worldnames_uuid	VARCHAR(50),"

					+ "type_data TEXT(16000),"
					+ "itemstack TEXT(16000),"
					+ "PRIMARY KEY(uuid))");
//					+ "FOREIGN KEY(shop_name) REFERENCES shops(name),"
//					+ "FOREIGN KEY(custom_recipe_price) REFERENCES custom_prices_recipes(uuid))");
			ps.executeUpdate();
			
			System.out.println("===> shopItems");
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shopitem_permissions ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "					
					+ "name VARCHAR(50), "					
					+ "PRIMARY KEY(id))");

			ps.executeUpdate();
			System.out.println("===> shopItems => permissions");
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shopitem_worlds ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "					
					+ "name VARCHAR(50), "					
					+ "PRIMARY KEY(id))");

			ps.executeUpdate();
			System.out.println("===> shopItems => worldNames");
			
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS shopitem_locations ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "				
					+ "distance		INT(20),"
					+ "dis_world	VARCHAR(40),"	
					+ "dis_locX		INT(20),"
					+ "dis_locY		INT(20),"
					+ "dis_locZ		INT(20),"
								
					+ "PRIMARY KEY(id))");

			ps.executeUpdate();
			System.out.println("===> shopItems => locations");
			
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
//			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS custom_prices ("
//					+ "uuid CHAR(36), "	
//					+ "amount INT(100),"
//					+ "item_display_name VARCHAR(100),"
//					+ "itemstack TEXT(16000),"
//					+ "PRIMARY KEY(uuid))");
//			ps.executeUpdate();
//			System.out.println("=====> custom_prices");
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS uniques ("
					+ "uuid CHAR(36), "	
					+ "item_display_name VARCHAR(100),"
					+ "price FLOAT(20), "
					+ "itemstack TEXT(16000),"
					+ "PRIMARY KEY(uuid))");
			ps.executeUpdate();
			System.out.println("=====> uniques");
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		System.out.println("===TABLE LOADING FINNISHED===");
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
				LoadShopItems(shop);
				_shopManager._shops.add(shop);
								
			}
			_shopManager.UpdateTabCompliters();
		} catch (SQLException e) {
			e.printStackTrace();
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
					_shopManager._material_prices.put(mat, 0.0);
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
					_shopManager._material_prices.put(Material.getMaterial(mat_name), (double)rs.getFloat(i++));
				}
				System.out.println("Material prices loaded");
			}
		} catch (SQLException e) 
		{
			System.out.println("Couldnt add materials!");
			e.printStackTrace();
		}
		
		
		
	}
	
	public void LoadShopItems(ShopBase shop) throws SQLException
	{
		PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM shopitems WHERE shop_name='"+shop._name+"';");
		ResultSet rs2 = ps.executeQuery();
		
		if(rs2.isBeforeFirst())
		{
			while(rs2.next())
			{
				int i = 1;
				UUID uuid = UUID.fromString(rs2.getString(i++));
				String shopName = rs2.getString(i++);
				ShopItemType siType = ShopItemType.valueOf(rs2.getString(i++));
				String displayName = rs2.getString(i++);
				int amount = rs2.getInt(i++);
				int page = rs2.getInt(i++);
				int slot = rs2.getInt(i++);
				
				float own_price = rs2.getFloat(i++);
				int max_amount = rs2.getInt(i++);
				int fill_amount = rs2.getInt(i++);
				int fill_delay = rs2.getInt(i++);
				
				int selltimeStart = rs2.getInt(i++);
				int selltimeEnd = rs2.getInt(i++);

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

				sis.SetUUID(uuid);
				
				if(sis instanceof ShopItemStockable)
				{
					ShopItemModData modData = new ShopItemModData();
					modData._ownPrice = own_price;
					modData._maxAmount = max_amount;
					modData._fillAmount = fill_amount;
					modData._fillDelayMinutes =fill_delay;
					modData._sellTimeStart = selltimeStart;
					modData._sellTimeEnd = selltimeEnd;
					
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM shopitem_locations WHERE uuid='"+uuid.toString()+"';");
					ResultSet rs = ps.executeQuery();
					
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							int l = 1;
							l++; l++; //id uuid
							int distance = rs.getInt(l++);
							Location loc = new Location(Bukkit.getWorld(rs.getString(l++)), rs.getInt(l++), rs.getInt(l++), rs.getInt(l++));
							modData.AddLocation(distance, loc);
							//modData.
							
						}
						System.out.println("distance added");
					}
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM shopitem_permissions WHERE uuid='"+uuid.toString()+"';");
					rs = ps.executeQuery();
					
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							modData.AddPermission(rs.getString(3));						
						}
						System.out.println("permission added");
					}
					
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM shopitem_worlds WHERE uuid='"+uuid.toString()+"';");
					rs = ps.executeQuery();
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							modData.AddWorldName(rs.getString(3));						
						}
						System.out.println("world added");
					}
					
					
					
					((ShopItemStockable)sis).SetModData(modData);
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
	
	public void DeleteShop(ShopBase shop)
	{
		if(_main.GetSQL() == null)
			return;
		
		PreparedStatement ps;
		try 
		{		
			ps = _main.GetSQL().GetConnection().prepareStatement(""
					+ "DELETE FROM shops WHERE name='"+shop._name+"'");
			
			ps.executeUpdate();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void SaveShop(ShopBase shop)
	{
		boolean lock = shop.HasLocked();
		shop.SetLocked(true);
		PreparedStatement ps;
		try 
		{
			ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO shops "
					+ "(name, display_name, pages,shop_type, sellM, buyM, expire_percent, expire_cooldown)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, shop._name);
			ps.setString(2, shop._displayName);
			ps.setInt	(3, shop.get_items().size());
			ps.setString(4, "1");
			ps.setFloat	(5, (float)shop.get_sellM());
			ps.setFloat	(6, (float)shop.get_buyM());
			ps.setFloat	(7, (float)shop.get_expire_percent());
			ps.setInt	(8, shop.get_expire_cooldown_m());		
			ps.executeUpdate();
			
			int page = 0;
			for (ShopItemSeller[] siss : shop.get_items()) 
			{
				for(int slot = 0; slot < siss.length; ++slot)
				{
					ShopItemSeller sis = siss[slot];
					ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM shopitems WHERE page="+page+" AND slot="+slot+" AND shop_name='"+shop._name+"';");
					ps.executeUpdate();	
					if(sis == null)
					{												
						continue;
					}

					SaveShopItem(sis, page, slot);
					
				}
				page++;
			}
			
		} catch (Exception e) 
		{
			System.out.println("Couldnt save shop data, probably SQL's shops table is missing");
			e.printStackTrace();
		}
		
		shop.SetLocked(lock);
	}
	
	public void SaveShopItem(ShopItemSeller sis, int page, int slot)
	{
		if(sis.GetShop() == null)
			return;
		
		int i = 1;
		PreparedStatement ps,ps2;
		try 
		{
			ps = _main.GetSQL().GetConnection().prepareStatement("INSERT INTO shopitems "
					+ "(uuid, shop_name, type , item_display_name, amount, page, slot, own_price, max_amount, fill_amount, fill_delay, selltime_start, selltime_end,type_data,itemstack) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(i++, sis.GetUUID().toString());
			ps.setString(i++, sis.GetShop()._name);
			ps.setString(i++, sis.GetItemType().toString());
			ps.setString(i++, ImusAPI._metods.GetItemDisplayName(sis.GetRealItem()));
			ps.setInt(i++, sis.Get_amount());
			ps.setInt(i++, page);
			ps.setInt(i++, slot);
			
			double ownPrice = -1;
			int max_amount = -1;
			int fill_amount = -1;
			int fill_delay = -1;
			int sellTimeStart = -1;
			int sellTimeEnd = -1;
			if(sis instanceof ShopItemStockable)
			{
				ShopItemModData modData = ((ShopItemStockable)sis).GetModData();
				ownPrice = modData._ownPrice;
				max_amount = modData._maxAmount;
				fill_amount = modData._fillAmount;
				fill_delay = modData._fillDelayMinutes;
				sellTimeStart = modData._sellTimeStart;
				sellTimeEnd = modData._sellTimeEnd;
				
				ps2 = _main.GetSQL().GetConnection().prepareStatement(String.format("DELETE FROM shopitem_permissions WHERE uuid='%s';",sis.GetUUID().toString()));
				ps2.executeUpdate();
				
				if(modData._permissions != null)
				{
					for(String permission : modData._permissions)
					{
						ps2 = _main.GetSQL().GetConnection().prepareStatement("INSERT INTO shopitem_permissions "
								+ "(uuid, name) VALUES (?,?)");
						ps2.setString(1, sis.GetUUID().toString());
						ps2.setString(2, permission);
						ps2.executeUpdate();
					}
				}
				
				ps2 = _main.GetSQL().GetConnection().prepareStatement(String.format("DELETE FROM shopitem_worlds WHERE uuid='%s';",sis.GetUUID().toString()));
				ps2.executeUpdate();
				if(modData._worldNames != null)
				{
					for(String worldName : modData._worldNames)
					{
						ps2 = _main.GetSQL().GetConnection().prepareStatement("INSERT INTO shopitem_worlds "
								+ "(uuid, name) VALUES (?,?)");
						ps2.setString(1, sis.GetUUID().toString());
						ps2.setString(2, worldName);
						ps2.executeUpdate();
					}
				}
				
				ps2 = _main.GetSQL().GetConnection().prepareStatement(String.format("DELETE FROM shopitem_locations WHERE uuid='%s';",sis.GetUUID().toString()));
				ps2.executeUpdate();			
				if(modData._locations != null)
				{
					for(Tuple<Integer, Location> disLoc : modData._locations)
					{
						int l = 1;
						ps2 = _main.GetSQL().GetConnection().prepareStatement("INSERT INTO shopitem_locations "
								+ "(uuid, distance, dis_world, dis_locX, dis_locY, dis_locZ) VALUES (?,?,?,?,?,?)");
						Location loc = disLoc.GetValue();
						ps2.setString(l++, sis.GetUUID().toString());					
						ps2.setInt(l++, disLoc.GetKey());
						ps2.setString(l++, loc.getWorld().getName());
						ps2.setInt(l++, loc.getBlockX());
						ps2.setInt(l++, loc.getBlockY());
						ps2.setInt(l++, loc.getBlockZ());
						ps2.executeUpdate();
					}
				}
				
				
				
			}
			//ps.setFloat(i++, (sis.GetItemPrice() instanceof PriceOwn) ? (float)((PriceOwn)sis.GetItemPrice()).GetPrice() : -1.0f);
			ps.setFloat(i++, (float)ownPrice);
			ps.setInt(i++, max_amount);
			ps.setInt(i++, fill_amount);
			ps.setInt(i++, fill_delay);
			ps.setInt(i++, sellTimeStart);
			ps.setInt(i++, sellTimeEnd);
			
			ps.setString(i++, new Gson().toJson(sis.GetJsonData()));
			ps.setString(i++, _main.GetMetods().EncodeItemStack(sis.GetRealItem()));			
			ps.executeUpdate();
			
		
			
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Saving shopitem: Couldnt add item");
		}
		
	}

	public void SaveUniqueItem(ShopItemUnique siu)
	{
		System.out.println("saving unique");
		
		if(siu.GetRealItem().getType() == Material.AIR) return;
		
		PreparedStatement ps;
		try 
		{
			ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO uniques "
					+ "(uuid, item_display_name, price, itemstack)"
					+ "VALUES (?, ?, ?, ?)");
			
			int i = 1;
			ps.setString(i++, siu.GetUUID().toString());
			ps.setString(i++, ImusAPI._metods.GetItemDisplayName(siu.GetRealItem()));
			ps.setFloat(i++, (float)siu.GetItemPrice().GetPrice());
			ps.setString(i++, ImusAPI._metods.EncodeItemStack(siu.GetRealItem()));
			ps.executeUpdate();
		} 
		catch (Exception e) 
		{
			System.out.println("Couldnt save unique");
			e.printStackTrace();
		}	
	}
	
	public void DeleteUniqueItem(ShopItemUnique siu)
	{
		try 
		{
			_main.GetSQL().GetConnection().prepareStatement("DELETE FROM uniques WHERE uuid='"+siu.GetUUID().toString()+"'");
			
		} 
		catch (Exception e) 
		{
			System.out.println("Couldnt save unique");
			e.printStackTrace();
		}	
	}
	
	public void LoadUniques()
	{
		if(_main.GetSQL() == null)
			return;
		
		PreparedStatement ps;
		try {
			ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM uniques");
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst())
			{
				//NO DATA
				System.out.println("Uniques not found!");
				return;
			}
			while(rs.next())
			{
				int i = 1;
				UUID uuid = UUID.fromString(rs.getString(i++));
				i++;
				double price = (double)rs.getFloat(i++);
				ItemStack stack = ImusAPI._metods.DecodeItemStack(rs.getString(i++));
				ShopItemUnique unique = new ShopItemUnique(_main, null, stack, 1);
				unique.SetUUID(uuid);
				unique.GetItemPrice().SetPrice(price);
				_shopManager.GetUniqueManager()._uniques.put(unique.GetUUID(),unique);
				
				
			}
		} catch (SQLException e) 
		{
			System.out.println("Loading Unique ERROR");
			e.printStackTrace();
		}
	}
}
