package imu.TokenTp.Commands;


//Imports for the base command class.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import imu.TokenTp.Interfaces.CommandInterface;
import imu.TokenTp.Other.ItemMetods;
import imu.TokenTp.main.Main;
 
public class TokenTtpTpCmd implements CommandInterface
{
	Main _main = null;
	ItemMetods _itemM = null;
	public TokenTtpTpCmd(Main main)
	{
		_main = main;
		_itemM = main.getItemM();
	}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 

    	if(args.length > 0)
    		return false;
        
    	
    	
        return true;
    }
 
}