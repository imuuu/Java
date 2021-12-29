package imus.iWaystones.Events;

import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Main.ImusWaystones;
import imu.iWaystones.Managers.WaystoneManager;

public class WaystoneEvents implements Listener
{
	ImusWaystones _main = ImusWaystones._instance;
	WaystoneManager _waystoneManager;
	Cooldowns _cds = new Cooldowns();
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
		cmds.put("( CONFIRM )", "/iw confirm "+wStone.GetLoc().getWorld().getName()+" "+wStone.GetLoc().toVector());
		//ImusAPI._metods.SendMessageCommands(e.getPlayer(), cmds, " // ");
		ImusAPI._metods.SendMessageCommands(e.getPlayer(), "Waystone creation started! ",cmds, "","");
		_waystoneManager.SetPlayerConfirmation(e.getPlayer().getUniqueId(), wStone);
	}
	
	@EventHandler
	public void OnBlockInteract(PlayerInteractEvent e)
	{
		if(e.getClickedBlock() != null)
		{
			//System.out.println("interact: "+ e.getClickedBlock().getType() + " loc: "+e.getClickedBlock().getLocation().toVector());
		}
		
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK || !_waystoneManager.IsWaystone(e.getClickedBlock())) return;
		
		e.setCancelled(true);
		
		if(_waystoneManager.IsTeleporting(e.getPlayer())) 
		{
			return;
		}
		
		if(!_cds.isCooldownReady("click")) return;
		
		_cds.setCooldownInSeconds("click", 0.5f);
		
		
		Waystone ws = _waystoneManager.GetWaystone(e.getClickedBlock());
		
		if(!_waystoneManager.IsValid(ws))
		{
			_waystoneManager.RemoveWaystone(ws);
			return;
		}
		
		if(!_waystoneManager.HasDiscovered(e.getPlayer(), ws))
		{
			_waystoneManager.AddDiscovered(e.getPlayer().getUniqueId(), ws.GetUUID(), true);
			e.getPlayer().sendMessage(Metods.msgC("&3You have discovered new waystone named as "+ws.GetName()));
			return;
		}
		
		if(ImusAPI._instance.GetOpenedInv(e.getPlayer()) != null) return;
	
		_waystoneManager.OpenWaystone(e.getPlayer(),ws);
		
	
	}
	
	@EventHandler
	public void OnBreakPlace(BlockBreakEvent e)
	{
		if(!_waystoneManager.IsWaystone(e.getBlock())) return;
		
		Waystone ws =_waystoneManager.GetWaystone(e.getBlock());
		ws.SendMessageToOwner("&3Someone has &cDestroyed &3your &ewaystone &3named as "+ws.GetName());
		_waystoneManager.RemoveWaystone(ws);
		e.getPlayer().sendMessage(Metods.msgC("&3Waystone has been &cDestroyed"));
		e.getPlayer().playSound(ws.GetLoc(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.1f, 0.1f);
	}
	
	
	
}
