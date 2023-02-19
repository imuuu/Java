package imu.DontLoseItems.CustomItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import imu.DontLoseItems.CustomItems.RarityItems.Hell_Double_Sword;
import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.other.RarityItem;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ImusUtilities;

public final class Hell_Sword_Controller
{

	private final String _PD_HELL_SWORD = "HELL_SWORD";
	private Cooldowns _cds;
	private RarityItem[] _hellSwords = {
//			new Hell_Double_Sword(ITEM_RARITY.Common, 		new double[] { 0 }), // not used
//			new Hell_Double_Sword(ITEM_RARITY.Uncommon, 	new double[] { 0 }), // not used
//			new Hell_Double_Sword(ITEM_RARITY.Rare, 		new double[] { 0 }), // not used
			new Hell_Double_Sword(ITEM_RARITY.Epic, 		new double[] {}),
			new Hell_Double_Sword(ITEM_RARITY.Mythic, 		new double[] {}),
			new Hell_Double_Sword(ITEM_RARITY.Legendary,	new double[] {}), };
	
	private HashSet<Throwable_DoubleSword> _throwable;
	
//	public class EntityOffset
//	{
//		Entity Entity;
//		Vector OffSet;
//		public EntityOffset(Entity entity, Vector offSet)
//		{
//			super();
//			Entity = entity;
//			OffSet = offSet;
//		}	
//	}
	private enum Throwable_State
	{
		TO_TARGET,
		DOT_DAMAGE,
		RETURNING,
	}
	public class Throwable_DoubleSword
	{
		
		public ArmorStand ArmorStand;
		public UUID Uuid;
		public String Str_uuid;
		private Location Loc_start;
		private Location Loc_end;
		public ITEM_RARITY Rarity;
		public Vector Direction;
		public Player Player;
		public Location _lastTriggeredLoc;
		public LinkedList<Entity> _entities;
		public HashSet<UUID> _ingnoreUUIDS;
		public Location TargetBlock = null;
		Throwable_State state = Throwable_State.TO_TARGET;
	
		
		public Throwable_DoubleSword(Player player, ArmorStand armorStand, Location destination,Vector dir,ITEM_RARITY rarity)
		{
			ArmorStand = armorStand;
			Rarity = rarity;
			Uuid = armorStand.getUniqueId();
			Str_uuid = Uuid.toString();
			Loc_start = armorStand.getLocation();
			Loc_end = destination;
			Direction = dir;
			Player = player;
			_entities = new LinkedList<>();
			_ingnoreUUIDS = new HashSet<>();
			_ingnoreUUIDS.add(player.getUniqueId());
		}
		
		public Location GetTargetBlock()
		{
			return TargetBlock.clone().subtract(Direction.clone().normalize().multiply(0.5f));
		}
		
		public Location GetStartLoc()
		{
			return Loc_start.clone();
		}
		
		public Location GetEndLoc()
		{
			return Loc_end.clone();
		}
	}
	public Hell_Sword_Controller()
	{
		_cds = new Cooldowns();
		_throwable = new HashSet<>();
	}

	public RarityItem GetRarityItem(ITEM_RARITY rarity)
	{
		for (RarityItem sword : _hellSwords)
		{
			if (sword.Rarity == rarity)
			{
				return sword;
			}
		}
		return null;
	}
	public ItemStack CreateHellSword(ITEM_RARITY rarity)
	{
		Hell_Double_Sword rarityItem = (Hell_Double_Sword) GetRarityItem(rarity);
		ItemStack stack = rarityItem.GetItemStack();

		ArrayList<String> lores = new ArrayList<>();
		lores.add(" ");
		lores.add("&9Able to Throw the second sword");
		lores.add(" ");
		lores.add("&9Throw distance of "+(int)rarityItem.GetThrowDistance());
		
		lores.add(" ");
		lores.add("&7'Forged in the flames of the Nether,");
		lores.add("&7this sword was once wielded by a");
		lores.add("&7great warrior who vanquished hordes");
		lores.add("&7of demons with its throwing ability.'");

		Metods._ins.SetLores(stack, lores.toArray(new String[lores.size()]), false);

		Metods._ins.setPersistenData(stack, _PD_HELL_SWORD, PersistentDataType.INTEGER, rarity.GetIndex());

		return stack;
	}

