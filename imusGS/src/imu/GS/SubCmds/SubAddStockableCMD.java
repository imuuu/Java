package imu.GS.SubCmds;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.GS.Main.Main;
import imu.GS.Other.CmdData;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItems.ShopItemStockable;
import imu.iAPI.Interfaces.CommandInterface;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;
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
    	ItemStack stack = player.getInventory().getItemInMainHand();
    	if(stack == null || stack.getType() == Material.AIR) return false;
		
    	ShopBase shop = _main.get_shopManager().GetShop(shopName);
		
		if(shop != null)
		{
			shop.AddNewItem(new ShopItemStockable(_main, shop, stack , player.getInventory().getItemInMainHand().getAmount()),true);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Shop not found!");
			return false;
		}
		player.sendMessage(Metods.msgC("&9"+ImusAPI._metods.GetItemDisplayName(stack)+" &ahas been added to &3"+shopName));
		
        return false;
    }

	@Override
	public void FailedMsg(CommandSender arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
    
   
   
}