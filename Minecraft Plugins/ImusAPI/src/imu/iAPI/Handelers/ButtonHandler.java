package imu.iAPI.Handelers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Enums.INV_ACTION;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.Interfaces.ICustomInventory;


public class ButtonHandler implements Listener 
{
	private Plugin _plugin;
	private Map<Integer, IBUTTONN> _buttons;
	
	private ICustomInventory _customInventory;
	
	private INVENTORY_AREA _inventoryLock = INVENTORY_AREA.NONE;
	
	private long _lastClickTime = 0;
	private final long COOLDOWN_IN_MILLIS = 100; // milliseconds

    
    public ButtonHandler(Plugin plugin, ICustomInventory customInventory) 
    {
       _buttons = new HashMap<>();
       _customInventory = customInventory;
       _plugin = plugin;
      
    }
    
    public void SetInventoryLock(INVENTORY_AREA lock)
    {
    	_inventoryLock = lock;
    }
    
    public void AddButton(IBUTTONN button) 
    {
        _buttons.put(button.GetPosition(), button);
    }
    
    public void AddButton(int position, IBUTTONN button) 
    {
        _buttons.put(position, button);
    }
    
    public IBUTTONN RemoveButton(int position)
    {
    	return _buttons.remove(position);
    }
    
    public IBUTTONN RemoveButton(IBUTTONN button)
    {
    	return RemoveButton(button.GetPosition());
    }
    
    public IBUTTONN GetButton(int position)
    {
    	if(!_buttons.containsKey(position)) return null;
    	
    	return _buttons.get(position);
    }
    
    public void OnHandlerOpen()
    {
    	 _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
    }
    
    public void OnHandlerClose()
    {
    	HandlerList.unregisterAll(this);
    }
    
    @EventHandler
	public void OnInventoryCloseEvent(InventoryCloseEvent event)
	{
    	 if(!event.getInventory().equals(_customInventory.GetInventory()))
         {
         	return;
         }
    	 
    	 _customInventory.OnClose();
	}
   
    @EventHandler
    public void OnInventoryClickEvent(InventoryClickEvent event) 
    {
        if(!event.getInventory().equals(_customInventory.GetInventory()))
        {
        	return;
        }
        
        IBUTTONN button = _buttons.get(event.getRawSlot());
        
        INVENTORY_AREA area = GetInventoryArea(event);
        if(_inventoryLock == INVENTORY_AREA.UPPER_LOWER_INV 
        		|| area == _inventoryLock)
        {
        	 event.setCancelled(true);
        }
        
        System.out.println("event action: "+event.getAction() + " AREA: "+ area);
        
        if(area == INVENTORY_AREA.LOWER_INV)
        {
        	if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) 
        	{
        		event.setCancelled(true);
        		return;
        	}
        }
        
        if(area == INVENTORY_AREA.UPPER_INV)
        {
        	 switch (event.getAction()) 
        	 {
             case PLACE_ALL:
             case PLACE_ONE:
             case PLACE_SOME:
             case SWAP_WITH_CURSOR:
             {
            	 if(button != null)
            	 {
            		 event.setCancelled(true);
            		 return;
            	 }
            	 
            	 ItemStack droppedItem = event.getCursor();
                 boolean success = CreateButtonForSlot(INV_ACTION.DROP, droppedItem, event.getSlot());

                 if(success) event.setCancelled(true);
                 
                 return;
             }
             case PICKUP_ALL:
             {
            	 boolean onPickUpAll = _customInventory.OnPickupAll(button, event.getSlot());
            	 
            	 if(!onPickUpAll)
            	 {
            		 event.setCancelled(true);
            	 }
            	 
            	 if(button == null) break;
            	 
            	 if(!button.IsPositionLocked()) 
        		 {
            		 RemoveButton(button);
        		 }
             }
			default:
				break;
        	 }
        }
        
        
        
        if(button == null) return;
        
        System.out.println("==> There is button: "+event.getRawSlot());
        
        if(button.IsPositionLocked())
        {
        	 //keeps buttons same place
            event.setCancelled(true); 
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - _lastClickTime < COOLDOWN_IN_MILLIS) 
        {
            event.setCancelled(true);
            return;
        }
        
        _lastClickTime = currentTime;
        
        button.Action(event);
        
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) 
    {
    	if(!event.getInventory().equals(_customInventory.GetInventory()))
        {
        	return;
        }

    	for (Integer slot : event.getRawSlots()) 
    	{
            if (slot < _customInventory.GetSize()) 
            { 
            	if (GetButton(slot) != null) 
            	{
            		event.setCancelled(true);
            		return;
            	}
            }
        }
    	
    	for (Integer slot : event.getRawSlots()) 
    	{
            if (slot < _customInventory.GetSize()) 
            { 
                ItemStack draggedItem = event.getOldCursor();
                CreateButtonForSlot(INV_ACTION.DRAG, draggedItem, slot);   
            }
        }
    	
    }
    
    private boolean CreateButtonForSlot(INV_ACTION inv_action, ItemStack item, int slot) {

    	System.out.println(" "+inv_action+" "+item + " slot: "+slot + " Created: "+ (GetButton(slot) == null ? "true" : "false"));
    	
    	if(inv_action == INV_ACTION.DROP)
    	{
    		return _customInventory.OnDropitem(item, slot);
    	}
    	
    	if(inv_action == INV_ACTION.DRAG)
    	{
    		return _customInventory.OnDragitem(item, slot);
    	}
    	
    	return false;
        
     
    }
    
    private INVENTORY_AREA GetInventoryArea(InventoryClickEvent e)
    {
    	if(e.getRawSlot() != e.getSlot())
    	{
    		return INVENTORY_AREA.LOWER_INV;
    	}
    	
    	if(e.getRawSlot() == e.getSlot())
    	{
    		return INVENTORY_AREA.UPPER_INV;
    	}
    	
    	return INVENTORY_AREA.NONE;
    }
    
    public void UpdateButtons(boolean clearEmpties)
    {
    	if(clearEmpties)
    	{
    		Inventory inv = _customInventory.GetInventory();
        	for(int i = 0; i < _customInventory.GetSize(); i++)
        	{
        		inv.setItem(i, new ItemStack(Material.AIR));	
        	}
    	}
    	
    	
    	for(IBUTTONN button : _buttons.values())
    	{
    		UpdateButton(button.GetPosition());
    	}
    }
    
    public void UpdateButton(int position)
    {
    	IBUTTONN button = GetButton(position);
    	button.OnUpdate();
    	
    	_customInventory.
    	GetInventory().
    	setItem(button.GetPosition(), button.GetItemStack());
    }
}

