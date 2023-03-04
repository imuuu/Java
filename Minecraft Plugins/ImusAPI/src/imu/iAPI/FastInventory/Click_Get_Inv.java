package imu.iAPI.FastInventory;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;

public class Click_Get_Inv extends CustomInvLayout
{
	public ArrayList<ItemStack> _stacks;
	public Click_Get_Inv(Player player, String name, ArrayList<ItemStack> stacks)
	{
		super(ImusAPI._instance, player, name+ " &3=> &bClick &3to Copy", stacks.size());
		_stacks = stacks;
	}

	enum BUTTON implements IButton
	{
		NONE, ITEM,
	}

	@Override
	public void invClosed(InventoryCloseEvent e)
	{

	}

	@Override
	public void onClickInsideInv(InventoryClickEvent e)
	{
		BUTTON button = GetButtonPress(e);
		Integer slot = GetSLOT(e.getCurrentItem());
		if (slot == null)
			return;

		switch (button)
		{
		case ITEM:
			ItemStack stack = _stacks.get(slot).clone();
			int amount = 1;
			
			if(		e.getClick() == ClickType.RIGHT 
					&& !Metods._ins.isArmor(stack) 
					&& !Metods._ins.isTool(stack)) amount = 64;
			
			Metods._ins.InventoryAddItemOrDrop(stack, _player, amount);
			return;

		case NONE:
			return;

		default:
			break;

		}
	}

	BUTTON GetButtonPress(InventoryClickEvent e)
	{
		ItemStack stack = e.getCurrentItem();
		if (stack == null)
			return BUTTON.NONE;

		String buttonName = getButtonName(e.getCurrentItem());
		if (buttonName == null)
			return BUTTON.NONE;

		return BUTTON.valueOf(buttonName);
	}

	@Override
	public void setupButtons()
	{
		ItemStack empty = Metods.setDisplayName(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE), " ");

		for (int i = 0; i < _size; i++)
		{
			if (i >= _stacks.size())
			{

				SetITEM(i, empty);
				continue;
			}
			ItemStack stack = _stacks.get(i).clone();
			SetButton(stack, BUTTON.ITEM);
			SetITEM(i, stack);
		}
	}

	@Override
	public void openThis()
	{
		super.openThis();
		setupButtons();
	}

}
