package imu.iAPI.Interfaces;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface IBUTTONN
{
	public ItemStack GetItemStack();
	public void SetItemStack(ItemStack stack);
	
	public void SetMaxStackAmount(int amount);
	public int GetMaxStackAmount();
	public int GetPosition();
	public boolean IsPositionLocked();
	public void SetLockPosition(boolean lockPostion);
	//void OnClick(Player whoClicked, ClickType clickType);
	
	public void OnUpdate();
	public void Action(InventoryClickEvent event);
}
