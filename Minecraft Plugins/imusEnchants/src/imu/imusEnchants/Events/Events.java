package imu.imusEnchants.Events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.Enchants.EnchantedItem;
import imu.imusEnchants.Enchants.NodeBooster;
import imu.imusEnchants.Enums.CALCULATION_MODE;
import imu.imusEnchants.Managers.ManagerEnchants;
import imu.imusEnchants.main.CONSTANTS;
import imu.imusEnchants.main.ImusEnchants;

public class Events implements Listener
{
	Plugin _plugin;

	public Events()
	{
		_plugin = ImusEnchants.Instance;

	}

	@EventHandler
	public void OnCraftItem(CraftItemEvent event)
	{
		System.out.println("crafting");
		ItemStack result = event.getCurrentItem();
		if (result == null)
			return;

		System.out.println("crafting: " + result);
		if (!ItemUtils.IsTool(result))
			return;

		System.out.println("enchant item: ");
		EnchantedItem eItem = new EnchantedItem(result);
		eItem.SetTooltip();
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Block clickedBlock = event.getClickedBlock();

			if (clickedBlock != null && clickedBlock.getType() == Material.ENCHANTING_TABLE)
			{
				Player player = event.getPlayer();
				event.setCancelled(true);
				ManagerEnchants.Instance.OpenEnchantingInventory(player);
			}
		}
	}

	@EventHandler
	public void OnPrepareAnvil(PrepareAnvilEvent event)
	{
		AnvilInventory inv = event.getInventory();

		if (!ItemUtils.IsTool(inv.getItem(0)))
			return;

		if (!ItemUtils.IsTool(inv.getItem(1)))
			return;

		ItemStack stack1 = inv.getItem(0);
		ItemStack stack2 = inv.getItem(1);

		if (EnchantedItem.HasSlots(stack1) || EnchantedItem.HasSlots(stack2))
		{
			event.setResult(new ItemStack(Material.AIR));
		}
	}
	
	@EventHandler
	public void OnSmithing(PrepareSmithingEvent event)
	{
		SmithingInventory inv = event.getInventory();
		if (NodeBooster.IsBooster(inv.getItem(0)))
		{
			event.getViewers().get(0).sendMessage(Metods.msgC("&cBoosters can't be use in smithing table"));
			event.getViewers().get(0).closeInventory();
			return;
		}

		if (!ItemUtils.IsTool(inv.getItem(3)) && !ItemUtils.IsArmor(inv.getItem(3)))
			return;

		ItemStack stack = inv.getItem(3);

		if (!EnchantedItem.HasSlots(stack))
			return;

		EnchantedItem eItem = new EnchantedItem(stack);
		EnchantedItem.SetUpgraded(stack, true);
		eItem.SetTooltip();

		event.setResult(stack);
	}

	@EventHandler
	public void OnGrindstone(InventoryClickEvent event)
	{
		if (!(event.getInventory() instanceof GrindstoneInventory))
			return;

		if (event.getRawSlot() != 2)
			return;

		ItemStack item = event.getCurrentItem();
		if (!EnchantedItem.HasSlots(item))
			return;

		event.setCancelled(true);
		event.getWhoClicked().sendMessage(Metods.msgC("&cYou cannot use &6slotted &citem on a grindstone!"));
		event.getWhoClicked().closeInventory();
	}

	@EventHandler
	public void OnEntityShootBow(EntityShootBowEvent event)
	{
		if (CONSTANTS.CROSSBOW_CONSUME_ARROW_WITH_INFINITY)
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		ItemStack bow = event.getBow();
		if (bow == null || bow.getType() != Material.CROSSBOW)
			return;

		event.setConsumeItem(false);
	}
	
	@EventHandler
	public void OnCraftItemTool(PrepareItemCraftEvent event)
	{
		ItemStack[] matrix = event.getInventory().getMatrix();
		ItemStack firstItem = null;
		int sameMaterialCount = 0;
		int enchantedItems = 0;
		int slotSum = 0;
		int minSlots = Integer.MAX_VALUE;
		int maxSlots = Integer.MIN_VALUE;
		int totalItems = 0;
		for (ItemStack item : matrix)
		{
			if (item == null || item.getType() == Material.AIR)
			{
				continue;
			}

			totalItems++;
			if (!(ItemUtils.IsArmor(item) || ItemUtils.IsTool(item)))
				continue;

			if (firstItem == null)
			{
				firstItem = item;
				sameMaterialCount++;
			} 
			else if (item.getType() == firstItem.getType())
			{
				sameMaterialCount++;
			}

			Integer slots = EnchantedItem.GetSlots(item);
			if (slots <= 0)
				continue;

			enchantedItems++;
			slotSum += slots;
			minSlots = Math.min(slots, minSlots);
			maxSlots = Math.max(slots, maxSlots);
		}

		if (sameMaterialCount == 2)
		{
			event.getInventory().setResult(new ItemStack(Material.AIR));
			return;
		}

		CALCULATION_MODE mode = CONSTANTS.CRAFTING_SLOT_CALCULATION;

		if (sameMaterialCount == 3 && totalItems == 3 && enchantedItems == 3)
		{
			ItemStack stack = new ItemStack(firstItem.getType());
			EnchantedItem eItem = new EnchantedItem(stack);

			switch (mode)
			{
			case MIN:
				eItem.SetSlots(minSlots);
				break;
			case MAX:
				eItem.SetSlots(maxSlots);
				break;
			case AVERAGE:
				int averageSlots = sameMaterialCount > 0 ? slotSum / sameMaterialCount : 0;
				eItem.SetSlots(averageSlots);
				break;
			}

			eItem.SetTooltip();
			event.getInventory().setResult(stack);
		}

	}

}
