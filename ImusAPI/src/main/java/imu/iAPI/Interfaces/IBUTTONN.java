package imu.iAPI.Interfaces;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface IBUTTONN
{
	public ItemStack getItemStack();
	public void setItemStack(ItemStack stack);
	
	public void setMaxStackAmount(int amount);
	public int getMaxStackAmount();
	public int getPosition();
	public boolean isPositionLocked();
	public void setLockPosition(boolean lockPostion);
	//void OnClick(Player whoClicked, ClickType clickType);
	
	public void onUpdate();
	public void action(InventoryClickEvent event);
	
	public void setLastClickType(ClickType clickType);
	public ClickType getLastClickType();
}
