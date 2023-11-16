package imu.iAPI.Interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface ICustomInventory
{
	public void OnClick(InventoryClickEvent e, IBUTTONN button);
	
	public void OnOpen();
	
	public void OnClose();
	
	public Inventory GetInventory();
	
	public int GetSize();
	
	public Player GetPlayer();
	
	public void Open(Player player);
	
	public Plugin GetPlugin();
	
	public boolean OnDropitem(ItemStack item, int slot);
	public boolean OnDragitem(ItemStack item, int slot);
	public boolean OnPickupAll(IBUTTONN button, int slot);
}
