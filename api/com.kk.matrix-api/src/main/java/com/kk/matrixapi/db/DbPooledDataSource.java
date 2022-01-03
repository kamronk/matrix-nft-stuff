package com.kk.matrixapi.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Singleton datasource object for getting connections to the database. Object is alive
 * for the entirety of the application and can be accessed from any class that needs
 * a database connection.
 * @author Deximal Development
 *
 */

public class DbPooledDataSource {
	
	public static float getStat(String query) {
		float retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				retVar = rs.getFloat(1);
		}
		rs.close();
		stmt.close();
		connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return retVar;
	}

	//The connection pool that will maintain connection pool logic for the whole application
	private static DbConnectionPool pool;
	//Boolean for whether the singleton has been initialized yet
	private static boolean isInitialized = false;
	
	//Thread safe static constructor
	static {		
		pool = new DbConnectionPool();
		pool.setDriver("com.mysql.jdbc.Driver");

		pool.setDatabaseName("matrix_nfts");
		pool.setPort("3306");
		pool.setHost("localhost");
		pool.setUsername("app_usr".toCharArray());
		pool.setPassword("matrix_nfts_pw".toCharArray());

		isInitialized = true;
	}
	
	/**
	 * Returns a connection to the database that has been configured in application.properties
	 * @return Connection - connection to the configured database
	 */
	public static Connection getConnection()
	{
		if( !isInitialized )
		{
			init();
		}
		Connection con = null;
		try {
			
			con =  pool.getPooledConnection();
			
		} catch (ClassNotFoundException e) {
			System.out.println("No driver can be found for class " + pool.getDriver());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Failed to initialize connection.");
			e.printStackTrace();
		}
		
		if(con == null)
		{
			System.out.println("Connection could not be initialized. Check database configurations.");
		}
		
		return con;
	}
	
	/**
	 * Initializes a connection pool for the datasource with configured application properties.
	 */
	private static void init()
	{
		//TODO: Get configurations from application.properties rather than hard coding them in the code.
		pool = new DbConnectionPool();
		pool.setDriver("com.mysql.jdbc.Driver");
		pool.setDatabaseName("matrix_nfts");
		pool.setPort("3306");
		pool.setHost("localhost");
		pool.setUsername("app_usr".toCharArray());
		pool.setPassword("matrix_nfts_pw".toCharArray());
		
		
		isInitialized = true;
	}
	
}
