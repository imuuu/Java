package imu.WorldRestore.Events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import imu.WorldRestore.Managers.ChunkManager;
import imu.WorldRestore.main.Main;

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
	public void onBreak(BlockBreakEvent e)
	{
		
		Location loc = e.getBlock().getLocation();
		if(isRightWorld(loc))
		{
			System.out.println("Its right world");
			_cManager.setChunkAuto(e.getBlock(),1);
		}
		
		
	}
	
	boolean isRightWorld(Location loc)
	{
		if(loc.getWorld().getName().equalsIgnoreCase(_worldName))
			return true;
		
		return false;		
	}
	
	
	
	
}
