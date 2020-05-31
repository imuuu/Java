package imu.UIC.Commands;


//Imports for the base command class.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import imu.UIC.Interfaces.CommandInterface;
import imu.UIC.main.Main;
 
public class UICCmd implements CommandInterface
{
	Main _main = null;
	public UICCmd(Main main)
	{
		_main = main;
	}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    
    	if(args.length > 0)
    		return false;
        
    	sender.sendMessage("Write /help UltimateCustomItems");
    	
    	
        return true;
    }
 
}