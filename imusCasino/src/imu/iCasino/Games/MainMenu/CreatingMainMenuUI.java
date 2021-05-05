package imu.iCasino.Games.MainMenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;

public class CreatingMainMenuUI extends CustomInvLayout
{
	
	public CreatingMainMenuUI(Plugin main, Metods metods, Player player) {
		super(main, metods, player, "Creating Menu", 6*9);

	}
	
	enum BUTTON implements IButton
	{
		NONE,
		BACK;
	}
	
	public BUTTON getButton(ItemStack stack)
	{
		String name = getButtonName(stack);
		if(name != null)
			return BUTTON.valueOf(name);
		
		return BUTTON.NONE;
	}
	
	@Override
	public void invClosed(InventoryClickEvent e) {
		
	}

	@Override
	public void setupButtons() 
	{
		setupButton(BUTTON.BACK, Material.RED_CARPET, "BACK", 1);
	}


	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		
	}
}

