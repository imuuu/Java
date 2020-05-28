package imu.WorldRestore.Events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import imu.WorldRestore.Managers.ChunkManager;
import imu.WorldRestore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class Event1 implements Listener
{
	
	Block b = null;
	Main _main = null;
	ChunkManager _cManager = null;
	
	String _worldName = "";
	String _targetWorldName = "";
	public Event1(Main main)
	{
		_main = main;
		_cManager = main.getChunkManager();
		_worldName = _cManager.getWorldName();
		_targetWorldName = _cManager.getDefTargetWorldName();
	}
	
	@EventHandler
	public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent e)
	{
		if(isRightWorld(e.getPlayer().getLocation()))
		{
			_main.getPlayerData().savePlayerQuitLocation(e.getPlayer(),false);
		}
		
	}
	@EventHandler
	public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e)
	{
		if(isRightWorld(e.getPlayer().getLocation()))
		{
			if(_main.getPlayerData().checkIfPlayerIsTagged(e.getPlayer()))
			{
				e.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "You have been teleported to spawn due to chunk was updated while you were offline!");
			}
		}
		
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e)
	{
		
		Location loc = e.getBlock().getLocation();
		if(isRightWorld(loc))
		{
			System.out.println("Its right world");
			_cManager.setChunkAuto(e.getBlock(),-1, 1);
		}
		
		
	}
	
	boolean isRightWorld(Location loc)
	{
		if(loc.getWorld().getName().equalsIgnoreCase(_worldName))
			return true;
		
		return false;		
	}
	
	
	
	
}
