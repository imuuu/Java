package imu.DontLoseItems.CustomItems.RarityItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.other.RarityItem;

public class Hell_Double_Sword extends RarityItem
{
	
	public Hell_Double_Sword( ITEM_RARITY rarity, double[] values)
	{
		super(GetBaseItemStack(rarity), ChatColor.DARK_RED + "Hell Double Sword", rarity, values);
		
	}
	
	private static ItemStack GetBaseItemStack(ITEM_RARITY rarity)
	{
		switch (rarity)
		{
			case Common: 	return new ItemStack(Material.WOODEN_SWORD);
			case Uncommon: 	return new ItemStack(Material.WOODEN_SWORD);
			case Rare: 		return new ItemStack(Material.WOODEN_SWORD);
			case Epic: 		return new ItemStack(Material.IRON_SWORD);
			case Mythic: 	return new ItemStack(Material.DIAMOND_SWORD);
			case Legendary: return new ItemStack(Material.NETHERITE_SWORD);
		}
		
		return new ItemStack(Material.STONE);
	}
	public int GetThrowDistance()
	{
		switch (Rarity)
		{
		case Epic: return 3;
		case Mythic: return 8;
		case Legendary: return 20;
		default: return 0;
		
		}
	}
	
	public void OnStackOnBlock()
	{
		
	}
	
	
		


}
