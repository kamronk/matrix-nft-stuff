package com.kk.matrixapi.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class Sale {

	private int saleId;
	private String txId;
	private int nftId;
	private double amount;

	public int getSaleId(){
		return this.saleId;
	}
	public void setSaleId(int saleIdIn){
		this.saleId = saleIdIn;
	}

	public String getTxId(){
		return this.txId;
	}
	public void setTxId(String txIdIn){
		this.txId = txIdIn;
	}

	public int getNftId(){
		return this.nftId;
	}
	public void setNftId(int nftIdIn){
		this.nftId = nftIdIn;
	}

	public double getAmount(){
		return this.amount;
	}
	public void setAmount(double amountIn){
		this.amount = amountIn;
	}

	public Sale(int saleId, String txId, int nftId, double amount){
		this.saleId = saleId;
		this.txId = txId;
		this.nftId = nftId;
		this.amount = amount;
	}
	public Sale(){}

	public static ArrayList<Sale> getSales(){
		ArrayList<Sale> elems = new ArrayList<Sale>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM sale ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new Sale ( 
					rs.getInt(1)
					, rs.getString(2)
					, rs.getInt(3)
					, rs.getDouble(4)

				));
		}
		rs.close();
		stmt.close();
		connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return elems;
	}

	public int insertSale(int saleId, String txId, int nftId, double amount) {
		int retVar = 0;
		try {
			Connection connectionSale = DbPooledDataSource.getConnection();
			String insertSalestmtStr = "INSERT INTO sale (saleId, txId, nftId, amount) VALUES (?,?,?,?)";
			PreparedStatement prepSaleStat = connectionSale.prepareStatement(insertSalestmtStr);
			prepSaleStat.setInt(1, saleId);
			prepSaleStat.setString(2, txId);
			prepSaleStat.setInt(3, nftId);
			prepSaleStat.setDouble(4, amount);
			prepSaleStat.executeUpdate();
			ResultSet generatedKeys = prepSaleStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepSaleStat.close();
			connectionSale.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean updateSale(int saleId, String txId, int nftId, double amount) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE sale SET saleId = ? , txId = ? , nftId = ? , amount = ?  WHERE saleId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, saleId);
			stat.setString(2, txId);
			stat.setInt(3, nftId);
			stat.setDouble(4, amount);
			stat.executeUpdate();
			retVar = true;
			stat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean deleteSale(int saleId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM sale WHERE saleId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, saleId);
			stat.executeUpdate();
			retVar = true;
			stat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

}
