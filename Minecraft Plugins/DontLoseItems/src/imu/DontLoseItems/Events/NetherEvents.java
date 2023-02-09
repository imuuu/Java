package imu.DontLoseItems.Events;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import imu.DontLoseItems.main.DontLoseItems;
import imu.iAPI.LootTables.ImusLootTable;
import imu.iAPI.Other.ConfigMaker;
import imu.iAPI.Other.Metods;
import imu.iAPI.Utilities.ImusUtilities;


public class NetherEvents implements Listener
{

	private Random _rand;
	
	private World _nether;
	
	private boolean _enableMagmaCube = true;
	private boolean _enableBlazeFireBallSpread = true;
	private boolean _enableGhastFireBall = true;
	private boolean _enableWaterBottleBuff = true;
	private boolean _enableShieldBlockArrow = true;
	private boolean _enableWitherSpeed = true;
	private boolean _enableHoglinSpeed = true;
	private boolean _enableDoubleFireDamage = true;
	
	private boolean _enableOnlySmallMagmaCubeLava = true;
	
	private long _ghastMaterialDelayTicks = 12;
	
	private final int _maxWaterBottleUses = 3;
	private int _chanceEntityHaveShield = 15;
	private final int _radiusOfGhastBall = 4;
	private final int _radiusOfBlazeBall = 2;
	
	
	private final Material[] _ghastBallMaterials = new Material[]
	{
		Material.BLACK_TERRACOTTA,
		Material.GRAY_TERRACOTTA,
		Material.PURPLE_TERRACOTTA,
		Material.RED_TERRACOTTA,
		Material.ORANGE_TERRACOTTA,
		Material.YELLOW_TERRACOTTA,
		Material.WHITE_TERRACOTTA,
		Material.AIR,
	};
	
	private HashSet<Location> _mutationBlock;
	
	private HashSet<EntityType> _validEquipEnteties;
	

	public NetherEvents()
	{
		_rand = new Random();

		// GetSettings();
		_mutationBlock = new HashSet<>();
		_nether = Bukkit.getWorld("world_nether");
		
		GetSettings();
		
		InitEntityTypes();
		
	}
	
	private void InitEntityTypes()
	{
		_validEquipEnteties = new HashSet<>();
		_validEquipEnteties.add(EntityType.WITHER_SKELETON);
		_validEquipEnteties.add(EntityType.PIGLIN);
		_validEquipEnteties.add(EntityType.ZOMBIFIED_PIGLIN);
		_validEquipEnteties.add(EntityType.ZOMBIFIED_PIGLIN);
		_validEquipEnteties.add(EntityType.SKELETON);
	}
	@SuppressWarnings("unused")
	private boolean IsNether(World world)
	{
		return world == _nether;
	}
	private boolean IsNether(Entity entity)
	{
		return entity.getWorld() == _nether;
	}
	@SuppressWarnings("unused")
	private boolean IsNether(Block block)
	{
		return block.getWorld() == _nether;
	}
	@SuppressWarnings("unused")
	private boolean IsNether(Location loc)
	{
		return loc.getWorld() == _nether;
	}
	
	

	
	@EventHandler
	public void OnEntitySpawn(CreatureSpawnEvent e) 
	{
		if(!IsNether(e.getEntity())) return;
		
		EntityType entityTypo = e.getEntityType();
	    
		if (entityTypo == EntityType.WITHER_SKELETON && _enableWitherSpeed) 
	    {
	        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
	    }
		
		if (entityTypo == EntityType.HOGLIN && _enableHoglinSpeed) 
	    {
	        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
	    }
	    
	    if(!_enableShieldBlockArrow) return;
	    
	    if(!_validEquipEnteties.contains(entityTypo)) return;
	    
	    if (_rand.nextInt(100) < _chanceEntityHaveShield) 
	    {
	    	ItemStack shield = new ItemStack(Material.SHIELD);

	    	e.getEntity().getEquipment().setItemInOffHand(shield);

        }
	}
	
