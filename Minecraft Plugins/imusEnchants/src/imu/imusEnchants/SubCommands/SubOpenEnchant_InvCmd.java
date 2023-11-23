package imu.imusEnchants.SubCommands;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.iAPI.CmdUtil.CmdData;
import imu.iAPI.Interfaces.CommandInterface;
import imu.imusEnchants.Inventories.TestINv;
import imu.imusEnchants.Managers.ManagerEnchants;

public class SubOpenEnchant_InvCmd implements CommandInterface
{
	CmdData _data;

	public SubOpenEnchant_InvCmd(CmdData data) 
	{
		_data = data;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
    	//CardManager.Instance.OpenCreateCardInv((Player)sender);

    	//ManagerEnchants.Instance.OpenEnchantingInventory((Player)sender);
    	
    	new TestINv().Open((Player)sender);
		sender.sendMessage("Opening INV");
        return false;
    }
    
   
	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		
	}
    
   
   
}