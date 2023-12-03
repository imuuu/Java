package imu.iAPI.Buttons;

import java.util.function.Consumer;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Interfaces.IBUTTONN;

public class Button implements IBUTTONN
{
	private ItemStack _stack;
	private int _position;
	private boolean _lockPosition = true;
	private int _maxStackAmount = 1;
	private Consumer<InventoryClickEvent> _onClickAction;
	
	private ClickType _lastClickType = ClickType.UNKNOWN;
	
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
	
	@Override
	public void setMaxStackAmount(int amount)
	{
		_maxStackAmount = amount;
	}

	@Override
	public int getMaxStackAmount()
	{
		return _maxStackAmount;
	}
	
	public void SetAction(Consumer<InventoryClickEvent> onClickAction)
	{
		_onClickAction = onClickAction;
	}
	
//	@Override
//	public void OnClick(Player whoClicked, ClickType clickType)
//	{
//		
//	}

	@Override
	public int getPosition()
	{
		return _position;
	}
	
	public void setLockPosition(boolean lockPostion)
	{
		_lockPosition = lockPostion;
	}
	
	public boolean isPositionLocked()
	{
		return _lockPosition;
	}
	
	public void action(InventoryClickEvent event)
	{
		if (_onClickAction != null) 
		{
			_onClickAction.accept(event);
        }
	}

	@Override
	public ItemStack getItemStack()
	{
		return _stack;
	}
	
	@Override
	public void setItemStack(ItemStack stack)
	{
		_stack = stack;
	}

	@Override
	public void onUpdate()
	{
		
		
	}

	@Override
	public void setLastClickType(ClickType clickType)
	{
		_lastClickType = clickType;
	}

	@Override
	public ClickType getLastClickType()
	{
		return _lastClickType;
	}

	

	
	
	

}
