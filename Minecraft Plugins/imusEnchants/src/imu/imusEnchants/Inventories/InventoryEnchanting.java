package imu.imusEnchants.Inventories;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.InvUtil.CustomInventory;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.Enchants.EnchantedItem;
import imu.imusEnchants.Enchants.INode;
import imu.imusEnchants.Enchants.Node;
import imu.imusEnchants.Enchants.NodeBooster;
import imu.imusEnchants.Enchants.NodeEnchant;
import imu.imusEnchants.Managers.ManagerEnchants;
import imu.imusEnchants.main.CONSTANTS;
import imu.imusEnchants.main.ImusEnchants;

public class InventoryEnchanting extends CustomInventory
{
	private ImusEnchants _main = ImusEnchants.Instance;
	
	private int _enchantSlot = GetSize()-5;
	private EnchantedItem _enchantedItem;
	
	private final ItemStack EMPTY_BLACK_SLOT;
	
	private BukkitRunnable monitoringTask;

	public InventoryEnchanting()
	{
		super(ImusEnchants.Instance, "Enchanting 2v", 6 * 9);
		
		EMPTY_BLACK_SLOT = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemUtils.SetDisplayNameEmpty(EMPTY_BLACK_SLOT);
	}
	
	  private void startMonitoring() {
	        monitoringTask = new BukkitRunnable() {
	            @Override
	            public void run() {
	                if (_enchantedItem == null) 
	                {
	                    System.out.println("EnchantedItem is null");
	                } else 
	                {
	                    System.out.println("EnchantedItem is not null");
	                    ItemStack stack = _enchantedItem.GetItemStack();
	                    if (stack == null) 
	                    {
	                        System.out.println("EnchantedItem's ItemStack is null");
	                    } else 
	                    {
	                        System.out.println("EnchantedItem's ItemStack is not null: " + stack.getType());
	                    }
	                }
	                
	                if(GetButton(_enchantSlot) != null)
	                {
	                	System.out.println("Enchant button is "+GetButton(_enchantSlot).GetItemStack().getType());
	                	return;
	                }
	                
	                System.out.println("Enchant button is NULL");
	            }
	        };
	        monitoringTask.runTaskTimer(_main, 0L, 10L); // Schedule to run every tick
	    }

	    private void stopMonitoring() {
	        if (monitoringTask != null) {
	            monitoringTask.cancel();
	        }
	    }
	@Override
	public INVENTORY_AREA SetInventoryLock()
	{
		return INVENTORY_AREA.NONE;
	}
	
	@Override
	public void OnOpen()
	{
		super.OnOpen();
		//startMonitoring();
		InitButtons();
		
	}
	@Override
	public void OnClose()
	{
		//stopMonitoring();
		super.OnClose();
	}
	
	private ItemStack GetEnchantItem()
	{
		return GetInventory().getItem(_enchantSlot);
	}
	private void ClearTable()
	{
		for(int i = 0; i < GetSize(); i++)
		{
			if(ManagerEnchants.REDSTRICTED_SLOTS.contains(i)) continue;

			Button button = new Button(i, EMPTY_BLACK_SLOT);
			AddButton(button);
		}
	}
	private void InitButtons()
	{
		ItemStack stack; 
		ItemStack emptyPurple = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		
		ItemUtils.SetDisplayNameEmpty(emptyPurple);
		
		ClearTable();
		
		Button button;
		//Empties
		button = new Button(GetSize()-3, emptyPurple);
		AddButton(button);
		
		button = new Button(GetSize()-7, emptyPurple);
		AddButton(button);
		
		button = new Button(GetSize()-13, emptyPurple);
		AddButton(button);
		
		
		if(GetPlayer().isOp())
		{
			stack = new ItemStack(Material.NETHER_STAR);
			ItemUtils.SetDisplayName(stack, "&eREROLL Slots");
			ItemUtils.AddLore(stack, "&6Visible OPs only", true);
			button = new Button(GetSize()-14, stack, inventoryClickEvent -> 
			{
				ButtonOPreroll((Button)GetButton(GetSize()-14), inventoryClickEvent);
		    });
			AddButton(button);
		}
		else
		{
			button = new Button(GetSize()-14, emptyPurple);
			AddButton(button);
		}
		
		button = new Button(GetSize()-15, emptyPurple);
		AddButton(button);
		
		stack = new ItemStack(Material.BOOKSHELF);
		ItemUtils.SetDisplayName(stack, "&eBuy Enchants");
		button = new Button(GetSize()-6, stack, inventoryClickEvent -> 
		{
			ButtonOpenEnchantbuy((Button)GetButton(GetSize()-6), inventoryClickEvent);
	    });
		AddButton(button);
		
		stack = new ItemStack(Material.ENCHANTING_TABLE);
		ItemUtils.SetDisplayName(stack, "&eEnchant");
		button = new Button(GetSize()-4, stack, inventoryClickEvent -> 
		{
			ButtonEnchantItem((Button)GetButton(GetSize()-4), inventoryClickEvent);
	    });
		AddButton(button);
		
		//slot for enchant item
		RemoveButton(_enchantSlot);
		
		UpdateButtons(true);
	}
	

