package imu.GS.Invs;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import imu.GS.Main.Main;
import imu.GS.Managers.ShopEnchantManager;
import imu.GS.Other.EnchantINFO;
import imu.iAPI.Convs.ConvModData;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Interfaces.IModDataInv;
import imu.iAPI.Interfaces.IModDataValue;
import imu.iAPI.Other.Cooldowns;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;

public class EnchantmentModifyINV extends CustomInvLayout implements IModDataInv
{
	private ShopEnchantManager _eManager;
	private Main _main;
	private final int MAX_SELECTOR = SEL_NAME.values().length-1;
	private int[] _selector_array;

	private int _clickedEnch;
	private boolean _saveData = false;
	Cooldowns _cds = new Cooldowns();
	public EnchantmentModifyINV(Plugin main, Player player) {
		super(main, player, "&5===>Modifying Enchantment", 6 * 9);
		_main = (Main)main;
		_eManager = _main.GetShopEnchantManager();
		
		_selector_array = new int[Enchantment.values().length];

	}

	@Override
	public boolean SetModData(IModDataValue value, String anwser) 
	{
		EnchantINFO eInfo = _eManager.GetInfo(Enchantment.values()[_clickedEnch]);
		SEL_NAME selector = SEL_NAME.values()[_selector_array[_clickedEnch]];
		
		if(!Metods._ins.isDigit(anwser)) return false;
		double number = Double.parseDouble(anwser);
		switch (selector) 
		{
		case MAX_LEVEL:
			eInfo.SetMaxLevel((int) Math.round(number));
			break;
		case MAX_PRICE:
			if(!eInfo.SetPrice(eInfo.GetMinPrice(),eInfo.GetMaxPrice().SetPrice(number))) return false;
			break;
		case MIN_PRICE:
			if(!eInfo.SetPrice(eInfo.GetMinPrice().SetPrice(number), eInfo.GetMaxPrice())) return false;
			break;
		case RAW_MULTIPLIER:
			eInfo.Set_rawMultiplier(number);
			break;


		}
		
		_eManager.SaveEnchantInfoAsync(eInfo);
		_player.sendMessage(Metods.msgC("&b"+eInfo.GetName().toUpperCase()+" &3 data has been saved"));
		openThis();
		return true;
	}
	
	@Override
	public void SetModDataFAILED(IModDataValue value, String qustion, String anwser) 
	{
		SEL_NAME selector = SEL_NAME.values()[_selector_array[_clickedEnch]];
		String failedMsg = "";
		switch (selector) 
		{
		case MAX_LEVEL:		
		case RAW_MULTIPLIER:
			failedMsg = "&cNeed to be number!";
			break;
		case MAX_PRICE:
		case MIN_PRICE:
			failedMsg = "&2Max &e> &cmin!";
			break;
		


		}
		
		_player.sendMessage(Metods.msgC("&cInvalid syntax! "+failedMsg));
		Metods._ins.ConversationWithPlayer(_player, new ConvModData(value,this,anwser));
	}


	@Override
	public void openThis() 
	{
		super.openThis();
		setupButtons();	
	}
	
	BUTTON GetButtonPress(InventoryClickEvent e)
	{
		ItemStack stack = e.getCurrentItem();
		if(stack == null)
			return BUTTON.NONE;
		
		String buttonName = getButtonName(e.getCurrentItem());
		if(buttonName == null)
			return BUTTON.NONE;
		
		return BUTTON.valueOf(buttonName);
	}
	
	enum BUTTON implements IButton
	{
		NONE,
		ENCHANT,
	}
	
	enum ENCH implements IModDataValue
	{
		NONE
	}
	
	@Override
	public void invClosed(InventoryCloseEvent e) 
	{

	}

	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		if(!_cds.isCooldownReady("click")) return;
		_cds.setCooldownInSeconds("click", 0.1);
		BUTTON button = GetButtonPress(e);
		
