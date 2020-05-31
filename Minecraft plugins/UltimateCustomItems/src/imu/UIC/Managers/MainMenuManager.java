package imu.UIC.Managers;

import org.bukkit.entity.Player;

import imu.UIC.INVs.MainMenuINV;
import imu.UIC.main.Main;

public class MainMenuManager extends MenuM
{
	public MainMenuManager(Main main) {
		super(main);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openNewInv(Player player)
	{
		MainMenuINV inv = new MainMenuINV(_main, player, "MainMenu", 9*6);
		inv.openThis();
	}
}