	@EventHandler
	public void OnBlockingArrow(EntityDamageByEntityEvent e) 
	{
		if(!_enableShieldBlockArrow) return;
		
		if(!IsNether(e.getEntity())) return;
		
		if(!_validEquipEnteties.contains(e.getEntityType())) return;
		
		if(e.getEntity() instanceof Player) return;
		
	    if (e.getDamager().getType() != EntityType.ARROW) return;
	    
	    if(!(e.getEntity() instanceof LivingEntity)) return;

	    
	    
	    LivingEntity entity = (LivingEntity)e.getEntity();
	    
        if (entity.getEquipment().getItemInOffHand().getType() != Material.SHIELD) return;
       
        
        Arrow arrow = (Arrow) e.getDamager();
        Vector velocity = arrow.getVelocity();
        arrow.setBounce(false);
        arrow.setShooter(entity);
        arrow.setVelocity(velocity.multiply(10));
        e.setCancelled(true);
	}
	
	 @EventHandler
	  public void onHoglinAttack(EntityDamageByEntityEvent e) 
	 {
	    if (e.getEntity() instanceof Player && e.getDamager().getType() == EntityType.HOGLIN) 
	    {
	    	Player player = (Player) e.getEntity();
	    	Hoglin hoglin = (Hoglin) e.getDamager();
	    	
	        Vector direction = player.getLocation().toVector().subtract(hoglin.getLocation().toVector());
	        direction.setY(8);
	        direction.normalize();
	        
	        
	        
	        player.setVelocity(direction);
	    }
	  }
	
	@EventHandler
	public void OnEntityDamage(EntityDamageEvent e)
	{
		if(!IsNether(e.getEntity())) return;
		
		if(!_enableDoubleFireDamage) return;
		
		Entity entity = e.getEntity();
		
		if(!(entity instanceof Player)) return;
		
		//Player player = (Player) entity;
		
		DamageCause cause = e.getCause();
		
		if(cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK)
		{
			e.setDamage(e.getDamage() * 2);
		}

		
		

	}
	
	@EventHandler
	public void OnProtectileHit(ProjectileHitEvent e)
	{
		if (!(e.getEntity() instanceof Fireball))
		{
			return;
		}
		
		if(!IsNether(e.getEntity())) return;
		
		Block block = e.getHitBlock();
		
		boolean isSearchedBlock = false;
		if(block == null)
		{
			if(e.getHitEntity() == null || !_enableGhastFireBall) return; //here is chast only thing!
			
			Location testLoc = e.getHitEntity().getLocation().clone();
			
			 for(int i = 0; i < 3; ++i)
			 {
				 block =  testLoc.add(0, -1, 0).getBlock();
				 
				 if(block != null && block.getType() != Material.AIR)
					 break;
			 }
			 
			 if(block == null || block.getType() == Material.AIR)  return;
			
			 isSearchedBlock = true;
		}
		
		Location hitLoc = block.getLocation();
		
		Fireball fireball = (Fireball)e.getEntity();
		
		if(fireball.getShooter() instanceof Ghast && _enableGhastFireBall)
		{
			
			LinkedList<Block> blocks = new LinkedList<>();
			//List<Block> blocks = tnt.GetBlocks(e.getLocation());
    		
			for(Location loc : ImusUtilities.CreateSphere(hitLoc, _radiusOfGhastBall))
			{
				if(loc.getBlock() == null || loc.getBlock().getType() == Material.AIR) continue;
				
				 //.setType(Material.STONE);
				
				EntityExplodeEvent explodeEvent = new EntityExplodeEvent(fireball, loc, blocks, 0);
	    		Bukkit.getServer().getPluginManager().callEvent(explodeEvent);
	    		
	    		if(explodeEvent.isCancelled()) return;
	    		
	    		if(_mutationBlock.contains(loc)) continue;
	    		
	    		_mutationBlock.add(loc);
	    		
				blocks.add(loc.getBlock());
			}
			
			ChangeBlockType(blocks, _ghastBallMaterials, _ghastMaterialDelayTicks, 0);
			
			return;
		}
		
		if(fireball.getShooter() instanceof Blaze && _enableBlazeFireBallSpread && !isSearchedBlock)
		{
			for(Location loc : ImusUtilities.CreateSphere(hitLoc, _radiusOfBlazeBall))
			{
				if(loc.getBlock() == null || loc.getBlock().getType() == Material.AIR) continue;
				
				Block firePos = loc.add(0, 1, 0).getBlock();
				
				if(firePos == null || firePos.getType() != Material.AIR) continue;
				

				firePos.setType(Material.FIRE);
			}
			return;
		}

	}
	
