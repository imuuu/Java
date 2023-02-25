package imu.DontLoseItems.CustomItems;

import imu.DontLoseItems.main.DontLoseItems;
import imu.iAPI.Main.ImusAPI;
import imu.iAPI.Utilities.ImusUtilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class VoidTotemController {
    private static VoidTotemController instance;

    private final String defaultWorld = "World";

    public VoidTotemController() 
    {
        instance = this;
        Bukkit.addRecipe(GetRecipe());
    }

    public void findSafeBlock(Player player) 
    {
        final long start = System.currentTimeMillis();

        Location mid = player.getLocation();
        mid.setY(50);
        World end = mid.getWorld();
        assert end != null;

        int radius = 10;
        int step = 1;
        int total = 0;
        

        new BukkitRunnable() 
        {
			
			@Override
			public void run()
			{
				 LinkedList<Location> list = ImusUtilities.CreateSphere(mid, 50, ImusAPI.AirHashSet, null);
				 
				
				 
				 new BukkitRunnable() 
				 {
					
					@Override
					public void run()
					{
						 if(list.size() <= 0) 
						 {
							 Location locc = player.getBedSpawnLocation() != null ? player.getBedSpawnLocation() : Objects.requireNonNull(Bukkit.getServer().getWorld(defaultWorld)).getSpawnLocation();
							 player.teleport(locc);
							 System.out.println("not found");
							 return ;
						 }
						 
						System.out.println("found block"+ list.get(0).toVector()+" type: "+list.get(0).getBlock());
						player.teleport(findTop(list.get(0)));
					}
				}.runTask(DontLoseItems.Instance);
			}
		}.runTaskAsynchronously(DontLoseItems.Instance);
        
    }

    private Location findTop(Location bottom) 
    {
    	bottom.setY(255);
    	
    	while(bottom.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
    	{
    		bottom.add(0,-1,0);
    		
    		if( bottom.getY() > 0) break;
    	}
        return bottom;
    }

    public SmithingRecipe GetRecipe() {
        NamespacedKey key = new NamespacedKey(DontLoseItems.Instance, "voidtotem");
        RecipeChoice.MaterialChoice totemChoice = new RecipeChoice.MaterialChoice(Material.TOTEM_OF_UNDYING);
        RecipeChoice.MaterialChoice elytraChoice = new RecipeChoice.MaterialChoice(Material.ELYTRA);


        return new SmithingRecipe(
                key,
                GetVoidtotemItem(),
                totemChoice,
                elytraChoice);
    }

    private ItemStack GetVoidtotemItem() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        assert meta != null : "ItemMeta for voidtotem is null!";

        meta.setDisplayName(ChatColor.of(new Color(122, 55, 173)) + "Void Totem");
        meta.setLore(List.of("Why do we fall?", "So we can learn to pick ourselves up."));
        meta.getPersistentDataContainer().set(new NamespacedKey(DontLoseItems.Instance, "totemtype"), PersistentDataType.STRING, "void");

        item.setItemMeta(meta);
        return item;
    }

    public VoidTotemController instance() {
        return instance;
    }
}
