package imus.iFishing.Events;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftFishHook;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Other.Metods;
import imu.iFishing.Main.ImusFishing;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityFishingHook;

public class FishingEvents implements Listener
{
	public FishingEvents() 
	{
		Bukkit.getPluginManager().registerEvents(this, ImusFishing._instance);
	}
	
	
	@EventHandler
	public void OnFishing(PlayerFishEvent e)
	{

		if(e.getState() != State.BITE) return;;
		
		CraftFishHook  hook = (CraftFishHook)e.getHook();
		
		EntityFishingHook eHook =  hook.getHandle();
		Location loc = e.getPlayer().getLocation();
		ItemStack fishingRod = e.getPlayer().getInventory().getItemInMainHand();
		int unbLvl = Metods._ins.GetItemStackEnchantCount(fishingRod, Enchantment.DURABILITY);

		double random = ThreadLocalRandom.current().nextDouble(0, 100+1);
		//System.out.println("random: "+random + " level: "+unbLvl + "chance: "+(unbLvl != 0 ? (100.0/unbLvl) : 0.0));
		if( random > (unbLvl != 0 ? (100/unbLvl) : 0))
		{
			Metods._ins.giveDamage(fishingRod, 1, true);
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				eHook.a(CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR)));
			}
		}.runTaskLater(ImusFishing._instance, 3);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				
				if(e.getPlayer().getInventory().getItemInMainHand().getType() != Material.FISHING_ROD) return;
				
				CraftWorld nmsWorld = (CraftWorld)e.getPlayer().getWorld();
				
				EntityHuman eHuman = ((CraftPlayer)e.getPlayer()).getHandle();
				EntityFishingHook h =  new EntityFishingHook(eHuman,nmsWorld.getHandle(), 0, 0);

				nmsWorld.addEntity(h, null);
				h.a(loc.getX(), loc.getY(), loc.getZ());
				
			}
		}.runTaskLater(ImusFishing._instance, 30);

	}
}
