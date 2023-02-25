package imu.DontLoseItems.CustomEnd.EndCustomEvents;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

import imu.DontLoseItems.CustomEnd.EndEvents;
import imu.DontLoseItems.main.DontLoseItems;

public class EndEvent_RandomEntityTypeEnderman extends EndEvent
{
	private LinkedList<Entity> _spawnedEntites = new LinkedList<>();
	private EntityType _spawnType;
	private World _end;
	private final int MAX_ENTITES_IN_CHUNK = 3;
	private EntityType[] _types = 
		{
			EntityType.CREEPER,	
			EntityType.ZOMBIE,	
			EntityType.PIGLIN_BRUTE,	
			EntityType.WITCH,	
			EntityType.BAT,	
			EntityType.SHULKER,	
			EntityType.RAVAGER,	
			EntityType.CAVE_SPIDER,	
			EntityType.WITHER_SKELETON,	
			EntityType.SNOWMAN,	

		};
	public EndEvent_RandomEntityTypeEnderman()
	{
		super("Endermans chances its type", 10);
		
		for(World w :  DontLoseItems.Instance.getServer().getWorlds() )
		{
			if(w.getEnvironment() == Environment.THE_END)
			{
				_end = w;
				break;
			}
		}
	}

	@Override
	public void OnEventStart()
	{
		_spawnType = _types[ThreadLocalRandom.current().nextInt(_types.length)];
		for(Entity ent : _end.getLivingEntities())
		{
			if(!EndEvents.Instance.IsPlayerUnstableArea(ent)) continue;
			
			ChanceEntity(ent);
			
		}
	}
	
	@Override
	public void OnEventEnd()
	{
		for(Entity e : _spawnedEntites)
		{
			
			if(e == null || !e.isValid()) continue;
			
			e.remove();
		}
		_spawnedEntites.clear();
	}
	private void ChanceEntity(Entity ent)
	{
		if(ent.getType() != EntityType.ENDERMAN) return;
		
		if(!EndEvents.Instance.IsPlayerUnstableArea(ent)) return;
		
		Enderman livEnt = (Enderman)ent;
		Location loc = livEnt.getLocation().clone();
		livEnt.remove();
		Entity newEnt =_end.spawnEntity(loc, _spawnType);
		_spawnedEntites.add(newEnt);
	}
	@EventHandler
	public void OnEntitySpawn(EntitySpawnEvent e)
	{
		if(e.isCancelled()) return;
		
		if(e.getEntityType() != EntityType.ENDERMAN) return;
		
		if(e.getEntity().getLocation().getChunk().getEntities().length > MAX_ENTITES_IN_CHUNK) 
		{
			 e.setCancelled(true);
			 return;
		}
		
		ChanceEntity(e.getEntity());

		
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
		
	}

	@Override
	public void OnPlayerJoinMiddleOfEvent(Player player)
	{
		
	}

	@Override
	public void OnOneTickLoop()
	{
		
	}

}
