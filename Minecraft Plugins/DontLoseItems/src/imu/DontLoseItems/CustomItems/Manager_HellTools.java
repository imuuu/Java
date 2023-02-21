package imu.DontLoseItems.CustomItems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import imu.DontLoseItems.Enums.ITEM_RARITY;

public class Manager_HellTools implements Listener
{
	public static Manager_HellTools Instance;

	public Hell_Pickaxe_Controller HellPickController;
	public Hell_Sword_Controller HellSword_Controller;
	public Hell_ThrowingAxe_Controller HellAxe_Controller;
	public Hell_Hoe_Controller HellHoe_Controller;
	
	public Manager_HellTools() 
	{
		Instance = this;
		HellPickController = new Hell_Pickaxe_Controller();
		HellSword_Controller = new Hell_Sword_Controller();
		HellAxe_Controller = new Hell_ThrowingAxe_Controller();
		HellHoe_Controller = new Hell_Hoe_Controller();
	}
	
	public ItemStack CreateHellPickaxe(ITEM_RARITY rarity)
	{
		return HellPickController.CreateHellPickaxe(rarity);
	}
	public ItemStack CreateHellTripleSword(ITEM_RARITY rarity)
	{
		return HellSword_Controller.CreateHellSword(rarity);
	}
	
	public ItemStack CreateHellDoubleAxe(ITEM_RARITY rarity)
	{
		return HellAxe_Controller.CreateHellAxe(rarity);
	}
	
	public ItemStack CreateHellHoe(ITEM_RARITY rarity)
	{
		return HellHoe_Controller.CreateHellHoe(rarity);
	}
	
	public void OnDisable()
	{
		HellSword_Controller.OnDisable();
		HellAxe_Controller.OnDisable();
		HellHoe_Controller.OnDisable();
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
