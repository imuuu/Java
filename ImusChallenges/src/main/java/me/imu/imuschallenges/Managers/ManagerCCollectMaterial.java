package me.imu.imuschallenges.Managers;

import me.imu.imuschallenges.ImusChallenges;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ManagerCCollectMaterial implements Listener
{
    private ImusChallenges _main = ImusChallenges.getInstance();
    private static ManagerCCollectMaterial _instance;

    public static ManagerCCollectMaterial getInstance() {return _instance;}

    private Set<Material> _collectedMaterials;
    private Set<Material> _unCollectedMaterials;
    private Map<UUID, Set<Material>> _playerCollectedMaterials;

    private final ConcurrentLinkedQueue<PlayerMaterialPair> buffer = new ConcurrentLinkedQueue<>();
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    public ManagerCCollectMaterial(ImusChallenges main)
    {
        _instance = this;
        this._collectedMaterials = new HashSet<>();
        this._playerCollectedMaterials = new HashMap<>();

        //creates tables, loads collected materials and creates uncollected materials
        createTables();
        startBufferProcessor();

    }

    private static class PlayerMaterialPair
    {
        final Player player;
        final Material material;

        PlayerMaterialPair(Player player, Material material)
        {
            this.player = player;
            this.material = material;
        }
    }

    private void startBufferProcessor()
    {
        scheduler.runTaskTimerAsynchronously(_main, () -> {
            while (!buffer.isEmpty())
            {
                PlayerMaterialPair pair = buffer.poll();
                if (pair != null)
                {
                    saveMaterialCollection(pair.player, pair.material);
                }

                if(pair != null && !_collectedMaterials.contains(pair.material))
                {
                    _collectedMaterials.add(pair.material);
                }
            }
        }, 40L, 100L); // Runs every 5 seconds, adjustable
    }

    private void createUncollectedMaterials()
    {
        _unCollectedMaterials = new HashSet<>();
        Bukkit.getLogger().info("[ImusChallenges] Creating uncollected materials... total of: " + Material.values().length + " materials");
        for (Material material : Material.values())
        {
            if (!_collectedMaterials.contains(material))
            {
                _unCollectedMaterials.add(material);
            }
        }
    }

    private void createTables()
    {
        Bukkit.getLogger().info("[ImusChallenges] Creating tables...");
        try (Connection con = _main.GetSQL().GetConnection())
        {
            PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS collected_materials (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "material VARCHAR(50) NOT NULL," +
                    "player_name CHAR(36) NOT NULL," +
                    "player_uuid CHAR(36) NOT NULL," +
                    "collection_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            ps.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            Bukkit.getLogger().info("[ImusChallenges] Tables created!");
            loadCollectedMaterials();
        }

    }

    private void loadCollectedMaterials()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try (Connection con = _main.GetSQL().GetConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT material FROM collected_materials"))
                {
                    try (ResultSet rs = ps.executeQuery())
                    {
                        while (rs.next())
                        {
                            Material material = Material.getMaterial(rs.getString("material"));
                            if (material != null)
                            {
                                //Bukkit.getLogger().info("[ImusChallenges] Loaded collected material: " + material.name());
                                _collectedMaterials.add(material);
                            }
                        }
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    Bukkit.getScheduler().runTask(_main, () -> createUncollectedMaterials());
                }
            }
        }.runTaskAsynchronously(_main);
    }

    private void loadPlayerCollectedMaterials(Player player)
    {
        loadPlayerCollectedMaterials(player.getUniqueId());
    }

    private void loadPlayerCollectedMaterials(UUID playerUuid)
    {
        Set<Material> playerMaterials = new HashSet<>();

        String query = "SELECT material FROM collected_materials WHERE player_uuid = ?";
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try (Connection con = _main.GetSQL().GetConnection();
                     PreparedStatement ps = con.prepareStatement(query))
                {

                    ps.setString(1, playerUuid.toString());
                    try (ResultSet rs = ps.executeQuery())
                    {
                        while (rs.next())
                        {
                            Material material = Material.getMaterial(rs.getString("material"));
                            if (material != null)
                            {
                                playerMaterials.add(material);
                            }
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    Bukkit.getScheduler().runTask(_main, () -> _playerCollectedMaterials.put(playerUuid, playerMaterials));
                }
            }
        }.runTaskAsynchronously(_main);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        UUID playerUuid = event.getPlayer().getUniqueId();
        loadPlayerCollectedMaterials(playerUuid);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        UUID playerUuid = event.getPlayer().getUniqueId();
        _playerCollectedMaterials.remove(playerUuid);
    }

    public boolean isMaterialCollected(Material material)
    {
        return _collectedMaterials.contains(material);
    }

   /* public void markMaterialAsCollected(Material material, Player player)
    {
        if (_collectedMaterials.add(material))
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    String query = "INSERT INTO collected_materials (material, player_name, player_uuid) VALUES (?, ?, ?)";
                    try (Connection con = _main.GetSQL().GetConnection();
                         PreparedStatement ps = con.prepareStatement(query))
                    {

                        ps.setString(1, material.name());
                        ps.setString(2, player.getName());
                        ps.setString(3, player.getUniqueId().toString());
                        ps.executeUpdate();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(_main);

            // Update the player's collected materials in memory
            _playerCollectedMaterials.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(material);
        }
    }*/

    public void markMaterialAsCollected(Material material, Player player)
    {
        if (!_collectedMaterials.contains(material))
        {
            _collectedMaterials.add(material);
            buffer.offer(new PlayerMaterialPair(player, material));
        }
    }


    private void saveMaterialCollection(Player player, Material material)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                String query = "INSERT INTO collected_materials (material, player_name, player_uuid) VALUES (?, ?, ?)";
                try (Connection con = _main.GetSQL().GetConnection();
                     PreparedStatement ps = con.prepareStatement(query))
                {

                    ps.setString(1, material.name());
                    ps.setString(2, player.getName());
                    ps.setString(3, player.getUniqueId().toString());
                    ps.executeUpdate();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(_main);
    }

    // Additional methods as needed...
}
