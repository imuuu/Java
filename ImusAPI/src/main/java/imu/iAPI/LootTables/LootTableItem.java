package imu.iAPI.LootTables;

import org.bukkit.inventory.ItemStack;

public class LootTableItem<T>
{
	public T value;
	public int weight;

	public int maxAmount = -1;

	public LootTableItem(T value, int weight)
	{
		this.value = value;
		this.weight = weight;
	}

	public LootTableItem(T value, int weight, int maxAmount)
	{
		this.value = value;
		this.weight = weight;
		this.maxAmount = maxAmount;
	}

}
