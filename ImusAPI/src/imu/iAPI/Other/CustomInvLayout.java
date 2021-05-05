package imu.iAPI.Other;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Interfaces.CustomInv;
import imu.iAPI.Interfaces.IButton;


public abstract class CustomInvLayout implements Listener, CustomInv
{
	protected Plugin _main = null;
	protected Metods _metods = null;
	String _name="";
	protected int _size = 0;
	
	protected Inventory _inv = null;
	protected Player _player = null;

	public CustomInvLayout(Plugin main, Metods metods, Player player, String name, int size)
	{
		_main = main;
		_metods = metods;
		_name = name;
		_size = size;
		_player = player;		
		_inv =  _main.getServer().createInventory(null, _size, _name);		
		_main.getServer().getPluginManager().registerEvents(this, _main);
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
	public void invClose(InventoryClickEvent e)
	{
		if(isThisInv(e))
		{
			invClosed(e);
			HandlerList.unregisterAll(this);			
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		if(isThisInv(e) && (e.getRawSlot() == e.getSlot()))
		{
			onClickInsideInv(e);
		}
	}
	
	public void setButton(ItemStack stack, IButton b)
	{
		_metods.setPersistenData(stack, pd_buttonType, PersistentDataType.STRING, b.toString());
	}
	
	public String getButtonName(ItemStack stack)
	{
		String button = _metods.getPersistenData(stack, pd_buttonType, PersistentDataType.STRING);
		if(button != null)
			return button;
		
		return null;
	}
	
	public ItemStack setupButton(IButton b, Material material, String displayName, int itemSlot)
	{
		ItemStack sbutton = new ItemStack(material);
		_metods.setDisplayName(sbutton, displayName);
		setButton(sbutton, b);
		_inv.setItem(itemSlot, sbutton);
		return _inv.getItem(itemSlot);
	}
	
	
	
	
}
