package imu.DontLoseItems.CustomItems;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.main.DontLoseItems;
import imu.iAPI.FastInventory.Fast_Inventory;
import imu.iAPI.FastInventory.Manager_FastInventories;

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
	
	
}
