package imu.DontLoseItems.CustomItems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import imu.DontLoseItems.Enums.ITEM_RARITY;

public class Manager_HellTools implements Listener
{
	public static Manager_HellTools Instance;
	
	
	//private HashMap<UUID, MINE_DIRECTION> _playerLastMineDir;
	//private HashMap<UUID, Location[]> _minedBlocks;
	//private Cooldowns _cds;

	public Hell_Pickaxe_Controller HellPickController;
	public Hell_Sword_Controller HellSword_Controller;
	
	public Manager_HellTools() 
	{
		Instance = this;
		HellPickController = new Hell_Pickaxe_Controller();
		HellSword_Controller = new Hell_Sword_Controller();

		//_cds = new Cooldowns();
	}
	
	public ItemStack CreateHellPickaxe(ITEM_RARITY rarity)
	{
		return HellPickController.CreateHellPickaxe(rarity);
	}
	public ItemStack CreateHellDoubleSword(ITEM_RARITY rarity)
	{
		return HellSword_Controller.CreateHellSword(rarity);
	}
	
	public void OnDisable()
	{
		HellSword_Controller.OnDisable();
	}
	
	@EventHandler
	private void OnInteract(PlayerInteractEvent e)
	{
		
		HellPickController.OnBlockInteract(e);
		HellSword_Controller.OnThrow(e);
	}
	
	@EventHandler
	private void OnBlockBreak(BlockBreakEvent e)
	{
		if(e.isCancelled()) return;
		
		HellPickController.OnHellPickBlockBreak(e);
	}
	
	
}
