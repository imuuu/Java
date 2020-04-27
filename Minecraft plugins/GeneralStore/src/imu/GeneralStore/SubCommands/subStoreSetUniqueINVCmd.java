package imu.GeneralStore.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.Other.ItemMetods;
import imu.GeneralStore.Other.ShopManager;
import imu.GeneralStore.main.Main;

public class subStoreSetUniqueINVCmd implements CommandInterface
{
	Main _main =null;
	ItemMetods itemM = new ItemMetods();
	
	Player player;
	ShopManager shopManager = null;
	public subStoreSetUniqueINVCmd(Main main)
	{
		_main = main;
		shopManager = _main.getShopManager();
	}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        player = (Player) sender;
        
        
        
        player.sendMessage("/gs uniques");
		
        return false;
    }
    
   
    
    
   
}