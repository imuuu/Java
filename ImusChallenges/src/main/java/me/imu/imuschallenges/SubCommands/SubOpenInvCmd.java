package me.imu.imuschallenges.SubCommands;


import imu.iAPI.CmdUtil.CmdData;
import imu.iAPI.Interfaces.CommandInterface;
import imu.iAPI.Utilities.ImusUtilities;
import me.imu.imuschallenges.Inventories.TestInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubOpenInvCmd implements CommandInterface
{
	CmdData _data;

	public SubOpenInvCmd(CmdData data)
	{
		_data = data;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
    	//CardManager.Instance.OpenCreateCardInv((Player)sender);

    	//ManagerEnchants.Instance.OpenEnchantingInventory((Player)sender);
    	
    	//new TestINv().Open((Player)sender);
    	
    	Player player = ((Player)sender);
    	
    	//ImusUtilities.SetFakeBlock(player, Material.BEDROCK, ((Player)sender).getLocation().add(0, 3, 0));
		sender.sendMessage("Opening INV");
		new TestInventory().open(player);

		ImusUtilities.SendCenteredMessage(player, "======== HERE IS NICE DAY ========");
		
        return false;
    }
    
   
	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		
	}
    
   
   
}