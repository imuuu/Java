package imu.GS.Invs;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import imu.GS.ENUMs.ModDataShop;
import imu.GS.Interfaces.IModData;
import imu.GS.Main.Main;
import imu.GS.Prompts.ConvShopMod;
import imu.GS.ShopUtl.ShopBase;
import imu.GS.ShopUtl.ShopModData;
import imu.iAPI.Interfaces.IButton;
import imu.iAPI.Other.CustomInvLayout;
import imu.iAPI.Other.Metods;
import net.md_5.bungee.api.ChatColor;

public class ShopBaseModify extends CustomInvLayout
{
	private ShopBase _shopBase;
	private ShopModData _shopModData;
	private Main _main;
	public ShopBaseModify(Plugin main, Player player, ShopBase shopBase) {
		super(main, player, "&6Modifying Shop: "+shopBase.GetDisplayName(), 9*3);
		_shopBase = shopBase;
		_main = (Main)main;		
		SetModData(new ShopModData().ReadShop(shopBase));
		
		
	}

	enum BUTTON implements IButton
	{
		NONE,
		BACK,
		CONFIRM,
		SHOP_NAME,
		//SHOP_DISPLAYNAME,
		SHOP_SELL_MUL,
		SHOP_BUY_MUL,
		SHOP_EXPIRE_PERCENT,
		SHOP_EXPIRE_COOLDOWN,
		SHOP_LOCKED,
		SHOP_ABSOLUTE_POS;
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
	
	public void SetModData(IModData shopModData)
	{
		_shopModData = (ShopModData)shopModData;		
	}
	
	@Override
	public void setupButtons() 
	{
		for(int i = 0; i < _size; ++i){_inv.setItem(i, Metods.setDisplayName(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " "));}
		
		setupButton(BUTTON.BACK, Material.RED_STAINED_GLASS_PANE, "&b<= &cBACK", _size-9);
		setupButton(BUTTON.CONFIRM, Material.GREEN_STAINED_GLASS_PANE, "&bCONFIRM", _size-1);
		
		String m1m2 = Metods.msgC("&bM1: &aSet &bM2: &cDefault");
		String setTo = Metods.msgC("&6Set To &9");
		
		ItemStack stack = new ItemStack(Material.PAPER);
		Metods.setDisplayName(stack, "&6Rename shop name");
		_metods.addLore(stack, setTo +_shopModData.GetValueStr(ModDataShop.NAME, "&2", "", "NONE"), false);_metods.addLore(stack, m1m2, false);	
		_inv.setItem(0, SetButton(stack, BUTTON.SHOP_NAME));
		
		stack = new ItemStack(Material.GOLD_INGOT);
		Metods.setDisplayName(stack, "&6Sell Multiplier");
		_metods.addLore(stack, setTo +_shopModData.GetValueStr(ModDataShop.SELL_MUL, "&2", "", "NONE"), false);_metods.addLore(stack, m1m2, false);	
		_inv.setItem(2, SetButton(stack, BUTTON.SHOP_SELL_MUL));
		
		stack = new ItemStack(Material.IRON_INGOT);
		Metods.setDisplayName(stack, "&6Buy Multiplier");
		_metods.addLore(stack, setTo +_shopModData.GetValueStr(ModDataShop.BUY_MUL, "&2", "", "NONE"), false);_metods.addLore(stack, m1m2, false);	
		_inv.setItem(4, SetButton(stack, BUTTON.SHOP_BUY_MUL));
		
		stack = new ItemStack(Material.STICKY_PISTON);
		Metods.setDisplayName(stack, "&6Absolute position");
		_metods.addLore(stack, setTo +_shopModData.GetValueStr(ModDataShop.ABSOLUTE_POS, "&2", "", "NONE"), false);_metods.addLore(stack, m1m2, false);	
		_inv.setItem(6, SetButton(stack, BUTTON.SHOP_ABSOLUTE_POS));
		
		stack = new ItemStack(Material.BARRIER);
		Metods.setDisplayName(stack, "&6Lock");
		_metods.addLore(stack, setTo +_shopModData.GetValueStr(ModDataShop.LOCKED, "&2", "", "NONE"), false);_metods.addLore(stack, m1m2, false);		
		_inv.setItem(8, SetButton(stack, BUTTON.SHOP_LOCKED));
		
		stack = new ItemStack(Material.COMPASS);
		Metods.setDisplayName(stack, "&6Expire percent");
		_metods.addLore(stack, setTo +_shopModData.GetValueStr(ModDataShop.EXPIRE_PERCENT, "&2", "", "NONE"), false);_metods.addLore(stack, m1m2, false);	
		_inv.setItem(10, SetButton(stack, BUTTON.SHOP_EXPIRE_PERCENT));
		
		stack = new ItemStack(Material.CLOCK);
		Metods.setDisplayName(stack, "&6Expire Cooldown");
		_metods.addLore(stack, setTo +_shopModData.GetValueStr(ModDataShop.EXPIRE_COOLDOWN, "&2", "", "NONE"), false);_metods.addLore(stack, m1m2, false);	
		_inv.setItem(12, SetButton(stack, BUTTON.SHOP_EXPIRE_COOLDOWN));
	}

	
	void Back()
	{	
		_player.closeInventory();
		new ShopModINV(_main, _player, _shopBase).openThis();
	}
	
