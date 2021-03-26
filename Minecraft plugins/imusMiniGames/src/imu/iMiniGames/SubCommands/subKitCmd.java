package imu.iMiniGames.SubCommands;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.iMiniGames.Interfaces.CommandInterface;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.CombatManager;
import net.md_5.bungee.api.ChatColor;

public class subKitCmd implements CommandInterface
{
	Main _main = null;
	String _subCmd = "";
	CombatManager _com;
	String[] _subs; // {"create"};	 
 	public subKitCmd(Main main, String[] sub_cmds) 
	{
		_main = main;
		_subs = sub_cmds;
		_com = _main.get_combatManager();
	}

 	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;
        
        _main.get_itemM().printArray("args", args);
        if(args.length < 3)
    	{
        	player.sendMessage(ChatColor.RED + "Something wrong");
    		return false;
    	}
        
        String kitName = StringUtils.join(Arrays.copyOfRange(args, 2, args.length)," ");

    	System.out.println("kit name: "+kitName);
    	
    	_com.addKit(kitName, player.getInventory().getContents());
    	player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Kit has been saved!"));
    	
    	
  
        
		
        return false;
    }
    
    
    
   
   
}