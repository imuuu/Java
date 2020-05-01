package imu.GeneralStore.Other;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import imu.GeneralStore.main.Main;
import net.md_5.bungee.api.ChatColor;

public class EnchantInvModify extends CustomInvLayout implements Listener
{
	ItemStack _orginalStack, _copy,_orginalStack_copy = null;
	ItemStack _empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

	EnchantsManager _enchM = null;
	
	String _pd_modify = "";
	String _pd_vButton = "gs.uModifyVb";
	
	Double[] _prices = {};
	Double[] _defautl_prices = {};
	String[] _tooltip_strs= {ChatColor.AQUA+ "======Ench======",
							ChatColor.DARK_PURPLE + "maxPrice : " + ChatColor.GOLD +"",
							ChatColor.DARK_PURPLE + "minPrice : " + ChatColor.GOLD +"",
							ChatColor.DARK_PURPLE + "maxLevel: " + ChatColor.GOLD + "",
							ChatColor.DARK_PURPLE + "minLevel : " + ChatColor.GOLD + "",
							ChatColor.AQUA+ "======Ench======",
							""+ChatColor.DARK_PURPLE + ChatColor.MAGIC+ "# "+ChatColor.GREEN + "CONFIRM by CLICK!"+ ChatColor.DARK_PURPLE+ChatColor.MAGIC+ " #"};
	
	int _total_value_states = 3; //include 0
	int _value_state_now = 0;
	
	Double[] price_button_values = {0.1, 1.0, 10.0, 100.0, 1000.0};
	Double[] price_button_multiv = {1.0, 5.0, 10.0};

	
	public EnchantInvModify(Main main, Player player, String name, ItemStack enchItem) 
	{
		super(main, player, name, 9*3);
		
		_main.getServer().getPluginManager().registerEvents(this, _main);
		
		_enchM = _main.getEnchManager();
		_pd_modify = _enchM.pd_modify;
		
		INIT(enchItem);
	}
	
	public void INIT(ItemStack uniqueItemStack)
	{
		_orginalStack = uniqueItemStack;
		_orginalStack_copy = _orginalStack.clone();
		_enchM.removeModifyData(_orginalStack_copy);
		_copy = _orginalStack.clone();
		
		_prices =_enchM.getEnchPriceData(_orginalStack);		
		_defautl_prices = _prices.clone();
		_value_state_now = 0;
		
		makeInv();		
	}
	
	enum LABELS
	{
		GO_BACK(99),
		COPY(80),
		REMOVE(81),
		VALUE_STATE(10),
		VALUE_PRICE_BUTTON(11),
		RESET_ITEM(12),
		CONFIRM(13);
				
		int type;
		
		LABELS(int i)
		{
			this.type = i;
		}
		public int getType()
		{
			return type;
		}		
	}
	
	void makeInv()
	{		
		
		itemM.setDisplayName(_empty, " ");
		for(int i = 0; i < _size ; ++i)
		{
			_inv.setItem(i, _empty);
		}
		
		refreshButtons();
		
		refrestItem();
				
	}
	
	
	void refreshButtons()
	{
		ItemStack back_button = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		itemM.setDisplayName(back_button, ChatColor.AQUA + "<< BACK");
		setButtonSwitch(back_button, LABELS.GO_BACK.getType());
		_inv.setItem(9, back_button);
		
		ItemStack copy_button = new ItemStack(Material.SLIME_BALL);
		itemM.setDisplayName(copy_button, ChatColor.AQUA + "Copy to your inv");
		setButtonSwitch(copy_button, LABELS.COPY.getType());
		_inv.setItem(8, copy_button);
		
		ItemStack remove_button = new ItemStack(Material.LAVA_BUCKET);
		itemM.setDisplayName(remove_button, ChatColor.RED + "Remove as unique item");
		setButtonSwitch(remove_button, LABELS.REMOVE.getType());
		_inv.setItem(_size-1, remove_button);
		
		ItemStack reset_item = new ItemStack(Material.WATER_BUCKET);
		itemM.setDisplayName(reset_item, ChatColor.AQUA + "RESET PRICE");
		setButtonSwitch(reset_item, LABELS.RESET_ITEM.getType());
		_inv.setItem(4+8, reset_item);
		
		refreshValueStateButton();
		refrestItem();
		refreshPriceButtons();
	}
	
