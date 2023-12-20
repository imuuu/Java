package imu.iAPI.LootTables;

import java.util.HashMap;
import java.util.Map;

public class Manager_ImusLootTable
{
    private Map<String, ImusLootTable<?>> lootTables = new HashMap<>();

    public Manager_ImusLootTable()
    {
    }

    public void addLootTable(String name, ImusLootTable<?> lootTable)
    {
        lootTables.put(name, lootTable);
    }

    public ImusLootTable<?> getLootTable(String name)
    {
        return lootTables.get(name);
    }

    public void removeLootTable(String name)
    {
        lootTables.remove(name);
    }
}
