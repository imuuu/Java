package imu.GS.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import imu.GS.ENUMs.ItemPriceType;
import imu.GS.ENUMs.SQL_TABLES;
import imu.GS.ENUMs.ShopItemType;
import imu.GS.ENUMs.TransactionAction;
import imu.GS.Main.Main;
import imu.GS.Other.CustomPriceData;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ShopItemModData;
import imu.GS.ShopUtl.ShopNormal;
import imu.GS.ShopUtl.ItemPrice.PriceCustom;
import imu.GS.ShopUtl.ItemPrice.PriceOwn;
import imu.GS.ShopUtl.ItemPrice.PriceUnique;
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
		try(Connection con =_main.GetSQL().GetConnection()) 
		{
			_main.getLogger().info("===LOADING TABLES===");
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.shops.toString()+" ("
					+ "uuid CHAR(36) NOT NULL, "
					+ "name VARCHAR(100) NOT NULL, "
					+ "display_name VARCHAR(100), "
					+ "pages INT(100), "
					+ "shop_type INT(100), "
					+ "sellM FLOAT(20), "
					+ "buyM FLOAT(20), "
					+ "expire_percent FLOAT(20),"
					+ "expire_cooldown INT(100),"
					+ "locked INT(1),"		
					+ "customer_can_sell INT(1),"
					+ "absolutePositions INT(1), PRIMARY KEY(uuid));");
			ps.executeUpdate();
			
			_main.getLogger().info("==> shops");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.shopitems.toString()+" (" 
					+ "uuid CHAR(36), "	
					+ "shop_uuid CHAR(36) NOT NULL, "
					+ "type VARCHAR(200) NOT NULL, "
					+ "item_display_name VARCHAR(100),"
					+ "amount INT(100),"
					+ "page INT(100), "
					+ "slot INT(27),"
					
					+ "price_type 		VARCHAR(20),"
					+ "max_amount 		INT(20),"
					+ "fill_amount 		INT(20),"
					+ "fill_delay 		INT(20),"
					
					+ "selltime_start 	INT(20),"
					+ "selltime_end		INT(20),"
					
					+ "type_data TEXT(16000),"
					+ "itemstack TEXT(16000),"
					+ "PRIMARY KEY(uuid));");

			ps.executeUpdate();
			
			_main.getLogger().info("===> shopItems");
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.shopitem_permissions.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "					
					+ "name VARCHAR(50), "					
					+ "PRIMARY KEY(id));");

			ps.executeUpdate();
			_main.getLogger().info("===> shopItems => permissions");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.shopitem_worlds.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "					
					+ "name VARCHAR(50), "					
					+ "PRIMARY KEY(id));");

			ps.executeUpdate();
			_main.getLogger().info("===> shopItems => worldNames");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.shopitem_locations.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "				
					+ "distance		INT(20),"
					+ "dis_world	VARCHAR(40),"	
					+ "dis_locX		INT(20),"
					+ "dis_locY		INT(20),"
					+ "dis_locZ		INT(20),"
								
					+ "PRIMARY KEY(id));");

			ps.executeUpdate();
			_main.getLogger().info("===> shopItems => locations");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.price_customs.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "				
					+ "amount INT(20), "				
					+ "itemstack TEXT(16000),"								
					+ "PRIMARY KEY(id));");

			ps.executeUpdate();
			_main.getLogger().info("===> price type => custom");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.price_values.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36), "
					+ "name VARCHAR(36), "				
					+ "amount FLOAT(20), "
					+ "mark VARCHAR(10), "					
					+ "PRIMARY KEY(id));");

			ps.executeUpdate();
			_main.getLogger().info("===> price type => priceValues");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.custom_price.toString()+" ("
					+ "uuid CHAR(36), "				
					+ "minimum_stack_amount FLOAT(20), "							
					+ "PRIMARY KEY(uuid));");

			ps.executeUpdate();
			_main.getLogger().info("===> CustomPrice");
			
			
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.price_materials.toString()+" ("
					+ "material VARCHAR(50), "
					+ "price FLOAT(20), "
					+ "PRIMARY KEY(material)"
					+ ");");
			ps.executeUpdate();
			
			_main.getLogger().info("====> material_prices");	
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.tags_material.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "material_name VARCHAR(50) NOT NULL, "
					+ "tag_name VARCHAR(30),"
					+ "PRIMARY KEY(id));");
			ps.executeUpdate();
			_main.getLogger().info("=====> tags_materials");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.tags_shopitems.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "sib_uuid CHAR(36) NOT NULL, "
					+ "tag_name VARCHAR(30),"
					+ "PRIMARY KEY(id));");
			ps.executeUpdate();
			_main.getLogger().info("=====> tags_shopitems");

			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.uniques.toString()+" ("
					+ "uuid CHAR(36), "	
					+ "item_display_name VARCHAR(100),"
					+ "price FLOAT(20), "
					+ "itemstack TEXT(16000),"
					+ "PRIMARY KEY(uuid));");
			ps.executeUpdate();
			_main.getLogger().info("=====> uniques");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_TABLES.log_transaction.toString()+" ("
					+ "id INT NOT NULL AUTO_INCREMENT, "	
					+ "datetime DATETIME DEFAULT(NOW()), "	
					+ "player_uuid CHAR(36),"
					+ "player_name VARCHAR(20),"
					+ "action VARCHAR(10),"
					+ "shop_uuid CHAR(36),"
					+ "shopname VARCHAR(100) NOT NULL,"
					+ "shopitem_uuid CHAR(36),"
					+ "amount INT(10),"
					+ "price FLOAT(20), "
					+ "custom_price_view VARCHAR(10000), "
					+ "itemstack_displayname VARCHAR(100), "
					+ "itemstack TEXT(16000),"
					+ "PRIMARY KEY(id));");
			ps.executeUpdate();
			_main.getLogger().info("=====> log");
			
			con.close();
			ps.close();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		_main.getLogger().info("===TABLE LOADING FINNISHED===");
	}
	
	public BukkitTask LogPurchaseAsync(Player player, ShopItemBase sib, int amount, TransactionAction tAction)
	{
		//_main.getLogger().info("Loging..");
		return new BukkitRunnable() 
		{	
			@Override
			public void run() 
			{
				try
				{
					Connection con = _main.GetSQL().GetConnection();
					PreparedStatement ps = con.prepareStatement("INSERT INTO "+SQL_TABLES.log_transaction.toString()+" "
							+ "(player_uuid, player_name, action, shop_uuid, shopname, shopitem_uuid, amount, price, custom_price_view, itemstack_displayname, itemstack) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
					int i = 1;
					ps.setString(i++, player.getUniqueId().toString());
					ps.setString(i++, player.getName());
					ps.setString(i++, tAction.toString());
					ps.setString(i++, sib.GetShop().GetUUID().toString());
					ps.setString(i++, sib.GetShop().GetName());
					ps.setString(i++, sib.GetUUID().toString());
					ps.setInt(i++, amount);
					ps.setFloat(i++, (float)sib.GetItemPrice().GetPrice());
					ps.setString(i++, sib.GetItemPrice() instanceof PriceCustom ? ((PriceCustom)sib.GetItemPrice()).GetViewStringOfItems(amount) : "");
					ps.setString(i++, ImusAPI._metods.GetItemDisplayName(sib.GetRealItem()));
					ps.setString(i++, ImusAPI._metods.EncodeItemStack(sib.GetRealItem()));
					ps.executeUpdate();
					
					ps.close();
					con.close();
					
				} 
				catch (Exception e) 
				{
					Bukkit.getLogger().info("ShopManagerSQL:LogPurchaseAsync ERROR");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(_main);
	}
	
	void LoadShops()
	{
		if(_main.GetSQL() == null)
			return;

		try
		{
			Connection con = _main.GetSQL().GetConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.shops.toString()+"");
			ResultSet rs = ps.executeQuery();

			if(!rs.isBeforeFirst())
			{
				//NO DATA
				_main.getLogger().info("No shops found!");
				return;
			}
			while(rs.next())
			{
				int i = 1;
				UUID uuid = UUID.fromString(rs.getString(i++));
				String name = rs.getString(i++);
				String _displayName = rs.getString(i++);
				int pages = rs.getInt(i++);
				//int type  = rs.getInt(i++);
				i++;
				float sellM = rs.getFloat(i++);
				float buyM = rs.getFloat(i++);
				float expirePrercent = rs.getFloat(i++);
				int expireCooldown = rs.getInt(i++);
				boolean locked = (rs.getInt(i++) != 0);
				boolean customerCanOnlySell = (rs.getInt(i++) != 0);
				boolean absolutePos = (rs.getInt(i++) != 0);
				ShopBase shop = new ShopNormal(_main, uuid,_displayName, pages);
				shop.set_sellM(sellM);
				shop.set_buyM(buyM);
				shop.set_expire_percent(expirePrercent);
				shop.set_expire_cooldown_m(expireCooldown);
				shop.SetLocked(locked);
				shop.SetCustomersCanOnlyBuy(customerCanOnlySell);
				shop.SetAbsolutePosBool(absolutePos);
				
				
				_shopManager.GetShops().add(shop);
				_main.getLogger().info("Shop loaded named: "+ name);
						
				
			}
			
			ps.close();
			con.close();
			
			for(ShopBase sBase : _shopManager.GetShops())
			{
				LoadShopItems(sBase);
			}
		
			
			_shopManager.UpdateTabCompliters();
		} catch (SQLException e) 
		{
			Bukkit.getLogger().info("ShopManagerSQL:LoadShops: ERROR");
			e.printStackTrace();
		}
		
	}
	
	void LoadMaterialPrices()
	{
		
		
		try 
		{
			Connection con = _main.GetSQL().GetConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.price_materials.toString()+";");
			ResultSet rs = ps.executeQuery();
			int i = 1;
			if(!rs.isBeforeFirst())
			{
				//empty!
				PreparedStatement ps2 = con.prepareStatement("INSERT INTO "+SQL_TABLES.price_materials.toString()+" (material, price) VALUES(?,?);");
				for(Material mat : Material.values())
				{
					i = 1;

					ps2.setString(i++, mat.name());
					ps2.setFloat(i++, 0.0f);
					ps2.addBatch();
					_shopManager.PutMaterialPrice(mat, 0.0);
				}
				
				ps2.executeBatch();
				ps2.close();
			}
			else
			{
				while(rs.next())
				{
					i = 1;
					String mat_name = rs.getString(i++);
					_shopManager.PutMaterialPrice(Material.getMaterial(mat_name), (double)rs.getFloat(i++));
				}
				_main.getLogger().info("Material prices loaded");
			}
			
			con.close();
			ps.close();
		}
		catch (SQLException e) 
		{
			//_main.getLogger().info("Couldnt add materials!");
			Bukkit.getLogger().info("ShopManagerSQL:LoadMaterialPrices: ERROR");

		}
		
		
	}
	
	void SaveMaterialPrice(Material mat, double price)
	{
		;
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO "+SQL_TABLES.price_materials.toString()+" (material, price) VALUES(?,?);");)
		{
			//_main.getLogger().info("try to save to data base material "+mat+ " and price: "+price);
			
			ps.setString(1, mat.name());
			ps.setFloat(2, (float)price);
			ps.executeUpdate();
		} 
		catch (Exception e) 
		{
			Bukkit.getLogger().info("ShopManagerSQL:SavematerialPrice:Couldnt Save Material ( "+mat.toString()+" ) to database");
		}
		
	}
	
	@SuppressWarnings("deprecation")
	void LoadShopItems(ShopBase shop) throws SQLException
	{
		Connection con = _main.GetSQL().GetConnection();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.shopitems.toString()+" WHERE shop_uuid='"+shop.GetUUID().toString()+"';");
		ResultSet rs2 = ps.executeQuery();
		
		if(rs2.isBeforeFirst())
		{
			while(rs2.next())
			{
				int i = 1;
				UUID uuid = UUID.fromString(rs2.getString(i++));			
				i++;//String shopName = rs2.getString(i++);
				ShopItemType siType = ShopItemType.valueOf(rs2.getString(i++));				
				i++;//String displayName = rs2.getString(i++);
				int amount = rs2.getInt(i++);
				int page = rs2.getInt(i++);
				int slot = rs2.getInt(i++);
				
				//float own_price = rs2.getFloat(i++);
				ItemPriceType priceType = ItemPriceType.valueOf(rs2.getString(i++));
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
					sis = new ShopItemSeller(_main, shop, stack, amount);
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
					//modData._itemPrice = own_price > -1 ? new PriceOwn().SetPrice(own_price) : null;
					modData._maxAmount = max_amount;
					modData._fillAmount = fill_amount;
					modData._fillDelayMinutes =fill_delay;
					modData._sellTimeStart = selltimeStart;
					modData._sellTimeEnd = selltimeEnd;
					
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_TABLES.shopitem_locations.toString()+" WHERE uuid='"+uuid.toString()+"';");
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
						_main.getLogger().info("distance added");
					}
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_TABLES.shopitem_permissions.toString()+" WHERE uuid='"+uuid.toString()+"';");
					rs = ps.executeQuery();
					
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							modData.AddPermission(rs.getString(3));						
						}
						_main.getLogger().info("permission added");
					}
					
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_TABLES.shopitem_worlds.toString()+" WHERE uuid='"+uuid.toString()+"';");
					rs = ps.executeQuery();
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							modData.AddWorldName(rs.getString(3));						
						}
						_main.getLogger().info("world added");
					}
					
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_TABLES.tags_shopitems+" WHERE sib_uuid='"+uuid.toString()+"';");
					rs = ps.executeQuery();
					
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							modData.AddTag(rs.getString(3).toLowerCase());
						}
					}
					
					
					
					((ShopItemStockable)sis).SetModData(modData);
					sis.Set_amount(amount);
					
					
				}
					
				switch (priceType) {
				case None:
					sis.SetItemPrice( _main.get_shopManager().GetPriceMaterialAndCheck(stack));
					break;
				case PriceCustom:
					sis.SetItemPrice( GetPriceCustom(uuid));
					break;
				case PriceOwn:
					sis.SetItemPrice(new PriceOwn().SetPrice(GetPriceValue(uuid)));
					break;
				case PriceUnique:
					sis.SetItemPrice(GetUniquePrice( _shopManager.GetUniqueManager().GetUniqueUUID(stack)));
					break;
				
				}
				
				if(!Strings.isNullOrEmpty(typeData))
					sis.ParseJsonData(new JsonParser().parse(typeData).getAsJsonObject());
			
				//_main.getLogger().info("sis: "+sis.GetRealItem().getType()+ " price: "+sis.GetItemPrice());
				shop.SetItem(sis, page, slot);
				
			}
		}
		
		con.close();
		ps.close();
		//_main.get_shopManager().CheckShopItems(shop);
		
	}
	
	double GetPriceValue(UUID shopItemSellerUUID) throws SQLException 
	{
		Connection con = _main.GetSQL().GetConnection();
		double priceValue = 0;
		PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.price_values.toString()+" WHERE uuid='"+shopItemSellerUUID.toString()+"';");
		ResultSet rs = ps.executeQuery();
		if(rs.isBeforeFirst()) 
		{
			rs.next();
			priceValue = (double)rs.getFloat(4);
		}
		
		con.close();
		ps.close();
		
		return priceValue;
	}
	
	void RemovePriceValue(UUID shopItemSellerUUID) throws SQLException
	{
		Connection con = _main.GetSQL().GetConnection();
		PreparedStatement ps = con.prepareStatement(String.format("DELETE FROM "+SQL_TABLES.price_values.toString()+" WHERE uuid='%s';",shopItemSellerUUID.toString()));
		ps.executeUpdate();
		con.close();
		ps.close();
	}
	
	void SavePriceValue(UUID shopItemSellerUUID, double value) throws SQLException
	{		
		//RemovePriceValue(shopItemSellerUUID);
		Connection con =_main.GetSQL().GetConnection();
		PreparedStatement ps = con.prepareStatement("INSERT INTO "+SQL_TABLES.price_values.toString()+" "
				+ "(uuid, name, amount, mark) VALUES (?,?, ?, ?)");
		ps.setString(1, shopItemSellerUUID.toString());
		ps.setString(2, "");
		ps.setFloat(3, (float)value);
		ps.setString(4, "$");
		ps.executeUpdate();
		
		con.close();
		ps.close();
	}
	
	PriceCustom GetPriceCustom(UUID uuid) throws SQLException 
	{
		Connection con = _main.GetSQL().GetConnection();
		PreparedStatement ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.price_customs.toString()+" WHERE uuid='"+uuid.toString()+"';");
		ResultSet rs = ps.executeQuery();
		PriceCustom pc = new PriceCustom();
		ArrayList<CustomPriceData> datas = new ArrayList<>();
		if(rs.isBeforeFirst())
		{		
			
			while(rs.next())
			{
				ItemStack stack = ImusAPI._metods.DecodeItemStack(rs.getString(4));
				stack.setAmount(1);
				datas.add(new CustomPriceData(stack, rs.getInt(3)));							
			}
			
		}
		ps.close();
		
		ps = con.prepareStatement("SELECT * FROM "+SQL_TABLES.custom_price.toString()+" WHERE uuid='"+uuid.toString()+"';");
		rs = ps.executeQuery();
		int minimumStackAmount = 1;
		if(rs.isBeforeFirst())
		{
			rs.next();
			minimumStackAmount = rs.getInt(2);
		}
		
		CustomPriceData[] array = new CustomPriceData[datas.size()];
		for(int i = 0; i < array.length; i++) {array[i] = datas.get(i);}
			
		pc.SetItemsAndPrice(array, GetPriceValue(uuid), minimumStackAmount);
		
		con.close();
		ps.close();
		
		return pc;
	}
	
	void RemovePriceCustom(UUID shopItemUUID) throws SQLException
	{
		Connection con = _main.GetSQL().GetConnection();
		PreparedStatement ps = con.prepareStatement(String.format("DELETE FROM "+SQL_TABLES.price_customs.toString()+" WHERE uuid='%s';",shopItemUUID.toString()));
		ps.executeUpdate();
		
		con.close();
		ps.close();
	}
	
	void SavePriceCustom(UUID shopItemUUID, PriceCustom pc) throws SQLException
	{
		Connection con = _main.GetSQL().GetConnection();
		PreparedStatement ps = con.prepareStatement("INSERT INTO "+SQL_TABLES.price_customs.toString()+" "
				+ "(uuid, amount, itemstack) VALUES (?,?,?)");
		for(CustomPriceData item : pc.GetItems())
		{
			_main.getLogger().info("saving custom item: "+item._stack.getType());
			
			ps.setString(1, shopItemUUID.toString());
			ps.setInt(2, item._amount);
			ps.setString(3, ImusAPI._metods.EncodeItemStack(item._stack));
			ps.addBatch();
			
		}
		
		ps.executeBatch();
		ps.close();
		
		if(pc.GetPrice() > 0)
		{
			SavePriceValue(shopItemUUID, pc.GetPrice());
		}
		
		ps = con.prepareStatement("REPLACE INTO "+SQL_TABLES.custom_price.toString()+" "
				+ "(uuid, minimum_stack_amount) VALUES (?,?)");
		ps.setString(1, shopItemUUID.toString());
		ps.setInt(2, pc.GetMinimumStackAmount());
		ps.executeUpdate();
		
		con.close();
		ps.close();
		
	}
	
	public BukkitTask DeleteShopAsync(ShopBase shop)
	{
		if(_main.GetSQL() == null)
			return null;
		return new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement(""
						+ "DELETE FROM "+SQL_TABLES.shops.toString()+" WHERE uuid='"+shop.GetUUID().toString()+"'");)
				{	
					ps.executeUpdate();
									
				} catch (Exception e) 
				{
					Bukkit.getLogger().info("ShopManagerSQL:DeleteShopAsync:TRY TO DELETE SHOP NAMED "+shop.GetName()+".. something went wrong!");
					//e.printStackTrace();
				}
				
				
				DeleteAllShopItems(shop);
				
			}
		}.runTaskAsynchronously(_main);
		
		
	}
	
	public void DeleteShopItem(ShopItemBase sib)
	{
		
		if(sib == null) return;
		
		try {
			Connection con = _main.GetSQL().GetConnection();
			PreparedStatement ps = con.prepareStatement(""
					+ "DELETE FROM "+SQL_TABLES.shopitems.toString()+" WHERE uuid='"+sib.GetUUID()+"'");			
			ps.executeUpdate();

			ps = con.prepareStatement(""
					+ "DELETE FROM "+SQL_TABLES.shopitem_locations.toString()+" WHERE uuid='"+sib.GetUUID()+"';");			
			ps.executeUpdate();

			
			ps = con.prepareStatement(""
					+ "DELETE FROM "+SQL_TABLES.shopitem_permissions.toString()+" WHERE uuid='"+sib.GetUUID()+"';");			
			ps.executeUpdate();

			
			ps = con.prepareStatement(""
					+ "DELETE FROM "+SQL_TABLES.shopitem_worlds.toString()+" WHERE uuid='"+sib.GetUUID()+"';");			
			ps.executeUpdate();

			
			ps = con.prepareStatement(""
					+ "DELETE FROM "+SQL_TABLES.price_customs.toString()+" WHERE uuid='"+sib.GetUUID()+"';");			
			ps.executeUpdate();

			

		

			ps = con.prepareStatement(String.format("DELETE FROM "+SQL_TABLES.tags_shopitems.toString()+" WHERE sib_uuid='%s';",sib.GetUUID().toString()));
			ps.executeUpdate();
			ps.close();
			
			con.close();
			
			//System.out.println("sib deleted: "+sib.GetDisplayItem().getType());
		} 
		catch (Exception e) 
		{
			Bukkit.getLogger().info("ShopManagerSQL:DeleteShopItem:Error happend deleting shopitem: "+sib.GetItemType()+"");
		}
		
		try 
		{
			RemovePriceCustom(sib.GetUUID());
			RemovePriceValue(sib.GetUUID());
		} 
		catch (Exception e) {
			System.out.println("shoppi errori");
		}
		
		
		
	}
	void DeleteAllShopItems(ShopBase shop)
	{
		
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_TABLES.shopitems+" WHERE shop_uuid='"+shop.GetUUID().toString()+"';");) 
		{
			ps.executeUpdate();
		} catch (Exception e) 
		{
			Bukkit.getLogger().info("ShopManagerSQL:DeleteAllShopItems: ERROR");
		}
		
		for (ShopItemSeller[] siss : shop.get_items()) 
		{
			for(int slot = 0; slot < siss.length; ++slot)
			{
				DeleteShopItem(siss[slot]);
			}
		}
	}
	public BukkitTask SaveShopAsync(ShopBase shop)
	{
		//boolean lock = shop.HasLocked();
		//shop.SetLocked(true);
		
		return new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				shop._temp_lock = true;
				
				DeleteAllShopItems(shop);
				
				
				try(PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO "+SQL_TABLES.shops.toString()+" "
						+ "(uuid, name, display_name, pages,shop_type, sellM, buyM, expire_percent, expire_cooldown, locked, customer_can_sell,absolutePositions)"
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)");) 
				{
					_main.getLogger().info("Saving shop: "+shop.GetName());
					
					ps.setString(1, shop.GetUUID().toString());
					ps.setString(2, shop.GetName());
					ps.setString(3, shop.GetDisplayName());
					ps.setInt	(4, shop.get_items().size());
					ps.setString(5, "1");
					ps.setFloat	(6, (float)shop.get_sellM());
					ps.setFloat	(7, (float)shop.get_buyM());
					ps.setFloat	(8, (float)shop.get_expire_percent());
					ps.setInt	(9, shop.get_expire_cooldown_m());		
					ps.setInt	(10, (shop.HasLocked() ? 1 : 0));		
					ps.setInt	(11, (shop.GetCustomersCanOnlyBuy() ? 1 : 0));		
					ps.setInt	(12, (shop.GetAbsolutePosBool() ? 1 : 0));		
					ps.executeUpdate();
					_main.getLogger().info("===> Data saved database");
					
					_main.getLogger().info("=====> Data saved to shop array");
					
				} catch (Exception e) 
				{
					Bukkit.getLogger().info("ShopManagerSQL:SaveShopAsync:Couldnt save shop data, probably SQL's shops table is missing");
					//e.printStackTrace();
				}
				
				int page = 0;
				for (ShopItemSeller[] siss : shop.get_items()) 
				{
					for(int slot = 0; slot < siss.length; ++slot)
					{
						ShopItemSeller sis = siss[slot];
						
						if(sis == null)
						{												
							continue;
						}
						
						SaveShopItem(sis, page, slot);
						
					}
					page++;
				}
				
				_main.GetTagManager().LoadAllShopItemTagsNamesAsync();
				shop._temp_lock = false;
			}
		}.runTaskAsynchronously(_main);

	}

	public void SaveShopItem(ShopItemSeller sis, int page, int slot)
	{
		if(sis.GetShop() == null)
			return;
		
		int i = 1;
		DeleteShopItem(sis);
		
		try 
		{
			Connection con = _main.GetSQL().GetConnection();
			PreparedStatement ps = con.prepareStatement("REPLACE INTO "+SQL_TABLES.shopitems.toString()+" "
					+ "(uuid, shop_uuid, type , item_display_name, amount, page, slot, price_type, max_amount, fill_amount, fill_delay, selltime_start, selltime_end,type_data, itemstack) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
			
			ps.setString(i++, sis.GetUUID().toString());
			ps.setString(i++, sis.GetShop().GetUUID().toString());
			ps.setString(i++, sis.GetItemType().toString());
			ps.setString(i++, ImusAPI._metods.GetItemDisplayName(sis.GetRealItem()));
			ps.setInt(i++, sis.Get_amount());
			ps.setInt(i++, page);
			ps.setInt(i++, slot);
			
			ItemPriceType priceType = ItemPriceType.None;
			int max_amount = -1;
			int fill_amount = -1;
			int fill_delay = -1;
			int sellTimeStart = -1;
			int sellTimeEnd = -1;
			if(sis instanceof ShopItemStockable)
			{
				ShopItemModData modData = ((ShopItemStockable)sis).GetModData(); 
				//ownPrice = modData._itemPrice.getClass().equals(PriceOwn.class) ? modData._itemPrice.GetPrice() : -1 ;
				max_amount = modData._maxAmount;
				fill_amount = modData._fillAmount;
				fill_delay = modData._fillDelayMinutes;
				sellTimeStart = modData._sellTimeStart;
				sellTimeEnd = modData._sellTimeEnd;
				
//				ps2 = _main.GetSQL().GetConnection().prepareStatement(String.format("DELETE FROM "+SQL_TABLES.shopitem_permissions.toString()+" WHERE uuid='%s';",sis.GetUUID().toString()));
//				ps2.executeUpdate();
				PreparedStatement ps2;
				if(modData._permissions != null)
				{
					ps2 = con.prepareStatement("INSERT INTO "+SQL_TABLES.shopitem_permissions.toString()+" "
							+ "(uuid, name) VALUES (?,?)");
					for(String permission : modData._permissions)
					{
						
						ps2.setString(1, sis.GetUUID().toString());
						ps2.setString(2, permission);
						ps2.addBatch();
					}
					ps2.executeBatch();
					ps2.close();
				}
				
//				ps2 = _main.GetSQL().GetConnection().prepareStatement(String.format("DELETE FROM "+SQL_TABLES.shopitem_worlds.toString()+" WHERE uuid='%s';",sis.GetUUID().toString()));
//				ps2.executeUpdate();
				if(modData._worldNames != null)
				{
					ps2 = con.prepareStatement("INSERT INTO "+SQL_TABLES.shopitem_worlds.toString()+" "
							+ "(uuid, name) VALUES (?,?)");
					for(String worldName : modData._worldNames)
					{
						
						ps2.setString(1, sis.GetUUID().toString());
						ps2.setString(2, worldName);
						ps2.addBatch();
					}
					ps2.executeBatch();
					ps2.close();
				}
				
//				ps2 = _main.GetSQL().GetConnection().prepareStatement(String.format("DELETE FROM "+SQL_TABLES.shopitem_locations.toString()+" WHERE uuid='%s';",sis.GetUUID().toString()));
//				ps2.executeUpdate();			
				if(modData._locations != null)
				{
					ps2 = con.prepareStatement("INSERT INTO "+SQL_TABLES.shopitem_locations.toString()+" "
							+ "(uuid, distance, dis_world, dis_locX, dis_locY, dis_locZ) VALUES (?,?,?,?,?,?)");
					for(Tuple<Integer, Location> disLoc : modData._locations)
					{
						int l = 1;
						
						Location loc = disLoc.GetValue();
						ps2.setString(l++, sis.GetUUID().toString());					
						ps2.setInt(l++, disLoc.GetKey());
						ps2.setString(l++, loc.getWorld().getName());
						ps2.setInt(l++, loc.getBlockX());
						ps2.setInt(l++, loc.getBlockY());
						ps2.setInt(l++, loc.getBlockZ());
						ps2.addBatch();
					}
					ps2.executeBatch();
					ps2.close();
				}
				
				if(!sis.GetTags().isEmpty())
				{
					_main.GetTagManager().SaveTagsAsync(sis);
						
				}
				
			}
			
			if(sis.GetItemPrice().getClass().equals(PriceCustom.class))
			{
				priceType = ItemPriceType.PriceCustom;
				//_main.getLogger().info("item: "+sis.GetDisplayItem().getType() + " contains custom price");
				SavePriceCustom(sis.GetUUID(), ((PriceCustom)sis.GetItemPrice()));
				
			}
			
			if(sis.GetItemPrice().getClass().equals(PriceOwn.class))
			{
				priceType = ItemPriceType.PriceOwn;
				//_main.getLogger().info("item: "+sis.GetDisplayItem().getType() + " contains PriceOwn price");
				SavePriceValue(sis.GetUUID(), sis.GetItemPrice().GetPrice());
			}
			
			if(sis.GetItemPrice().getClass().equals(PriceUnique.class))
			{
				priceType = ItemPriceType.PriceUnique;
				//_main.getLogger().info("item: "+sis.GetDisplayItem().getType() + " contains PriceOwn price");
				//SavePriceValue(sis.GetUUID(), sis.GetItemPrice().GetPrice());
			}
			
			//ps.setFloat(i++, (sis.GetItemPrice() instanceof PriceOwn) ? (float)((PriceOwn)sis.GetItemPrice()).GetPrice() : -1.0f);
			ps.setString(i++, priceType.toString());
			ps.setInt(i++, max_amount);
			ps.setInt(i++, fill_amount);
			ps.setInt(i++, fill_delay);
			ps.setInt(i++, sellTimeStart);
			ps.setInt(i++, sellTimeEnd);
			
			ps.setString(i++, new Gson().toJson(sis.GetJsonData()));
			ps.setString(i++, ImusAPI._metods.EncodeItemStack(sis.GetRealItem()));			
			ps.executeUpdate();
			
			con.close();
			ps.close();
			
		
			
			
		} 
		catch (Exception e) 
		{
			//e.printStackTrace();
			Bukkit.getLogger().info("ShopManagerSQL:SaveShopItem:Saving shopitem: Couldnt add item");
		}
		
	}

	BukkitTask SaveUniqueItemAsync(ShopItemUnique siu)
	{
		return new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				
				if(siu.GetRealItem().getType() == Material.AIR) return;
				
				
				try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO uniques "
						+ "(uuid, item_display_name, price, itemstack)"
						+ "VALUES (?, ?, ?, ?)");)
				{
					
					
					int i = 1;
					ps.setString(i++, siu.GetUUID().toString());
					ps.setString(i++, ImusAPI._metods.GetItemDisplayName(siu.GetRealItem()));
					ps.setFloat(i++, (float)siu.GetItemPrice().GetPrice());
					ps.setString(i++, ImusAPI._metods.EncodeItemStack(siu.GetRealItem()));
					ps.executeUpdate();
				} 
				catch (Exception e) 
				{
					Bukkit.getLogger().info("ShopManagerSQL:SaveUniqueItemAsync:Couldnt save unique");
					//e.printStackTrace();
				}	
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	public BukkitTask DeleteUniqueItemAsync(ShopItemBase sib)
	{
		return new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM uniques WHERE uuid='"+sib.GetUUID().toString()+"';");)
				{					
					ps.executeUpdate();
				} 
				catch (Exception e) 
				{
					Bukkit.getLogger().info("ShopManagerSQL:DeleteUniqueItemAsync:Couldnt save unique");
					//e.printStackTrace();
				}	
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	private PriceUnique GetUniquePrice(UUID uuid)
	{
		PriceUnique priceUnique  = new PriceUnique();
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM uniques WHERE uuid='"+uuid.toString()+"';");)
		{
			//_main.getLogger().info("getting unique price");
			
			ResultSet rs = ps.executeQuery();
			if(rs.isBeforeFirst())
			{
				rs.next();
				//_main.getLogger().info("rs: "+rs.getDouble(3));
				double price = (double)rs.getFloat(3);
				priceUnique.SetPrice(price);
			}
		} 
		catch (Exception e) 
		{
			Bukkit.getLogger().info("ShopManagerSQL:GetUniquePrice:Couldnt get unique price");
			//e.printStackTrace();
		}	
		return priceUnique;
	}
	
	void LoadUniques()
	{
		if(_main.GetSQL() == null)
			return;
		
		
		try(PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM uniques");) 
		{		
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst())
			{
				//NO DATA
				_main.getLogger().info("Uniques not found!");
				return;
			}
			while(rs.next())
			{
				int i = 1;
				UUID uuid = UUID.fromString(rs.getString(i++));
				i++;
				double price = (double)rs.getFloat(i++);
				ItemStack stack = ImusAPI._metods.DecodeItemStack(rs.getString(i++));
				_main.getLogger().info("Unique loaded: "+price);
				ShopItemUnique unique = new ShopItemUnique(_main, null, stack, 1);
				unique.SetUUID(uuid);
				unique.GetItemPrice().SetPrice(price);
				_shopManager.GetUniqueManager()._uniques.put(unique.GetUUID(),unique);
				
				
			}
		} catch (SQLException e) 
		{
			Bukkit.getLogger().info("ShopManagerSQL:LoadUniques:Loading Unique ERROR");
			//e.printStackTrace();
		}
	}
}
