package imu.imusEnchants.Enchants;

public class NodeBooster extends Node
{
	public int Power = 1;
	
	@Override
    public String Serialize() 
 	{
		return this.getClass().getSimpleName() + 
				":" + GetX() + 
				":" + GetY() + 
				":" + Power;
    }

    @Override
    public void Deserialize(String data) 
    {
        String[] parts = data.split(":");
        _x = Integer.parseInt(parts[1]);
        _y = Integer.parseInt(parts[2]);
        Power = Integer.parseInt(parts[3]);
    }
}
