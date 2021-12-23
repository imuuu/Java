package imu.iWaystones.SubCmds;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import imu.iAPI.Interfaces.CommandInterface;
import imu.iAPI.Other.Metods;
import imu.iWaystones.Main.ImusWaystones;
import imu.iWaystones.Other.CmdData;

public class SubWaystoneConfirmationCmd implements CommandInterface
{
	CmdData _data;
	public SubWaystoneConfirmationCmd(CmdData data) 
	{
		_data = data;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
    	if(args.length != 2)
    	{
    		return false;
    	}
    	
    	try 
		{
			UUID uuid = UUID.fromString(args[1]);
			ImusWaystones._instance.GetWaystoneManager().ConfirmWaystoneCreation(uuid);
			sender.sendMessage(Metods.msgC("&3Waystone has been registered"));
		} 
    	catch (Exception e) 
		{
    		Bukkit.getLogger().info("UUID incorrect!");
		}
        return false;
    }
    
   
	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		
	}
    
   
   
}