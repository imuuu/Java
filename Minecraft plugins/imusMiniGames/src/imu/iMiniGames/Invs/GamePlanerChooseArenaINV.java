package imu.iMiniGames.Invs;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.iMiniGames.Arenas.SpleefArena;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.SpleefManager;
import imu.iMiniGames.Other.CustomInvLayout;

public class GamePlanerChooseArenaINV extends CustomInvLayout implements Listener
{
	SpleefManager _spleefManager;
	
	ArrayList<SpleefArena> _arenas;
	
	String pd_buttonType = "gs.sModI.buttonType";
	
	
	int _tooltip_size = 0;
	int _current_page = 0;
	
	public GamePlanerChooseArenaINV(Main main, Player player, String name) 
	{
		super(main, player, name, 4*9);
		
		_spleefManager = main.get_spleefManager();
		_arenas = _spleefManager.getArenas();
		
		_tooltip_size = _size-9;
		
		refresh();
	}
	
	public enum BUTTON
	{
		NONE,
		ARENA,
		GO_LEFT,
		GO_RIGHT;
	}
	
	void refresh()
	{
		
	}
	
	int totalPages()
	{
		int pages =(int) Math.round(((_arenas.size()-1)/(_tooltip_size))+0.5);
		return pages-1;
	}
	
	void chanceCurrentPage(int i)
	{
		_current_page = _current_page + i;
		if(_current_page < 0)
		{
			_current_page = 0;
		}
		if(_current_page > totalPages())
		{
			_current_page = totalPages();
		}
	}
	
	void setButton(ItemStack stack, BUTTON b)
	{
		_itemM.setPersistenData(stack, pd_buttonType, PersistentDataType.STRING, b.toString());
	}
	
	BUTTON getButton(ItemStack stack)
	{
		String button = _itemM.getPersistenData(stack, pd_buttonType, PersistentDataType.STRING);
		if(button != null)
			return BUTTON.valueOf(button);
		
		return BUTTON.NONE;
	}
	
	@EventHandler
	public void onInvClickEvent(InventoryClickEvent e) 
	{
		int rawSlot = e.getRawSlot();
		int slot = e.getSlot();
		
		if(isThisInv(e) && (rawSlot == slot))
		{			
			e.setCancelled(true);
			ItemStack stack = e.getCurrentItem();
			
			BUTTON button = getButton(stack);
			int item_id = (_current_page * _tooltip_size)+slot;
			switch (button) 
			{
			case NONE:
				
				break;
			case GO_LEFT:
				chanceCurrentPage(-1);
				refresh();
				break;
			case GO_RIGHT:
				chanceCurrentPage(1);
				refresh();
				break;
			default:
				break;
			}
			
		}	
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			HandlerList.unregisterAll(this);
		}
	}

}
