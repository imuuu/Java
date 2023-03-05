package imu.DontLoseItems.CustomItems;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.main.DontLoseItems;
import imu.DontLoseItems.other.Manager_LegendaryUpgrades;
import imu.iAPI.Other.Metods;


public class Manager_HellTools implements Listener
{
	public static Manager_HellTools Instance;

	public Hell_Pickaxe_Controller HellPickController;
	public Hell_Sword_Controller HellSword_Controller;
	public Hell_ThrowingAxe_Controller HellAxe_Controller;
	public Hell_Hoe_Controller HellHoe_Controller;
	public Hell_ReflectShieldController Hell_ReflectShield_Controller;

	@SuppressWarnings("unused")
	private BukkitTask _task;
	
	public Manager_HellTools() 
	{
		Instance = this;
		HellPickController = new Hell_Pickaxe_Controller();
		HellSword_Controller = new Hell_Sword_Controller();
		HellAxe_Controller = new Hell_ThrowingAxe_Controller();
		HellHoe_Controller = new Hell_Hoe_Controller();
		Hell_ReflectShield_Controller = new Hell_ReflectShieldController();
		
		//AddItemsToTestInv();
		Runnable();
	}
	
//	private void AddItemsToTestInv()
//	{
//		Fast_Inventory fastInv = new Fast_Inventory("Hell Tools", "Hell Inv", new ArrayList<>());
//		fastInv.AddStack(CreateHellDoubleAxe(ITEM_RARITY.Legendary));
//		Manager_FastInventories.Instance.RegisterFastInventory(fastInv);
//	}

	private void Runnable()
	{
		_task = new BukkitRunnable() 
		{				
			@Override
			public void run() 
			{
				
				//ReflectArrowController.OnReflectArrowLoop();
				Hell_ReflectShield_Controller.OnReflectArrowLoop();
			}

			
		}.runTaskTimer(DontLoseItems.Instance, 20 * 3, 1);	
	}
	
	public ItemStack CreateHellPickaxe(ITEM_RARITY rarity)
	{
		return HellPickController.CreateItem(rarity);
	}
	public ItemStack CreateHellTripleSword(ITEM_RARITY rarity)
	{
		return HellSword_Controller.CreateItem(rarity);
	}
	
	public ItemStack CreateHellDoubleAxe(ITEM_RARITY rarity)
	{
		return HellAxe_Controller.CreateItem(rarity);
	}
	
	public ItemStack CreateHellHoe(ITEM_RARITY rarity)
	{
		return HellHoe_Controller.CreateItem(rarity);
	}
	
	public ItemStack CreateHellReflectShield(ITEM_RARITY rarity)
	{
		return Hell_ReflectShield_Controller.CreateItem(rarity);
	}
	
	public boolean IsTier_Pickaxe(ItemStack stack, ITEM_RARITY rarity)
	{
		return HellPickController.IsTier(stack, rarity);
	}
	
	public boolean IsTier_Axe(ItemStack stack, ITEM_RARITY rarity)
	{
		return HellAxe_Controller.IsTier(stack, rarity);
	}
	
	public boolean IsTier_Shield(ItemStack stack, ITEM_RARITY rarity)
	{
		return Hell_ReflectShield_Controller.IsTier(stack, rarity);
	}
	
	public boolean IsTier_Sword(ItemStack stack, ITEM_RARITY rarity)
	{
		return HellSword_Controller.IsTier(stack, rarity);
	}
	
	public boolean IsTier_Hoe(ItemStack stack, ITEM_RARITY rarity)
	{
		return HellHoe_Controller.IsTier(stack, rarity);
	}
	
	
	public void OnDisable()
	{
		HellSword_Controller.OnDisable();
		HellAxe_Controller.OnDisable();
		HellHoe_Controller.OnDisable();
	}
	
	@EventHandler
	public void OnEntityDamageByEntityEvent(EntityDamageByEntityEvent e) 
	{
		if(e.isCancelled()) return;
		
		Hell_ReflectShieldController.Instance.OnBlockingArrow(e);
	}
	
	@EventHandler
	private void OnInteract(PlayerInteractEvent e)
	{
		
		if(e.getClickedBlock() != null && e.getClickedBlock().getType().isInteractable()) return;
		
		HellPickController.OnBlockInteract(e);
		HellSword_Controller.OnThrow(e);
		HellAxe_Controller.OnThrow(e);
		HellHoe_Controller.OnUse(e);
	}
	
