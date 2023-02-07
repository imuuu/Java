package imu.iAPI.LootTables;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ImusLootTable<T>
{
	private List<LootTableItem<T>> items = new ArrayList<>();
	private int totalWeight = 0;
	private Random random = new Random();

	public void Add(T value, int weight)
	{
		totalWeight += weight;
		items.add(new LootTableItem<>(value, weight));
	}

	public T GetLoot()
	{
		int randomIndex = random.nextInt(totalWeight);
		
		for (LootTableItem<T> item : items)
		{
			if (randomIndex < item.weight)
			{
				return item.value;
			}
			randomIndex -= item.weight;
		}
		return null;
	}
	
	public LinkedList<T> GetLoot(int rolls)
	{
		LinkedList<T> _items = new LinkedList<>();
		
		for(int i = 0; i < rolls; i++)
		{
			_items.add(GetLoot());
		}
		
		return _items;
	}
	
	public void AddLootAsItemStack(Inventory inv, int rolls)
	{
		List<T> loots = GetLoot(rolls);
		
		for(T loot : loots)
		{
			ItemStack stack = (ItemStack)loot;
			
			if(stack == null) continue;
			
			inv.addItem(stack);
			//System.out.println("loot: "+(ItemStack)loot);
		}
	}
}
