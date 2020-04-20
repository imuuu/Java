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

public class subStoreRemoveCmd implements CommandInterface
{
	Main _main = Main.getInstance();
	ItemMetods itemM = new ItemMetods();
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;
      
        ShopManager shopManager = _main.shopManager;
        
        
        String nameShop = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");
        //nameShop=nameShop.substring(1,nameShop.length()-1);
        if(shopManager.isExists(nameShop))
        {
        	player.sendMessage(ChatColor.GREEN+"Shop has been removed!");
        	shopManager.removeShop(nameShop);
        	return false;
        }
        
        player.sendMessage(ChatColor.RED + "Couldn't find shop with that name!");
       
        
        
		
        return false;
    }
   
}