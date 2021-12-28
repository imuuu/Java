package imu.iWaystones.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import imu.iAPI.Main.ImusAPI;
import imu.iWaystone.Upgrades.BaseUpgrade;
import imu.iWaystone.Upgrades.PlayerUpgradePanel;
import imu.iWaystone.Waystones.Waystone;
import imu.iWaystones.Enums.SQL_tables;
import imu.iWaystones.Enums.UpgradeType;
import imu.iWaystones.Main.ImusWaystones;

public class WaystoneManagerSQL 
{
	ImusWaystones _main = ImusWaystones._instance;
	WaystoneManager _waystoneManager;
	
	public WaystoneManagerSQL(WaystoneManager wm)
	{
		_waystoneManager = wm;
	}
	
	public void CreateTables()
	{
		if(_main.GetSQL() == null)
			return;
		
		
		try (Connection con = _main.GetSQL().GetConnection();)
		{
			PreparedStatement ps;
			
			
			_main.getLogger().info("===LOADING TABLES===");
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_tables.waystones.toString()+"("
					+ "uuid CHAR(36) NOT NULL, "
					+ "name VARCHAR(100), "
					+ "loc_world VARCHAR(20), "
					+ "loc_x INT NOT NULL, "
					+ "loc_y INT NOT NULL, "
					+ "loc_z INT NOT NULL, "
					+ "display_item TEXT(16000), "
					+ "PRIMARY KEY(uuid));");
			ps.executeUpdate();
			ps.close();
			_main.getLogger().info("==> Waystones");
			//_main.GetSQL().GetConnection().close();
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_tables.discovered.toString()+"("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid_player CHAR(36) NOT NULL, "
					+ "uuid_ws CHAR(36) NOT NULL, "
					+ "PRIMARY KEY(id));");
			ps.executeUpdate();
			ps.close();
			_main.getLogger().info("==> Discovered");
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_tables.waystone_owners.toString()+"("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid_player CHAR(36) NOT NULL, "
					+ "player_name VARCHAR(50) NOT NULL, "
					+ "uuid_ws CHAR(36) NOT NULL, "
					+ "PRIMARY KEY(id));");
			ps.executeUpdate();
			ps.close();
			_main.getLogger().info("==> owners");
			
			
			ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_tables.upgrades.toString()+"("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid_ws CHAR(36) NOT NULL, "
					+ "uuid_player CHAR(36) NOT NULL, "
					+ "upgrade_name VARCHAR(20) NOT NULL, "
					+ "tier INT(10) NOT NULL, "
					+ "PRIMARY KEY(id));");
			ps.executeUpdate();
			ps.close();
			_main.getLogger().info("==> upgrades");

		} catch (Exception e) 
		{
			//e.printStackTrace();
			_main.getLogger().info("===> TABLE LOADING ERROR===");
			return;
		}
		_main.getLogger().info("===TABLE LOADING FINNISHED===");
	}
	
	Waystone LoadUpgrades(Waystone waystone)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_tables.upgrades.toString()+" "
				+ "WHERE uuid_ws='"+waystone.GetUUID().toString()+"';"))
		{
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst())
			{
				return waystone;
			}
			while(rs.next())
			{
				UUID uuid_player = UUID.fromString(rs.getString(3));
				UpgradeType type = UpgradeType.valueOf(rs.getString(4));
				int tier = rs.getInt(5);
				BaseUpgrade upgrade = _waystoneManager.GetNewUpgrade(type);
				upgrade.SetCurrentier(tier);
				waystone.SetPlayerUpgrade(uuid_player, upgrade);
				return waystone;
			}
		} 
		catch (Exception e) 
		{
			_main.getLogger().info("===> LOADING ERROR: LoadUpgrades ===");
		}
		return waystone;
	}
	
	void SaveUpgrades(Waystone waystone)
	{
		
		try (Connection con = _main.GetSQL().GetConnection()){
			
			for(Map.Entry<UUID, PlayerUpgradePanel> entry: waystone.GetPlayerUpgrades().entrySet())
			{
				for(BaseUpgrade upgrade : entry.getValue().GetUpgrades())
				{
					PreparedStatement ps = con.prepareStatement("REPLACE INTO "+SQL_tables.upgrades.toString()+" "
							+ "(uuid_ws,uuid_player,upgrade_name, tier) VALUES (?,?,?,?);");
					
					int i = 1;
					ps.setString(i++, waystone.GetUUID().toString());
					ps.setString(i++, entry.getKey().toString());
					ps.setString(i++, upgrade._id.toString());
					ps.setInt(i++, upgrade.GetCurrentTier());
					ps.executeUpdate();
					ps.close();
				}
				
			}
		} 
		catch (Exception e) 
		{
			_main.getLogger().info("===> SAVING ERROR: SaveUpgrades ===");
		}		
	}
	
	public void SaveUpgradeAsync(UUID uuid_player,UUID uuid_ws , BaseUpgrade upgrade)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				
				try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_tables.upgrades.toString()+" "
						+ "WHERE uuid_ws=? AND uuid_player=? AND upgrade_name=?;"))
				{
					
					ps.setString(1, uuid_ws.toString());
					ps.setString(2, uuid_player.toString());
					ps.setString(3, upgrade._id.toString());
					ps.executeUpdate();
					ps.close();
				} 
				catch (Exception e) {
					_main.getLogger().info("===> SAVING ERROR1: SaveUpgradeAsync ===");
				}
				
				try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO "+SQL_tables.upgrades.toString()+" "
						+ "(uuid_ws,uuid_player,upgrade_name,tier) VALUES (?,?,?,?);"))
				{
					
					ps.setString(1, uuid_ws.toString());
					ps.setString(2, uuid_player.toString());
					ps.setString(3, upgrade._id.toString());
					ps.setInt(4, upgrade.GetCurrentTier());
					ps.executeUpdate();
					ps.close();
				} 
				catch (Exception e) {
					_main.getLogger().info("===> SAVING ERROR2: SaveUpgradeAsync ===");
				}
			}
		}.runTaskAsynchronously(_main);
		
	}
	
	void RemoveUpgrades(Waystone waystone)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_tables.upgrades.toString()+" "
				+ "WHERE uuid_ws='"+waystone.GetUUID().toString()+"';"))
		{
			ps.executeUpdate();
			
		} catch (SQLException e) {
			
			_main.getLogger().info("===> REMOVING ERROR: RemoveUpgrade ===");
		}
	}
	
	Waystone LoadWaystoneOwner(Waystone waystone)
	{
		try	
				(
				PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_tables.waystone_owners.toString()+" "
				+ "WHERE uuid_ws='"+waystone.GetUUID().toString()+"';");
				
				)
		{
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst())
			{
				return waystone;
			}
			while(rs.next())
			{
				waystone.SetOwner(rs.getString(3), UUID.fromString(rs.getString(2)));
				return waystone;
			}
			
		} catch (SQLException e) {
			
			_main.getLogger().info("===> LOADING ERROR: LoadWaystoneOwner ===");
			//e.printStackTrace();
		}
		return waystone;
	}
	
	void SaveWaystoneOwner(UUID uuid_player, String player_name,UUID uuid_ws)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO "+SQL_tables.waystone_owners.toString()+" "
				+ "(uuid_player,player_name,uuid_ws) VALUES (?,?,?);");)
		{
			
			int i = 1;
			ps.setString(i++, uuid_player.toString());
			ps.setString(i++, player_name);
			ps.setString(i++, uuid_ws.toString());			
			ps.executeUpdate();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	void RemoveWaystoneOwner(UUID uuid_player, UUID uuid_ws)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_tables.waystone_owners.toString()+" "
				+ "WHERE uuid_player='"+uuid_player.toString()+"' AND uuid_ws='"+uuid_ws.toString()+"';");)
		{			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void SaveWaystone(Waystone waystone)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("REPLACE INTO "+SQL_tables.waystones.toString()+" "
				+ "(uuid, name, loc_world, loc_x, loc_y, loc_z, display_item) VALUES (?,?,?,?,?,?,?)");)
		{
			
			int i = 1;
			ps.setString(i++, waystone.GetUUID().toString());
			ps.setString(i++, waystone.GetName());
			ps.setString(i++, waystone.GetLoc().getWorld().getName());
			ps.setInt(i++, waystone.GetLoc().getBlockX());
			ps.setInt(i++, waystone.GetLoc().getBlockY());
			ps.setInt(i++, waystone.GetLoc().getBlockZ());
			ps.setString(i++, ImusAPI._metods.EncodeItemStack(waystone.GetDisplayItem()));
			ps.executeUpdate();
			SaveWaystoneOwner(waystone.GetOwnerUUID(), waystone.GetOwnerName(), waystone.GetUUID());
			SaveUpgrades(waystone);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	public void SaveWaystoneAsync(Waystone waystone)
	{
		new BukkitRunnable() {		
			@Override
			public void run() 
			{
				SaveWaystone(waystone);				
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void RemoveWaystoneAsync(Waystone waystone)
	{
		new BukkitRunnable() {		
			@Override
			public void run() 
			{
				RemoveWaystone(waystone);				
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void RemoveWaystone(Waystone waystone)
	{

		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_tables.waystones.toString()+ " WHERE uuid='"+waystone.GetUUID().toString()+"';");)
		{
			
			//System.out.println("try to remove: "+waystone.GetUUID());
			ps.executeUpdate();
			RemoveWaystoneOwner(waystone.GetOwnerUUID(), waystone.GetUUID());
			RemoveUpgrades(waystone);
		} catch (SQLException e) {

			Bukkit.getLogger().info("Couldnt delete waystone from database! waystone UUID: "+waystone.GetUUID().toString());
			e.printStackTrace();
		}
	}
	
	public void LoadWaystones()
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_tables.waystones.toString()+";");)
		{
			System.out.println("LOADING waystones!: ");
			_waystoneManager.GetWaystones().clear();
			
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst())
			{
				return;
			}
			while(rs.next())
			{
				int i = 1;
				
				UUID uuid = UUID.fromString(rs.getString(i++));
				String name = rs.getString(i++);
				World world = Bukkit.getWorld(rs.getString(i++));
				int x = rs.getInt(i++);
				int y = rs.getInt(i++);
				int z = rs.getInt(i++);
				ItemStack displayItem = ImusAPI._metods.DecodeItemStack(rs.getString(i++));
				Waystone newWaystone = new Waystone(new Location(world,x,y,z));
				newWaystone.SetName(name);
				newWaystone.SetDisplayitem(displayItem);
				newWaystone.SetUUID(uuid);
				//newWaystone.SetOwner(null);
				newWaystone = LoadWaystoneOwner(newWaystone);
				newWaystone = LoadUpgrades(newWaystone);
				_waystoneManager.SaveWaystone(newWaystone, false);					
			}
			
		} catch (SQLException e) {
			
			System.out.println("Couldnt load waystones!");
			e.printStackTrace();
		}
	}
	
	public void LoadWaystonesAsync()
	{
		new BukkitRunnable() {		
			@Override
			public void run() 
			{
				LoadWaystones();
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void SaveDiscoveredAsync(UUID uuid_player, UUID uuid_ws)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() {
				SaveDiscovered(uuid_player, uuid_ws);
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void SaveDiscovered(UUID uuid_player, UUID uuid_ws)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("INSERT INTO "+SQL_tables.discovered.toString()+" "
				+ "(uuid_player,uuid_ws) VALUES (?,?);");)
		{
			
			int i = 1;
			ps.setString(i++, uuid_player.toString());
			ps.setString(i++, uuid_ws.toString());			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void LoadDiscoveredWaystonesAsync()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() {
				LoadDiscoveredWaystones();
			}
		}.runTaskAsynchronously(_main);
	}
	public void LoadDiscoveredWaystones()
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("SELECT * FROM "+SQL_tables.discovered.toString()+"");)
		{
			_waystoneManager.GetDiscovered().clear();
			System.out.println("LOADING waystones!");
			
			ResultSet rs = ps.executeQuery();
			if(!rs.isBeforeFirst())
			{
				return;
			}
			while(rs.next())
			{
				int i = 2;
				
				UUID uuid_player = UUID.fromString(rs.getString(i++));
				UUID uuid_ws = UUID.fromString(rs.getString(i++));
				_waystoneManager.AddDiscovered(uuid_player, uuid_ws, false);
			}
			
		} catch (SQLException e) {
			
			System.out.println("Couldnt load waystones!");
			//e.printStackTrace();
		}
	}
	
	
	public void RemoveDiscoveredAsync(UUID uuid_ws)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() {
				RemoveDiscovered(uuid_ws);
			}
		}.runTaskAsynchronously(_main);
	}
	public void RemoveDiscovered(UUID uuid_ws)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_tables.discovered.toString()+" WHERE uuid_ws='"+uuid_ws+"';");)
		{
			
			ps.executeUpdate();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Couldnt remove discover!");
		}		
		
	}
	
	public void RemoveDiscoveredFromPlayerAsync(UUID uuid_player, UUID uuid_ws)
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() {
				RemoveDiscoveredFromPlayer(uuid_player, uuid_ws);
			}
		}.runTaskAsynchronously(_main);
	}
	
	public void RemoveDiscoveredFromPlayer(UUID uuid_player, UUID uuid_ws)
	{
		try (PreparedStatement ps = _main.GetSQL().GetConnection().prepareStatement("DELETE FROM "+SQL_tables.discovered.toString()+" WHERE uuid_player='"+uuid_player.toString()+"' AND uuid_ws='"+uuid_ws+"' ;");)
		{			
			ps.executeUpdate();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Couldnt remove player discover!");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

