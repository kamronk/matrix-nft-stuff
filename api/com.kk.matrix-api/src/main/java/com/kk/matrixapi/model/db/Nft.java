package com.kk.matrixapi.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class Nft {

	private int nftId;
	private String tokenId;
	private int likes;
	private String contractAddress;
	private int tokenIdInt;

	public int getNftId(){
		return this.nftId;
	}
	public void setNftId(int nftIdIn){
		this.nftId = nftIdIn;
	}

	public String getTokenId(){
		return this.tokenId;
	}
	public void setTokenId(String tokenIdIn){
		this.tokenId = tokenIdIn;
	}

	public int getLikes(){
		return this.likes;
	}
	public void setLikes(int likesIn){
		this.likes = likesIn;
	}

	public String getContractAddress(){
		return this.contractAddress;
	}
	public void setContractAddress(String contractAddressIn){
		this.contractAddress = contractAddressIn;
	}

	public int getTokenIdInt(){
		return this.tokenIdInt;
	}
	public void setTokenIdInt(int tokenIdIntIn){
		this.tokenIdInt = tokenIdIntIn;
	}

	public Nft(int nftId, String tokenId, int likes, String contractAddress, int tokenIdInt){
		this.nftId = nftId;
		this.tokenId = tokenId;
		this.likes = likes;
		this.contractAddress = contractAddress;
		this.tokenIdInt = tokenIdInt;
	}
	public Nft(){}

	public static ArrayList<Nft> getNfts(){
		ArrayList<Nft> elems = new ArrayList<Nft>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM nft ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new Nft ( 
					rs.getInt(1)
					, rs.getString(2)
					, rs.getInt(3)
					, rs.getString(4)
					, rs.getInt(5)

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

	public int insertNft(int nftId, String tokenId, int likes, String contractAddress, int tokenIdInt) {
		int retVar = 0;
		try {
			Connection connectionNft = DbPooledDataSource.getConnection();
			String insertNftstmtStr = "INSERT INTO nft (nftId, tokenId, likes, contractAddress, tokenIdInt) VALUES (?,?,?,?,?)";
			PreparedStatement prepNftStat = connectionNft.prepareStatement(insertNftstmtStr);
			prepNftStat.setInt(1, nftId);
			prepNftStat.setString(2, tokenId);
			prepNftStat.setInt(3, likes);
			prepNftStat.setString(4, contractAddress);
			prepNftStat.setInt(5, tokenIdInt);
			prepNftStat.executeUpdate();
			ResultSet generatedKeys = prepNftStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepNftStat.close();
			connectionNft.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean updateNft(int nftId, String tokenId, int likes, String contractAddress, int tokenIdInt) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE nft SET nftId = ? , tokenId = ? , likes = ? , contractAddress = ? , tokenIdInt = ?  WHERE nftId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, nftId);
			stat.setString(2, tokenId);
			stat.setInt(3, likes);
			stat.setString(4, contractAddress);
			stat.setInt(5, tokenIdInt);
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

	public boolean deleteNft(int nftId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM nft WHERE nftId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, nftId);
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