	void setValueButtonValue(ItemStack stack, Double d)
	{
		itemM.setPersistenData(stack, _pd_vButton, PersistentDataType.DOUBLE, d);
	}
	
	Double getValueButtonValue(ItemStack stack)
	{
		return itemM.getPersistenData(stack, _pd_vButton, PersistentDataType.DOUBLE);
	}
	
	void goBack()
	{
		_enchM.removeModifyData(_orginalStack);
		_enchM.openEnchantINV(_player);
	}
	
	
	@EventHandler
	public void onInvCloseEvent(InventoryCloseEvent e)
	{
		if(isThisInv(e))
		{
			HandlerList.unregisterAll(this);
		}
	}
	
	@EventHandler
	public void onInvClickEvent(InventoryClickEvent e) 
	{
		if(isThisInv(e))
		{
			e.setCancelled(true);
			ItemStack stack = e.getCurrentItem();
			Integer switch_button = getButtonSwitch(stack);
			ClickType click = e.getClick();
			
			
			if(switch_button != null)
			{
				if(switch_button == LABELS.GO_BACK.getType())
				{
					goBack();
					return;
				}
				
				if(switch_button == LABELS.COPY.getType())
				{
					itemM.moveItemFirstFreeSpaceInv(_orginalStack_copy.clone(), _player, true);
					return;
				}
				
				if(switch_button == LABELS.REMOVE.getType())
				{
					Double[] price = {0.0,0.0,0.0};

					goBack();
					return;
				}
				
				if(switch_button == LABELS.VALUE_STATE.getType())
				{
					if(click == ClickType.LEFT)
						changeValueState(1);
					
					if(click == ClickType.RIGHT)
						changeValueState(-1);
					
					refreshButtons();
					return;
				}
				
				if(switch_button == LABELS.VALUE_PRICE_BUTTON.getType())
				{
					Double price_amount = getValueButtonValue(stack);
					Double multip = 1.0;
					if(click == ClickType.LEFT)
					{
						multip = price_button_multiv[0];
					}
					else if(click == ClickType.SHIFT_LEFT)
					{
						multip = price_button_multiv[1];
					}					
					else if(click == ClickType.RIGHT)
					{
						multip = price_button_multiv[0] * -1;
					}					
					else if(click == ClickType.SHIFT_RIGHT)
					{
						multip = price_button_multiv[1] * -1;
					}
					else if(click == ClickType.MIDDLE)
					{
						multip = price_button_multiv[2];
					}
					
					changePrice(price_amount*multip); 
					refreshButtons();
					return;
				}
				
				if(switch_button == LABELS.RESET_ITEM.getType())
				{
					_prices = _defautl_prices.clone();
					refreshButtons();
				}
				
				if(switch_button == LABELS.CONFIRM.getType())
				{
					_player.sendMessage(ChatColor.YELLOW + "Unique item has been chanced!");
					//_shopManager.addUniqueItem(_orginalStack, _prices.clone(), false);

					goBack();
				}
				
			}
		}
	}
	
	String getValueStateName()
	{
		String str = "";
		switch (_value_state_now) {
		case 0:
			str = "Min Level";
			break;

		case 1:
			str = "Max Level";
			break;
		case 2:
			str = "Min price";
			break;
		case 3:
			str = "Max price";
			break;
		}
		
		return str;
	}
	void changeValueState(int i)
	{
		_value_state_now += i;
		if(_value_state_now < 0)
			_value_state_now = _total_value_states;

		
		if(_value_state_now > _total_value_states)
			_value_state_now = 0;
		
		refreshValueStateButton();
	}
	