	@EventHandler
	private void OnPlayerQuit(PlayerQuitEvent e)
	{
		HellAxe_Controller.OnPlayerQuit(e);
	}
	@EventHandler
	private void OnBlockBreak(BlockBreakEvent e)
	{
		if(e.isCancelled()) return;
		
		HellPickController.OnHellPickBlockBreak(e);
	}
	
	@EventHandler
	private void OnProjectile(ProjectileHitEvent e)
	{
		if(e.isCancelled()) return;
		
		HellAxe_Controller.OnProjectileHit(e);
	}
	
	@EventHandler
	public void Smithing(PrepareSmithingEvent e)
	{

		//if(_workInProgress) return; 
		
		SmithingInventory inv = e.getInventory();
		
		ItemStack stack1 = inv.getItem(0);
		ItemStack stack2 = inv.getItem(1);
		if(stack1 == null || stack2 == null) return;
		
		ItemStack result = null;
		if(IsTier_Axe(stack1,ITEM_RARITY.Legendary) && Manager_LegendaryUpgrades.Instance.IsUpgradeHellAxe(stack2))
		{
			result = CreateHellDoubleAxe(ITEM_RARITY.Void);
			Metods.CloneEnchantments(stack1, result);
			e.setResult(result);
			return;
		}
		
		if(IsTier_Hoe(stack1, ITEM_RARITY.Legendary) && Manager_LegendaryUpgrades.Instance.IsUpgradeHellHoe(stack2))
		{
			result = CreateHellHoe(ITEM_RARITY.Void);
			Metods.CloneEnchantments(stack1, result);
			e.setResult(result);
			return;
		}
		
		if(IsTier_Pickaxe(stack1, ITEM_RARITY.Legendary) && Manager_LegendaryUpgrades.Instance.IsUpgradeHellPickaxe(stack2))
		{
			result = CreateHellPickaxe(ITEM_RARITY.Void);
			Metods.CloneEnchantments(stack1, result);
			e.setResult(result);
			return;
		}
		
		if(IsTier_Shield(stack1, ITEM_RARITY.Legendary) && Manager_LegendaryUpgrades.Instance.IsUpgradeHellShield(stack2))
		{
			result = CreateHellReflectShield(ITEM_RARITY.Void);
			Metods.CloneEnchantments(stack1, result);
			e.setResult(result);
			return;
		}
		
		if(IsTier_Sword(stack1, ITEM_RARITY.Legendary) && Manager_LegendaryUpgrades.Instance.IsUpgradeHellSword(stack2))
		{
			result = CreateHellTripleSword(ITEM_RARITY.Void);
			Metods.CloneEnchantments(stack1, result);
			e.setResult(result);
			return;
		}
		
		

	}
	
	private void GiveVoidTool(Player player, ItemStack baseStack,ItemStack stack)
	{
		Metods.CloneEnchantments(baseStack, stack);
		Metods._ins.InventoryAddItemOrDrop(stack, player );
	}
	
	@EventHandler
	public void SmithingClick(InventoryClickEvent e)
	{
		
		if(e.isCancelled()) return;
		
		if(!(e.getInventory() instanceof SmithingInventory)) return;
		
		if(e.getSlotType() != SlotType.RESULT) return;
		
		SmithingInventory inv = (SmithingInventory)e.getInventory();
				
		if(inv.getItem(0) == null || inv.getItem(1) == null) return;
		
		boolean found = false;
		
		if(IsTier_Axe(e.getCurrentItem(),ITEM_RARITY.Void)) 		found = true;
		if(IsTier_Hoe(e.getCurrentItem(),ITEM_RARITY.Void)) 		found = true;
		if(IsTier_Pickaxe(e.getCurrentItem(),ITEM_RARITY.Void)) 	found = true;
		if(IsTier_Shield(e.getCurrentItem(),ITEM_RARITY.Void)) 		found = true;
		if(IsTier_Sword(e.getCurrentItem(),ITEM_RARITY.Void)) 		found = true;


		if(!found) return;

		GiveVoidTool((Player)e.getWhoClicked(), inv.getItem(0), e.getCurrentItem());

		inv.setItem(0, new ItemStack(Material.AIR));
		inv.setItem(1, new ItemStack(Material.AIR));
	}
	
	
}
