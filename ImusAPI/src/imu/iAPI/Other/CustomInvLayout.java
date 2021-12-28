package imu.iAPI.Other;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import chestcleaner.sorting.SortingEvent;
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
	
	protected ItemStack _droppedStack = null;
	protected ItemStack _takenStack = null;
	protected int _droppedSlot = -1;
	protected int _takenSlot = -1;
	
	public CustomInvLayout(Plugin main, Player player, String name, int size)
	{
		_plugin = main;

		_name = Metods.msgC(name);
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
	
	//works only for example arraylists
	public int PageChance(int page,int rollAmount, int listSize, int slotAmount)
	{
		int maxPages =(int) Math.ceil(listSize/(double)slotAmount)-1;
		page += rollAmount;
		if(page < 0){page = maxPages; return page;}				
		if(page > maxPages) {page = 0; return page;}	
		return page;
	}
	
	public Player GetPlayer()
	{
		return _player;
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
		return isThisInv(e.getInventory());
	}
	
	public boolean isThisInv(Inventory inv)
	{
		return inv.equals(_inv);
	}
	
	public void onDisable()
	{
		_player.closeInventory();
	}
	
	public void openThis() 
	{	
		_player.openInventory(_inv);
		ImusAPI._instance.RegisterInvOpen(this);
		if(!HasRegistered())
			RegisterToEvents();
		
	}
	
	@EventHandler
	public void OnInvSort(SortingEvent e) //ChectCleaner.plugin support
	{
		if(isThisInv(e.getInventory()))
		{
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void invClose(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			invClosed(e);
			HandlerList.unregisterAll(this);
			_hasRegisteredEvents = false;
			ImusAPI._instance.UnregisterInv(this);
		}
	}
	
	void SetDroppedTakenStacks(InventoryClickEvent e)
	{
		_takenStack = null;
		_takenSlot = -1;
		_droppedStack = null;
		_droppedSlot = -1;
		if(e.getCurrentItem() == null && e.getCursor() != null)
		{			
			_droppedStack = e.getCursor();
			_droppedSlot = e.getSlot();
			return;
		}
		
		if(e.getCurrentItem() != null && e.getCursor().getType() == Material.AIR)
		{		
			_takenStack = e.getCurrentItem();
			_takenSlot = e.getSlot();
			return;
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{

		if(isThisInv(e))
		{

			SetDroppedTakenStacks(e);
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
		Metods.setDisplayName(sbutton, Metods.msgC(displayName));
		SetButton(sbutton, b);
		if(itemSlot != null)
		{
			_inv.setItem(itemSlot, sbutton);
			return _inv.getItem(itemSlot);
		}
		return sbutton;
	}
	
	
	
	
	
}
