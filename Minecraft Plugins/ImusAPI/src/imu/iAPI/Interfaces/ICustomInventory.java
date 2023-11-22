package imu.iAPI.Interfaces;

import java.util.Stack;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface ICustomInventory
{	
	public void OnOpen();
	
	public void OnClose();
	
	public Inventory GetInventory();
	
	public int GetSize();
	
	public Player GetPlayer();
	
	public void Open(Player player);
	
	public Plugin GetPlugin();
	
	public boolean OnDropItem(ItemStack stack, int slot);
	public IBUTTONN OnDropItemSet(ItemStack stack, int slot);
	
	public boolean OnDragItem(ItemStack stack, int slot);
	public IBUTTONN OnDragItemSet(ItemStack stack, int slot);
	
	public boolean OnPickupAll(IBUTTONN button, int slot);
	
	public void Back();
	
	public void SetPageStack(Stack<ICustomInventory> pageStack);
	public Stack<ICustomInventory> GetPageStack();
}
