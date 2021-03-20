package imu.iMiniGames.Invs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Other.CustomInvLayout;

public class GamePlaner extends CustomInvLayout implements Listener
{
	String pd_buttonType = "sGP.buttonType";

	public GamePlaner(Main main, Player player, String name) 
	{
		super(main, player, name, 9*3);
		
		main.getServer().getPluginManager().registerEvents(this, _main);
		
		openThis();

	}
	

	public enum BUTTON
	{
		NONE,
		CONFIRM,
		EXIT,
		RESET,
		SET_ARENA,
		ADD_BET,
		ADD_PLAYERS,
		POTION_EFFECTS,
		ADD_BEST_OF_AMOUNT;
	}
	

	@EventHandler
	public void onInvClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			HandlerList.unregisterAll(this);
		}
	}
	
	void setButton(ItemStack stack, BUTTON b)
	{
		_itemM.setPersistenData(stack, pd_buttonType, PersistentDataType.STRING, b.toString());
	}
	
	public BUTTON getButton(ItemStack stack)
	{
		String button = _itemM.getPersistenData(stack, pd_buttonType, PersistentDataType.STRING);
		if(button != null)
			return BUTTON.valueOf(button);
		
		return BUTTON.NONE;
	}
	
	public ItemStack setupButton(BUTTON b, Material material, String displayName, int itemSlot)
	{
		ItemStack sbutton = new ItemStack(material);
		_itemM.setDisplayName(sbutton, displayName);
		setButton(sbutton, b);
		_inv.setItem(itemSlot, sbutton);
		return _inv.getItem(itemSlot);
	}

}
