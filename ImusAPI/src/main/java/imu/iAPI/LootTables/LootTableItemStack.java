package imu.iAPI.LootTables;

import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class LootTableItemStack extends ImusLootTable<ItemStack>
{
    private ImusLootTable<Integer> _dropAmounts;

    public LootTableItemStack()
    {
        InitDropAmounts();
    }

    private void InitDropAmounts()
    {
        _dropAmounts = new ImusLootTable<>();
        _dropAmounts.Add(5, 100);
        _dropAmounts.Add(18, 90);
        _dropAmounts.Add(19, 60);
        _dropAmounts.Add(28, 45);
        _dropAmounts.Add(44, 28);
        _dropAmounts.Add(64, 9);
    }

    public void SetDropAmounts(ImusLootTable<Integer> dropAmounts)
    {
        _dropAmounts = dropAmounts;
    }

    @Override
    public ItemStack GetLoot()
    {
        int randomIndex = ThreadLocalRandom.current().nextInt(GetTotalWeight());

        for (LootTableItem<ItemStack> item : items)
        {
            if (randomIndex < item.weight)
            {
                ItemStack stack  = item.value;
                if(item.maxAmount != -1)
                {
                    stack.setAmount(Math.min(item.maxAmount,getRandomAmountWithinDropRange(item.maxAmount)));
                }
                else
                {
                    stack.setAmount(getRandomAmountWithinDropRange(item.maxAmount));
                }

                if(stack.getAmount() > stack.getMaxStackSize())
                {
                    stack.setAmount(stack.getMaxStackSize());
                }

                return stack.clone();
            }
            randomIndex -= item.weight;
        }
        return null;
    }

    private int getRandomAmountWithinDropRange(int maxAmount)
    {
        int amount1 = _dropAmounts.GetLoot();
        int amount2 = _dropAmounts.GetLoot();

        if(maxAmount != -1 && amount1 > maxAmount)
        {
        	amount1 = maxAmount;
        }

        if(maxAmount != -1 && amount2 > maxAmount)
        {
        	amount2 = maxAmount;
        }

        return ThreadLocalRandom.current().nextInt(Math.min(amount1, amount2), Math.max(amount1, amount2) + 1);
    }
}
