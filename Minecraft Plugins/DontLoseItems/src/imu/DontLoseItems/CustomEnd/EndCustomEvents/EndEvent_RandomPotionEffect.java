package imu.DontLoseItems.CustomEnd.EndCustomEvents;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EndEvent_RandomPotionEffect extends EndEvent
{
	private PotionEffect[] _potionEffectsNegatives; 
	private PotionEffect[] _potionEffectsPositive;
	
	private final double _chanceToBePositive = 40;
	
	public EndEvent_RandomPotionEffect()
	{
		super("Random potion effects", 10);
		
	}
	public void InitPotionEffects()
	{
		_potionEffectsNegatives = new PotionEffect[]
		{
			new PotionEffect(PotionEffectType.HUNGER, 		(int)(GetDuration() * 20) , 1),
			new PotionEffect(PotionEffectType.WEAKNESS, 	(int)(GetDuration() * 20) , 1),
			new PotionEffect(PotionEffectType.CONFUSION, 	(int)(GetDuration() * 20) , 1),
		};
		
		_potionEffectsPositive = new PotionEffect[]
		{
			new PotionEffect(PotionEffectType.ABSORPTION, 		(int)(GetDuration() * 20) , 1),
			new PotionEffect(PotionEffectType.SPEED, 			(int)(GetDuration() * 20) , 1),
			new PotionEffect(PotionEffectType.HEALTH_BOOST, 	(int)(GetDuration() * 20) , 1),
			new PotionEffect(PotionEffectType.HEAL, 			(int)(GetDuration() * 20) , 1),
		};
	}
	@Override
	public void OnEventStart()
	{
		InitPotionEffects();
		for(Player player : GetPlayers())
		{
			AddEffects(player);
		}
	}
	
	@Override
	public void OnEventEnd()
	{
		AddChestLootBaseToAll(2);
	}
	
	private void AddEffects(Player player)
	{
		int size = 0;
		PotionEffect effect = null;
		if(ThreadLocalRandom.current().nextInt(100) < _chanceToBePositive)
		{
			size = ThreadLocalRandom.current().nextInt(_potionEffectsPositive.length);
			effect = _potionEffectsPositive[size];
			player.addPotionEffect(effect);
			return;
		}
		
		size = ThreadLocalRandom.current().nextInt(_potionEffectsNegatives.length);
		effect = _potionEffectsNegatives[size];
		player.addPotionEffect(effect);
		
	}
	
	@Override
	public String GetEventName()
	{
		
		return GetName();
	}

	@Override
	public String GetRewardInfo()
	{
		
		return "Chestloot base by &2+2";
	}

	

	@Override
	public String GetDescription()
	{
		return "&6Random potion effects";
	}
	@Override
	public void OnPlayerLeftMiddleOfEvent(Player player)
	{
		System.out.println("player LEFT middle of event");
		
	}
	@Override
	public void OnPlayerJoinMiddleOfEvent(Player player)
	{
		System.out.println("player JOIN middle of event");
		
	}
	@Override
	public void OnOneTickLoop()
	{
		// TODO Auto-generated method stub
		
	}

}
