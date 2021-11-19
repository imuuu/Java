package imu.GS.SubCmds;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.Main.Main;
import imu.GS.Other.CmdData;
import imu.GS.ShopUtl.ShopItems.ShopItemUnique;
import imu.iAPI.Interfaces.CommandInterface;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;

public class SubCreateUniqueCMD implements CommandInterface
{
	Main _main = null;

	CmdData _data;
	public SubCreateUniqueCMD(Main main, CmdData data) 
	{
		_main = main;
		_data = data;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
    	Player player = (Player) sender;
    	if(args.length < 2)
    	{
    		player.sendMessage(_data.get_syntaxText());
    		return false;
    	}
    	
    	String priceSTR = StringUtils.join(Arrays.copyOfRange(args, 2, args.length)," ");
    	
    	double price = 0;
    	if(ImusAPI._metods.isDigit(priceSTR))
    	{
    		price = Double.parseDouble(priceSTR);
    	}
    	
		ItemStack stack = player.getInventory().getItemInMainHand();
		if(stack == null)
			return false;
		
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				//stack.setAmount(1);
				ShopItemUnique siu = new ShopItemUnique(_main, null, stack, 1);
				_main.get_shopManager().GetUniqueManager().AddUniqueItem(siu);
				
			}
		}.runTaskAsynchronously(_main);
		
		
		player.sendMessage(Metods.msgC("&3You have created &5Unique&3 item with price tag of &2"+price));
		
        return false;
    }

	@Override
	public void FailedMsg(CommandSender arg0, String arg1) 
	{
		// TODO Auto-generated method stub
		
	}
    
   
   
}