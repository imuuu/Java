package imu.iAPI.Other;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MySQL 
{
	private Plugin _plugin;
	private String _host = "localhost";
	private Integer _port = 3306;
	private String _dataBase = "";
	private String _username ="root";
	private String _password ="";
	
	private Connection _connection;
	private Cooldowns _cds;
	
	private BukkitTask RunnableAsyncTask;
	public MySQL(Plugin plugin, String dataBaseName) 
	{
		_plugin = plugin;
		_dataBase = dataBaseName;
		_cds = new Cooldowns();
		LoadConfig();
		
		RunnableAsync();
		
	}
	
	public boolean IsConnected()
	{
		return (_connection == null ? false : true);
	}
	
	public void Connect() throws ClassNotFoundException, SQLException
	{
		if(!IsConnected())
		{
			Con("");
			
			PreparedStatement ps;
			try 
			{
				ps = _connection.prepareStatement("CREATE DATABASE IF NOT EXISTS "+_dataBase);
				ps.executeUpdate();
				Disconnect();
				
			} catch (Exception e) 
			{
				Bukkit.getLogger().info("Couldn't Create database named:" +_dataBase);
			}
			
			Con(_dataBase);
		}
		
	}
	void Con(String dataBase) throws ClassNotFoundException, SQLException
	{
		_connection = DriverManager.getConnection("jdbc:mysql://" 
				+ _host 
				+ ":"
				+_port
				+"/"
				+dataBase+"?autoReconnect=true",
				_username,
				_password);
	}
//	void Con(String dataBase) throws ClassNotFoundException, SQLException
//	{
//		_connection = DriverManager.getConnection("jdbc:mysql://" 
//				+ _host 
//				+ ":"
//				+_port
//				+"/"
//				+dataBase+"?useSSL=false",
//				_username,
//				_password);
//	}
	public void Disconnect()
	{
		if(IsConnected())
		{
			try {
				_connection.close();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public Connection GetConnection()
	{		
		try 
		{
			if(_connection == null || _connection.isClosed())
			{
				//System.out.println("IS CLOSED ==> RECONNECT!");
				Con(_dataBase);
			}
			
			//Con(_dataBase);
			
		} catch (Exception e) 
		{
			Bukkit.getLogger().info("Coulnd't reconnect to database named:" +_dataBase);
		}
		//System.out.println("sending new connection: "+_connection);
		return _connection;
	}
	
	public boolean ExecuteStatements(Iterable<String> statements)
	{
		try 
		{
			Connection con = GetConnection();
			Statement stmt = con.createStatement();
			for(String sm : statements)
			{
				stmt.addBatch(sm);
			}
			stmt.executeBatch();
			
			stmt.close();
			con.close();

			return true;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
			
		}
	}
	
	public void  ExecuteStatementsAsync(Iterable<String> statements)
	{
		new BukkitRunnable() {
			
			@Override
			public void run() 
			{
				ExecuteStatements(statements);				
			}
		}.runTaskAsynchronously(_plugin);
	}
	
	void RunnableAsync()
	{

		final String syntax = "["+_dataBase+"]";
		RunnableAsyncTask = new BukkitRunnable() 
		{			
			@Override
			public void run() 
			{	
				if(_cds.isCooldownReady("SQL_CONNECTION_CHECK"))
				{
					_cds.setCooldownInSeconds("SQL_CONNECTION_CHECK", 60*60);
					
					
					if(CheckConnection())
					{
						//Bukkit.getLogger().info(syntax+"Checking SQL connection and its TRUE");
					}else
					{
						Bukkit.getLogger().info(syntax+"Checking SQL connection and its FALSE");
					}
				}
				
			}
		}.runTaskTimerAsynchronously(_plugin, 20*60,20 * 60 * 5);
		
		
	}
	public boolean CheckConnection() 
	{		
		boolean connected = true;
		try
		{
			Connection con = GetConnection();
			PreparedStatement ps = con.prepareStatement("SHOW PROCESSLIST");
			ps.executeQuery();
			
			con.close();
			ps.close();
		} 
		catch (Exception e)
		{
			//System.out.println("[imusGS] Tried to check SQL connection but failed");
			//System.out.println(e);
			connected = false;
		}
		//RemovePriceValue(shopItemSellerUUID);
		
		return connected;
		
		
	}
	void LoadConfig()
	{
		ConfigMaker cm = new ConfigMaker(_plugin, "DataBaseSettings.yml");
		if(!cm.isExists())
		{
			Bukkit.getLogger().info("DataBaseSettings.yml created!");
			cm.saveConfig();
		}
		try {
			_host = cm.addDefault("Host", _host, "Host of Database");
			_port = cm.addDefault("Port", _port, "Port of Database");
			_dataBase = cm.addDefault("Database", _dataBase, "Database");
			_username = cm.addDefault("Username", _username, "User for database");
			_password = cm.addDefault("Password", _password, "User password for database");
			cm.addComments();
		} 
		catch (Exception e) {
			//e.printStackTrace();
			_plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED +"WARNING: Something went wrong loading "+cm.getFileName());
		}
		
		
	}
}