	void Confirm()
	{
		ShopBase shop = _main.get_shopManager().GetShop(_shopBase.GetName());
		if(shop == null)
		{
			_player.sendMessage(ChatColor.RED + "Couldn't find shop named as "+_shopBase.GetName());
			return;
		}
		shop.set_sellM(_shopModData._sellMultiplier);
		shop.set_buyM(_shopModData._buyMultiplier);
		shop.set_expire_percent(_shopModData._expire_percent);
		shop.set_expire_cooldown_m(_shopModData._expire_cooldown_minutes);
		shop.SetLocked(_shopModData._lock);
		shop.SetAbsolutePosBool(_shopModData._absoluteItemPosition);

		if(!shop.GetDisplayName().equalsIgnoreCase(_shopModData._name)) 
		{
			System.out.println("Setting name");
			_main.get_shopManager().RemoveShop(shop.GetName());
			shop.SetName(_shopModData._name);
			_main.get_shopManager().AddShop(shop);
			shop.SaveData();
			
		}
		
		_player.closeInventory();
	}
	
	
	@Override
	public void openThis() 
	{
		super.openThis();
		_main.RegisterInv(this);
		setupButtons();
	}
	@Override
	public void invClosed(InventoryCloseEvent e) {
		_main.UnregisterInv(this);
	}

	@Override
	public void onClickInsideInv(InventoryClickEvent e) 
	{
		BUTTON button = GetButtonPress(e);
		
		ConversationFactory cf = null;
		String question = null;
		Conversation conv = null;
		ModDataShop value = null;
		
		if(button != BUTTON.NONE) cf = new ConversationFactory(_main);
		
		switch(button)
		{
		case BACK:
			Back();
			return;
		case CONFIRM:
			Confirm();
			return;
		case NONE:
			break;
		case SHOP_NAME:
			question = Metods.msgC("&6Give shop's new name. You are able to use colors with color code( & )");
			value = ModDataShop.NAME;
			break;
		case SHOP_BUY_MUL:
			question = Metods.msgC("&6Give shop &9buy &6multiplier");
			value = ModDataShop.BUY_MUL;
			break;
		case SHOP_SELL_MUL:
			question = Metods.msgC("&6Give shop &aSell &6multiplier");
			value = ModDataShop.SELL_MUL;
			break;
		case SHOP_EXPIRE_COOLDOWN:
			question = Metods.msgC("&6Give Expire Cooldown in &aminutes");
			value = ModDataShop.EXPIRE_COOLDOWN;
			break;
		case SHOP_EXPIRE_PERCENT:
			question = Metods.msgC("&6Give Expire percent");
			value = ModDataShop.EXPIRE_PERCENT;
			break;
		case SHOP_LOCKED:
			_shopModData.SetAndCheck(ModDataShop.LOCKED, "");
			setupButtons();
			break;		
		case SHOP_ABSOLUTE_POS:
			_shopModData.SetAndCheck(ModDataShop.ABSOLUTE_POS, "");
			setupButtons();
			break;
		
		}
		
		if(value != null) 
		{
			conv = cf.withFirstPrompt(new ConvShopMod(value,this,_shopModData,question)).withLocalEcho(true).buildConversation(_player);
			_player.closeInventory();
			conv.begin();
			return;
		}
	}

	


}
