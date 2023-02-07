package imu.iAPI.LootTables;

public class LootTableItem<T>
{
	public T value;
	public int weight;

	public LootTableItem(T value, int weight)
	{
		this.value = value;
		this.weight = weight;
	}
}
