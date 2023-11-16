package imu.iAPI.Interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface IBUTTONN
{
	public ItemStack GetItemStack();
	public void SetItemStack(ItemStack stack);
	public void OnUpdate();
	
	public int GetPosition();
	public boolean IsPositionLocked();
	public void SetLockPosition(boolean lockPostion);
	void OnClick(Player whoClicked, ClickType clickType);
	
	public void Action(InventoryClickEvent event);
}
