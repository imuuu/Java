package imu.DontLoseItems.Enums;

public enum ITEM_RARITY
{
    Common,
    Uncommon,
    Rare,
    Epic,
    Mythic,
    Legendary;
    
	public int GetIndex()
    {
    	switch (this)
		{
		case Common:
			return 0;
		case Uncommon:
			return 1;
		case Rare:
			return 2;
		case Epic:
			return 3;
		case Mythic:
			return 4;
		case Legendary:
			return 5;
		default:
			return -1;
		}
    }
}
