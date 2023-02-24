package imu.DontLoseItems.other;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.DontLoseItems.CustomItems.Manager_HellTools;
import imu.DontLoseItems.Enums.ITEM_RARITY;
import imu.iAPI.Other.Metods;

public class Manager_LegendaryUpgrades
{
	public static Manager_LegendaryUpgrades Instance;

	public final String PD_HELL_HELMET = "PD_UPGRADE_HELL_HELMET";
	public final String PD_HELL_CHEST = "PD_UPGRADE_HELL_CHEST";
	public final String PD_HELL_LEGG = "PD_UPGRADE_HELL_LEGG";
	public final String PD_HELL_BOOTS = "PD_UPGRADE_HELL_BOOTS";

	public final String PD_HELL_HOE = "PD_UPGRADE_HELL_HOE";
	public final String PD_HELL_PICKAXE = "PD_UPGRADE_HELL_PICKAXE";
	public final String PD_HELL_SWORD = "PD_UPGRADE_HELL_SWORD";
	public final String PD_HELL_AXE = "PD_UPGRADE_HELL_AXE";
	public final String PD_HELL_SHIELD = "PD_UPGRADE_HELL_SHIELD";

	public Manager_LegendaryUpgrades()
	{
		Instance = this;
	}
	
	public ItemStack AddSyntax(ItemStack stack)
	{
		Metods._ins.addDisplayName(stack, ChatColor.AQUA+"UPGRADE &5to ", true);
		stack = Manager_HellArmor.Instance.RemoveArmorData(stack);
		
		List<String> lores = new ArrayList<>();
		
		lores.add(" ");
		lores.add("&3Combine &6with an equivalent &elegendary &6item");
		lores.add("&6in &3a Smithing Table &6to &5complete &6this &bitem.");
		lores.add(" ");
		lores.add("&4&k# "+"&4This item alone doesn't have any legendary effects!");
		
//		for(String lore : lores)
//		{
//			
//		}
		stack = Metods._ins.addLore(stack, lores);

		return stack;
	}
	public ItemStack Get_UpgradeHellHelmet()
	{
		ItemStack stack = Manager_HellArmor.Instance.CreateHellHelmet(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_HELMET, PersistentDataType.INTEGER, 1);
		
		stack.setType(Material.GOLDEN_HELMET);
		return stack;
	}

	public ItemStack Get_UpgradeHellChest()
	{
		ItemStack stack = Manager_HellArmor.Instance.CreateHellChestplate(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_CHEST, PersistentDataType.INTEGER, 1);
		
		stack.setType(Material.GOLDEN_CHESTPLATE);
		return stack;
	}

	public ItemStack Get_UpgradeHellLeggings()
	{
		ItemStack stack = Manager_HellArmor.Instance.CreateHellLeggins(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_LEGG, PersistentDataType.INTEGER, 1);
		
		stack.setType(Material.GOLDEN_LEGGINGS);
		return stack;
	}

	public ItemStack Get_UpgradeHellBoots()
	{
		ItemStack stack = Manager_HellArmor.Instance.CreateHellBoots(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_BOOTS, PersistentDataType.INTEGER, 1);
		
		stack.setType(Material.GOLDEN_BOOTS);
		return stack;
	}
	
	

	public ItemStack Get_UpgradeHellHoe()
	{
		ItemStack stack = Manager_HellTools.Instance.CreateHellHoe(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_HOE, PersistentDataType.INTEGER, 1);
		stack = Manager_HellTools.Instance.HellHoe_Controller.RemoveHellHoe(stack);
		stack.setType(Material.GOLDEN_HOE);
		return stack;
	}

	public ItemStack Get_UpgradeHellPickaxe()
	{
		ItemStack stack = Manager_HellTools.Instance.CreateHellPickaxe(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_PICKAXE, PersistentDataType.INTEGER, 1);
		stack = Manager_HellTools.Instance.HellPickController.RemoveHellPickaxe(stack);
		stack.setType(Material.GOLDEN_PICKAXE);
		return stack;
	}

	public ItemStack Get_UpgradeHellSword()
	{
		ItemStack stack = Manager_HellTools.Instance.CreateHellTripleSword(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_SWORD, PersistentDataType.INTEGER, 1);
		stack = Manager_HellTools.Instance.HellSword_Controller.RemoveHellSword(stack);
		stack.setType(Material.GOLDEN_SWORD);
		return stack;
	}

	public ItemStack Get_UpgradeHellAxe()
	{
		ItemStack stack = Manager_HellTools.Instance.CreateHellDoubleAxe(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_AXE, PersistentDataType.INTEGER, 1);
		stack = Manager_HellTools.Instance.HellAxe_Controller.RemoveHellAxe(stack);
		stack.setType(Material.GOLDEN_AXE);
		return stack;
	}

	public ItemStack Get_UpgradeHellShield()
	{
		ItemStack stack = Manager_HellArmor.Instance.CreateHellReflectShield(ITEM_RARITY.Legendary);
		stack = AddSyntax(stack);
		Metods._ins.setPersistenData(stack, PD_HELL_SHIELD, PersistentDataType.INTEGER, 1);
		stack.setType(Material.SHIELD);
		return stack;
	}

}
