package imu.imusEnchants.Managers;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.imusEnchants.Enums.MATERIAL_SLOT_RANGE;
import imu.imusEnchants.Inventories.InventoryEnchanting;
import imu.imusEnchants.main.CONSTANTS;
import imu.imusEnchants.main.ImusEnchants;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;

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
