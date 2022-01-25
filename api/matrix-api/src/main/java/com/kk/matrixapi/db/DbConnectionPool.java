package com.kk.matrixapi.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is used to implement connection pool functionality across the application.
 * It stores the information necessary for creating new connections, and has methods
 * to create connections when needed, and get free connections from the connection pool.
 * @author Deximal Development
 *
 */
public class DbConnectionPool {
	
	//Username for database connection
	private char[] username;
	//Password for database connection
	private char[] password;
	//Database host
	private String host;
	//Database driver
	private String driver;
	//Port the database is running on
	private String port;
	//Which database is being connected to at the target host
	private String databaseName;
	
	//The number of connections currently in use
	private int usedConnections;
	//The maximum number of connection in the pool
	//If 0, there can be unlimited connections.
	private final int maximumConnections;
	//A Thread safe queue for storing connections.
	private ConcurrentLinkedQueue<DbConnection> connections;
	
	
	/**
	 * Can be initialized with a list of any connections that may have been previously initialized.
	 * The maximum number of connections can also be set on initialization.
	 * @param initialConnections - A list of connections
	 * @param maximumConnections - An integer > 0
	 */
	public DbConnectionPool(List<DbConnection> initialConnections, int maximumConnections)
	{	
		this.connections = new ConcurrentLinkedQueue<DbConnection>();
		this.maximumConnections = maximumConnections;
		this.usedConnections = 0;
		
		if( initialConnections != null )
		{
			for(DbConnection con : initialConnections)
			{
				this.connections.add(con);
			}
		}
	}
	
	/**
	 * Initialize connection pool with some previously initialized connections.
	 * @param initialConnections
	 */
	public DbConnectionPool(List<DbConnection> initialConnections)
	{
		this(initialConnections, 100);
	}
	
	/**
	 * Initialize connection pool with a maximum number of connections that can
	 * be in the pool at any given time.
	 * @param maximumConnections
	 */
	public DbConnectionPool(int maximumConnections)
	{
		this(null, maximumConnections);
	}
	
	/**
	 * Initialize connection pool with no connections and the default maximum
	 * number of connections that can be in the pool at once.
	 */
	public DbConnectionPool()
	{
		this(null, 100);
	}
	
	/**
	 * Returns a java.sql.Connection object for getting a connection to the database.
	 * Connection comes from the pool or is initialized and added to the pool if the
	 * pool has not reached its limit.
	 * @return Connection - Configured connection to the database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public synchronized Connection getPooledConnection() throws SQLException, ClassNotFoundException
	{
		DbConnection connection = null;
		boolean found = false;
		
		while(!found)
		{
//			System.out.println("Checking for connection in pool.");
			connection = connections.poll();
			
			if(connection == null && (usedConnections < maximumConnections || maximumConnections == 0) )
			{
//				System.out.println("No connection found and limit has not been reached.");
//				System.out.println("Getting class for name: "+this.getDriver());
				Class.forName(this.getDriver());
				Connection con = DriverManager.getConnection(this.getJdbcUrl());
//				System.out.println("Initializing a new connection for the pool");
				connection = new DbConnection(this, con);
			}
			else {
				if(connection != null)
				{
//					System.out.println("Available connection found in pool.");
				}
				else if(usedConnections >= maximumConnections && maximumConnections != 0)
				{
//					System.out.println("No connections are available and the pool is full.");
				}
			}
			try {
				if(connection != null && !connection.isClosed() )
				{	
//					System.out.println("Connection is not closed. Using this connection.");
					found = true;
				} else {
//					System.out.println("Connection unavailable. waiting to check pool again.");
					found = false;
					this.wait(6);
				}
			} catch (InterruptedException e) {
//				System.out.println("Thread interrupted before a connection could be found.");
				break;
			} catch (SQLException e)
			{
				e.printStackTrace();
				break;
			}
		}
		
		return connection;
	}
	
	/**
	 * Frees a connection and re-adds it to the connection pool for future use.
	 * @param con
	 * @throws SQLException
	 */
	protected synchronized void freeConnection(DbConnection con) throws SQLException {
//		System.out.println("Freeing up connection.");
		if (this.maximumConnections == 0 || connections.size() < this.maximumConnections) {
//			System.out.println("Connection is now set as available.");
			connections.add(con);
		} else {
//			System.out.println("Closing connection and removing from pool to free up space.");
			con.poolDrop();
		}

		--usedConnections;
	}
	
	/**
	 * Closes all connections and removes them from the connection pool.
	 * @throws SQLException
	 */
	public synchronized void closeAll() throws SQLException {
		int connectionCount = connections.size();
//		System.out.println("Removing "+connectionCount+" connections from the pool");
		for(DbConnection c : connections)
		{
//			System.out.println("Removing the "+connectionCount+"th connection.");
			c.poolDrop();
			--connectionCount;
		}
//		System.out.println("To be sure.. The size of the pool is now: "+connections.size());
	}
	
	/**
	 * Generates a jdbc url using the configurations set for the pool (driver class, username, etc..)
	 * @return Jdbc Url for pool configurations
	 * @throws SQLException
	 */
	private String getJdbcUrl() throws SQLException
	{
		if(this.getDriver().equals("com.mysql.jdbc.Driver"))
		{
			return "jdbc:mysql://"+this.getHost()+":"+this.getPort()+"/"+this.getDatabaseName()+"?user="+(new String(this.getUsername()))+
						"&password="+(new String(this.password));
		}
		else
		{
			throw new SQLException("Database Driver must be for MySQL");
		}
	}

	/**
	 * Getter method for configured username.
	 * @return username
	 */
	public char[] getUsername() {
		return username;
	}

	/**
	 * Setter method for connection username.
	 * @param username
	 */
	public void setUsername(char[] username) {
		this.username = username;
	}

	/**
	 * Getter method for configured password.
	 * @return null
	 */
	public char[] getPassword() {
		return null;
	}

	/**
	 * Setter method for connection password.
	 * @param password
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}

	/**
	 * Getter method for configured host.
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Setter method for connection host
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Getter method for configured driver class.
	 * @return
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * Setter method for connection driver class
	 * @param driver
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * Getter method for configured port.
	 * @return
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Setter method for connection port
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Getter method for configured database name (or sid)
	 * @return
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * Setter method for connection database name (or sid)
	 * @param databaseName
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

}
