package imu.GeneralStore.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.Other.ItemMetods;
import imu.GeneralStore.Other.Shop;
import imu.GeneralStore.main.Main;

public class subStoreCostCmd implements CommandInterface
{
	Main _main = Main.getInstance();
	ItemMetods itemM = new ItemMetods();
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;

    	ItemStack stack = player.getInventory().getItemInMainHand();
  	
    	if(stack != null && stack.getType() != Material.AIR)
    	{
    		System.out.println("here");
    		Shop tempShop = new Shop("cost",false);
    		ItemStack copy = new ItemStack(stack);
    		tempShop.setShopStackAmount(copy, 1);
    		Double[] prices = tempShop.calculatePriceOfItem(copy, 0, true);
    		player.sendMessage(ChatColor.GREEN+"Prices to this item is: "
    				+ChatColor.DARK_PURPLE+"minPrice: "+ChatColor.GOLD+prices[0]
    				+ChatColor.DARK_PURPLE+" maxPrice: "+ChatColor.GOLD+prices[1]
    				+ChatColor.DARK_PURPLE+" priceProsent: "+ChatColor.GOLD+prices[2]);
    		
    		
    		return false;
    	}else
    	{
    		player.sendMessage(ChatColor.RED+"You don't have item in your hand!");
        }
  
        //player.sendMessage("/gs cost");

        
		
        return false;
    }
    
   
   
}