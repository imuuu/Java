package imu.iAPI.Buttons;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Interfaces.IBUTTONN;

public class Button implements IBUTTONN
{
	private ItemStack _stack;
	private int _position;
	private boolean _lockPosition = true;
	private Consumer<InventoryClickEvent> _onClickAction;
	
	public Button(int position, ItemStack stack, Consumer<InventoryClickEvent> onClickAction)
	{
		_stack = stack;
		_position = position;
		_onClickAction = onClickAction;
	}
	
	public Button(int position, ItemStack stack)
	{
		_stack = stack;
		_position = position;
	}
	
	public void SetAction(Consumer<InventoryClickEvent> onClickAction)
	{
		_onClickAction = onClickAction;
	}
	
	@Override
	public void OnClick(Player whoClicked, ClickType clickType)
	{
		
	}

	@Override
	public int GetPosition()
	{
		return _position;
	}
	
	public void SetLockPosition(boolean lockPostion)
	{
		_lockPosition = lockPostion;
	}
	
	public boolean IsPositionLocked()
	{
		return _lockPosition;
	}
	
	public void Action(InventoryClickEvent event)
	{
		if (_onClickAction != null) 
		{
			_onClickAction.accept(event);
        }
	}

	@Override
	public ItemStack GetItemStack()
	{
		return _stack;
	}
	
	@Override
	public void SetItemStack(ItemStack stack)
	{
		_stack = stack;
	}

	@Override
	public void OnUpdate()
	{
		
		
	}

	
	
	

}
