package imu.imusEnchants.Inventories;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.InvUtil.CustomInventory;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.Enchants.EnchantedItem;
import imu.imusEnchants.Enchants.INode;
import imu.imusEnchants.Enchants.Node;
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
	public InventoryEnchanting()
	{
		super(ImusEnchants.Instance, "Enchanting 2v", 6 * 9);
		
		EMPTY_BLACK_SLOT = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemUtils.SetDisplayNameEmpty(EMPTY_BLACK_SLOT);
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
		InitButtons();
		
	}
	@Override
	public void OnClose()
	{
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
	public boolean OnDragitem(ItemStack item, int slot)
	{
		System.out.println("OnDrag: "+slot);
		return OnDropitem(item, slot);
	}
	
	@Override
	public boolean OnDropitem(ItemStack item, int slot)
	{
		if (slot == _enchantSlot) 
		{
			System.out.println("Looking valid item");
			if(!ItemUtils.IsTool(item)) 
			{
				System.out.println("its not tool: "+item);
				return true;
			}
			
			Button button = new Button(slot, item);
			LoadItem(button, false);
			button.SetLockPosition(false);
			AddButton(button);
			
	        //item.setAmount(0);
	        return false;
		}
		
		if(GetButton(slot) == null)
		{
			System.out.println("Empty slot");
			Material material = item.getType();
			if(!ManagerEnchants.VALID_INVENTORY_MATERIALS.contains(material)) 
			{
				System.out.println("its not valid: "+item);
				return true;
			}
			
			if(material == CONSTANTS.BOOSTER_MATERIAL) LoadBooster(item, slot);
			if(material == CONSTANTS.ENCHANT_MATERIAL) LoadEnchant(item, slot);
			
			
			return false;
		}
		
		System.out.println("Default slot");
         
        
        if (GetButton(slot) != null) return false;
		
		SetButton(item, slot);
        item.setAmount(0);
        
        return true;
	}
	
	private void LoadItem(IBUTTONN button, boolean forceReveal)
	{
		ItemStack stack = button.GetItemStack();
		
		System.out.println("=====> LOADING ITEM: "+stack.getType());
		
		_enchantedItem = new EnchantedItem(stack);
		_enchantedItem.Reveal(forceReveal);
		
		for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
		{
		    for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
		    {
		        INode node = _enchantedItem.Get_nodes()[i][j];
		        int flatIndex = i * CONSTANTS.ENCHANT_COLUMNS + j;

		        if(node.IsLocked())  continue;

		        RemoveButton(flatIndex);
		    }
		}

		UpdateButtons(true);
		
	}

	@Override
	public boolean OnPickupAll(IBUTTONN button, int slot)
	{
		
		if(slot == _enchantSlot)
		{
			EnchantedItem item = _enchantedItem;
			_enchantedItem = null;
			System.out.println("picking the enchant item");
			
			for (INode node : item.GetUnlockedNodes())
			{
				int nodeSlot = node.GetFlatIndex();
				
				Button b = new Button(nodeSlot, EMPTY_BLACK_SLOT);
				AddButton(b);
				UpdateButton(nodeSlot);
			}
			
			//UpdateButtons(true);
		}
		return true;
	}
	
	private void LoadEnchant(ItemStack stack, int slot)
	{
		Enchantment enchant = ItemUtils.GetEnchants(stack).iterator().next();
		NodeEnchant nodeEnchant = new NodeEnchant(enchant);
		
		_enchantedItem.SetNode(nodeEnchant, slot);
		_enchantedItem.SaveUnlockedNodes(GetEnchantItem());
		
		Button button = new Button(slot, stack);
		button.SetLockPosition(false);
		AddButton(button);
	}
	
	private void LoadBooster(ItemStack stack, int slot)
	{
		Button button = new Button(slot, stack);
		button.SetLockPosition(false);
		AddButton(button);
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
		ItemStack stack = button.GetItemStack();
		
		ItemUtils.AddLore(stack,Arrays.asList("davai"));
		
		Player player = (Player) event.getWhoClicked();
		player.sendMessage("ExamplePress v4");
		
		UpdateButton(button);
	}

}
