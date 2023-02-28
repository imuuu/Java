package imu.DontLoseItems.other;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.DontLoseItems.CustomItems.CustomItem;
import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.iAPI.Other.Metods;

public class RarityItem extends CustomItem
{
	
	public ITEM_RARITY Rarity;
	
	public double[] Values;
	private final static String _PD_HELL_TIER = "HELL_TIER";
	
	public RarityItem(ItemStack stack, String name, ITEM_RARITY rarity, double[] values)
	{
		super(stack, name);
		
		Rarity = rarity;
		Values = values;
	}
	
	public ItemStack GetItemStack()
	{
		ItemStack stack = Stack.clone();
		Metods.setDisplayName(stack, GetColor(Rarity)+" "+Name);
		Metods._ins.setPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING, Rarity.toString());
		return stack;
	}
	
	//private abstract ItemStack GetItemStackBase;
	
	public static ITEM_RARITY GetRarity(ItemStack stack)
	{
		String data  = Metods._ins.getPersistenData(stack, _PD_HELL_TIER, PersistentDataType.STRING);
		
		if(data == null) return ITEM_RARITY.Common;
		
		return ITEM_RARITY.valueOf(data);
	}
	private String GetColor(ITEM_RARITY rarity) 
	{
	    switch (rarity) {
	        case Common:
	            return ChatColor.GRAY + rarity.toString();
	        case Uncommon:
	            return ChatColor.GREEN + rarity.toString();
	        case Rare:
	            return ChatColor.YELLOW + rarity.toString();
	        case Epic:
	            return ChatColor.LIGHT_PURPLE + rarity.toString();
	        case Mythic:
	            return ChatColor.AQUA + rarity.toString();
	        case Legendary:
	            return ChatColor.GOLD + rarity.toString();
	        case Void:
	            return ChatColor.BLACK + rarity.toString().toUpperCase();
	            
	        default:
	            return ChatColor.WHITE + rarity.toString();
	    }
	}
	
	
}
