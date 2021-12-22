package imus.iWaystones.Events;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import imu.iAPI.Main.ImusAPI;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Main.ImusWaystones;
import imu.iWaystones.Managers.WaystoneManager;

public class WaystoneEvents implements Listener
{
	ImusWaystones _main = ImusWaystones._instance;
	WaystoneManager _waystoneManager;
	
	public WaystoneEvents() 
	{
		ImusWaystones._instance.getServer().getPluginManager().registerEvents(this, ImusWaystones._instance);
		_waystoneManager = ImusWaystones._instance.GetWaystoneManager();
	}
	
	
	@EventHandler
	public void OnBlockPlace(BlockPlaceEvent e)
	{

		Waystone wStone = _waystoneManager.GetWaystone(e.getBlock());
		if(wStone == null ) return;
		
		wStone.SetOwner(e.getPlayer());
		
		HashMap<String, String> cmds = new HashMap<String, String>();
		cmds.put("( CONFIRM )", "/iw confirm "+e.getPlayer().getUniqueId());
		//ImusAPI._metods.SendMessageCommands(e.getPlayer(), cmds, " // ");
		ImusAPI._metods.SendMessageCommands(e.getPlayer(), "Waystone created! ",cmds, "","");
		_waystoneManager.SetPlayerConfirmation(e.getPlayer().getUniqueId(), wStone);
	}
	
}
