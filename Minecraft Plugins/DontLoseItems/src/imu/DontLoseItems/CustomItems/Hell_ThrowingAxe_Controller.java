package imu.DontLoseItems.CustomItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import imu.DontLoseItems.CustomItems.Arrow_ReflectController.ArrowReflect;
import imu.DontLoseItems.CustomItems.RarityItems.Hell_Double_Sword;
import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.DontLoseItems.other.RarityItem;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ImusUtilities;

public final class Hell_ThrowingAxe_Controller
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
	public Hell_ThrowingAxe_Controller()
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
//			if(armorStand.getLocation().distance(ar.GetEndLoc()) < 1)
//			{
//				it.remove();
//				RemoveThrowable(ar);
//				continue;
//			}
			

			if(ImusUtilities.IsPositionBehind(armorStand.getLocation(), ar.GetEndLoc(), ar.Direction.clone()))
			{
				System.out.println("Got the destination");
				it.remove();
				RemoveThrowable(ar);
				continue;
			}
			
			EulerAngle rot = armorStand.getRightArmPose();
			EulerAngle rotNew = rot.add(20, 0, 0);
			armorStand.setRightArmPose(rotNew);
			
			armorStand.teleport(loc.clone().add(ar.Direction.clone().multiply(0.1)));
			
//			if(_cds.isCooldownReady("test"))
//			{
//				System.out.println("Destroyed by cd");
//				it.remove();
//				RemoveThrowable(ar);
//				continue;
//			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void InitThrow(Player player,ItemStack stack)
	{
		ArmorStand armorStand = (ArmorStand)player.getWorld().spawnEntity(player.getLocation().add(0, 0.5f, 0), EntityType.ARMOR_STAND);
		armorStand.setArms(true);
		armorStand.setGravity(false);
		armorStand.setVisible(true);
		armorStand.setSmall(false);
		armorStand.setMarker(true);
		armorStand.setItemInHand(stack.clone()); //depricated
		
		
		//System.out.println("yaw: "+player.getLocation().getYaw() + " pitch"+player.getLocation().getPitch());
		double pitch = player.getLocation().getPitch();
		armorStand.setRightArmPose(new EulerAngle(Math.toRadians(-10+pitch), Math.toRadians(0), Math.toRadians(0)));
		
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
		_throwable.add(new Throwable_DoubleSword(player,armorStand, endLoc, dir,rarity));
		
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
