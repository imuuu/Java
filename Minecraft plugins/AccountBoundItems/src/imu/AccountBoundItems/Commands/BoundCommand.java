package imu.AccountBoundItems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.AccountBoundItems.Interfaces.CommandInterface;
import imu.AccountBoundItems.Other.ItemABI;
import imu.AccountBoundItems.Other.ItemMetods;
 
public class BoundCommand implements CommandInterface
{
	ItemABI itemAbi = new ItemABI();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    	//System.out.println("args:" + args.length);
    	
    	double price = 0;
    	//if(args.length > 1 && args.length < 4)
    	//{
    	//	System.out.println("its 3"+args[1]);
    	//	if(args.length == 2)
    	//	{
    	//		if(itemAbi.isDigit(args[1]))
    	//		{
    	//			price = Double.parseDouble(args[1]);
    	//		}
    	//		
    	//	}
    	//	
    	//	if(args.length == 3)
    	//	{
    	//		if(price == 0)
    	//		{
    	//			if(itemAbi.isDigit(args[2]))
        //			{
        //				price = Double.parseDouble(args[2]);
        //			}
    	//		}
    	//	}
    	//	
    	//}
    	//
    	if(price == 0)
    	{
    		if(args.length > 0)
        		return false;
    	}
    	
    	System.out.println("LAPI HINTAA! "+ price);
    	
       
    	
    	Player player = (Player)sender;
    	ItemStack stack = player.getInventory().getItemInMainHand();	
    	
    	player.sendMessage("Lores printed");
    	itemAbi.printLores(stack);
    	player.sendMessage("Enchants printed");
    	itemAbi.printEnchants(stack);
    	System.out.println(itemAbi.findLoreIndex(stack, "Fortify III"));
    	
    	
        return true;
    }
 
}