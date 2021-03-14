package imu.iMiniGames.Invs;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import imu.iMiniGames.Arenas.SpleefArena;
import imu.iMiniGames.Main.Main;
import imu.iMiniGames.Managers.PlanerManager;
import imu.iMiniGames.Other.SpleefDataCard;
import imu.iMiniGames.Other.SpleefGameCard;
import imu.iMiniGames.Prompts.ConvPromptGamePlaner;

public class SpleefGamePlaner extends GamePlaner
{

	SpleefDataCard _card;
	PlanerManager _pm;
	
	ArrayList<Integer> wrongs = new ArrayList<>();
	
	public SpleefGamePlaner(Main main, Player player, SpleefDataCard card) 
	{
		super(main, player, ChatColor.BLUE + ""+ChatColor.BOLD + "Spleef Planer");
		
		_card = card;
		_pm = main.get_planerManager();

		reset();
	}
	
	void reset()
	{
		setupButtons();
		checkAnwsers();
	}
	
	void setupButtons() 
	{
		setupButton(BUTTON.EXIT, Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "EXIT", _size-9);
		setupButton(BUTTON.CONFIRM, Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "CONFIRM", _size-1);
		setupButton(BUTTON.RESET, Material.LAVA_BUCKET, ChatColor.RED +""+ChatColor.BOLD+ "RESET", 8);
		
		ItemStack b_arena = addLoreSetRemove(setupButton(BUTTON.SET_ARENA, Material.PAINTING, ChatColor.AQUA + "Set Arena", 0));
		String arenaName = _main.get_spleefManager().getArena(0) != null ? _main.get_spleefManager().getArena(0).get_name() : "No arena";
		_card.putDataValue(0, arenaName);
		
		_itemM.addLore(b_arena, "Not implemented!", true);
		
		addLoreSetRemove(setupButton(BUTTON.ADD_BET,Material.GOLD_INGOT, ChatColor.AQUA + "Place Bet", 2));
		
		addLoreSetRemove(setupButton(BUTTON.ADD_PLAYERS, Material.WITHER_SKELETON_SKULL, ChatColor.AQUA + "Add Players", 4));
	}
	
	ItemStack addLoreSetRemove(ItemStack stack)
	{
		return _itemM.addLore(stack, ChatColor.AQUA + "M1: "+ChatColor.GREEN + "Set"+ChatColor.AQUA + " M2: "+ChatColor.RED + "Remove", false);		
	}
	
	void setPDvalue(ItemStack stack, String value)
	{
		_pm.setPDvalue(stack,value);
		String str = "Set to: ";
		
		_itemM.removeLore(stack, str);
		_itemM.addLore(stack, ChatColor.AQUA + str +ChatColor.DARK_GREEN + value, true);
		
	}
	void removePDvalue(ItemStack stack)
	{
		if(stack == null)
			return;
		
		_pm.removePDvalue(stack);
		String str = "Set to: ";
		
		_itemM.removeLore(stack, str);
		
	}
	
	void checkAnwsers()
	{
		if(!_card.get_invDataValues().isEmpty())
		{
			for(int i = 0; i < _inv.getContents().length; ++i)
			{
				String value = _card.getDataValue(i);
				if(value != null)
				{
					setPDvalue(_inv.getItem(i), value);
					if(wrongs.contains(i))
					{
						_inv.getItem(i).setType(Material.YELLOW_STAINED_GLASS);
					}
				}else
				{
					removePDvalue(_inv.getItem(i));
				}
			}
		}
			
	}
	