		switch (button) 
		{
		case ENCHANT:
			int slot = GetSLOT(e.getCurrentItem());
			if(e.getClick() == ClickType.RIGHT) 
			{
				MoveSelector(e.getCurrentItem());
				RefresItem(slot);
				return;
			}
			if(e.getClick() == ClickType.MIDDLE)
			{
				_selector_array[slot]=SEL_NAME.values().length-1;
			}
			_clickedEnch = slot;
			Modify(SEL_NAME.values()[_selector_array[slot]]);
			break;
		case NONE:
			return;

		
		}
		
		//setupButtons();
	}
	
	enum SEL_NAME
	{
		
		MAX_PRICE,
		MIN_PRICE,
		RAW_MULTIPLIER,
		MAX_LEVEL,
		
	}
	void Modify(SEL_NAME selector)
	{
		String str = "";
		switch (selector) 
		{
			case MAX_LEVEL:
				str = "&3Give &2max &bLevel &7(-1 sets to &r&ldefault&7)";
			break;
			case MAX_PRICE:
				str = "&3Give &2max &4Price";
			break;
			case MIN_PRICE:
				str = "&3Give &cmin &4Price";
			break;
		case RAW_MULTIPLIER:
			str = "&3Give Raw multiplier";
			break;

		}
		Metods._ins.ConversationWithPlayer(_player, new ConvModData(ENCH.NONE,this,str));
		_player.closeInventory();
	}
	int GetSelector(ItemStack stack)
	{
		Integer slot = GetSLOT(stack);
		if(slot == null) slot = 0;
		return _selector_array[slot];
	}
	
	void MoveSelector(ItemStack stack)
	{
		Integer slot = GetSLOT(stack);
		_selector_array[slot]++;
		if(_selector_array[slot] < 0) _selector_array[slot] = MAX_SELECTOR;
		if(_selector_array[slot] > MAX_SELECTOR) _selector_array[slot] = 0;
		
		if(SEL_NAME.values()[_selector_array[slot]] == SEL_NAME.MAX_LEVEL)
		{
			_selector_array[slot] = 0;
		}
	}
	
	void RefresItem(int slot)
	{
		EnchantINFO eInfo = _eManager.GetInfo(Enchantment.values()[slot]);
		ItemStack stack = SetButton(new ItemStack(Material.ENCHANTED_BOOK), BUTTON.ENCHANT);
		
		Metods.setDisplayName(stack, "&5"+Enchantment.values()[slot].getKey().getKey().toUpperCase());
							
		String[] lores = new String[]
				{
						"&6M1 &2Modify &6value   &e&   &6M2 &bMove &6Selector",
						" ",					
						"&6 Max Price:&2 " + eInfo.GetMaxPrice().GetPrice(),
						"&6 Min Price:&2 " + eInfo.GetMinPrice().GetPrice(),
						"&6 Raw Mult:&2 " + eInfo.Get_rawMultiplier(),
						"&7(&e Modify by M3&7)&6 Max Level:&2 " + eInfo.GetMaxLevel(),
				};
		
		final int offset = 2;
		for(int l = 0; l < lores.length; l++)
		{						
			if((_selector_array[slot]+offset) != (l+offset)) continue;
			String[] ar = lores[(l+offset)].split(" ");
			for(int ll= 0; ll < ar.length; ll++)
			{
				ar[ll] = "&l"+ar[ll];
			}
			
			//lores[(l+offset)] = "&b&l==> "+lores[(l+offset)];
			lores[(l+offset)] = "&b&l==> "+Metods._ins.CombineArrayToOneString(ar, " ");
			break;
		}
		
		Metods._ins.SetLores(stack, lores, false);
		SetITEM(slot, stack);
	}
	
	@Override
	public void setupButtons() {
		
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				int i = 0;
				for(; i < Enchantment.values().length; ++i)
				{
					RefresItem(i);
				}
				for(;i < _size-9; ++i)
				{
					setupButton(BUTTON.NONE, Material.BLACK_STAINED_GLASS_PANE, " ", i);
				}
				
				for(;i < _size; ++i)
				{
					setupButton(BUTTON.NONE, Material.ORANGE_STAINED_GLASS_PANE, " ", i);
				}
			}
		}.runTaskAsynchronously(_main);
		
	}

	
	

	


	

}
