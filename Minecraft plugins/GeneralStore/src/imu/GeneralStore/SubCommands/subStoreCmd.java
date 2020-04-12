package imu.GeneralStore.SubCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.main.Main;

public class subStoreCmd implements CommandInterface
{
	Main main = Main.getInstance();
	
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
 
        //if(args.length > 1) return false;

        newInvenotry(player);
        player.sendMessage("IT is store prk!");
        return false;
    }
    
    public void newInvenotry(Player player)
    {
    	
    	main.shop1.openShopInv(player);
    }
 
}