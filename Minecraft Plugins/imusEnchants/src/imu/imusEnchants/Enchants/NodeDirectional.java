package imu.imusEnchants.Enchants;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import imu.imusEnchants.Enums.DIRECTION;

public abstract class NodeDirectional extends Node
{
	private int _steps = 1;
	protected DIRECTION[] _directions = new DIRECTION[] {DIRECTION.UP};
	
	public NodeDirectional()
	{
		SetLock(false);
		RandomizeDirection(InitDirectionAmount());
	}
	
	public NodeDirectional(int steps)
	{
		_steps = steps;
		RandomizeDirection(InitDirectionAmount());
		SetLock(false);
	}
	public abstract int InitDirectionAmount();

	public void RandomizeDirection(int amount) 
	{
	    DIRECTION[] directions = DIRECTION.values();
	    Random random = new Random();

	    DIRECTION[] selectedDirections = new DIRECTION[amount];

	    for (int i = 0; i < amount; i++) 
	    {
	        DIRECTION randomDirection;
	        do 
	        {
	            randomDirection = directions[random.nextInt(directions.length)];
	        } 
	        while (Arrays.asList(selectedDirections).contains(randomDirection));

	        selectedDirections[i] = randomDirection;
	    }

	    _directions = selectedDirections;
	}
	
	protected String GetSerializedDirections()
	{
		return Arrays.stream(_directions)
                .map(Enum::name)
                .collect(Collectors.joining(","));
	}
	
	protected DIRECTION[] GetDeserializedDirections(String part)
	{
		String[] directionNames = part.split(",");
        return Arrays.stream(directionNames)
                            .map(DIRECTION::valueOf)
                            .toArray(DIRECTION[]::new);
	}

	@Override
	public String Serialize()
	{
		String dir = GetSerializedDirections();
		
		return this.getClass().getSimpleName() + 
				":" + GetX() + 
				":" + GetY() + 
				":" + dir + 
				":" + _steps;
	}

	@Override
	public void Deserialize(String data)
	{
		
		String[] parts = data.split(":");
		_x = Integer.parseInt(parts[1]);
		_y = Integer.parseInt(parts[2]);
		_directions = GetDeserializedDirections(parts[3]);
		_steps = Integer.parseInt(parts[4]);
	}

}
