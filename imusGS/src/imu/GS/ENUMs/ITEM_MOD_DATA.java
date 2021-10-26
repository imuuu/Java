package imu.GS.ENUMs;

public enum ITEM_MOD_DATA 
{
	OWN_PRICE(0),
	MAX_AMOUNT(1),
	FILL_AMOUNT(2),
	FILL_DELAY(3),
	DISTANCE(4),
	SELL_TIME_START(5),
	SELL_TIME_END(6),
	PERMISSIONS(7),
	WORLD_NAMES(8);
	
	public final int _id;
	
	private ITEM_MOD_DATA(int id) 
	{
		_id = id;
		
	}
	
	public  int GetID(int id)
	{
		return _id;
	}
}
