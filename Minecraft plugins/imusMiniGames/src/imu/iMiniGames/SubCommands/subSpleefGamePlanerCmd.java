package imu.iMiniGames.SubCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.iMiniGames.Interfaces.CommandInterface;
import imu.iMiniGames.Invs.SpleefGamePlaner;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.SpleefDataCard;
import imu.iMiniGames.Other.SpleefGameCard;

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
	
        SpleefGameCard gCard = _main.get_spleefGameHandler().getPlayerSGameCard(player.getUniqueId());
        if(gCard != null)
        {
        	 new SpleefGamePlaner(_main, player,gCard.get_spleefDataCard());
        }else
        {
        	 new SpleefGamePlaner(_main, player,new SpleefDataCard(player));
        }
       
        
  
        
		
        return false;
    }
    
   
   
}