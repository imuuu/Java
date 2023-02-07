package imu.iAPI.Utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
	
	public static LinkedList<Location> CreateSphere(Location center, int radius) 
	{
		LinkedList<Location> positions = new LinkedList<>();
	    int radiusSquared = radius * radius;

	    for (int x = -radius; x <= radius; x++) {
	        for (int y = -radius; y <= radius; y++) {
	            for (int z = -radius; z <= radius; z++) {
	                if (x * x + y * y + z * z <= radiusSquared) {
	                    Location loc = center.clone().add(x, y, z);

	                    positions.add(loc);
	                }
	            }
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
