package imu.GeneralStore.SubCommands;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.Other.ItemMetods;
import imu.GeneralStore.Other.ShopManager;
import imu.GeneralStore.main.Main;

public class subStoreCreateCmd implements CommandInterface
{
	Main _main = Main.getInstance();
	ItemMetods itemM = new ItemMetods();
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;
 
        System.out.println("args size: "+args.length);
        itemM.printArray("Args2", args);
        ShopManager shopManager = _main.getShopManager();
        
        
        String nameShop = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");
        nameShop = itemM.addColor(nameShop);
        //nameShop=nameShop.substring(1,nameShop.length()-1);
        if(shopManager.isExists(nameShop))
        {
        	player.sendMessage(ChatColor.RED + "Shop name already exists");
        	return false;
        }
        
        shopManager.addShop(nameShop);
        player.sendMessage(ChatColor.GREEN+"Shop has been made!");
        
        
		
        return false;
    }
   
}