package imu.UIC.Managers;

import org.bukkit.entity.Player;

import imu.UIC.INVs.MainMenuINV;
import imu.UIC.Interfaces.ImenuM;
import imu.UIC.main.Main;

public abstract class MenuM implements ImenuM
{
	Main _main;
	public MenuM(Main main) 
	{
		_main = main;
	}
	
	@Override
	public void openNewInv(Player player) 
	{
		MainMenuINV inv = new MainMenuINV(_main, player, "MainMenu", 9*6);
		inv.openThis();
	}
	
	
}
