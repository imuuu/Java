package imu.UIC.Managers;

import org.bukkit.entity.Player;

import imu.UIC.INVs.SetMenuINV;
import imu.UIC.Interfaces.ImenuM;
import imu.UIC.main.Main;

public class SetMenuManager implements ImenuM
{
	Main _main = null;
	public SetMenuManager(Main main) 
	{
		_main = main;
	}
	
	@Override
	public void openNewInv(Player player) 
	{
		SetMenuINV inv = new SetMenuINV(_main, player, "SetMenu", 9*3);
		inv.openThis();
	}
	
	
}
