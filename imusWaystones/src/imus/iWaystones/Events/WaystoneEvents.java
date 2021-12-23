package imus.iWaystones.Events;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Metods;
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

		Waystone wStone = _waystoneManager.TryToCreateWaystone(e.getBlock());
		if(wStone == null ) return;
		
		wStone.SetOwner(e.getPlayer());
		
		HashMap<String, String> cmds = new HashMap<String, String>();
		cmds.put("( CONFIRM )", "/iw confirm "+e.getPlayer().getUniqueId());
		//ImusAPI._metods.SendMessageCommands(e.getPlayer(), cmds, " // ");
		ImusAPI._metods.SendMessageCommands(e.getPlayer(), "Waystone created! ",cmds, "","");
		_waystoneManager.SetPlayerConfirmation(e.getPlayer().getUniqueId(), wStone);
	}
	
	@EventHandler
	public void OnBlockInteract(PlayerInteractEvent e)
	{
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK || !_waystoneManager.IsWaystone(e.getClickedBlock())) return;
		if(ImusAPI._instance.GetOpenedInv(e.getPlayer()) != null) return;

		_waystoneManager.OpenWaystone(e.getPlayer(),_waystoneManager.GetWaystone(e.getClickedBlock()));
		e.setCancelled(true);
	
	}
	
	@EventHandler
	public void OnBreakPlace(BlockBreakEvent e)
	{
		if(!_waystoneManager.IsWaystone(e.getBlock())) return;
		
		_waystoneManager.RemoveWaystone(_waystoneManager.GetWaystone(e.getBlock()));
		e.getPlayer().sendMessage(Metods.msgC("&3Waystone has been &cDestroyid"));
	}
	
	
	
}
