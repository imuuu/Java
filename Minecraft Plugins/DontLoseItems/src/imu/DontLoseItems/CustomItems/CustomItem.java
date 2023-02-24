package imu.DontLoseItems.CustomItems;

import org.bukkit.inventory.ItemStack;

public abstract class CustomItem
{
	public ItemStack Stack;
	public String Name;
	
	public CustomItem(ItemStack stack, String name)
	{
		this.Stack = stack;
		this.Name = name;
	}
	
	public abstract ItemStack GetItemStack();
}
