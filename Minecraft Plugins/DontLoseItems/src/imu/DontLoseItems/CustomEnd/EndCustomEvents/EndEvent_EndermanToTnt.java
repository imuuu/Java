package imu.DontLoseItems.CustomEnd.EndCustomEvents;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import imu.DontLoseItems.CustomEnd.EndEvents;
import imu.DontLoseItems.main.DontLoseItems;

public class EndEvent_EndermanToTnt extends EndEvent
{
	private LinkedList<Entity> _spawnedEntites = new LinkedList<>();
	private EntityType _spawnType;
	private World _end;
	private final int MAX_ENTITES_IN_CHUNK = 2;
	private int _tntCounter = 0;
	private int MAX_TNT = 30;
	private EntityType[] _types = 
		{

			EntityType.PRIMED_TNT,	
		};
	public EndEvent_EndermanToTnt()
	{
		super("Tnt every where!", 30);
		
		for(World w :  DontLoseItems.Instance.getServer().getWorlds() )
		{
			if(w.getEnvironment() == Environment.THE_END)
			{
				_end = w;
				break;
			}
		}
		
		ChestLootAmount = 2;
	}

	@Override
	public void OnEventStart()
	{
		_tntCounter = 0;
		_spawnType = _types[0];
		for(Entity ent : _end.getLivingEntities())
		{
			if(!EndEvents.Instance.IsPlayerUnstableArea(ent)) continue;
			
			ChanceEntity(ent);
			
		}
	}
	
	@Override
	public void OnEventEnd()
	{
		_tntCounter = 0;
		AddChestLootBaseToAll(ChestLootAmount);
		for(Entity e : _spawnedEntites)
		{
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
		
		_tntCounter++;
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
		
		if(_tntCounter > MAX_TNT)
		{
			e.setCancelled(true);
			return;
		}
		ChanceEntity(e.getEntity());

		
	}
	
	@EventHandler
	public void OnEntitySpawn(EntityExplodeEvent e)
	{
		if(e.isCancelled()) return;
		
		if(!EndEvents.Instance.IsPlayerUnstableArea(e.getEntity())) return;
		
		if(e.getEntityType() != EntityType.PRIMED_TNT) return;
		
		_tntCounter--;
	}
	@Override
	public String GetEventName()
	{
		
		return GetName();
	}

	@Override
	public String GetRewardInfo()
	{
		
		return "Chestloot base by &2+"+ChestLootAmount;
	}

	@Override
	public String GetDescription()
	{
		return "&6Whatch out tnt!";
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