	void changePrice(Double addAmount)
	{
		Double[] copy_price = _prices.clone();
		double newPrice =  Math.round((copy_price[_value_state_now] + addAmount)* 100.0) / 100.0;;
		
		if(newPrice < 0)
		{
			newPrice = 0;
		}
		
		if(_value_state_now == 0 && newPrice > _prices[1])
		{
			copy_price[1] = newPrice;
		}
		
		if(_value_state_now == 2 && newPrice > _prices[3])
		{
			copy_price[3] = newPrice;
		}
		
		copy_price[_value_state_now] = newPrice;
		
		if(!_enchM.isEnchantPriceValid(copy_price))
		{
			_player.sendMessage(ChatColor.DARK_RED + "That's not valid price!");
			return;
		}
		
		_prices = copy_price;
	}
	void refreshValueStateButton()
	{
		ItemStack value_state_button = new ItemStack(Material.BEDROCK);
		switch(_value_state_now)
		{
			case 0:
				value_state_button = new ItemStack(Material.IRON_BLOCK);
				itemM.setDisplayName(value_state_button, ChatColor.AQUA + getValueStateName());
				break;
			case 1:
				value_state_button = new ItemStack(Material.GOLD_BLOCK);
				itemM.setDisplayName(value_state_button, ChatColor.AQUA +  getValueStateName());
				break;
			case 2:
				value_state_button = new ItemStack(Material.DIAMOND_BLOCK);
				itemM.setDisplayName(value_state_button, ChatColor.AQUA +  getValueStateName());
				break;
			case 3:
				value_state_button = new ItemStack(Material.EMERALD_BLOCK);
				itemM.setDisplayName(value_state_button, ChatColor.AQUA +  getValueStateName());
				break;
		}
		itemM.addLore(value_state_button, ChatColor.DARK_PURPLE + "Change between minlvl, maxlvl, minPrice, maxPrice", false);
		setButtonSwitch(value_state_button, LABELS.VALUE_STATE.getType());
		_inv.setItem(4, value_state_button);
	}
	
	void refreshPriceButtons()
	{

		ItemStack price_button; //= new ItemStack(Material.BEDROCK);
		int first = 20;
		
		String disp = ChatColor.YELLOW + "===> "+ChatColor.GREEN+"+"+ ChatColor.AQUA +"/"+ ChatColor.RED + "- ";
		Material[] mats = {Material.COAL_ORE,Material.IRON_ORE,Material.GOLD_ORE,Material.DIAMOND_ORE,Material.EMERALD_ORE};
		
		String[] displays = {disp +ChatColor.GOLD + price_button_values[0], disp +ChatColor.GOLD + price_button_values[1], 
							disp +ChatColor.GOLD + price_button_values[2], disp +ChatColor.GOLD + price_button_values[3], 
							disp +ChatColor.GOLD + price_button_values[4]};
 		
			
		for(int i = 0; i < 5;++i)
		{

			price_button = new ItemStack(mats[i]);
			itemM.setDisplayName(price_button,displays[i]);
			setValueButtonValue(price_button, price_button_values[i]);
			setButtonSwitch(price_button, LABELS.VALUE_PRICE_BUTTON.getType());
			
			itemM.addLore(price_button, ChatColor.GREEN+"M3"+ ChatColor.AQUA +"  / "+ ChatColor.RED + "     :  "+ChatColor.YELLOW+price_button_multiv[2]+"x", false);
			itemM.addLore(price_button, ChatColor.GREEN+"SM1"+ ChatColor.AQUA +" / "+ ChatColor.RED + "SM2:  "+ChatColor.YELLOW+price_button_multiv[1]+"x", false);
			itemM.addLore(price_button, ChatColor.GREEN+"M1"+ ChatColor.AQUA +"  / "+ ChatColor.RED + "  M2:  "+ChatColor.YELLOW+price_button_multiv[0]+"x", false);
			itemM.addLore(price_button, "---------------",false);
			itemM.addLore(price_button, ChatColor.DARK_PURPLE + getValueStateName() +": "+ChatColor.GOLD + _prices[_value_state_now], false);

			
			_inv.setItem(first+i, price_button);
		}
		
		
		
	}
	
	
	void refrestItem()
	{
		setTooltip();
		setButtonSwitch(_copy, LABELS.CONFIRM.getType());
		_inv.setItem(4+9, _copy);
	}
	
	void removeToolTip()
	{
		for(String str : _tooltip_strs)
		{
			itemM.removeLore(_copy, str);
		}
	}
	
	void setTooltip()
	{
		removeToolTip();
		String[] tooltip_strs= {_tooltip_strs[0],
								_tooltip_strs[1] + _prices[3],
								_tooltip_strs[2] + _prices[2],
								_tooltip_strs[3] + _prices[1],
								_tooltip_strs[4] + _prices[0],
								_tooltip_strs[5],
								_tooltip_strs[6]};
		for(String str : tooltip_strs)
		{
			itemM.addLore(_copy, str, false);
		}
	}
}
