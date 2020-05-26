package imu.WorldRestore.SubCommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import imu.WorldRestore.Interfaces.CommandInterface;
import imu.WorldRestore.Managers.ChunkManager;
import imu.WorldRestore.Other.ChunkCard;
import imu.WorldRestore.main.Main;

public class subCmd implements CommandInterface
{
	Main _main = null;
	ChunkManager _cManager = null;
	public subCmd(Main main) 
	{
		_main = main;
		_cManager = _main.getChunkManager();
	}
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) 
    {
        Player player = (Player) sender;

        
        //String nameShop = StringUtils.join(Arrays.copyOfRange(args, 1, args.length)," ");

        player.sendMessage(ChatColor.GREEN + "fixingt");
        ChunkCard card = new ChunkCard(_cManager, player.getWorld().getChunkAt(player.getLocation()), _cManager.getDefTargetWorldName(), 30, 100, 0);
        _main.getChunkHandler().fixChunk(card);
    	
    	
  
        
		
        return false;
    }
    
   
   
}