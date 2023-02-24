package imu.DontLoseItems.Events;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

public class ElytraGenerationEvents implements Listener
{

	private int elytraChance = 25;
	private ItemStack replacement = new ItemStack(Material.DIAMOND_BLOCK, 1);

	public ElytraGenerationEvents()
	{
		// TODO get elytrachance from config
	}

	@EventHandler
	public void ElytraGenerationEvent(ChunkPopulateEvent e)
	{
		if (e.getWorld().getEnvironment() != World.Environment.THE_END) return;
		
		
		List<ItemFrame> frames = stream(e.getChunk()
				.getEntities())
				.sequential()
				.filter(x -> x.getType() == EntityType.ITEM_FRAME)
				.map(x -> (ItemFrame) x)
				.collect(Collectors.toList());
		for (ItemFrame f : frames)
		{
			if (ThreadLocalRandom.current().nextInt(100) > elytraChance)
			{
				f.setItem(replacement);
			}
		}
	}
}
