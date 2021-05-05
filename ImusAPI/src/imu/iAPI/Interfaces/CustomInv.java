package imu.iAPI.Interfaces;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface CustomInv 
{
	String pd_buttonType = "cButton";
	

	public void setButton(ItemStack stack, IButton b);
	
	public String getButtonName(ItemStack stack);
	
	public ItemStack setupButton(IButton b, Material material, String displayName, int itemSlot);
	
	void setupButtons();
	
	@EventHandler
	public void onClick(InventoryClickEvent e);
	
	public void onClickInsideInv(InventoryClickEvent e);
	
	@EventHandler
	public void invClose(InventoryClickEvent e);
	
	public void invClosed(InventoryClickEvent e);
	
}
