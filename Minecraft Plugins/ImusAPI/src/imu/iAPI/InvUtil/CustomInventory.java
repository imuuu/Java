package imu.iAPI.InvUtil;

import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import imu.iAPI.Buttons.Button;
import imu.iAPI.Enums.INVENTORY_AREA;
import imu.iAPI.Handelers.ButtonHandler;
import imu.iAPI.Interfaces.IBUTTONN;
import imu.iAPI.Interfaces.ICustomInventory;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Utilities.ItemUtils;

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
	public boolean OnDropItem(ItemStack stack, int slot)
	{
		if (GetButton(slot) != null) return false;

		Button button = new Button(slot, stack);
        AddButton(button);
         
        return true;
	}
	
	@Override
	public void OnDropItemSet(ItemStack stack, int slot)
	{	
		
	}
	
	@Override
	public boolean OnDragItem(ItemStack stack, int slot)
	{
		if (GetButton(slot) != null) return false;
		
		Button button = new Button(slot, stack);
        AddButton(button);
         
        return true;
	}
	
	@Override
	public void OnDragItemSet(ItemStack stack, int slot)
	{
		
	}
	
	@Override
	public boolean OnPickupAll(IBUTTONN button, int slot)
	{
		return false;
	}
	
	protected IBUTTONN SetButton(ItemStack stack, int slot)
	{
		 IBUTTONN button = new Button(slot, stack.clone());
         _buttonHandler.AddButton(button); 
         UpdateButton(slot);
         return button;
	}
	
	//>>>> Page System
	private Stack<ICustomInventory> _pageStack = new Stack<>();
	
	@Override
	public void SetPageStack(Stack<ICustomInventory> pageStack)
	{
		_pageStack = pageStack;
	}
	
	@Override
	public Stack<ICustomInventory> GetPageStack()
	{
		return _pageStack;
	}
	
    public void OpenPage(ICustomInventory newPage) 
    {
        if (this != newPage) 
        {
            _pageStack.push(this);
            CloseCurrentAndOpenNewPage(newPage);
        }
    }

    public void Back() 
    {
        if (!_pageStack.isEmpty()) 
        {
            ICustomInventory previousPage = _pageStack.pop();
            CloseCurrentAndOpenNewPage(previousPage);
        }
    }
    
    private void CloseCurrentAndOpenNewPage(ICustomInventory newPage) 
    {
        newPage.SetPageStack(_pageStack);
        newPage.Open(GetPlayer());
    }   
    
    protected void AddDefaultBackButton(int slot)
    {
    	ItemStack stack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
    	ItemUtils.SetDisplayName(stack, "&bGo Back");
    	Button button = new Button(GetSize()-9, stack, inventoryClickEvent -> 
		{
			Back();
	    });
		AddButton(button);
    }
    //<<<< Page System
	
	//BUTTON HANDLING
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
	
	//>>>> TOUCH HANDLING
	protected void AddTouch(int position) 
	{
        _buttonHandler.AddTouch(position);
    }

    protected void AddTouch(IBUTTONN button) 
    {
    	 _buttonHandler.AddTouch(button);
    }

    protected void RemoveTouch(int position) 
    {
        _buttonHandler.RemoveTouch(position);
    }
    
    protected void RemoveTouch(IBUTTONN button) 
    {
        _buttonHandler.RemoveTouch(button);
    }

    protected boolean IsTouched(int position) 
    {
        return _buttonHandler.IsTouched(position);
    }
    
    protected boolean IsTouched(IBUTTONN button) 
    {
        return _buttonHandler.IsTouched(button);
    }
    
    protected void ClearTouches() 
    {
    	_buttonHandler.ClearTouches();
    }
    //<<<< TOUCH SYSTEM
    
    
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
