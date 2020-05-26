package imu.WorldRestore.Other;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import imu.WorldRestore.main.Main;

public class ChunkHandler 
{
	Main _main = null;
	WorldGuard _wg = null;
	ItemMetods itemM = null;	
	ChunkFileHandler _chunkFileHandler = null;
	
	int _auto_chunkFixDelay = 10; // how often auto
	int _auto_chunkAmount = 2;
	
	int _chunkFixDelay = 10;
	int _chunkAmount = 2;
	
	BukkitTask _chunkfixer = null;
	public ChunkHandler(Main main) 
	{
		_main = main;
		_wg = _main.getWorldGuard();
		itemM = _main.getItemM();
		
		autoChunkFixer();
	}
	
	public void SetupHanddlers()
	{
		_chunkFileHandler = _main.getChunkFileHandler();
	}
	
	public void set_auto_chunkFixDelay(int _auto_chunkFixDelay) 
	{
		this._auto_chunkFixDelay = _auto_chunkFixDelay;
	}

	public void set_auto_chunkAmount(int _auto_chunkAmount) 
	{
		this._auto_chunkAmount = _auto_chunkAmount;
	}

	public void set_chunkFixDelay(int _chunkFixDelay) 
	{
		this._chunkFixDelay = _chunkFixDelay;
	}

	public void set_chunkAmount(int _chunkAmount) 
	{
		this._chunkAmount = _chunkAmount;
	}

	public void fixChunk(ChunkCard card)
	{
		Chunk chunk = card.getChunk();
		
		int x = chunk.getBlock(0, 0, 0).getLocation().getBlockX();
		int y = card.get_minY();
		int z = chunk.getBlock(0, 0, 0).getLocation().getBlockZ();

		int dy = y;
		int dz = z;
		
		World world = card.getWorld();
		World targetWorld = card.getTargetWorld();
		
		Block block,dblock;
		int count = 0;
		for(int i=0; i < 16; i++)
		{
			for(int j=0; j < 16;j++)
			{
				for(int k=y; k < card.get_maxY()+1; k++)
				{					
					block = world.getBlockAt(x, y, z);
					dblock = targetWorld.getBlockAt(x,y,z);
					Material dType = dblock.getType();
										
					//replace block from other world if there is difference					
					if((block.getType() != dType || block.getState() != dblock.getState()) && !isBlockProtected(block))
					{
						count++;
						
						//get main blocks front so it will speed up process
						if(dType == Material.STONE || dType == Material.DIRT || dType == Material.AIR || dType == Material.GRAVEL || dType == Material.SAND || dType== Material.COBBLESTONE)
						{
							block.setType(dType);
						}
						else 
						{
							itemM.copyBlock(dblock, block);
						}
					}
				y++;
				}			
			z++;
			y=dy;
			}		
		x++;
		z=dz;
		}
		
		
		System.out.println("TOTAL COUNT : "+count);
		
	
	}
	
	boolean isBlockProtected(Block block)
	{
		RegionContainer con = _wg.getPlatform().getRegionContainer();
		RegionQuery quarry = con.createQuery();
		ApplicableRegionSet set = quarry.getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
	
		if(set.getRegions().size() > 0)
		{
			return true;
		}
		
		return false;
	}
	public void fixChunk(ChunkCard card, int minY, int maxY)
	{
		card.set_minY(minY);
		card.set_maxY(maxY);
		fixChunk(card);
	}
	
	public void fixChunks(ArrayList<ChunkCard> cards)
	{
		if(cards.isEmpty())
		{
			return;
		}
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				int amount = _chunkAmount;
				if(cards.size() < amount)
				{
					amount = cards.size();
				}
				
				for(int i = 0; i < amount ; ++i)
				{
					fixChunk(cards.get(i));
				}
				
				for(int i = 0; i < amount ; ++i)
				{
					if(!cards.isEmpty())
					{
						cards.remove(0);
					}
					
				}
				if(cards.isEmpty())
				{
					cancel();
					System.out.println("fixing chunks: local runnable canceled");
				}
				
			}
		}.runTaskTimer(_main, 0, 20 *_chunkFixDelay);
	}
	
	void autoChunkFixer()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				if(_chunkFileHandler == null)
				{
					System.out.println("cfh is null in chunkhandler");
					return;
				}
				ItemMetods itemM = new ItemMetods(_main);
				long start = System.currentTimeMillis();
				ArrayList<ChunkCard> cards = _chunkFileHandler.getBeingRepairedChunks(_auto_chunkAmount);
				long elapsedTime = System.currentTimeMillis()-start;
				System.out.println("TIME WAS: "+elapsedTime);	
				
				itemM.printArray("chunks", cards.toArray());
				fixChunks(cards);
				
			}
		}.runTaskTimer(_main, 10, 20*_auto_chunkFixDelay);
	}
	
	
	//void do_not_use_fixChunkExperiment(ChunkCard card)
	//{
	//	
	//	Chunk fromBukkitChunk = card.getTargetWorld().getChunkAt(card.getChunk().getX(), card.getChunk().getZ());
	//	Chunk toBukkitChunk = card.getChunk();
	//	fromBukkitChunk.load();
	//	toBukkitChunk.load();
	//	
	//	
	//	CraftChunk fromCC = (CraftChunk) fromBukkitChunk;
	//	CraftChunk toCC = (CraftChunk) toBukkitChunk;
	//	net.minecraft.server.v1_15_R1.Chunk fromChunk = fromCC.getHandle();
	//	ChunkSection[] sec = fromChunk.getSections().clone();
	//	net.minecraft.server.v1_15_R1.Chunk toChunk = toCC.getHandle();
	//	//ChunkSection[] x = toChunk.getSections();
	//	//x = sec.clone();
	//	
	//}
	
}
