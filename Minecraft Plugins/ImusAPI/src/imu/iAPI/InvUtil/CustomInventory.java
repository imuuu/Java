package imu.iAPI.InvUtil;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Enums.INV_ACTION;
import imu.iAPI.Handelers.ButtonHandler;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.Interfaces.ICustomInventory;
import imu.iAPI.Main.ImusAPI;

public abstract class CustomInventory implements ICustomInventory
{
	private Plugin _plugin;
	private Inventory _inv;
	private Player _player;
	private String _name;
	private int _size;
	
	private ButtonHandler _buttonHandler;
	
	public CustomInventory(Plugin main, String name, int size)
	{
		_plugin = main;
		_size = AdjustSize(size);

		_name = name;
		_buttonHandler = new ButtonHandler(_plugin, this);
		_inv =  _plugin.getServer().createInventory(null, _size, _name);
	}
	
	protected void AddButton(IBUTTONN button)
	{
		_buttonHandler.AddButton(button);
	}
	
	protected IBUTTONN GetButton(int position)
	{
		return _buttonHandler.GetButton(position);
	}
	
	protected IBUTTONN RemoveButton(IBUTTONN button)
	{
		return _buttonHandler.RemoveButton(button.GetPosition());
	}
	
	protected IBUTTONN RemoveButton(int position)
	{
		return _buttonHandler.RemoveButton(position);
	}
	
	protected void UpdateButtons(boolean clearEmpties)
	{
		_buttonHandler.UpdateButtons(clearEmpties);
	}
	
	protected void UpdateButton(int position)
	{
		_buttonHandler.UpdateButton(position);
	}
	
	protected void UpdateButton(IBUTTONN button)
	{
		_buttonHandler.UpdateButton(button.GetPosition());
	}
	
	@Override
	public void OnClick(InventoryClickEvent e, IBUTTONN button)
	{
		
	}

	@Override
	public void OnOpen()
	{
		_buttonHandler.SetInventoryLock(SetInventoryLock());
		_buttonHandler.OnHandlerOpen();
		ImusAPI._instance.RegisterCustomInventory(this);
	}

	@Override
	public void OnClose()
	{
		_buttonHandler.OnHandlerClose();
		ImusAPI._instance.UnregisterCustomInventory(this);
		
	}
	
	public abstract INVENTORY_AREA SetInventoryLock();
	
	@Override
	public Inventory GetInventory()
	{
		
		return _inv;
	}
	
	@Override
	public int GetSize()
	{
		return _size;
	}
	
	@Override
	public Player GetPlayer()
	{
		return _player;
	}
	
	@Override
	public Plugin GetPlugin()
	{
		return _plugin;
	}
	
	private int AdjustSize(int size)
	{
		if(size <= 0)
		{
			size = 1;
		}
		
		int result = (size + 8) / 9 * 9;
	    if (result > 54) 
	    {
	        return 54; 
	    }
	    return result;
	}

	@Override
	public void Open(Player player)
	{
		_player = player;
		player.openInventory(_inv);
		OnOpen();
	}
	
	@Override
	public boolean OnDropitem(ItemStack item, int slot)
	{
		if (GetButton(slot) != null) return false;
		
		SetButton(item, slot);
        item.setAmount(0);
         
        return true;
	}
	
	@Override
	public boolean OnPickupAll(IBUTTONN button, int slot)
	{
		return false;
	}
	
	@Override
	public boolean OnDragitem(ItemStack item, int slot)
	{
		if (GetButton(slot) != null) return false;
		
		SetButton(item, slot);
         
        return true;
	}
	
	protected IBUTTONN SetButton(ItemStack stack, int slot)
	{
		 IBUTTONN button = new Button(slot, stack);
         _buttonHandler.AddButton(button); 
         UpdateButton(slot);
         return button;
	}
	
	//EXAMPLE
//	private void InitButtons()
//	{
//		Button button = new Button(0, new ItemStack(Material.SMOOTH_STONE), inventoryClickEvent -> 
//		{
//	        ExamplePress((Button)GetButton(0), inventoryClickEvent);
//	    });
//		
	//OR
//		Button button = new Button(0, new ItemStack(Material.SMOOTH_STONE), this::ExamplePress);
//		
//		AddButton(button);
//		
//		UpdateButtons(true);
//	}
//	
//	private void ExamplePress(Button button, InventoryClickEvent event)
//	{
//		button.GetItemStack();
//		Player player = (Player) event.getWhoClicked();
//		player.sendMessage("ExamplePress");
	
		//if want to add tooltip or something
//      UpdateButton(button);
//	}

	
}
