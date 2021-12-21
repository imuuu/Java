package imu.GS.CMDs;


//Imports for the base command class.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import imu.GS.Main.Main;
import imu.iAPI.Interfaces.CommandInterface;
 
public class Cmd implements CommandInterface
{
	Main _main = null;

	public Cmd(Main main)
	{
		_main = main;
	}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    	
    	if(args.length > 0)
    		return false;
      
    	
    	
        return true;
    }
    
	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
 
}