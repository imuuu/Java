package imu.iAPI.Other;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Interfaces.CustomInv;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Main.ImusAPI;

public abstract class CustomInvLayout implements Listener, CustomInv
{
	protected Plugin _plugin = null;
	protected Metods _metods = ImusAPI._metods;
	String _name="";
	protected int _size = 0;
	
	protected Inventory _inv = null;
	protected Player _player = null;
	boolean _hasRegisteredEvents = false;
	
	protected DENY_ITEM_MOVE _denyItemMove = DENY_ITEM_MOVE.BOTH;
	public CustomInvLayout(Plugin main, Player player, String name, int size)
	{
		_plugin = main;

		_name = name;
		_size = size;
		_player = player;		
		_inv =  _plugin.getServer().createInventory(null, _size, _name);		
		RegisterToEvents();
	}
	protected enum DENY_ITEM_MOVE
	{
		UPPER_INV,
		LOWER_INV,
		BOTH,
		NONE,
	}
	public void RegisterToEvents()
	{
		_hasRegisteredEvents = true;
		_plugin.getServer().getPluginManager().registerEvents(this, _plugin);
	}
	
	public boolean HasRegistered()
	{
		return _hasRegisteredEvents;
	}
	
	public Inventory GetInv()
	{
		return _inv;
	}
	
	public boolean isThisInv(InventoryEvent e) 
	{
		if(e.getInventory().equals(_inv))
		{
			return true;
		}
		return false;
	}
		
	public void onDisable()
	{
		_player.closeInventory();
	}
	
	public void openThis() 
	{
		_player.openInventory(_inv);		
	}
	
	@EventHandler
	public void invClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			invClosed(e);
			HandlerList.unregisterAll(this);
			_hasRegisteredEvents = false;
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if(isThisInv(e))
		{
//			System.out.println("raw slot: "+e.getRawSlot());
//			System.out.println("slot slot: "+e.getSlot());
//			System.out.println();
			switch (_denyItemMove) 
			{
			case BOTH:
				e.setCancelled(true);
				break;
			case LOWER_INV:
				if(e.getRawSlot() != e.getSlot())
					e.setCancelled(true);
				break;
			case UPPER_INV:
				if(e.getRawSlot() == e.getSlot())
					e.setCancelled(true);
				break;
			default:
				break;
			
			}
			
			if((e.getRawSlot() == e.getSlot()))
			{
				onClickInsideInv(e);				
			}
		}
		
		
	}
	
	
	
	@Override
	public ItemStack SetButton(ItemStack stack, IButton b)
	{
		return _metods.setPersistenData(stack, pd_buttonType, PersistentDataType.STRING, b.toString());
	}
	
	@Override
	public String getButtonName(ItemStack stack)
	{
		String button = _metods.getPersistenData(stack, pd_buttonType, PersistentDataType.STRING);
		if(button != null)
			return button;
		
		return null;
	}
	
	
	
	@Override
	public ItemStack setupButton(IButton b, Material material, String displayName, Integer itemSlot)
	{
		ItemStack sbutton = new ItemStack(material);
		_metods.setDisplayName(sbutton, displayName);
		SetButton(sbutton, b);
		if(itemSlot != null)
		{
			_inv.setItem(itemSlot, sbutton);
			return _inv.getItem(itemSlot);
		}
		return sbutton;
	}
	
	
	
	
	
}
