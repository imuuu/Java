package imu.iWaystones.Managers;

import java.sql.PreparedStatement;

import imu.iWaystones.Enums.SQL_tables;
import imu.iWaystones.Main.ImusWaystones;

public class WaystoneManagerSQL 
{
	ImusWaystones _main = ImusWaystones._instance;
	
	public WaystoneManagerSQL()
	{
		
	}
	
	public void LoadTables()
	{
		if(_main.GetSQL() == null)
			return;
		
		PreparedStatement ps;
		try 
		{
			_main.getLogger().info("===LOADING TABLES===");
			ps = _main.GetSQL().GetConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+SQL_tables.Waystones.toString()+"("
					+ "uuid CHAR(36) NOT NULL, "
					+ "name VARCHAR(100), "
					+ "loc_world INT NOT NULL, "
					+ "loc_x INT NOT NULL, "
					+ "loc_y INT NOT NULL, "
					+ "loc_z INT NOT NULL, "
					+ "PRIMARY KEY(uuid));");
			ps.executeUpdate();
			
			_main.getLogger().info("==> Waystones");
			
			
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		_main.getLogger().info("===TABLE LOADING FINNISHED===");
	}
}
