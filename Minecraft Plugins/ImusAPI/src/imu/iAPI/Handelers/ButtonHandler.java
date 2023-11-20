package imu.iAPI.Handelers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

import imu.iAPI.Enums.INVENTORY_AREA;
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

        if(area == INVENTORY_AREA.LOWER_INV)
        {
        	if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) 
        	{
        		event.setCancelled(true);
        		return;
        	}
        }
        
        System.out.println("event action: "+event.getAction() + " AREA: "+ area);
        if(area == INVENTORY_AREA.UPPER_INV)
        {
        	 System.out.println("====== > event action: "+event.getAction() + " AREA: "+ area);
        	 switch (event.getAction()) 
        	 {
             case PLACE_ALL:
             case PLACE_ONE:
             case PLACE_SOME:
             case SWAP_WITH_CURSOR:
             {
            	 event.setCancelled(true);
            	 if(button != null)
            	 {
            		 return;
            	 }
            	             	 
            	 ItemStack droppedItem = event.getCursor();

                 boolean cancel = _customInventory.OnDropItem(droppedItem, event.getSlot());
                                
                 if(!cancel) 
                 {
                	return;
                 }
                 
                 final ItemStack newItem = event.getCursor().clone();
                 event.getCursor().setAmount(0);
                
                 //next frame
                 _plugin.getServer().getScheduler().runTask(_plugin, () -> 
                 {
                     _customInventory.GetInventory().setItem(event.getSlot(), newItem);
                     _customInventory.OnDropItemSet(
                    		 newItem, event.getSlot());
                 });
                 
                 return;
             }
             case MOVE_TO_OTHER_INVENTORY:
             case PICKUP_ALL:
             {
            	 HandlePickupAll(event, button);
            	 break;
             }
             case PICKUP_HALF:
             {
            	ItemStack itemInSlot = _customInventory.GetInventory().getItem(event.getSlot());
        	    if (itemInSlot != null && itemInSlot.getType().getMaxStackSize() == 1) 
        	    {
        	    	HandlePickupAll(event, button);
        	    } 
        	    else 
        	    {
        	        System.out.println("this isnt supported yet pickup type:  "+event.getAction());
        	    }
        	    break;
             }
             case DROP_ONE_SLOT:
             case DROP_ALL_SLOT:
             case HOTBAR_SWAP:
             {
            	 System.out.println("EVENT CANCELED");
            	 event.setCancelled(true);
            	 return;
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
//                ItemStack draggedItem = event.getOldCursor();
//                boolean success = CreateButtonForSlot(INV_ACTION.DRAG, draggedItem, slot);  
//                
//                if(success)
//                {
//                	event.setCancelled(true);
//                	return;
//                }
                
            	event.setCancelled(true);
            	
                ItemStack draggedItem = event.getOldCursor();
                boolean cancel = _customInventory.OnDragItem(draggedItem, slot);
                               
                if(!cancel) 
                {
                	return;
                }
                event.setCancelled(false);
                final ItemStack newItem = event.getOldCursor().clone();
                event.getOldCursor().setAmount(0);
               
                //next frame
                _plugin.getServer().getScheduler().runTask(_plugin, () -> 
                {
                    _customInventory.GetInventory().setItem(slot, newItem);
                    _customInventory.OnDragItemSet(newItem,slot);
                });
            }
        }
    	
    }
    
//    private void HandlePickupAll(InventoryClickEvent event, IBUTTONN button) {
//        
//    	int slot = event.getSlot();
//    	//button.SetItemStack(_customInventory.GetInventory().getItem(slot));
//    	boolean onPickUpAll = _customInventory.OnPickupAll(button, slot);
//
//        if (!onPickUpAll) 
//        {
//            event.setCancelled(true);
//        }
//
//        if (button != null && !button.IsPositionLocked()) 
//        {
//            RemoveButton(button);
//        }
//    }
    
