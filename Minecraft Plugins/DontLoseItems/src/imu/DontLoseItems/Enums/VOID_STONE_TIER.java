package imu.DontLoseItems.Enums;

public enum VOID_STONE_TIER
{
	NONE,
	NORMAL,
	RARE;
	
	public int GetIndex()
    {
    	switch (this)
		{
		case NONE: return -1;
		case NORMAL:return 0;
		case RARE:return 1;

		}
		return -1;
    }
	
	public static VOID_STONE_TIER GetFromIndex(int i)
    {
    	switch (i)
		{
		case -1: 	return NONE;
		case 0:		return NORMAL;
		case 1:		return RARE;

		}
		return NONE;
    }
}