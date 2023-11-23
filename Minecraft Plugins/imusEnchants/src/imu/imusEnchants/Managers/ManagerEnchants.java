package imu.imusEnchants.Managers;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ItemUtils;
import imu.iAPI.Utilities.ItemUtils.ToolMaterial;
import imu.imusEnchants.Enchants.EnchantedItem;
import imu.imusEnchants.Enchants.INode;
import imu.imusEnchants.Enchants.NodeBooster;
import imu.imusEnchants.Enchants.NodeDirectional;
import imu.imusEnchants.Enchants.NodeEnchant;
import imu.imusEnchants.Enums.MATERIAL_SLOT_RANGE;
import imu.imusEnchants.Inventories.InventoryEnchanting;
import imu.imusEnchants.main.CONSTANTS;
import imu.imusEnchants.main.ImusEnchants;

public class ManagerEnchants
{
	public static ManagerEnchants Instance;
	private ImusEnchants _main = ImusEnchants.Instance;

	public static final HashSet<Integer> REDSTRICTED_SLOTS = new HashSet<>(
			Arrays.asList(
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 3,
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 7,
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 13,
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 14,
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 15,
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 6,
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 4,
					CONSTANTS.ENCHANT_ROWS * CONSTANTS.ENCHANT_COLUMNS - 5

			));

//	public static final HashSet<Material> VALID_INVENTORY_MATERIALS = new HashSet<>(
//			Arrays.asList(CONSTANTS.BOOSTER_MATERIAL, CONSTANTS.ENCHANT_MATERIAL
//
//			));
	
	public static final INode[] VALID_NODES = new INode[]
			{
			   new NodeBooster(),
			   new NodeEnchant(),
			   new NodeDirectional()
			};

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

	public static ItemStack GetBooster(int power)
	{
		return NodeBooster.GetBoosterStack(power);
	}

	public INode GetNode(ItemStack stack, EnchantedItem enchantedItem)
	{
		for(INode node : VALID_NODES)
		{
			if(node.IsValidGUIitem(enchantedItem, stack))
			{
				if(node instanceof NodeBooster) return new NodeBooster();
				if(node instanceof NodeEnchant) return new NodeEnchant();
				if(node instanceof NodeDirectional) return new NodeDirectional();
			}
		}
		return null;
	}
	public boolean IsValidGUIitem(Player player, EnchantedItem enchantedItem, ItemStack stack)
	{
		
		Material material = stack.getType();
		
		for(INode node : VALID_NODES)
		{
			System.out.println("Checking valid nodes: "+node);
			if(node.IsValidGUIitem(enchantedItem, stack))
			{
				return true;
			}
		}
		
		
		if (material == CONSTANTS.ENCHANT_MATERIAL)
		{
			if (!CONSTANTS.ENABLE_MULTIPLE_SAME_ENCHANTS && enchantedItem.ContainsEnchant(stack))
			{
				if (player != null)
					player.sendMessage(Metods.msgC("&2Item has already that enchant!"));
				return false;
			}

			return true;
		}

//		if (material == CONSTANTS.BOOSTER_MATERIAL)
//		{
//			return NodeBooster.IsBooster(stack);
//		}
		return false;

	}

	public boolean IsValidToEnchant(ItemStack stack)
	{
		if (!ItemUtils.IsTool(stack))
		{
			return false;
		}

		if (!EnchantedItem.HasSlots(stack) && stack.getEnchantments().size() > 0)
			return false;

		return true;
	}

	public static MATERIAL_SLOT_RANGE GetMaterialSlotsRange(Material material)
	{
		ToolMaterial toolMat = ItemUtils.GetToolMaterial(material);

		switch (toolMat)
		{
		case DIAMOND:
			return MATERIAL_SLOT_RANGE.DIAMOND;
		case GOLDEN:
			return MATERIAL_SLOT_RANGE.GOLD;
		case IRON:
			return MATERIAL_SLOT_RANGE.IRON;
		case NETHERITE:
			return MATERIAL_SLOT_RANGE.NETHERITE;
		case OTHER:
		case STONE:
			return MATERIAL_SLOT_RANGE.STONE;
		case WOODEN:
			return MATERIAL_SLOT_RANGE.WOOD;
		}
		return MATERIAL_SLOT_RANGE.WOOD;

	}

}
