package imu.WorldRestore.Other;

import org.bukkit.Chunk;
import org.bukkit.World;

import imu.WorldRestore.Managers.ChunkManager;

public class ChunkCard 
{
	String _id = "";
	Chunk _chunk = null;
	
	String _targetWorld = "";
	
	int _minY = 256;
	int _maxY = 0;
	
	long _timeStamp = 0;
	
	ChunkManager _cManager = null;
	public ChunkCard(ChunkManager cManager, Chunk chunk, String targetWorldName, int y) 
	{
		_cManager = cManager;
		_chunk = chunk;
		_timeStamp = System.currentTimeMillis();
		_targetWorld = targetWorldName;
		_id = _cManager.createChunkID(_chunk);		
		setMinMaxY(y);
	}
	public ChunkCard(ChunkManager cManager, Chunk chunk, String targetWorldName, int minY, int maxY, long timestamp) 
	{
		_cManager = cManager;
		_chunk = chunk;
		_timeStamp = timestamp;
		_targetWorld = targetWorldName;
		set_minY(minY);
		set_maxY(maxY);
		_id = _cManager.createChunkID(_chunk);		
	}
	
	public void setMinMaxY(int y)
	{		
		y = checkerY(y);
		if(y < _minY)
		{
			_minY = y;			
		}
					
		if(y > _maxY) 
		{
			_maxY=y;			
		}
			
	}
	
	public void Refresh(int y)
	{
		setMinMaxY(y);
		_timeStamp = System.currentTimeMillis();
	}
	
	public String getId()
	{
		return _id;
	}
	
	public Chunk getChunk()
	{
		return _chunk;
	}
	public World getWorld()
	{
		return _chunk.getWorld();
	}
	
	public World getTargetWorld()
	{
		return _cManager.getMain().getServer().getWorld(_targetWorld);
	}
	
	
	public String get_targetWorldName() {
		return _targetWorld;
	}

	public int get_minY() 
	{
		return _minY;
	}
	int checkerY(int y)
	{
		if(y > 256)
		{
			y = 256;
		}else if(y < 0)
		{
			y = 0;
		}
		return y;
	}
	
	public void set_minY(int y)
	{
		y = checkerY(y);
		_minY = y;
	}
	public void set_maxY(int y)
	{
		y = checkerY(y);
		_maxY = y;
	}

	public int get_maxY() {
		return _maxY;
	}

	public long get_timeStamp() {
		return _timeStamp;
	}

	public void printData()
	{
		System.out.println("===CHUNK DATA===");
		System.out.println("Id: "+_id);
		System.out.println("Chunk: "+_chunk);
		System.out.println("TargetW: "+_targetWorld);
		System.out.println("Stamp: "+_timeStamp);
		System.out.println("MinY: "+_minY);
		System.out.println("MaxY: "+_maxY);
		System.out.println("===CHUNK DATA===");
	}
}
