package imu.GS.SubCmds;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import imu.GS.Main.Main;
import imu.GS.Other.CmdData;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.GS.ShopUtl.ShopItems.ShopItemUnique;
import imu.iAPI.Interfaces.CommandInterface;
import net.md_5.bungee.api.ChatColor;

public class SubAddStockableCMD implements CommandInterface
{
	Main _main = null;

	CmdData _data;
	public SubAddStockableCMD(Main main, CmdData data) 
	{
		_main = main;
		_data = data;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
    	Player player = (Player) sender;
    	if(args.length < 3)
    	{
    		player.sendMessage(_data.get_syntaxText());
    		return false;
    	}
    	String shopName = StringUtils.join(Arrays.copyOfRange(args, 2, args.length)," ");
    	
    	System.out.println("name: "+shopName);
		ShopBase shop = _main.get_shopManager().GetShop(shopName);
		if(shop != null)
		{

			shop.AddNewItem(new ShopItemStockable(_main, shop, player.getInventory().getItemInMainHand(), player.getInventory().getItemInMainHand().getAmount()),true);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Shop no found!");
		}
        return false;
    }

	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
    
   
   
}