package imu.imusEnchants.Enums;

import java.util.Random;

public enum MATERIAL_SLOT_RANGE
{
	NETHERITE(10, 20),
	DIAMOND(4, 16),
    IRON(2, 14),
    GOLD(2, 14),
    STONE(1,10),
    WOOD(1, 8);

    private final int minSlots;
    private final int maxSlots;

    MATERIAL_SLOT_RANGE(int minSlots, int maxSlots) 
    {
        this.minSlots = minSlots;
        this.maxSlots = maxSlots;
    }

    public int GetRandomSlots() {
        return new Random().nextInt((maxSlots - minSlots) + 1) + minSlots;
    }
}
