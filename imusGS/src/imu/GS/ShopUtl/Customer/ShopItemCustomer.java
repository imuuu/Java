package imu.GS.ShopUtl.Customer;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import imu.GS.Main.Main;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopItemBase;
import imu.GS.ShopUtl.ItemPrice.ItemPrice;
import imu.GS.ShopUtl.ItemPrice.PriceMaterial;
import imu.GS.ShopUtl.ItemPrice.PriceMoney;
import imu.iAPI.Main.ImusAPI;

public class ShopItemCustomer extends ShopItemBase
{
	ArrayList<ItemStack> _player_itemstack_refs = new ArrayList<>();
	Player _player;
	//public int itemSlot = slot;
	public ShopItemCustomer(Main main, ShopBase shopBase,Player player,ItemStack real, int amount) 
	{
		super(main,shopBase,real, amount);
		_player = player;
		
		//AddPlayerItemStackRef("constuctiopn",real);
		//System.out.println("ShopItemCustomer created");
		
	}
	
	@Override
	protected void SetShowPrice(ItemPrice price) 
	{
		if(price instanceof PriceMaterial)
		{
			double p = price.GetPrice();
			((PriceMoney)price).SetShowPrice(p * _shopBase.get_buyM());
			return;
		}
		
	}
	
	public void AddPlayerItemStackRef(String id, ItemStack stack)
	{
		//System.out.println("stack: "+stack+" added to: "+_player_itemstack_refs.size()+ " id: "+id);
		_player_itemstack_refs.add(stack);
	}
	
	void RemovePlayerRef(int index)
	{
		//System.out.println("index: "+index+" removed");
		_player_itemstack_refs.remove(index);
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
			ItemStack s = _player_itemstack_refs.get(i); //=> s is null
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
		System.out.println("plus amount");
		int left = amount;
		Integer[] slots = ImusAPI._metods.InventoryAddItemOrDrop(_real_stack.clone(), _player,amount);
		for(Integer slot : slots)
		{
			//System.out.println("slot: "+slot);
			if(slot == null) continue;
			AddPlayerItemStackRef("plus amount1",_player.getInventory().getItem(slot));
		}
//		for(ItemStack s : _player_itemstack_refs)
//		{
//			int num = 64 - s.getAmount();
//			if(num == 0)
//				continue;
//			
//			if(num > left)
//				num = left;
//			
//			s.setAmount(s.getAmount() + num);
//			
//			left -= num;
//			
//			if(left <= 0)
//				break;
//		}
//
//		int leftOver;
//		if(left > 64)
//		{
//			leftOver = left % 64;
//		}
//		else
//		{
//			leftOver = left;
//		}
//
//		int full_stacks_amount = (left -leftOver) == 0 ? 0 : (left -leftOver) / 64;
//		ItemStack newStack;
//
//		for(int i = 0; i < full_stacks_amount; ++i)
//		{
//			newStack= _real_stack.clone();
//			newStack.setAmount(64);
//			
//			Integer[] slots = _metods.InventoryAddItemOrDrop(newStack, _player);
//			if(slots.length == 0)
//			{
//				//System.out.println("not space found minus: "+64);
//				AddAmount(64 * -1);
//				continue;
//			}
//				
//			
//			//total_amount_setted += newStack.getAmount();
//			for(int slot = 0; slot < slots.length; slot++)
//			{
//				AddPlayerItemStackRef("plus amount1",_player.getInventory().getItem(slot));
//			}
//			
//		}
//		//AddAmount(total_amount_setted);
//		if(leftOver == 0)
//			return;
//		//total_amount_setted = 0;
//		//System.out.println("N4: "+n4);
//		newStack = _real_stack.clone();
//		newStack.setAmount(leftOver);
//		Integer[] slots = _metods.InventoryAddItemOrDrop(newStack, _player);
//		if(slots.length == 0)
//		{
//			System.out.println("not space found minus2: "+leftOver);
//			AddAmount(leftOver * -1);
//			return;
//		}
//		
//		for(int slot = 0; slot < slots.length; slot++)
//		{
//			AddPlayerItemStackRef("plus amount2",_player.getInventory().getItem(slot));
//		}
	

	}

	@Override
	public JsonObject GetJsonData() {
		
		return null;
	}

	@Override
	public void ParseJsonData(JsonObject data) {
		
		
	}

	
	

}
