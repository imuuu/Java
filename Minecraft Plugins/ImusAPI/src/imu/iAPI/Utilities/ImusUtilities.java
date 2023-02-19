package imu.iAPI.Utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import imu.iAPI.Main.ImusAPI;

public  class ImusUtilities
{
	@SuppressWarnings("unchecked")
	public static <T> T[] CombineArrays(T[] array1, T[] array2) 
	{
	    List<T> list = new ArrayList<>(Arrays.asList(array1));
	    list.addAll(Arrays.asList(array2));
	    return list.toArray((T[])Array.newInstance(array1.getClass().getComponentType(), list.size()));
	}
	
	public static <T> List<T> CombineArrays(List<T> list1, List<T> list2) 
	{
	    List<T> combinedList = new ArrayList<>(list1);
	    combinedList.addAll(list2);
	    return combinedList;
	}
	
	public static <T> T[] AddElementAtIndex(T[] array, T element, int index) 
	{
	    if (index < 0 || index > array.length) {
	        throw new IndexOutOfBoundsException("Invalid index: " + index);
	    }
	    @SuppressWarnings("unchecked")
		T[] new_array = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), array.length + 1);
	    System.arraycopy(array, 0, new_array, 0, index);
	    new_array[index] = element;
	    System.arraycopy(array, index, new_array, index + 1, array.length - index);
	    return new_array;
	}
	
	public static <T> T[] RemoveElementAtIndex(T[] array, int index) 
	{
	    if (index < 0 || index >= array.length) 
	    {
	        throw new IndexOutOfBoundsException("Invalid index: " + index);
	    }
	    @SuppressWarnings("unchecked")
		T[] new_array = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), array.length - 1);
	    System.arraycopy(array, 0, new_array, 0, index);
	    System.arraycopy(array, index + 1, new_array, index, array.length - index - 1);
	    return new_array;
	}
	
	public static LinkedList<Location> CreateSphere(Location center, int radius, HashSet<Material> ignoreSet, HashSet<Material> includeSet) 
	{
		
		LinkedList<Location> positions = new LinkedList<>();
	    int radiusSquared = radius * radius;

	    for (int x = -radius; x <= radius; x++) {
	        for (int y = -radius; y <= radius; y++) {
	            for (int z = -radius; z <= radius; z++) {
	                if (x * x + y * y + z * z <= radiusSquared) {
	                	
	                	Location loc = center.clone().add(x, y, z);
	    				Block b = loc.getBlock();
	    				
	    				if(b == null) continue;
	    				
	    				if(includeSet != null && !includeSet.isEmpty() && !includeSet.contains(b.getType()))
	    				{
	    					//System.out.println("werent in include list");
	    					continue;
	    				}
	    				
	    				if(ignoreSet != null && ignoreSet.contains(b.getType()))
	    				{
	    					//System.out.println("werent in ignore list");
	    					continue;
	    				}
	    				
	    				positions.add(loc);	
	                }
	            }
	        }
	    }
	    //System.out.println("Got blocks size of: "+positions.size());
	    return positions;
	}
	
	private static LinkedList<Location> CreateCirclePlatform(Location center, int radius, HashSet<Material> ignoreSet, HashSet<Material> includeSet)
	{
		LinkedList<Location> positions = new LinkedList<>();
		int x = center.getBlockX();
		int y = center.getBlockY();
		int z = center.getBlockZ();
		
		for (int i = x - radius; i <= x + radius; i++)
		{
			for (int k = z - radius; k <= z + radius; k++)
			{
				Location loc = new Location(center.getWorld(), i, y, k);
				Block b = loc.getBlock();
				if(b == null) continue;
				
				if(includeSet != null && !includeSet.isEmpty() && !includeSet.contains(b.getType()))
				{
					continue;
				}
				
				if(ignoreSet != null && ignoreSet.contains(b.getType())) continue;
				
				positions.add(loc);	
			}
		}
		
		return positions;
	}
	
	public static void ChangeBlockType(Iterable<Block> list, Material[] mat_list, long delay, int index) {
	    
		Bukkit.getScheduler().runTaskLater(ImusAPI._instance, () -> 
	    {
	    	for(Block block : list)
	    	{
	    		block.setType(mat_list[index]);
	    	}
	    	
	    	int newIndex  = index + 1;
	    	
	    	if(newIndex < mat_list.length)
	    	{
	    		ChangeBlockType(list, mat_list, delay, newIndex);
	    	}

	    }, delay);
	}
	
	
//	public static boolean IsPositionBehind(Location currentLoc, Location targetLocation)
//	{
////		double distance = targetLocation.distance(currentLoc);
////		Vector targetRightDir = targetLocation.subtract(currentLoc).toVector().normalize().multiply(distance);
////		targetLocation = targetRightDir.add(targetRightDir)
////		Location loc = new Location(targetLocation.getWorld(), targetRightDir.getX(),targetRightDir.getY(),targetRightDir.getZ());
//		
//		return IsPositionBehind(currentLoc, targetLocation,targetLocation.clone().subtract(currentLoc).toVector().normalize());
//	}
	
	public static boolean IsPositionBehind(Location currentLoc, Location targetLocation, Vector direction)
	{		
		Vector current = currentLoc.toVector();
		Vector target = targetLocation.toVector();		
		Vector first = target.clone().subtract(current.clone());
		
		double dot =  first.dot(direction);

		if(dot < 0) return true;
		
		return false;
		
		
	}
	
	public static float[] GetYawPitch(Location loc)
	{
		return GetYawPitch(loc.toVector());
	}
	
	public static float[] GetYawPitch(Vector vector)
	{
		vector = vector.normalize();
		float yaw = (float) Math.toDegrees(Math.atan2(vector.getZ(), vector.getX())) - 90;
		float pitch = (float) Math.toDegrees(Math.asin(vector.getY()));
		
		return new float[] {yaw, pitch};
	}
//	public static LinkedList<Block> CreateSphere(Location center, int radius) 
//	{
//		LinkedList<Block> positions = new LinkedList<>();
//	    int radiusSquared = radius * radius;
//
//	    for (int x = -radius; x <= radius; x++) {
//	        for (int y = -radius; y <= radius; y++) {
//	            for (int z = -radius; z <= radius; z++) {
//	                if (x * x + y * y + z * z <= radiusSquared) {
//	                    Location loc = center.clone().add(x, y, z);
//
//	                    positions.add(loc.getBlock());
//	                }
//	            }
//	        }
//	    }
//	    
//	    return positions;
//	}
	
}
