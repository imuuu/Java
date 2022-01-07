package imu.GS.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.ENUMs.SQL_TABLES;
import imu.GS.Main.Main;
import imu.GS.Other.EnchantINFO;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;
import imu.iAPI.Other.Metods;

public class ShopEnchantManager 
{
	
	private double _def_rawMultiplier = 0.3f;
	HashMap<Enchantment, EnchantINFO> _enchantInfos = new HashMap<>();
		
	Main _main;
	public ShopEnchantManager(Main main) 
	{
		_main = main;
	}
	
	public EnchantINFO GetInfo(Enchantment ench)
	{
		if(!_enchantInfos.containsKey(ench)) _enchantInfos.put(ench, new EnchantINFO(ench, 0, ench.getMaxLevel(), _def_rawMultiplier));
		
		return _enchantInfos.get(ench);
		
	}
	void PrintINFO(String metodName, String info)
	{
		Bukkit.getLogger().info(Metods.msgC(_main._pluginNamePrefix+":&6"+getClass().getSimpleName()+":&5"+metodName+": &2" +info));
	}
	void PrintERROR(String metodName, String info)
	{
		Bukkit.getLogger().info(Metods.msgC(_main._pluginNamePrefix+":&6"+getClass().getSimpleName()+":&5"+metodName+": &c" +info));
	}
	public void CreateTables()
	{
		LinkedList<String> statements = new LinkedList<>();
		String strS = "CREATE TABLE IF NOT EXISTS "+SQL_TABLES.enchants.toString()+" ("
				+ "name VARCHAR(50) NOT NULL,"
				+ "minlvl INT(10),"
				+ "maxlvl INT(10),"
				+ "raw_mult FLOAT(20),"
				+ "price_type VARCHAR(10),"
				+ "PRIMARY KEY(name));";
		statements.add(strS);
		
		strS = "CREATE TABLE IF NOT EXISTS "+SQL_TABLES.enchant_price_money.toString()+" ("
				+ "name VARCHAR(50) NOT NULL,"
				+ "min FLOAT(20),"
				+ "max FLOAT(20),"
				+ "PRIMARY KEY(name));";
		statements.add(strS);
		
		if(ExecuteStatements(statements, "CreateTables")) PrintINFO("CreateTables", "Loaded!");
				
	}
	boolean ExecuteStatements(List<String> statements, String metodName)
	{
		try 
		{
			Connection con = _main.GetSQL().GetConnection();
			Statement stmt = con.createStatement();
			for(String sm : statements)
			{
				//System.out.println("quarry: "+sm);
				stmt.addBatch(sm);
			}
			stmt.executeBatch();
			
			stmt.close();
			con.close();

			return true;
		} 
		catch (Exception e) 
		{
			PrintERROR(metodName, "SQL ERROR");
			e.printStackTrace();
			return false;
			
		}
	}
	
	public void LoadEnchantInfos()
	{
		try 
		{
			Connection con = _main.GetSQL().GetConnection();
			PreparedStatement ps = con.prepareStatement("SELECT enchants.name, enchants.minlvl, enchants.maxlvl, enchants.raw_mult,enchants.price_type, enchant_price_money.min, enchant_price_money.max FROM enchants "
					+ "INNER JOIN enchant_price_money ON enchants.name = enchant_price_money.name;");
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst())
			{
				PrintINFO("LoadEnchantInfos", "No Enchant prices found!");
				rs.close();
				ps.close();
				con.close();
				return;
			}
			
			int count = 0;
			while(rs.next())
			{
				int i = 1;
				EnchantINFO eInfo = new EnchantINFO(Enchantment.getByKey(NamespacedKey.minecraft(rs.getString(i++))),rs.getInt(i++) ,rs.getInt(i++), rs.getFloat(i++));
				i++; //pricetype
				eInfo.SetPrice(new PriceMoney().SetPrice(rs.getFloat(i++)), new PriceMoney().SetPrice(rs.getFloat(i++)));
				_enchantInfos.put(eInfo.GetEnchantment(), eInfo);
				count++;
			}
			
			rs.close();
			ps.close();
			con.close();
			
			PrintINFO("LoadEnchantInfos", "Enchants Loaded: "+count);
		} 
		catch 
		(Exception e) 
		{
			PrintERROR("LoadEnchantInfos", "ERROR loading");
			e.printStackTrace();
		}
	}
	
	public void SaveEnchantInfoAsync(EnchantINFO eInfo)
	{
		if(!(eInfo.GetMaxPrice() instanceof PriceMoney))
		{
			PrintERROR("SaveEnchantInfoAsync", "The price class isnt implemented to sql: class: "+eInfo.GetMaxPrice().getClass().getSimpleName());
			return;
		}
		LinkedList<String> statements = new LinkedList<>();

		statements.add("REPLACE INTO "+SQL_TABLES.enchants+" (name, minlvl, maxlvl, raw_mult, price_type) VALUES("
				+ "'"+eInfo.GetName()+"',"
				+ eInfo.GetMinLevel()+","
				+ eInfo.GetMaxLevel()+","
				+ eInfo.Get_rawMultiplier()+","
				+ "'"+eInfo.GetMaxPrice().getClass().getSimpleName()+"'"
				+ ");");
		
		
		statements.add("REPLACE INTO "+ SQL_TABLES.enchant_price_money+ " (name,min,max) VALUES("
				+ "'"+eInfo.GetName()+"',"
				+ eInfo.GetMinPrice().GetPrice()+","
				+ eInfo.GetMaxPrice().GetPrice()
				+ ");");
		
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				ExecuteStatements(statements, "SaveEnchantInfoAsync");
			}
		}.runTaskAsynchronously(_main);
	}
	
	
}