	@Override
	public boolean OnDragItem(ItemStack item, int slot)
	{
		System.out.println("OnDrag: "+slot);
		return OnDropItem(item, slot);
	}
	
	@Override
	public void OnDragItemSet(ItemStack stack, int slot)
	{
		OnDropItemSet(stack, slot);
	}
	
	
	@SuppressWarnings("unused")
	@Override
	public boolean OnDropItem(ItemStack stack, int slot)
	{
		if (slot == _enchantSlot) 
		{
			if(!ItemUtils.IsTool(stack)) { return false; }
			
	        return true;
		}
		
		if(GetButton(slot) == null)
		{
			
			Material material = stack.getType();
			if(!ManagerEnchants.VALID_INVENTORY_MATERIALS.contains(material)) 
			{
				return false;
			}
			
			if(material == CONSTANTS.ENCHANT_MATERIAL)
			{
				if(!CONSTANTS.ENABLE_MULTIPLE_SAME_ENCHANTS && _enchantedItem.ContainsEnchant(stack)) 
				{
					GetPlayer().sendMessage(Metods.msgC("&2Item has already that enchant!"));
					return false;
				}
				
				return true;
			}
			
			if(material == CONSTANTS.BOOSTER_MATERIAL)
			{
				System.out.println("Booster");
				return true;
			}
			
			
			return false;
		}
		
//		System.out.println("Default slot");
//         
//        
//        if (GetButton(slot) != null) return false;
//		
//		SetButton(stack, slot);
//        stack.setAmount(0);
        
        return false;
	}
	
	@Override
	public void OnDropItemSet(ItemStack stack, int slot)
	{
		System.out.println("DropItemSet: "+stack.getType());
		if (slot == _enchantSlot) 
		{
			Button button = new Button(slot, stack);
			button.SetLockPosition(false);
			AddButton(button);
			LoadItem(button, false);
		}
		

		if(GetButton(slot) == null)
		{
			LoadNode(stack, slot);
		}
	}
	
	
	private void LoadItem(IBUTTONN button, boolean forceReveal)
	{
		ItemStack stack = button.GetItemStack();
		
		
		System.out.println("=====> LOADING ITEM: "+stack.getType());
		System.out.println("=====> LOADING ITEM: "+stack.getType());
		System.out.println("=====> LOADING ITEM: "+stack.getType());
		System.out.println("ARE SIMILAR: "+stack.isSimilar(GetEnchantItem()) +" stack: "+stack + " ================ stackEnch: "+ GetEnchantItem());
		_enchantedItem = new EnchantedItem(stack);
		_enchantedItem.SetTooltip();
		if(!_enchantedItem.IsRevealed())
		{
			
		}
		_enchantedItem.Reveal(forceReveal);
		
		LoadNodes(_enchantedItem);
		
		System.out.println("<======= LOADED: "+_enchantedItem.GetItemStack().getType());
		System.out.println("<======= LOADED: "+stack.getType());
		System.out.println("<======= LOADED: "+stack.getType());
		System.out.println("     ");
		System.out.println("     ");
		System.out.println("     ");

		
	}
	
	private boolean LoadNode(INode node)
	{
		return LoadNode(node.GetItemStack(), node.GetFlatIndex());
	}
	
	private boolean LoadNode(ItemStack stack, int slot)
	{
		Material material = stack.getType();
		
		if(material == CONSTANTS.BOOSTER_MATERIAL) return LoadBooster(stack, slot);
		if(material == CONSTANTS.ENCHANT_MATERIAL) return LoadEnchant(stack, slot);
		
		return false;
	}
	
