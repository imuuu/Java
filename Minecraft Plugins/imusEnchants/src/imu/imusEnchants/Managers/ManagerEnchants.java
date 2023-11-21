package imu.imusEnchants.Managers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Enums.ENCHANTMENT_TIER;
import imu.iAPI.Enums.ITEM_CATEGORY;
import imu.iAPI.Utilities.ImusUtilities;
import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.Enchants.EnchantedItem;
import imu.imusEnchants.Enums.MATERIAL_SLOT_RANGE;
import imu.imusEnchants.Inventories.InventoryEnchanting;
import imu.imusEnchants.main.CONSTANTS;
import imu.imusEnchants.main.ImusEnchants;

public class ManagerEnchants
{
	public static ManagerEnchants Instance;
	private ImusEnchants _main = ImusEnchants.Instance;
	
	 public static final HashSet<Integer> REDSTRICTED_SLOTS = new HashSet<>
	 (
        Arrays.asList
        (
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 3,
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 7,
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 13,
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 14,
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 15,
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 6,
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 4,
            CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 5
            
        )
    );
	 
	 public static final HashSet<Material> VALID_INVENTORY_MATERIALS = new HashSet<>
	 (
        Arrays.asList
        (
            CONSTANTS.BOOSTER_MATERIAL,
            CONSTANTS.ENCHANT_MATERIAL
            
        )
    );
	public ManagerEnchants()
	{
		Instance = this;
	}
	
	public void OpenEnchantingInventory(Player player)
	{
		new InventoryEnchanting().Open(player);
	}
	
	public static MATERIAL_SLOT_RANGE GetMaterialSlotsRange(ItemStack stack) 
	{
		return GetMaterialSlotsRange(stack.getType());
	}
	
	public boolean IsValidToEnchant(ItemStack stack)
	{
		if(!ItemUtils.IsTool(stack)) { return false; }
		
		if(!EnchantedItem.HasSlots(stack) && stack.getEnchantments().size() > 0) return false;
		
		return true;
	}
	
	public static MATERIAL_SLOT_RANGE GetMaterialSlotsRange(Material material) 
	{
	    if (material.name().contains("DIAMOND_")) 
	    {
	        return MATERIAL_SLOT_RANGE.DIAMOND;
	    } 
	    else if (material.name().contains("NETHERITE_")) 
	    {
	        return MATERIAL_SLOT_RANGE.NETHERITE;
	    } 
	    else if (material.name().contains("IRON_")) 
	    {
	        return MATERIAL_SLOT_RANGE.IRON;
	    } 
	    else if (material.name().contains("GOLDEN_")) 
	    {
	        return MATERIAL_SLOT_RANGE.GOLD;
	    }
	    else 
	    {
	        return MATERIAL_SLOT_RANGE.WOOD;
	    }
	}
	
	

}
