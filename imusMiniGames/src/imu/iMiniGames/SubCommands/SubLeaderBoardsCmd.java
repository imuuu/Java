package imu.iMiniGames.SubCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.iMiniGames.Interfaces.CommandInterface;
import imu.iMiniGames.Main.Main;

public class SubLeaderBoardsCmd implements CommandInterface
{
	Main _main = null;

	String _subCmd = "";
	public SubLeaderBoardsCmd(Main main, String subCmd) 
	{
		_main = main;
		_subCmd=subCmd;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;
	
        _main.get_combatManager().getLeaderBoard().showStats(player);
        
        
        return false;
    }
    
   
   
}