	@EventHandler
	public void OnEntityDeath(EntityDeathEvent e)
	{
		Entity entity = e.getEntity();

		// if(!entity.getWorld().getName().matches("world_nether")) return;
		if(!IsNether(entity)) return;
		
		if (entity instanceof MagmaCube && _enableMagmaCube)
		{
			MagmaCube mCube = (MagmaCube) entity;

			//System.out.println("size: " + mCube.getSize());

			if (mCube.getSize() > 1 && _enableOnlySmallMagmaCubeLava)
				return;

			Location loc = mCube.getLocation();

			Block block = loc.getBlock();
			
			block.setType(Material.LAVA);

		}
		

	}
	
	@EventHandler
	public void onPlayerDrinkWaterBottle(PlayerItemConsumeEvent e) 
	{
		if(!_enableWaterBottleBuff) return;
		
	    Player player = e.getPlayer();
	    
	    if(!IsNether(player)) return;
	    
	    ItemStack stack = e.getItem();
	    
	    if (stack.getType() != Material.POTION)  return;
	    
	    PotionMeta potionMeta = (PotionMeta) stack.getItemMeta();
	    PotionData potionData = potionMeta.getBasePotionData();
	    
	    if (potionData.getType() != PotionType.WATER)  return;
	      
	    player.setFireTicks(0);
        PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0);
        PotionEffect existingEffect = player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE);

        if (existingEffect != null) 
        {
          fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE,
          existingEffect.getDuration() + fireResistance.getDuration(),
          fireResistance.getAmplifier());
        }
        player.addPotionEffect(fireResistance);
        
        int uses = _maxWaterBottleUses;
        
        Integer us = Metods._ins.getPersistenData(stack, "water_bottle_uses", PersistentDataType.INTEGER);
        
        if(us != null) uses = us;
        
        uses --;
        
        stack = player.getInventory().getItemInMainHand();
        
        Metods.setDisplayName(stack, ChatColor.BLUE+"Water Bottle "+ChatColor.DARK_AQUA+uses +ChatColor.BLUE+" / "+ChatColor.DARK_PURPLE+_maxWaterBottleUses);
        
        Metods._ins.setPersistenData(stack, "water_bottle_uses", PersistentDataType.INTEGER, uses);
        
        if(uses <= 0) 
        {
        	//stack.setType(Material.POTION);
        	Metods._ins.removePersistenData(stack, "water_bottle_uses");
        	return;
        }

        e.setCancelled(true);
        
	  }
	
	@EventHandler
	public void ProjectileLaunch(ProjectileHitEvent e)
	{
		if (!(e.getEntity().getShooter() instanceof Skeleton))
			return;

		if (!(e.getHitEntity() instanceof Player))
			return;

		Player player = (Player) e.getHitEntity();

		if (player.getGameMode() != GameMode.SURVIVAL)
			return;

		if (player.isBlocking())
		{
			return;
		}

	}
	private void ChangeBlockType(Iterable<Block> list, Material[] mat_list, long delay, int index) {
	    
		Bukkit.getScheduler().runTaskLater(DontLoseItems.Instance, () -> 
	    {
	    	boolean remove = false;
	    	
	    	
	    	int newIndex  = index + 1;
	    	
	    	if(newIndex < mat_list.length)
	    	{
	    		ChangeBlockType(list, mat_list, delay, newIndex);
	    	}
	    	else
	    	{
	    		remove = true;
	    	}
	    	
	    	for(Block block : list)
	    	{
	    		if(remove) _mutationBlock.remove(block.getLocation());
	    		
	    		Material mat = block.getType();
	    		if(block == null || mat == Material.AIR) continue;
	    		
	    		if(mat.getBlastResistance() >= 1200) continue;
	    		
	    		if(mat.isInteractable())
	    		{
	    			block.breakNaturally();
	    			continue;
	    		}
	    		
	    		block.setType(mat_list[index]);
	    		
	    		
	    	}
	    	
	        
	    }, delay);
	}
	void GetSettings()
	{
		final String netherSettings = "NetherSettings";
		ConfigMaker cm = new ConfigMaker(DontLoseItems.Instance, netherSettings + ".yml");
		FileConfiguration config = cm.getConfig();

		// String dot_dmg = netherSettings+".durabilityDamageFromDot";
		
		
		if (!config.contains(netherSettings + "."))
		{
			// default values
			DontLoseItems.Instance.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "DontLoseItems : Default config made!");

			cm.saveConfig();
			//return;
		}
		
		String enableMagmaCube = netherSettings+".enableMagmaCubeLava";
		if(!config.contains(enableMagmaCube+".")) config.set(enableMagmaCube, _enableMagmaCube);
		
		String enableBlazeFireBallSpread = netherSettings + ".enableBlazeFireBallSpread";
		if(!config.contains(enableBlazeFireBallSpread)) config.set(enableBlazeFireBallSpread, _enableBlazeFireBallSpread);

		String enableGhastFireBall = netherSettings + ".enableGhastFireBall";
		if(!config.contains(enableGhastFireBall)) config.set(enableGhastFireBall, _enableGhastFireBall);

		String enableWaterBottleBuff = netherSettings + ".enableWaterBottleBuff";
		if(!config.contains(enableWaterBottleBuff)) config.set(enableWaterBottleBuff, _enableWaterBottleBuff);

		String enableShieldBlockArrow = netherSettings + ".enableShieldBlockArrow";
		if(!config.contains(enableShieldBlockArrow)) config.set(enableShieldBlockArrow, _enableShieldBlockArrow);

		String enableWitherSpeed = netherSettings + ".enableWitherSpeed";
		if(!config.contains(enableWitherSpeed)) config.set(enableWitherSpeed, _enableWitherSpeed);

		String enableHoglinSpeed = netherSettings + ".enableHoglinSpeed";
		if(!config.contains(enableHoglinSpeed)) config.set(enableHoglinSpeed, _enableHoglinSpeed);

		String enableDoubleFireDamage = netherSettings + ".enableDoubleFireDamage";
		if(!config.contains(enableDoubleFireDamage)) config.set(enableDoubleFireDamage, _enableDoubleFireDamage);

		String enableOnlySmallMagmaCubeLava = netherSettings + ".enableOnlySmallMagmaCubeLava";
		if(!config.contains(enableOnlySmallMagmaCubeLava)) config.set(enableOnlySmallMagmaCubeLava, _enableOnlySmallMagmaCubeLava);
		
		cm.saveConfig();
		
		_enableMagmaCube = config.getBoolean(enableMagmaCube);
		_enableBlazeFireBallSpread = config.getBoolean(enableBlazeFireBallSpread);
		_enableGhastFireBall = config.getBoolean(enableGhastFireBall);
		_enableWaterBottleBuff = config.getBoolean(enableWaterBottleBuff);
		_enableShieldBlockArrow = config.getBoolean(enableShieldBlockArrow);
		_enableWitherSpeed = config.getBoolean(enableWitherSpeed);
		_enableHoglinSpeed = config.getBoolean(enableHoglinSpeed);
		_enableDoubleFireDamage = config.getBoolean(enableDoubleFireDamage);
		_enableOnlySmallMagmaCubeLava = config.getBoolean(enableOnlySmallMagmaCubeLava);

		
		

	}

}
