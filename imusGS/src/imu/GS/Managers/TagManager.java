package imu.GS.Managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.ENUMs.SQL_TABLES;
import imu.GS.ENUMs.TagSubCmds;
import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;

public class TagManager 
{
	Main _main;
	private HashMap<Material, HashSet<String>> _tags_material = new HashMap<>();
	//private HashMap<UUID, ArrayList<String>> _tags_shopItems = new HashMap<>();
	
	public TagManager(Main main)
	{
		_main = main;
	}
	
	
	public String[] GetAllMaterialTags()
	{
		Set<String> newSet = new HashSet<>();
		for(HashSet<String> set : _tags_material.values())
		{
			for(String tag : set)
			{
				newSet.add(tag);
			}
		}
		
		return newSet.stream().toArray(String[] :: new);
	}
	public void UpdateTabList()
	{
		_main.get_tab_cmd1().SetRule("/gs tag "+TagSubCmds.increase_price, 3,  Arrays.asList(GetAllMaterialTags()));
		_main.get_tab_cmd1().SetRule("/gs tag "+TagSubCmds.set_price, 3,  Arrays.asList(GetAllMaterialTags()));
	}
	
	public ArrayList<Material> GetAllMaterialsWithTag(String tagName)
	{
		ArrayList<Material> mats = new ArrayList<>();
		for(Map.Entry<Material, HashSet<String>> entry : _tags_material.entrySet())
		{
			if(entry.getValue().contains(tagName.toLowerCase())) mats.add(entry.getKey());
		}
		
		return mats;
	}
	
	public void LoadTagsAsync()
	{
		new BukkitRunnable() {			
			@Override
			public void run() 
			{
				try {
					PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_TABLES.tags_material);
					ResultSet rs = ps.executeQuery();
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							Material mat = Material.getMaterial(rs.getString(2));
							String tagName = rs.getString(3).toLowerCase();
							if(!_tags_material.containsKey(mat)) _tags_material.put(mat, new HashSet<>());
							_tags_material.get(mat).add(tagName);
							//System.out.println("adding tag "+tagName+" to material: "+mat);
						}
					}
					
					ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_TABLES.tags_shopitems);
					rs = ps.executeQuery();
					
					if(rs.isBeforeFirst())
					{
						while(rs.next())
						{
							UUID sib_uuid = UUID.fromString(rs.getString(2));
							String tagName = rs.getString(3).toLowerCase();
							FindAndAddTag(sib_uuid, tagName);
							System.out.println("adding tag "+tagName+" to sib: "+sib_uuid);
						}
					}
					UpdateTabList();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(_main);
		
		
	}
	
	
	public void SaveTagAsync(Material mat, String tagName)
	{
		new BukkitRunnable() {			
			@Override
			public void run() 
			{
				PreparedStatement ps;
				try {
					
					if(!AddTag(mat, tagName)) return;
					
					ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO "+SQL_TABLES.tags_material.toString()+" "
							+ "(material_name, tag_name) VALUES (?,?)");
					ps.setString(1, mat.name());
					ps.setString(2, tagName);
					ps.executeUpdate();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				UpdateTabList();
			}
		}.runTaskAsynchronously(_main);
		
		
	}
	
	public void SaveTagAsync(ShopItemBase sib, String tagName)
	{
		new BukkitRunnable() {			
			@Override
			public void run() 
			{
				PreparedStatement ps;
				try {
					
					//if(!AddTag(sib, tagName)) return;
					sib.AddTag(tagName);
					ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO "+SQL_TABLES.tags_shopitems.toString()+" "
							+ "(sib_uuid, tag_name) VALUES (?,?)");
					ps.setString(1, sib.GetUUID().toString());
					ps.setString(2, tagName);
					ps.executeUpdate();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(_main);
	}
	
	boolean AddTag(Material mat, String tagName)
	{
		if(!_tags_material.containsKey(mat)) 
		{
			_tags_material.put(mat, new HashSet<>());
		}
		
		for(String tag : _tags_material.get(mat)) { if(tag.equalsIgnoreCase(tagName)) {return false;}}
		_tags_material.get(mat).add(tagName);
		return true;
	}
	
	public boolean FindAndAddTag(UUID sib_uuid, String tagName)
	{
//		if(!_tags_shopItems.containsKey(sib.GetUUID())) _tags_shopItems.put(sib.GetUUID(), new ArrayList<String>());
//		for(String tag : _tags_shopItems.get(sib.GetUUID())) { if(tag.equalsIgnoreCase(tagName)) {return false;}}
//		_tags_shopItems.get(sib.GetUUID()).add(tagName);
		
		for(ShopBase shop : _main.get_shopManager().GetShops())
		{
			for(ShopItemBase[] shopItemPages : shop.get_items())
			{
				for(ShopItemBase shopItem : shopItemPages)
				{
					if(shopItem.GetUUID().equals(sib_uuid))
					{
						if(!shopItem.AddTag(tagName)) return false;
						return true;
					}
				}
			}
		}
		return true;
		
		
	}
	
	
	public void RemoveTagAsync(Material mat, String tagName)
	{
		
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				PreparedStatement ps;
				_tags_material.get(mat).remove(tagName.toLowerCase());
								
				try {
					String quarry  ="DELETE FROM "+SQL_TABLES.tags_material.toString()+" WHERE material_name='"+mat.toString()+"' AND tag_name='"+tagName.toLowerCase()+"';";
					//System.out.println("Print quarry: "+quarry);
					ps = _main.GetSQL().GetConnection().prepareStatement(quarry);
					ps.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				UpdateTabList();
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	public void RemoveTagAsync(ShopItemBase sib, String tagName)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				PreparedStatement ps;
				sib.RemoveTag(tagName);
				try {
					ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_TABLES.tags_shopitems.toString()+" WHERE sib_uuid='"+sib.GetUUID().toString()+"' AND tag_name='"+tagName.toLowerCase()+"';");
					ps.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void RemoveAllAsync(Material mat)
	{	
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				_tags_material.put(mat, new HashSet<>());
				PreparedStatement ps;
								
				try {
					ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_TABLES.tags_material.toString()+" WHERE material_name='"+mat.toString()+"';");
					ps.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				UpdateTabList();
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void RemoveAllAsync(ShopItemBase sib)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				PreparedStatement ps;
				sib.ClearTags();
				try {
					ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_TABLES.tags_shopitems.toString()+" WHERE sib_uuid='"+sib.GetUUID().toString()+"';");
					ps.executeUpdate();
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				
			}
		}.runTaskAsynchronously(_main);
	}
	
	public boolean HasTag(Material mat, String tagName)
	{
		if(_tags_material.containsKey(mat))
			if(_tags_material.get(mat).contains(tagName.toLowerCase())) return true;
		return false;
	}
	
	public boolean HasTag(ItemStack stack, String tagName)
	{
		return HasTag(stack.getType(), tagName);
	}
	
	public boolean HasTag(ShopItemBase sib, String tagName)
	{
		if(sib.HasTag(tagName)) return true;
		return false;
	}
	
	
}
