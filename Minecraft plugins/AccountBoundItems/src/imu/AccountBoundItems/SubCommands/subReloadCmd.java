package imu.AccountBoundItems.SubCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.AccountBoundItems.Interfaces.CommandInterface;
import imu.AccountBoundItems.Other.ItemABI;
import imu.AccountBoundItems.Other.ItemMetods;
import imu.AccountBoundItems.Other.ServerMethods;
import imu.AccountBoundItems.main.Main;

public class subReloadCmd implements CommandInterface
{
	ItemABI itemAbi = new ItemABI();
	Main main = Main.getInstance();
	Player player;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        player = (Player) sender;

    	main.ConfigsSetup();
    	player.sendMessage("Price configs reloaded");
    	
    	
        
    	return false;
    }
     
}