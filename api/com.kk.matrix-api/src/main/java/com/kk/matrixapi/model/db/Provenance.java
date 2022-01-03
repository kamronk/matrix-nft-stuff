package com.kk.matrixapi.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class Provenance {

	private int provenanceId;
	private int nftId;
	private String fromAddress;
	private String toAddress;
	private String quantity;
	private int blockTime;
	private String txHash;

	public int getProvenanceId(){
		return this.provenanceId;
	}
	public void setProvenanceId(int provenanceIdIn){
		this.provenanceId = provenanceIdIn;
	}

	public int getNftId(){
		return this.nftId;
	}
	public void setNftId(int nftIdIn){
		this.nftId = nftIdIn;
	}

	public String getFromAddress(){
		return this.fromAddress;
	}
	public void setFromAddress(String fromAddressIn){
		this.fromAddress = fromAddressIn;
	}

	public String getToAddress(){
		return this.toAddress;
	}
	public void setToAddress(String toAddressIn){
		this.toAddress = toAddressIn;
	}

	public String getQuantity(){
		return this.quantity;
	}
	public void setQuantity(String quantityIn){
		this.quantity = quantityIn;
	}

	public int getBlockTime(){
		return this.blockTime;
	}
	public void setBlockTime(int blockTimeIn){
		this.blockTime = blockTimeIn;
	}

	public String getTxHash(){
		return this.txHash;
	}
	public void setTxHash(String txHashIn){
		this.txHash = txHashIn;
	}

	public Provenance(int provenanceId, int nftId, String fromAddress, String toAddress, String quantity, int blockTime, String txHash){
		this.provenanceId = provenanceId;
		this.nftId = nftId;
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.quantity = quantity;
		this.blockTime = blockTime;
		this.txHash = txHash;
	}
	public Provenance(){}

	public static ArrayList<Provenance> getProvenances(){
		ArrayList<Provenance> elems = new ArrayList<Provenance>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM provenance ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new Provenance ( 
					rs.getInt(1)
					, rs.getInt(2)
					, rs.getString(3)
					, rs.getString(4)
					, rs.getString(5)
					, rs.getInt(6)
					, rs.getString(7)

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

	public int insertProvenance(int provenanceId, int nftId, String fromAddress, String toAddress, String quantity, int blockTime, String txHash) {
		int retVar = 0;
		try {
			Connection connectionProvenance = DbPooledDataSource.getConnection();
			String insertProvenancestmtStr = "INSERT INTO provenance (provenanceId, nftId, fromAddress, toAddress, quantity, blockTime, txHash) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement prepProvenanceStat = connectionProvenance.prepareStatement(insertProvenancestmtStr);
			prepProvenanceStat.setInt(1, provenanceId);
			prepProvenanceStat.setInt(2, nftId);
			prepProvenanceStat.setString(3, fromAddress);
			prepProvenanceStat.setString(4, toAddress);
			prepProvenanceStat.setString(5, quantity);
			prepProvenanceStat.setInt(6, blockTime);
			prepProvenanceStat.setString(7, txHash);
			prepProvenanceStat.executeUpdate();
			ResultSet generatedKeys = prepProvenanceStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepProvenanceStat.close();
			connectionProvenance.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean updateProvenance(int provenanceId, int nftId, String fromAddress, String toAddress, String quantity, int blockTime, String txHash) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE provenance SET provenanceId = ? , nftId = ? , fromAddress = ? , toAddress = ? , quantity = ? , blockTime = ? , txHash = ?  WHERE provenanceId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, provenanceId);
			stat.setInt(2, nftId);
			stat.setString(3, fromAddress);
			stat.setString(4, toAddress);
			stat.setString(5, quantity);
			stat.setInt(6, blockTime);
			stat.setString(7, txHash);
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

	public boolean deleteProvenance(int provenanceId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM provenance WHERE provenanceId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, provenanceId);
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
