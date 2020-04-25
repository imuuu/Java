package imu.GeneralStore.Commands;


//Imports for the base command class.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.Other.ItemMetods;
import imu.GeneralStore.main.Main;
import net.md_5.bungee.api.ChatColor;
 
public class GeneralStoreCmd implements CommandInterface
{
	Main _main = Main.getInstance();
	ItemMetods itemM=new ItemMetods();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    	if(!_main.getShopManager().isReady())
    	{
    		sender.sendMessage(ChatColor.DARK_RED+"You can't use that right now");
    		return true;
    	}
    	
    	if(args.length > 0)
    		return false;
        
    	
    	Player p = (Player) sender;

        p.sendMessage("General Store123!");
        ItemStack stack = p.getInventory().getItemInMainHand();
        //ItemStack stack2 = p.getInventory().getItemInOffHand();
        System.out.println("STACK: "+stack);
        //if(itemM.giveDamage(stack,100,true))
		//{
        //	System.out.println("Prosent: "+itemM.getDurabilityProsent(stack));
		//}else
		//{
		//	p.sendMessage("Not valid item");
		//}
        itemM.setDamage(stack, 69);
        System.out.println("Stack: "+stack);
        //_main.getShopManager().getUniqueItemPrice(stack);
        //Shop shop = new Shop("plasd", false);
        //System.out.println(itemM.isSameStack(stack, stack2));
        //itemM.printHashMap(_main.shopManager.smart_prices);
        //itemM.printArray("TOTAL", shop.getSmartPrice(stack,false,0));
        //Double[] first = {0.0, 0.0, 0.0};
        //Double[] prices = shop.materialNEWPrices(stack, first, 0);
        
        //itemM.printArray("VALUES:", prices);
        //itemM.printHashMap(_main.shopManager.smart_prices);
        
        return true;
    }
 
}