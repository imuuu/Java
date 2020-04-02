package imu.DontLoseItems.Commands;

import org.bukkit.Material;
//Imports for the base command class.
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import imu.DontLoseItems.Interfaces.CommandInterface;
 
public class ExampleCmd implements CommandInterface
{
 
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
 
    	if(args.length > 0)
    		return false;
        
    	
    	Player p = (Player) sender;
    	p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND_HELMET));
    	p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND_CHESTPLATE));
    	p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND_LEGGINGS));
    	p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.DIAMOND_BOOTS));
        p.sendMessage("DROPPED!");
        return true;
    }
 
}