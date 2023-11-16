package imu.imusEnchants.Inventories;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.InvUtil.CustomInventory;
import imu.iAPI.Utilities.ItemUtils;
import imu.imusEnchants.Enchants.EnchantedItem;
import imu.imusEnchants.Enchants.Node;
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
	
	
	private void InitButtons()
	{
		ItemStack stack; 
		ItemStack emptyPurple = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		
		ItemUtils.SetDisplayNameEmpty(emptyPurple);
		
		for(int i = 0; i < GetSize(); i++)
		{
			Button button = new Button(i, EMPTY_BLACK_SLOT);
			AddButton(button);
		}
		Button button;
		//Empties
		button = new Button(GetSize()-3, emptyPurple);
		AddButton(button);
		
		button = new Button(GetSize()-7, emptyPurple);
		AddButton(button);
		
		button = new Button(GetSize()-13, emptyPurple);
		AddButton(button);
		
		button = new Button(GetSize()-14, emptyPurple);
		AddButton(button);
		
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
			LoadItem(button);
			button.SetLockPosition(false);
			AddButton(button);
			
	        //item.setAmount(0);
	        return false;
		}
		
		System.out.println("Default slot");
         
        
        if (GetButton(slot) != null) return false;
		
		SetButton(item, slot);
        item.setAmount(0);
         
        
        return true;
	}
	
	private void LoadItem(Button button)
	{
		ItemStack stack = button.GetItemStack();
		
		System.out.println("=====> LOADING ITEM: "+stack.getType());
		
		_enchantedItem = new EnchantedItem(stack);
		_enchantedItem.GenerateNodes();

		for (int i = 0; i < CONSTANTS.ENCHANT_ROWS; i++) 
		{
		    for (int j = 0; j < CONSTANTS.ENCHANT_COLUMNS; j++) 
		    {
		        Node node = _enchantedItem.Get_nodes()[i][j];
		        int flatIndex = i * CONSTANTS.ENCHANT_COLUMNS + j;

		        if(node.isLock)  continue;

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
			
			for (Node node : item.GetUnlockedNodes())
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
