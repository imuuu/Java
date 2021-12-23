package imu.iWaystones.Invs;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Other.CustomInvLayout;

public class WaystoneUpgradeMenu extends CustomInvLayout
{

	public WaystoneUpgradeMenu(Plugin main, Player player, String name, int size) 
	{
		super(main, player, name, size);
	}

	@Override
	public void invClosed(InventoryCloseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClickInsideInv(InventoryClickEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupButtons() {
		// TODO Auto-generated method stub
		
	}

}
