package imu.WorldRestore.Managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import imu.WorldRestore.Other.ChunkCard;
import imu.WorldRestore.Other.ChunkFileHandler;
import imu.WorldRestore.Other.ChunkHandler;
import imu.WorldRestore.main.Main;

public class ChunkManager 
{
	Main _main = null;
	
	ChunkHandler _chunkHandler = null;
	ChunkFileHandler _chunkFileHandler = null;
	
	String _worldName = "world";
	String _defTargetWorld = "dw";
	
	String _chunkYAML = "chunks.yml";
	String _chunkFixYAML = "chunks_being_fixed.yml";
	
	int _maxSizeChunkHash = 3;
	
	HashMap<Chunk, ChunkCard> _chunks = new HashMap<Chunk, ChunkCard>();
	boolean _chunksInUse = false;
	
	
	public ChunkManager(Main main) 
	{
		_main = main;			
	}
	
	public void SetupHandlers()
	{
		_chunkHandler = _main.getChunkHandler();
		_chunkFileHandler = _main.getChunkFileHandler();
	}
	public Main getMain()
	{
		return _main;
	}
		
	public String getChunksYAML()
	{
		return _chunkYAML;
	}
	public String getChunksFixYAML()
	{
		return _chunkFixYAML;
	}
	public void setChunk(Chunk chunk, String targetWorldName, int y)
	{
		ChunkCard cCard = _chunks.get(chunk);
		if(cCard != null)
		{
			cCard.Refresh(y);
		}else
		{
			cCard = new ChunkCard(this, chunk, targetWorldName, y);
			newChunk(cCard);
		}
		
	}
	
	public void setChunk(Block block)
	{
		
		setChunk(block.getChunk(), getDefTargetWorldName(), block.getLocation().getBlockY());
	}
	public void setChunkAuto(Block block,int yDelta)
	{
		Chunk chunk = block.getChunk();
		Location loc = block.getLocation();
		ChunkCard card = _chunks.get(chunk);
		if(card != null)
		{
			card.Refresh(loc.getBlockY());
			card.Refresh(loc.getBlockY()+yDelta);
		}else
		{
			card = new ChunkCard(this, chunk, getDefTargetWorldName(), loc.getBlockY(),loc.getBlockY()+yDelta,System.currentTimeMillis());
			newChunk(card);
		}
		
	}
	public void setChunk(Player player, String targetWorldName, int y)
	{
		setChunk(player.getWorld().getChunkAt(player.getLocation()), targetWorldName, y);
	}
	
	public String createChunkID(Chunk chunk)
	{
		return String.valueOf(chunk.getX())+":"+String.valueOf(chunk.getZ());
	}
	
	public String getDefTargetWorldName()
	{
		return _defTargetWorld;
	}
	
	public String getWorldName()
	{
		return _worldName;
	}
	public void newChunk(ChunkCard cCard)
	{
		Runtime r = Runtime.getRuntime();
		
		checkHashSize();
		_chunks.put(cCard.getChunk(), cCard);
		_chunkFileHandler.saveCard(cCard);
		
		
		//long memUsed = ((r.totalMemory() - r.freeMemory()));
		//System.out.println("memory usage: "+memUsed/ (1024 * 1024));
		//System.out.println("size of array of chunkCards: "+cs.size());
	}
	
	public void removeChunkCardFromChunks(ChunkCard card)
	{
		_chunks.remove(card.getChunk());
	}
	public boolean isChunksInHash(ChunkCard card)
	{
		if(_chunks.containsKey(card.getChunk()))
		{
			return true;
		}
		return false;
	}
	public ChunkCard getChunkCard(ChunkCard card)
	{
		return _chunks.get(card.getChunk());
	}
	void checkHashSize()
	{
		if(_chunks.size() > _maxSizeChunkHash)
		{
			saveAllChunks();
		}
	}
	
	public void fixChunk(ChunkCard card)
	{
		_chunkHandler.fixChunk(card);
	}
	public void fixChunks(ArrayList<ChunkCard> cards)
	{
		_chunkHandler.fixChunks(cards);
	}
	

	public void saveAllChunks()
	{		
		_chunkFileHandler.saveCardList(_chunks);
		_chunks.clear();			
	}

	
	
	
	
}
