package imu.GS.ShopUtl.ShopItems;

import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.Customer.ShopItemCustomer;

public class ShopItemCustomerShulkerBox extends ShopItemCustomer
{
	//ShopItemCustomer[] _shulkerContent;
	public ShopItemCustomerShulkerBox(Main main, ShopBase shopBase, Player player, ItemStack real, int amount) 
	{
		super(main, shopBase, player, real, amount);
		CalculateContent();
	}
	
	
	
	public void CalculateContent()
	{
		
		BlockStateMeta bsm =(BlockStateMeta)_real_stack.getItemMeta();
		if(bsm.getBlockState() instanceof ShulkerBox)
		{
			ShulkerBox shulker = (ShulkerBox)bsm.getBlockState();
			for(ItemStack stack : shulker.getInventory().getContents())
			{
				if(stack == null) continue;
				System.out.println("shulker content: "+stack);
			}
		}
	}

}