	SpleefGameCard confirm()
	{
		
		
		SpleefGameCard gameCard = new SpleefGameCard();
		gameCard.set_maker(_player);
		wrongs.clear();
		for(int i = 0; i < _inv.getContents().length; ++i)
		{
			ItemStack s = _inv.getItem(i);
			if(s != null)
			{
				
				BUTTON button = getButton(s);

				String value = _pm.getPDvalue(s);
				
				switch (button) 
				{
				case ADD_BET:
					if(value == null)
						continue;
					
					if(_itemM.isDigit(value))
					{
						gameCard.set_bet(Double.parseDouble(value));
					}else
					{
						_player.sendMessage(ChatColor.RED + "Please add only numbers");
						wrongs.add(i);
					}
					break;
				case ADD_PLAYERS:
					Player p;
					String[] players_str = value != null ? value.trim().replaceAll(" +"," ").split(" ") : null;

					if(value != null && players_str.length > 0)
					{
						boolean found = false;
						for(String pName : players_str)
						{
							p = Bukkit.getPlayer(pName);
							if(p != null)
							{
								found = true;
								gameCard.putPlayer(p);
							}
						}
						
						if(!found)
						{
							_player.sendMessage(ChatColor.RED+ "Didnt find any players with given names");
							wrongs.add(i);
						}
													
					}
					else
					{
						_player.sendMessage(ChatColor.RED + "There isn't any players");
						wrongs.add(i);
					}
					break;
				case SET_ARENA:
					SpleefArena arena = _main.get_spleefManager().getArena(value);
					if(arena != null)
					{
						gameCard.set_arena(arena);
					}
					else
					{
						_player.sendMessage(ChatColor.RED + "Didn't find arena with that name: " + value);
						wrongs.add(i);
					}
					
					break;
				default:
					break;
					
				}
			}
		}
		
		if(!wrongs.isEmpty())
		{
			
			return null;
		}
		else
		{
			gameCard.set_total_bet(gameCard.get_bet() * gameCard.get_players_accept().size());
			gameCard.set_spleefDataCard(_card);
			
			return gameCard;
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		int rawSlot = e.getRawSlot();
		int slot = e.getSlot();
		
		if(isThisInv(e) && (rawSlot == slot))
		{			
			e.setCancelled(true);
			ItemStack stack = e.getCurrentItem();
			
			ConversationFactory cf = null;
			String question = null;
			Conversation conv = null;
			
			BUTTON button = getButton(stack);
			
			if(e.getClick() == ClickType.LEFT)
			{
				switch (button) {
				
				case NONE:				
					break;
				
				case EXIT:
					_player.closeInventory();
					break;
				case CONFIRM:
					SpleefGameCard gameCard = confirm();
					if(gameCard != null)
					{	
						if(!_main.get_spleefGameHandler().repearStartGame(_player, gameCard))
						{
							_player.sendMessage(ChatColor.DARK_AQUA + "Arena was busy! Your plan has been saved and can be accesed /pla pla spleefGamePlaner: 230");
						}
						_player.closeInventory();
						
					}
					else
					{
						System.out.println("Card null");
						checkAnwsers();
					}
					break;
				case ADD_BET:
					cf = new ConversationFactory(_main);
					question = ChatColor.DARK_PURPLE + "Give your bet";
					conv = cf.withFirstPrompt(new ConvPromptGamePlaner(_main, _player, _card, slot, question)).withLocalEcho(true).buildConversation(_player);
					conv.begin();
					_player.closeInventory();
					break;
				case ADD_PLAYERS:
					cf = new ConversationFactory(_main);
					question = ChatColor.DARK_PURPLE + "Give players seprate with space!(ex: imu joksu789";
					conv = cf.withFirstPrompt(new ConvPromptGamePlaner(_main, _player, _card, slot, question)).withLocalEcho(true).buildConversation(_player);
					conv.begin();
					_player.closeInventory();
					break;
				case RESET:
					_card = new SpleefDataCard(_player);
					reset();
					break;

				default:
					break;
				}
			}
			else if(e.getClick() == ClickType.RIGHT)
			{
				switch (button) 
				{
				case ADD_BET:
					_card.removeDataValue(slot);
					if(wrongs.contains(slot))
						wrongs.remove(slot);
					//setupButtons();
					checkAnwsers();
					break;
				case ADD_PLAYERS:
					_card.removeDataValue(slot);
					if(wrongs.contains(slot))
						wrongs.remove(slot);
					//setupButtons();
					checkAnwsers();
					break;

				case SET_ARENA:
					break;
				default:
					break;
				
				
				
				}
			}
			
			
		}
	
	}
}
