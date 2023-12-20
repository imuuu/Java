package me.imu.imuschallenges.Managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import imu.iAPI.LootTables.LootTableItemStack;
import imu.iAPI.Other.Metods;
import me.imu.imuschallenges.CONSTANTS;
import me.imu.imuschallenges.Database.Tables.TablePlayerShopStats;
import me.imu.imuschallenges.Database.Tables.TablePlayers;
import me.imu.imuschallenges.ImusChallenges;
import me.imu.imuschallenges.Interfaces.ShopStatsCallback;
import me.imu.imuschallenges.Inventories.InventoryChallengeShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ManagerChallengeShop
{
    private static ManagerChallengeShop _instance;

    public static ManagerChallengeShop getInstance()
    {
        return _instance;
    }

    private LootTableItemStack _tier_1_lootTable;
    private LootTableItemStack _challengePointAmounts;
    private ArrayList<ItemStack> _generatedItems;
    private ArrayList<Integer> _itemCosts;
    private HashSet<Player> _hasShopOpen = new HashSet<>();
    private final HashSet<UUID> _hasPlayerBuyNormal =  new HashSet<>();

    private Dao<TablePlayerShopStats, Integer> playerShopStatsDao;

    private final int MAX_ITEMS = 16;
    //private final int TIME_BETWEEN_GENERATIONS = 3 * 60 * 60 * 20; // 3 hours in server ticks (20 ticks = 1 second)
    private final int TIME_BETWEEN_GENERATIONS = 30 * 20; // 30 seconds in server ticks (20 ticks = 1 second)

    private HashSet<Material> _excludedMaterials = new HashSet<>();

    public ManagerChallengeShop()
    {
        _instance = this;
        try
        {
            InitSQLData();
            playerShopStatsDao = DaoManager.createDao(ImusChallenges.getInstance().getSource(), TablePlayerShopStats.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        InitExcludedMaterials();
        initLoot();
        _generatedItems = new ArrayList<>();
        _itemCosts = new ArrayList<>();
        scheduleItemGeneration();
    }

    public void openShop(Player player)
    {
        new InventoryChallengeShop().open(player);
    }

    public void addPlayerToShop(Player player)
    {
        _hasShopOpen.add(player);
    }

    public void removePlayerFromShop(Player player)
    {
        _hasShopOpen.remove(player);
    }

    public ArrayList<Player> closeAllShops()
    {
        ArrayList<Player> players = new ArrayList<>();
        for (Player player : _hasShopOpen)
        {
            player.closeInventory();
            players.add(player);
        }
        _hasShopOpen.clear();
        return players;
    }
    private void InitExcludedMaterials()
    {
        _excludedMaterials.add(Material.NETHER_STAR);
        _excludedMaterials.add(Material.NETHERITE_SCRAP);
        _excludedMaterials.add(Material.NETHERITE_INGOT);
        _excludedMaterials.add(Material.NETHERITE_BLOCK);
        _excludedMaterials.add(Material.EMERALD_BLOCK);
        _excludedMaterials.add(Material.ELYTRA);
        _excludedMaterials.add(Material.EMERALD_ORE);
        _excludedMaterials.add(Material.DIAMOND_BLOCK);
        _excludedMaterials.add(Material.BEDROCK);
        _excludedMaterials.add(Material.BARRIER);
        _excludedMaterials.add(Material.DEBUG_STICK);

    }

    private void initLoot()
    {
        _tier_1_lootTable = new LootTableItemStack();

        _tier_1_lootTable.Add(new ItemStack(Material.STONE), 200, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.DIAMOND), 22, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.DIAMOND_BLOCK), 10, 16);
        _tier_1_lootTable.Add(new ItemStack(Material.NETHER_STAR), 1, 1);
        _tier_1_lootTable.Add(new ItemStack(Material.NETHERITE_SCRAP), 15, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.NETHERITE_BLOCK), 1, 1);
        _tier_1_lootTable.Add(new ItemStack(Material.NETHERITE_INGOT), 4, 12);
        _tier_1_lootTable.Add(new ItemStack(Material.EMERALD_BLOCK), 30, 33);
        _tier_1_lootTable.Add(new ItemStack(Material.ELYTRA), 1, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.EMERALD_ORE), 35, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.IRON_INGOT), 40, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.GOLD_INGOT), 40, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.LAPIS_LAZULI), 37, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.COAL), 70, 64);
        _tier_1_lootTable.Add(new ItemStack(Material.COAL_BLOCK), 28, 33);
        _tier_1_lootTable.Add(new ItemStack(Material.IRON_BLOCK), 28, 33);
        _tier_1_lootTable.Add(new ItemStack(Material.GOLD_BLOCK), 28, 33);
        _tier_1_lootTable.Add(new ItemStack(Material.LAPIS_BLOCK), 20, 30);
    }

    private int getRandomCost()
    {
        return ThreadLocalRandom.current().nextInt(CONSTANTS.MIN_CHALLENGE_POINTS_NORMAL, CONSTANTS.MAX_CHALLENGE_POINTS_NORMAL);
    }
    public ArrayList<ItemStack> getGeneratedItems()
    {
        return _generatedItems;
    }

    public ArrayList<Integer> getItemCosts()
    {
        return _itemCosts;
    }

    public int getSlotPrice(int slotNumber)
    {
        return (int) (CONSTANTS.FIRST_SLOT_PRICE * Math.pow(CONSTANTS.FIRST_SLOT_PRICE_POW, slotNumber));
    }
    private void generateItems()
    {
        _generatedItems.clear();
        _itemCosts.clear();
        _hasPlayerBuyNormal.clear();

        for (int i = 0; i < MAX_ITEMS; i++)
        {
            ItemStack item = _tier_1_lootTable.GetLoot();

            if (item == null)
                continue;

            if (item.getType() == Material.STONE)
            {
                item = getRandomItem();
            }

            if(generatedItemContainsMaterial(item.getType()))
            {
                i--;
                if(i < 0) i = 0;
                continue;
            }

            _generatedItems.add(item);
            _itemCosts.add(getRandomCost());
        }
    }
    private boolean generatedItemContainsMaterial(Material material)
    {
        for (ItemStack item : _generatedItems)
        {
            if (item.getType() == material)
                return true;
        }
        return false;
    }
    private ItemStack getRandomItem()
    {
        Material[] materials = Material.values();
        ItemStack stack = null;

        while (stack == null)
        {
            int index = (int) (Math.random() * materials.length);
            Material material = materials[index];

            if (ManagerCCollectMaterial.getInstance().isExcludedMaterial(material))
                continue;

            if (_excludedMaterials.contains(material))
                continue;



            if (material.isItem())
            {
                stack = new ItemStack(material);
                stack.setAmount(ThreadLocalRandom.current().nextInt(1, 64));
            }
        }

        if (stack.getAmount() > stack.getMaxStackSize())
            stack.setAmount(stack.getMaxStackSize());

        return stack;
    }

    public boolean hasPlayerBuyNormal(UUID uuid)
    {
        return _hasPlayerBuyNormal.contains(uuid);
    }

    public void setPlayerBuyNormal(UUID uuid, boolean value)
    {
        if(value)
        {
            _hasPlayerBuyNormal.add(uuid);
        }
        else
        {
            _hasPlayerBuyNormal.remove(uuid);
        }
    }

    private void scheduleItemGeneration()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ImusChallenges.getInstance(), new Runnable()
        {
            @Override
            public void run()
            {
                ArrayList<Player> players = closeAllShops();
                for (Player player : players)
                {
                    player.sendMessage(Metods.msgC("&9Challenge shop has been updated!"));
                }
                generateItems();

                for (Player player : players)
                {
                    openShop(player);
                }
            }
        }, 0L, TIME_BETWEEN_GENERATIONS);
    }

    // SQL ============================================================================================================

    private void InitSQLData() throws SQLException
    {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException
    {
        TableUtils.createTableIfNotExists(ImusChallenges.getInstance().getSource(), TablePlayerShopStats.class);
    }

    private void addShopStats(TablePlayerShopStats playerShopStats) throws SQLException
    {
        playerShopStatsDao.create(playerShopStats);
    }

    private TablePlayerShopStats getShopStatsByPlayerId(Player player) throws SQLException
    {
        // First, find or create the TablePlayers instance for this Player
        TablePlayers tablePlayer = ManagerPlayers.getInstance().findOrCreatePlayer(player);

        List<TablePlayerShopStats> stats = playerShopStatsDao.queryForEq("player_id", tablePlayer.getId());
        if (!stats.isEmpty())
        {
            return stats.get(0);
        }
        else
        {
            TablePlayerShopStats newShopStats = new TablePlayerShopStats();
            newShopStats.setPlayer(tablePlayer);

            newShopStats.setBought_normal_slots(CONSTANTS.PLAYER_SHOP_STATS_DEFAULT_VALUE);

            playerShopStatsDao.create(newShopStats);

            return newShopStats;
        }
    }


    public void addSlotsToPlayerShopAsync(Player player, int addSlotAmount)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                addSlotsToPlayerShop(player, addSlotAmount);
            }
        }.runTaskAsynchronously(ImusChallenges.getInstance());
    }

    private void addSlotsToPlayerShop(Player player, int addSlotAmount)
    {
        try
        {
            TablePlayers tablePlayer = ManagerPlayers.getInstance().findOrCreatePlayer(player);
            TablePlayerShopStats currentStats = getShopStatsByPlayerId(player);

            if (currentStats == null)
            {
                currentStats = new TablePlayerShopStats();
                currentStats.setPlayer(tablePlayer);
                currentStats.setBought_normal_slots(addSlotAmount);
                playerShopStatsDao.create(currentStats);
            }
            else
            {
                int newSlotCount = currentStats.getBought_normal_slots() + addSlotAmount;
                currentStats.setBought_normal_slots(newSlotCount);
                playerShopStatsDao.update(currentStats);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Asynchronously gets the shop stats for a player and executes a callback with the retrieved stats.
     *
     * @param player   The player whose stats are to be fetched.
     * @param callback The callback to be executed with the fetched stats main thread.
     */
    public void getShopStatsAsync(Player player, ShopStatsCallback callback)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    TablePlayerShopStats shopStats = getShopStatsByPlayerId(player);
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            callback.onShopStatsRetrieved(shopStats);
                        }
                    }.runTask(ImusChallenges.getInstance());
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(ImusChallenges.getInstance());
    }

}
