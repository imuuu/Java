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
import net.minecraft.world.item.enchantment.EnchantmentManager;

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
	public IBUTTONN OnDragItemSet(ItemStack stack, int slot)
	{
		return OnDropItemSet(stack, slot);
	}
	
	
	@SuppressWarnings("unused")
	@Override
	public boolean OnDropItem(ItemStack stack, int slot)
	{
		if (slot == _enchantSlot) 
		{
			if(!ManagerEnchants.Instance.IsValidToEnchant(stack))
			{
				GetPlayer().sendMessage(Metods.msgC("&cNot valid item to enchant!"));
				return false;
			}
			
	        return true;
		}
		
		if(GetButton(slot) == null)
		{
			boolean dROP = ManagerEnchants.Instance.IsValidGUIitem(GetPlayer(), _enchantedItem, stack);
			System.out.println("dROP: "+dROP);
			return dROP;
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
	public IBUTTONN OnDropItemSet(ItemStack stack, int slot)
	{
		System.out.println("DropItemSet: "+stack.getType());
		if (slot == _enchantSlot) 
		{
			Button button = new Button(slot, stack);
			button.SetLockPosition(false);
			AddButton(button);
			AddTouch(slot);
			LoadItem(button, false);
			return button;
		}
		

		if(GetButton(slot) == null)
		{
			AddTouch(slot);
			return LoadNode(stack, slot);
		}
		
		return null;
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
	
	private IBUTTONN LoadNode(INode node)
	{
		return LoadNode(node.GetItemStack(), node.GetFlatIndex());
	}
	
	private IBUTTONN LoadNode(ItemStack stack, int slot)
	{
		Material material = stack.getType();
		IBUTTONN button = null;
		if(material == CONSTANTS.BOOSTER_MATERIAL) button = LoadBooster(stack, slot);
		if(material == CONSTANTS.ENCHANT_MATERIAL) button = LoadEnchant(stack, slot);
		
		if(button == null)
		{
			INode nodee = ManagerEnchants.Instance.GetNode(stack, _enchantedItem);
			if(nodee != null) button = LoadGUIButton(nodee, stack, slot);
		}
		
		
		if(button != null)
		{
			INode node = _enchantedItem.GetNodeBySlot(slot);
			if(!node.GetGUIitemSet(_enchantedItem).getType().isAir())
			{
				System.out.println("setting Button itemStack");
				button.SetItemStack(node.GetGUIitemSet(_enchantedItem));
			}
		}
		
		return button;
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
		        
		        if(node.GetGUIitemLoad(enchantedItem).getType().isAir()) 
		        {
		        	RemoveButton(flatIndex);
		        	continue;
		        }
		        
		        if(LoadNode(node) == null)
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
			if(_enchantedItem == null) return false;
			
			if(!ManagerEnchants.Instance.IsValidGUIitem(GetPlayer(), _enchantedItem, GetInventory().getItem(slot))) 
			{
				System.out.println("not valid material: "+GetInventory().getItem(slot).getType());
				return false;
			}
			
			Node node = new Node();
			node.SetLock(false);
			_enchantedItem.SetNode(node, slot);
			
			button.SetItemStack(new ItemStack(Material.AIR)); //removes button
			return true;
		}
		
		return false;
	}
	
	private IBUTTONN LoadGUIButton(INode node, ItemStack stack, int slot)
	{	
		_enchantedItem.SetNode(node, slot);
		Button button = new Button(slot, stack);
		button.SetLockPosition(false);
		AddButton(button);		
		return button;
	}
	private IBUTTONN LoadEnchant(ItemStack stack, int slot)
	{
		NodeEnchant nodeEnchant = new NodeEnchant(stack);	
		_enchantedItem.SetNode(nodeEnchant, slot);

		Button button = new Button(slot, stack);
		button.SetLockPosition(false);
		AddButton(button);		
		return button;
	}
	
	private IBUTTONN LoadBooster(ItemStack stack, int slot)
	{
		NodeBooster nodeBooster = new NodeBooster();
		_enchantedItem.SetNode(nodeBooster, slot);
		
		Button button = new Button(slot, stack);
		button.SetLockPosition(false);
		AddButton(button);
		return button;		
	}
	private void ButtonOPreroll(Button button, InventoryClickEvent event)
	{
		if(_enchantedItem == null) return;
		
		IBUTTONN enchantItemButton = GetButton(_enchantSlot);
		RemoveButton(_enchantSlot);
		Button newButton = new Button(_enchantSlot, enchantItemButton.GetItemStack());
		newButton.SetLockPosition(false);
		AddButton(newButton);
		
		ClearTable();
		ClearTouches();
		AddTouch(_enchantSlot);
		LoadItem(newButton, true);
		
	}
	private void ButtonOpenEnchantbuy(Button button, InventoryClickEvent event)
	{
		System.out.println("Open new inv");
		OpenPage(new InventoryBuyEnchants());
	}
	
	private void ButtonEnchantItem(Button button, InventoryClickEvent event)
	{
		if(GetEnchantItem() == null || GetEnchantItem().getType().isAir()) return;
		
		_enchantedItem.SaveUnlockedNodes();
		
		IBUTTONN b = GetButton(_enchantSlot);
		_enchantedItem.ApplyEnchantsToItem();
		b.SetItemStack(_enchantedItem.GetItemStack());
		UpdateButton(b);
		
		System.out.println("     ");
		_enchantedItem.PrintNodes();
		System.out.println("     ");
//		System.out.println("  _enchantedItem   :"+_enchantedItem.GetItemStack());
//		System.out.println("     ");
//		System.out.println("     ");
//		System.out.println("     ");
//		Bukkit.getLogger().info("Enchanting the item: " + GetEnchantItem());
//		System.out.println("     ");
//		System.out.println("     ");
//		System.out.println("     ");
		
		ClearTable();
		ClearTouches();
		
		AddTouch(_enchantSlot);
		UpdateButtons(false);
	}

}
