package imu.AccountBoundItems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import imu.AccountBoundItems.Interfaces.CommandInterface;
import imu.AccountBoundItems.Other.ItemMetods;
 
public class BoundCommand implements CommandInterface
{
	ItemMetods itemM = new ItemMetods();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    	if(args.length > 0)
    		return false;
       
        return true;
    }
 
}