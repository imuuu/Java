package imu.DontLoseItems.CustomEnd.EndCustomEvents;

import org.bukkit.entity.Player;

public class EndEvent_RandomEntityTypeEnderman extends EndEvent
{

	public EndEvent_RandomEntityTypeEnderman()
	{
		super("Endermans chances its type", 10);
		
	}

	@Override
	public void OnEventStart()
	{
		
	}
	
	@Override
	public void OnEventEnd()
	{
		
	}

	@Override
	public String GetEventName()
	{
		
		return GetName();
	}

	@Override
	public String GetRewardInfo()
	{
		
		return "Chestloot base by +2";
	}

	@Override
	public String GetDescription()
	{
		return "&6Endermans has change the form!";
	}

	@Override
	public void OnPlayerLeftMiddleOfEvent(Player player)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnPlayerJoinMiddleOfEvent(Player player)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnOneTickLoop()
	{
		// TODO Auto-generated method stub
		
	}

}
