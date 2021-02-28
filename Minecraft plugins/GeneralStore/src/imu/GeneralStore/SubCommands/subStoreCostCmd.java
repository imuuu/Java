package imu.GeneralStore.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.Other.Shop;
import imu.GeneralStore.main.Main;

public class subStoreCostCmd implements CommandInterface
{
	Main _main = null;
	
	public subStoreCostCmd(Main main) 
	{
		_main = main;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;

    	ItemStack stack = player.getInventory().getItemInMainHand();
  	
    	if(stack != null && stack.getType() != Material.AIR)
    	{
    		Shop tempShop = new Shop(_main, "cost",false,false);
    		ItemStack copy = new ItemStack(stack);
    		tempShop.setShopStackAmount(copy, 64);
    		Double[] prices = tempShop.calculatePriceOfItem(copy, 0, true);

    		player.sendMessage(ChatColor.GREEN+"Prices to this item in amounts: "
    				+ChatColor.DARK_PURPLE+"1: "+ChatColor.GOLD+prices[0]
    				+ChatColor.DARK_PURPLE+" 8: "+ChatColor.GOLD+prices[1]
    				+ChatColor.DARK_PURPLE+" 64: "+ChatColor.GOLD+prices[2]);
    		
//    		player.sendMessage(ChatColor.GREEN+"Prices to this item is: "
//    				+ChatColor.DARK_PURPLE+"minPrice: "+ChatColor.GOLD+prices[0]
//    				+ChatColor.DARK_PURPLE+" maxPrice: "+ChatColor.GOLD+prices[1]
//    				+ChatColor.DARK_PURPLE+" priceProsent: "+ChatColor.GOLD+prices[2]);
    		    		
    		return false;
    	}else
    	{
    		player.sendMessage(ChatColor.RED+"You don't have item in your hand!");
        }
        return false;
    }
    
   
   
}