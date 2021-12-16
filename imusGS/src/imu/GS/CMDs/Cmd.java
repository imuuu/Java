package imu.GS.CMDs;


import org.bukkit.Material;
//Imports for the base command class.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import imu.GS.Main.Main;
import imu.iAPI.Interfaces.CommandInterface;
import imu.iAPI.Main.ImusAPI;
 
public class Cmd implements CommandInterface
{
	Main _main = null;

	public Cmd(Main main)
	{
		_main = main;
	}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    	if(ImusAPI._metods.doesStrArrayCointainStr(args, "kamat"))
    	{
    		Player p  = (Player)sender;
    		Inventory inv = p.getInventory();
    		for(int i = 0; i < inv.getContents().length; ++i)
    		{
    			inv.setItem(i, new ItemStack(Material.values()[i]));
    		}
    	}
    	if(args.length > 0)
    		return false;
        
    	
    	
        return true;
    }
    
	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
 
}