package imu.GS.Invs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Strings;

import imu.GS.Main.Main;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.CustomInvLayout;

public class LoaderPriceCustomInv extends CustomInvLayout
{
	Main _main;
	public LoaderPriceCustomInv(Plugin main, Player player) 
	{
		super(main, player, "&9Load Custom Price ", 6*9);
		_main = (Main)main;
		
	}
	
	
	enum BUTTON implements IButton
	{
		NONE,
		BACK,
		CONFIRM,
	}
	BUTTON GetBUTTON(ItemStack stack)
	{
		if(stack == null) return BUTTON.NONE;
		String bName = getButtonName(stack);		
		if(Strings.isNullOrEmpty(bName)) return BUTTON.NONE;
		return BUTTON.valueOf(bName);
	}
	
	
	@Override
	public void openThis() 
	{
		super.openThis();
		setupButtons();
		_main.RegisterInv(this);
	}
	@Override
	public void setupButtons() 
	{
		for(int i = _size-1; i > _size-28; i--) {setupButton(BUTTON.NONE, Material.BLACK_STAINED_GLASS_PANE, " ", i);}
		setupButton(BUTTON.BACK,Material.RED_STAINED_GLASS_PANE, "&c<== BACK", _size-9);
		setupButton(BUTTON.CONFIRM,Material.GREEN_STAINED_GLASS_PANE, "&9CONFIRM and add to shop", _size-1);

	}
	

	
	
	@Override
	public void invClosed(InventoryCloseEvent e) 
	{
		_main.UnregisterInv(this);
	}
	

	void Confirm()
	{

	}
	
	void Back()
	{
		
	}
	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		ItemStack stack = e.getCurrentItem();
		BUTTON button = GetBUTTON(stack);
		
		switch (button) {
		case NONE:
			break;
		
		case BACK:
			Back();
			break;
		case CONFIRM:
			Confirm();
			break;
		
		}
		
	}

	

	

}
