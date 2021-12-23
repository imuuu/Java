package imu.iWaystones.Invs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.CustomInvLayout;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Main.ImusWaystones;

public class WaystoneMenuInv extends CustomInvLayout {

	ImusWaystones _main;
	Waystone _waystone;
	
	public WaystoneMenuInv(Waystone waystone, Player player) 
	{
		super(ImusWaystones._instance, player, "", 4 * 9);
		_main = ImusWaystones._instance;
	}
	
	enum BUTTON implements IButton
	{
		NONE,
		GO_LEFT,
		GO_RIGHT,
		UPGRADE,
	}
	@Override
	public void invClosed(InventoryCloseEvent e) 
	{
		
	}

	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		
	}
	
	@Override
	public void openThis() {
		super.openThis();
		setupButtons();
	}
	
	@Override
	public void setupButtons() 
	{
		for(int i = _size-9; i < _size; i++) {setupButton(BUTTON.NONE, Material.CYAN_STAINED_GLASS_PANE, " ", i);}
	}

}
