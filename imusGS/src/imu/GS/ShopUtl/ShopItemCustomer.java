package imu.GS.ShopUtl;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

class ShopItemCustomer extends ShopItemBase
{
	ArrayList<ItemStack> _player_itemstack_refs = new ArrayList<>();
	//public int itemSlot = slot;
	public ShopItemCustomer(int slot, ItemStack real, int amount) 
	{
		super(real, amount);

	}
	
	public void AddPlayerItemStackRef(ItemStack stack)
	{
		_player_itemstack_refs.add(stack);
	}
	
	
	
	public void AddAmountToPlayer(int amount)
	{
		AddAmount(amount);
		//System.out.println("overide addmount");
		if(amount >= 0)
		{
			PlusAmount(amount);
			return;
		}
		MinusAmount(amount);
		
		
	}
	
	void MinusAmount(int amount)
	{
		int left = Math.abs(amount);
		for(int i = _player_itemstack_refs.size()-1; i >= 0 ; --i)
		{
			ItemStack s = _player_itemstack_refs.get(i);
			int num = s.getAmount() - left;
			if(num <= 0)
			{
				s.setAmount(0);
				_player_itemstack_refs.remove(i);
				left = Math.abs(num);
				if(left == 0)
				{
					break;
				}				
			}
			else
			{
				s.setAmount(num);
				break;
			}		
		}

		
	}
	
	void PlusAmount(int amount)
	{
		System.out.println("Plus "+amount);
		int left = amount;
		for(ItemStack s : _player_itemstack_refs)
		{
			int num = 64 - s.getAmount();
			if(num == 0)
				continue;
			
			if(num > left)
				num = left;
			
			s.setAmount(s.getAmount() + num);
			
			left -= num;
			
			if(left <= 0)
				return;
		}
		
		float n2 = left / 64;
		int n3 =(int) Math.floor(n2);
		int n4 = (int)Math.ceil((n2 - n3) * left);
		ItemStack newStack;
		for(int i = 0; i < n3; ++i)
		{
			newStack= _real_stack.clone();
			newStack.setAmount(64);
			AddPlayerItemStackRef(newStack);
		}
		newStack = _real_stack.clone();
		newStack.setAmount(n4);
		AddPlayerItemStackRef(newStack);

	}

}