//    private void HandlePickupAll(InventoryClickEvent event, IBUTTONN button) 
//    {
//        int slot = event.getSlot();
//        ItemStack itemBefore = _customInventory.GetInventory().getItem(slot);
//        System.out.println("item before" + itemBefore.getType());
//        // Custom logic to handle the pickup
//        boolean onPickUpAll = _customInventory.OnPickupAll(button, slot);
//        
//        
//        // Check if the slot is refilled immediately after picking up
//        ItemStack itemAfter = _customInventory.GetInventory().getItem(slot);
//        System.out.println("item after" + itemAfter.getType());
//        if (!itemBefore.isSimilar(itemAfter)) 
//        {
//            // Cancel the event if the slot is refilled with a different item
//        	System.out.println("cansel");
//            event.setCancelled(true);
//            return;
//        }
//
//        if (!onPickUpAll) {
//            event.setCancelled(true);
//        }
//
//        if (button != null && !button.IsPositionLocked()) {
//            RemoveButton(button);
//        }
//    }
    
    private void HandlePickupAll(InventoryClickEvent event, IBUTTONN button) 
    {
        int slot = event.getSlot();
        boolean onPickUpAll = _customInventory.OnPickupAll(button, slot);

        if (!onPickUpAll) 
        {
            event.setCancelled(true);
            return;
        }
        
        IBUTTONN b = GetButton(slot);
        
        if (b != null && b.GetItemStack().getType() == Material.AIR) 
        {
            RemoveButton(slot);
            return;
        }

        if(b == null) return;
        
        _plugin.getServer().getScheduler().runTask(_plugin, () -> 
        {
        	//System.out.println("Delay set item: "+slot + " ITEM: "+b.GetItemStack());
            _customInventory.GetInventory().setItem(slot, b.GetItemStack());
            _customInventory.GetPlayer().updateInventory();
        });
    }


    
    
//    private boolean CreateButtonForSlot(INV_ACTION inv_action, ItemStack item, int slot) {
//        System.out.println("CreateButtonForSlot called with action: " + inv_action + ", item: " + item + ", slot: " + slot);
//
//        boolean result = false;
//
//        if (inv_action == INV_ACTION.DROP) {
//            result = _customInventory.OnDropitem(item, slot);
//            System.out.println("After OnDropitem - item: " + item + ", result: " + result);
//        } else if (inv_action == INV_ACTION.DRAG) {
//            result = _customInventory.OnDragitem(item, slot);
//            System.out.println("After OnDragitem - item: " + item + ", result: " + result);
//        } else {
//            System.out.println("Action not recognized. Returning false.");
//        }
//
//        IBUTTONN button = GetButton(slot);
//        if (button != null) {
//            System.out.println("Button exists after operation - ItemStack: " + button.GetItemStack());
//        } else {
//            System.out.println("No button found at slot " + slot);
//        }
//
//        return result;
//    }
    
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
        		if(GetButton(i) != null) continue;
        		
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
    
    
    //SNAPSHOT SYSTEM
    private Map<String, Map<Integer, IBUTTONN>> _snapshots = new HashMap<>();

    public void TakeSnapshot(String snapshotName) 
    {
        Map<Integer, IBUTTONN> currentButtons = new HashMap<>(_buttons);
        _snapshots.put(snapshotName, currentButtons);
    }

    public void RestoreSnapshot(String snapshotName) 
    {
        Map<Integer, IBUTTONN> snapshot = _snapshots.get(snapshotName);
        if (snapshot != null) {
            _buttons.clear();
            _buttons.putAll(snapshot);
            UpdateButtons(true);
        }
    }
    
    public IBUTTONN GetButtonFromSnapshot(String snapshotName, int slotId) 
    {
        Map<Integer, IBUTTONN> snapshot = _snapshots.get(snapshotName);
        if (snapshot != null) 
        {
            return snapshot.get(slotId);
        }
        return null;
    }

    public Set<String> ListSnapshots() 
    {
        return _snapshots.keySet();
    }
    
    public boolean SnapshotExists(String snapshotName) 
    {
        return _snapshots.containsKey(snapshotName);
    }

    public void DeleteSnapshot(String snapshotName) 
    {
        _snapshots.remove(snapshotName);
    }
}

