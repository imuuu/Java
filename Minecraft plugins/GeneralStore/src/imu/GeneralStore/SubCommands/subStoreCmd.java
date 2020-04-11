package imu.GeneralStore.SubCommands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import imu.GeneralStore.Interfaces.CommandInterface;
import imu.GeneralStore.main.Main;

public class subStoreCmd implements CommandInterface
{
	Main main = Main.getInstance();
	
	HashMap<Player, ItemStack[]> p_invs = main.playerInvContent;
	
	String invName = "";
	
	public subStoreCmd() 
	{
		invName = main.shop1.getName();
	}
	
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
    	Inventory inv = main.getServer().createInventory(null, 9, invName);

    	player.openInventory(inv);
    }
 
}