package imu.DontLoseItems.Events;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import imu.DontLoseItems.main.DontLoseItems;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Other.ConfigMaker;
import imu.iAPI.Other.Metods;



public class DotEvents implements Listener
{
	
	private int _durabilityDamageArmor = 1;
	private int _arrowFoodLevelReduce = 1;
	private int _foodReduceChance = 65;
	
	
	private Random _rand;
	public DotEvents()
	{
		_rand = new Random();
		System.out.println("Dot event");
	
		GetSettings();
	}
	

	
	@EventHandler
	public void OnEntityDamage(EntityDamageEvent e)
	{
		Entity entity = e.getEntity();
		
		if(!(entity instanceof Player)) return;
		
		Player player = (Player) entity;
		
		DamageCause cause = e.getCause();
		
		if(cause == DamageCause.POISON)
		{
			Add_DurabilityLost_Armor(player, _durabilityDamageArmor);
		}
		
		if(cause == DamageCause.FIRE)
		{
			Add_DurabilityLost_Armor(player, _durabilityDamageArmor);
		}
		
		if(cause == DamageCause.FIRE_TICK)
		{
			Add_DurabilityLost_Armor(player, _durabilityDamageArmor);
		}
		
		if(cause == DamageCause.WITHER)
		{
			Add_DurabilityLost_Armor(player, _durabilityDamageArmor);
		}

	}
	

	
	@EventHandler
	public void ProjectileLaunch(ProjectileHitEvent e)
	{
		if(!(e.getEntity().getShooter() instanceof Skeleton)) return;
		
		if(!(e.getHitEntity() instanceof Player)) return;
		
		Player player = (Player)e.getHitEntity();
		
		if(player.getGameMode() != GameMode.SURVIVAL ) return;
		
		if(player.isBlocking() ) 
		{
			return;
		}

		int totalLevel = Metods._ins.GetArmorSlotEnchantCount(player, Enchantment.PROTECTION_PROJECTILE);
		int chance = _foodReduceChance -(totalLevel * 3);

		if( _rand.nextInt(100) < chance)
		{
			Add_HungerDamage(player, _arrowFoodLevelReduce);
		}

	}
	
	private void Add_HungerDamage(Player player, int hungerDmg)
	{
		int newHunger = player.getFoodLevel()-hungerDmg;
		
		player.setFoodLevel(newHunger);
		
	}
	private void Add_DurabilityLost_Armor(Player player, int durabilityLost)
	{
		if(durabilityLost <= 0) return;
		
		if(player.getGameMode() != GameMode.SURVIVAL) return;
		
		ItemStack[] armors = player.getInventory().getArmorContents();
		
		ItemStack helmet = armors[3];
		ItemStack chestplate = armors[2];
		ItemStack leggings = armors[1];
		ItemStack boots = armors[0];
		
		if(ImusAPI._metods.giveDamage(helmet, durabilityLost, true) && helmet.getType() == Material.AIR) player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1);
		if(ImusAPI._metods.giveDamage(chestplate, durabilityLost, true) && chestplate.getType() == Material.AIR) player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1);
		if(ImusAPI._metods.giveDamage(leggings, durabilityLost, true) && leggings.getType() == Material.AIR) player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1);
		if(ImusAPI._metods.giveDamage(boots, durabilityLost, true) && boots.getType() == Material.AIR) player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1);

	}
	
	void GetSettings()
	{
		final String dotSettings = "dotEvents";
		ConfigMaker cm = new ConfigMaker(DontLoseItems.Instance, dotSettings+".yml");
		FileConfiguration config=cm.getConfig();
		
		String dot_dmg =dotSettings+".durabilityDamageFromDot";
		String arrow_food_levelr =dotSettings+".arrowFoodLevelReduce";
		String arrow_food_hit_chance =dotSettings+".arrowFoodHitChance";
	

		
		if(!config.contains(dotSettings+".")) 
		{
			//default values
			DontLoseItems.Instance.getServer().getConsoleSender().sendMessage(ChatColor.AQUA +"DontLoseItems : Default config made!");		
			
			config.set(dot_dmg, _durabilityDamageArmor);
			config.set(arrow_food_levelr, _arrowFoodLevelReduce);
			config.set(arrow_food_hit_chance, _foodReduceChance);

			cm.saveConfig();
			return;
		}
		
		_durabilityDamageArmor = config.getInt(dot_dmg);
		_arrowFoodLevelReduce = config.getInt(arrow_food_levelr);
		_foodReduceChance = config.getInt(arrow_food_hit_chance);


		
			
	}
	
	
	
	
	
	
}
