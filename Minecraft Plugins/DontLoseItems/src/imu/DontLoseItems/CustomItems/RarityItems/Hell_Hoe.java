package imu.DontLoseItems.CustomItems.RarityItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.other.RarityItem;

public class Hell_Hoe extends RarityItem
{

	public Hell_Hoe( ITEM_RARITY rarity, double[] values)
	{
		super(GetBaseItemStack(rarity), ChatColor.DARK_RED + "Hell Hoe", rarity, values);
		
	}
	
	private static ItemStack GetBaseItemStack(ITEM_RARITY rarity)
	{
		switch (rarity)
		{
			case Common: 	return new ItemStack(Material.WOODEN_HOE);
			case Uncommon: 	return new ItemStack(Material.WOODEN_HOE);
			case Rare: 		return new ItemStack(Material.WOODEN_HOE);
			case Epic: 		return new ItemStack(Material.IRON_HOE);
			case Mythic: 	return new ItemStack(Material.DIAMOND_HOE);
			case Legendary: return new ItemStack(Material.NETHERITE_HOE);
		}
		
		return new ItemStack(Material.STONE);
	}
	
	public int GetSeedUsage()
	{
		switch (Rarity)
		{
		case Epic: return 3;
		case Mythic: return 2;
		case Legendary: return 1;
		default: return 0;
		
		}
	}
	
	public Material GetMaterial()
	{
		return GetBaseItemStack(Rarity).getType();
	}
	

	
	
	
	
	
	
}