	public boolean IsHellSword(ItemStack stack)
	{
		return Metods._ins.getPersistenData(stack, _PD_HELL_SWORD, PersistentDataType.INTEGER) != null;
	}
	
	private void RemoveThrowable(Throwable_DoubleSword td)
	{
		
		td.ArmorStand.remove();
	}
	
	public void OnDisable()
	{
		Iterator<Throwable_DoubleSword> it = _throwable.iterator();
		while (it.hasNext())
		{
			Throwable_DoubleSword ar = it.next();
			ar.ArmorStand.remove();
		}
		
		_throwable.clear();
	}
	public void OnThrowLoop()
	{
		if (_throwable.isEmpty())
			return;

		Iterator<Throwable_DoubleSword> it = _throwable.iterator();
		while (it.hasNext())
		{
			Throwable_DoubleSword ar = it.next();
			ArmorStand armorStand = ar.ArmorStand;
			
			if (armorStand == null)
			{
				it.remove();
				RemoveThrowable(ar);
				continue;
			}

			Location loc = armorStand.getLocation().clone();
			
			//particles
			Particle.DustOptions dustOptions = null;
			switch (ar.state)
			{
			case TO_TARGET:
				dustOptions = new DustOptions(Color.fromBGR(169, 14, 245), 1.5f);
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0,0.6f,0), 1, dustOptions);
				loc.getWorld().spawnParticle(Particle.DRIP_LAVA, loc.clone().add(0,0.6f,0), 5, 0.3,0.2,0.3);
				break;
			case DOT_DAMAGE:
				break;
			case RETURNING:
				dustOptions = new DustOptions(Color.fromBGR(255, 80, 144), 2f);
				loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(0,0.6f,0), 1, dustOptions);
				//loc.getWorld().spawnParticle(Particle.DRIP_LAVA, loc.clone().add(0,0.6f,0), 5, 0.3,0.2,0.3);
				break;
			
			

			
			}
			
			
			if(ar.Loc_end != null && ImusUtilities.IsPositionBehind(armorStand.getLocation(), ar.GetEndLoc(), ar.Direction.clone()))
			{
				//System.out.println("Got the destination");
//				it.remove();
//				RemoveThrowable(ar);
				ar.state = Throwable_State.RETURNING;
				//continue;
			}

			if(ar.state == Throwable_State.TO_TARGET && ar.TargetBlock != null && ImusUtilities.IsPositionBehind(armorStand.getLocation(), ar.GetTargetBlock(), ar.Direction.clone()))
			{
				ar.state = Throwable_State.RETURNING;
				System.out.println("Got the destination BLOCK");
				
			}
			
			for(Entity entity : ar._entities)
			{
				if(entity == null) continue;

				Location enLoc = loc.clone();//loc.clone().add(entityOffset.OffSet);

				enLoc.setPitch(entity.getLocation().getPitch());
				enLoc.setYaw(entity.getLocation().getYaw());
				
				entity.teleport(enLoc);

			}
			
			if(ar.state == Throwable_State.DOT_DAMAGE)
			{
				System.out.println("dot");
			}
			
			if(ar.state == Throwable_State.RETURNING)
			{
				Player player = ar.Player;
				if(player == null)
				{
					it.remove();
					RemoveThrowable(ar);
					continue;
				}
				
				Vector direction = player.getLocation().subtract(loc.clone()).toVector().normalize();
				
				ar.Direction = direction;
				ar.Loc_end = null;

				float[] yawPitch = ImusUtilities.GetYawPitch(direction.clone().multiply(-1));
				Location tp = loc.clone();
				
				tp.setYaw(yawPitch[0]);
				tp.setPitch(yawPitch[1]);
				armorStand.teleport(tp.add(ar.Direction.clone().multiply(1.2)));
				
				if(armorStand.getLocation().distance(player.getLocation()) < 1)
				{
					it.remove();
					RemoveThrowable(ar);
					//System.out.println("removed by close player");
				}
				
				continue;
			}
			if(ar.state != Throwable_State.TO_TARGET) continue;
			
			armorStand.teleport(loc.clone().add(ar.Direction.clone().multiply(1)));
			for(Entity entity : loc.getChunk().getEntities())
			{
				if(entity instanceof ArmorStand) continue;
				
				if(entity.getLocation().distance(armorStand.getLocation()) > 1) continue;
				
				if(ar._ingnoreUUIDS.contains(entity.getUniqueId())) continue;
				
				entity.setFireTicks(10);
				ar._entities.add(entity);
				
			}
			

		}
	}
	
	

	private void InitThrow(Player player,ItemStack stack)
	{
		ArmorStand armorStand = (ArmorStand)player.getWorld().spawnEntity(player.getLocation().add(0, 0.5f, 0), EntityType.ARMOR_STAND);
		armorStand.setArms(true);
		armorStand.setGravity(false);
		armorStand.setVisible(false);
		armorStand.setSmall(false);
		armorStand.setMarker(true);
		//armorStand.
		//armorStand.setItemInHand(stack.clone()); //depricated
		armorStand.getEquipment().setItemInMainHand(stack.clone());
		armorStand.getEquipment().setItemInOffHand(stack.clone());;
		armorStand.getEquipment().setChestplate(stack.clone());
		//armorStand.SetItem(stack.clone()); //depricated
		
		
		//System.out.println("yaw: "+player.getLocation().getYaw() + " pitch"+player.getLocation().getPitch());
		double pitch = player.getLocation().getPitch();
		armorStand.setRightArmPose(new EulerAngle(Math.toRadians(-10+pitch), Math.toRadians(0), Math.toRadians(0)));
		armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(-10+pitch), Math.toRadians(0), Math.toRadians(0)));
		
		//Integer tierIndex = Metods._ins.getPersistenData(stack, _PD_HELL_SWORD, PersistentDataType.INTEGER);
		ITEM_RARITY rarity = RarityItem.GetRarity(stack);
		
		Metods._ins.setPersistenData(armorStand, _PD_HELL_SWORD, PersistentDataType.INTEGER, rarity.GetIndex() );
		
		int throwDistance = ((Hell_Double_Sword)GetRarityItem(rarity)).GetThrowDistance();
		Location endLoc = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(throwDistance));
		
		Vector dir = endLoc.clone().subtract(player.getLocation()).toVector().normalize();
		//player.getWorld().getBlockAt(endLoc).setType(Material.STONE);
		//System.out.println("seding start: "+armorStand.getLocation().toVector() + " sendint end: "+endLoc.toVector());
		//armorStand.setVelocity(dir);
		//_cds.setCooldownInSeconds("test", 1.8);
		Throwable_DoubleSword td = new Throwable_DoubleSword(player,armorStand, endLoc, dir,rarity);
		
		Block b = player.getTargetBlockExact(throwDistance+3);
		
//		System.out.println("======================");
//		System.out.println("b: "+b);
		
		if(b != null ) 
		{
			//System.out.println("AND DISTANCE: "+b.getLocation().distance(player.getLocation()));
			td.TargetBlock = b.getLocation();
		}
		
		_throwable.add(td);
		
		//spawnChainParticles(armorStand.getLocation().clone(), endLoc, Particle., 1);
	}
	public void OnThrow(PlayerInteractEvent e)
	{
		
		if(e.getAction() == Action.PHYSICAL)
		{
			System.out.println("physical");
		}
		if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
		
		if(!IsHellSword(e.getItem())) return;
		
		
		ItemStack stack = e.getItem();
		System.out.println(" ");
		
		InitThrow(e.getPlayer(),stack);
		
		
		
	}

	
}