	private void LoadNodes(EnchantedItem enchantedItem)
	{
		for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
		{
		    for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
		    {
		        INode node = enchantedItem.Get_nodes()[i][j];
		        int flatIndex = i * CONSTANTS.ENCHANT_COLUMNS + j;
		        
		        //black glass
		        if(node.IsLocked()) continue;
		        
		        if(node.GetItemStack().getType().isAir()) 
		        {
		        	RemoveButton(flatIndex);
		        	continue;
		        }
		        
		        if(!LoadNode(node))
		        {
		        	Bukkit.getLogger().info("Couldn't load Node: "+node);
		        	RemoveButton(flatIndex);
		        	continue;
		        }
		    }
		}
		
		UpdateButtons(true);
	}
	@SuppressWarnings("unused")
	@Override
	public boolean OnPickupAll(IBUTTONN button, int slot)
	{
		System.out.println("On pickup: "+button.GetItemStack().getType());
		if(slot == _enchantSlot)
		{
			EnchantedItem item = _enchantedItem;
			_enchantedItem = null;
			
			
			for (INode node : item.GetUnlockedNodes())
			{
				int nodeSlot = node.GetFlatIndex();
				
				Button b = new Button(nodeSlot, EMPTY_BLACK_SLOT);
				AddButton(b);
				UpdateButton(nodeSlot);
			}
			
			button.SetItemStack(new ItemStack(Material.AIR));
			return true;
		}
		
		if(button != null)
		{
			
			Material material = GetInventory().getItem(slot).getType();
			
			if(!ManagerEnchants.VALID_INVENTORY_MATERIALS.contains(material)) 
			{
				System.out.println("not valid material: "+material);
				return false;
			}
			
			System.out.println("SETTING NODE: "+material);
			Node node = new Node();
			node.SetLock(false);
			_enchantedItem.SetNode(node, slot);
			
			button.SetItemStack(new ItemStack(Material.AIR)); //removes button
			return true;
		}
		
		
		return false;
	}
	
	private boolean LoadEnchant(ItemStack stack, int slot)
	{
		NodeEnchant nodeEnchant = new NodeEnchant(stack);	
		_enchantedItem.SetNode(nodeEnchant, slot);

		Button button = new Button(slot, stack);
		button.SetLockPosition(false);
		AddButton(button);		
		return true;
	}
	
	private boolean LoadBooster(ItemStack stack, int slot)
	{
		NodeBooster nodeBooster = new NodeBooster();
		_enchantedItem.SetNode(nodeBooster, slot);
		
		Button button = new Button(slot, stack);
		button.SetLockPosition(false);
		AddButton(button);
		return true;		
	}
	private void ButtonOPreroll(Button button, InventoryClickEvent event)
	{
		if(_enchantedItem == null) return;
		
		IBUTTONN enchantItemButton = GetButton(_enchantSlot);
		//ItemStack stack = enchantItemButton.GetItemStack().clone();
		RemoveButton(_enchantSlot);
		Button newButton = new Button(_enchantSlot, enchantItemButton.GetItemStack());
		newButton.SetLockPosition(false);
		AddButton(newButton);
		
		ClearTable();
		LoadItem(newButton, true);
		
	}
	private void ButtonOpenEnchantbuy(Button button, InventoryClickEvent event)
	{
		ItemStack stack = button.GetItemStack();
		
		ItemUtils.AddLore(stack,Arrays.asList("davai"));
		
		Player player = (Player) event.getWhoClicked();
		player.sendMessage("ExamplePress v4");
		
		UpdateButton(button);
	}
	
	private void ButtonEnchantItem(Button button, InventoryClickEvent event)
	{
		_enchantedItem.SaveUnlockedNodes();
		
		IBUTTONN b = GetButton(_enchantSlot);
		_enchantedItem.ApplyEnchantsToItem();
		b.SetItemStack(_enchantedItem.GetItemStack());
		UpdateButton(b);
		
		System.out.println("     ");
		_enchantedItem.PrintNodes();
		System.out.println("     ");
		System.out.println("  _enchantedItem   :"+_enchantedItem.GetItemStack());
		System.out.println("     ");
		System.out.println("     ");
		System.out.println("     ");
		Bukkit.getLogger().info("Enchanting the item: " + GetEnchantItem());
		System.out.println("     ");
		System.out.println("     ");
		System.out.println("     ");
		
		ClearTable();
		UpdateButtons(false);
	}

}
