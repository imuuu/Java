package imu.GeneralStore.Commands;


//Imports for the base command class.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.Other.ItemMetods;
import imu.GeneralStore.Other.Shop;
import imu.GeneralStore.main.Main;
 
public class GeneralStoreCmd implements CommandInterface
{
	Main _main = Main.getInstance();
	ItemMetods itemM=new ItemMetods();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    	if(args.length > 0)
    		return false;
        
    	
    	Player p = (Player) sender;

        p.sendMessage("General Store123!");
        ItemStack stack = p.getInventory().getItemInMainHand();
        System.out.println("STACK: "+stack);
        //if(itemM.giveDamage(stack,100,true))
		//{
        //	System.out.println("Prosent: "+itemM.getDurabilityProsent(stack));
		//}else
		//{
		//	p.sendMessage("Not valid item");
		//}
        System.out.println("==================================");
        System.out.println("==================================");
        System.out.println("==================================");
        Shop shop = new Shop("plasd", false);
        
        //itemM.printHashMap(_main.shopManager.smart_prices);
        
        itemM.printArray("TOTAL", shop.getSmartPrice(stack,false,0));
        //Double[] first = {0.0, 0.0, 0.0};
        //Double[] prices = shop.materialNEWPrices(stack, first, 0);
        
        //itemM.printArray("VALUES:", prices);
        //itemM.printHashMap(_main.shopManager.smart_prices);
        
        return true;
    }
 
}