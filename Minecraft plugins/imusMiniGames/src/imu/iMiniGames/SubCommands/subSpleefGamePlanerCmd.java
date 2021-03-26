package imu.iMiniGames.SubCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.iMiniGames.Interfaces.CommandInterface;
import imu.iMiniGames.Invs.SpleefGamePlaner;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.SpleefDataCard;
import net.md_5.bungee.api.ChatColor;

public class subSpleefGamePlanerCmd implements CommandInterface
{
	Main _main = null;

	String _subCmd = "";
	public subSpleefGamePlanerCmd(Main main, String subCmd) 
	{
		_main = main;
		_subCmd=subCmd;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;
        
        if(_main.isPlayerBlocked(player))
        {
        	_main.sendBlockedmsg(player);
        	return false;
        }
        
        if(!_main.get_spleefGameHandler().isPlayerPlanInQueue(player))
        {
        	if(!_main.get_spleefGameHandler().isPlayerInArena(player))
        	{
                if(_main.get_spleefManager().hasPlayerDataCard(player))
                {
                	 new SpleefGamePlaner(_main, player,_main.get_spleefManager().getPlayerDataCard(player));
                }else
                {
                	 new SpleefGamePlaner(_main, player,new SpleefDataCard(player));
                }
        	}else
        	{
        		player.sendMessage(ChatColor.RED + "You are in game, Can't use this command!");
        	}
        	
           
        }else
        {
        	player.sendMessage(ChatColor.RED + "You are already in queue!"+ChatColor.DARK_AQUA+ " You will receive invitation when your game starts!");
        }
        
        
  
        
		
        return false;
    }
    
   
   
}