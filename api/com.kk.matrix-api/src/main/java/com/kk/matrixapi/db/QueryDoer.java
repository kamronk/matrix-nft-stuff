package com.kk.matrixapi.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.model.db.Attribute;
import com.kk.matrixapi.model.json.RarityComposite;

public class QueryDoer {

	public static boolean updateRarity() {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			
			String stmtStr = "drop table IF EXISTS tempNftRarity; "
					+ "create table tempNftRarity (nftId int, ratioSum double, avgRatio double, minRatio double, attributeName varchar(500)) " + 
					"select a.nftId as nftId, sum(t.ratio) as ratioSum, sum(t.ratio) / count(a.attributeId) as avgRatio " + 
					"from attribute a " + 
					"	left join scarcity t on t.attributeTypeId = a.attributeTypeId and a.value = t.attributeValue " + 
					"group by a.nftId " + 
					"order by avgRatio;" + 
					"UPDATE tempNftRarity r" + 
					"SET r.minRatio = (select a.scarcityRatio from attribute a where a.nftId = r.nftId order by a.scarcityRatio limit 1); " +
					"UPDATE tempNftRarity r " + 
					"SET r.attributeName = (select a.value from attribute a where a.nftId = r.nftId order by a.scarcityRatio limit 1); ";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.executeUpdate();
			stat.close();
			
			connection.close();
			retVar = true;
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public static boolean updateScarcity(int numberOfAvatars) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "truncate scarcity; "
					+ "insert into scarcity (attributeTypeId, attributeValue, attributeTypeName, count, ratio) " + 
					"select attributeTypeId, value as attributeValue, name as attributeTypeName, numOfThis as count, numOfThis / ? as ratio from attributeCounts ";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, numberOfAvatars);
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

	public static boolean updateAttributeCountsView() {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "drop view IF EXISTS attributeCounts; "
					+ "CREATE VIEW attributeCounts AS " + 
					"select a.value, at.attributeTypeId, at.name, count(a.attributeId) as numOfThis " + 
					"from attribute a inner join attributeType at on at.attributeTypeId = a.attributeTypeId " + 
					"group by a.value, at.attributeTypeId, a.attributeTypeId " + 
					"order by numOfThis asc";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
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

	public static boolean updateNftTokenIdInt() {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE nft SET tokenIdInt = CONVERT(tokenId, SIGNED INTEGER)";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
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

	public static boolean updateAttributeScarcity(int attributeId, double scarcityRatio) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE attribute SET scarcityRatio = ?  WHERE attributeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setDouble(1, scarcityRatio);
			stat.setInt(2, attributeId);
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

	public static ArrayList<RarityComposite> getRanks(){
		ArrayList<RarityComposite> elems = new ArrayList<RarityComposite>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " select nft.tokenIdInt, avgRatio, minRatio, attributeName from tempNftRarity t "
					+ " join nft on nft.nftId = t.nftId "
					+ " order by avgRatio ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			int rank = 1;
			while (rs.next()) {
				elems.add(new RarityComposite ( 
					rs.getInt(1)
					, rank
					, rs.getDouble(2)
					, rs.getDouble(3)
					, rs.getString(4)
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

	public static ArrayList<Attribute> getAttributes(){
		ArrayList<Attribute> elems = new ArrayList<Attribute>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " select a.nftId as nftId, sum(t.ratio) as ratioSum, sum(t.ratio) / count(a.attributeId) as avgRatio, a.scarcityRatio, a.value\n" + 
					" from attribute a\n" + 
					"	left join tempScarcity t on t.attributeTypeId = a.attributeTypeId and a.value = t.attributeValue\n" + 
					" group by a.nftId\n" + 
					" order by avgRatio, a.scarcityRatio ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new Attribute ( 
					rs.getInt(1)
					, rs.getString(2)
					, rs.getInt(3)
					, rs.getInt(4)
					, rs.getDouble(5)

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

	public static int getProvenanct(String txHash, int nftId){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT provenanceId FROM provenance where txHash = ? AND nftId = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, txHash);
			prepStat.setInt(2, nftId);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return id;
	}

	public static int getSale(String txId, int nftId){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT saleId FROM sale where txId = ? AND nftId = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, txId);
			prepStat.setInt(2, nftId);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return id;
	}

	public static int getAttributeId(String value, int nftId, int attributeTypeId){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT attributeId FROM attribute where value = ? AND nftId = ? AND attributeTypeId = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, value);
			prepStat.setInt(2, nftId);
			prepStat.setInt(3, attributeTypeId);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return id;
	}

	public static int getAttributeTypeId(String typeName){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT attributeTypeId FROM attributeType where name = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, typeName);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return id;
	}

	public static int getNumberOfAvatars(){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT count(nftId) FROM nft";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return id;
	}

	public static int getNftId(String tokenId){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT nftId FROM nft where tokenId = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, tokenId);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return id;
	}

	public static int insertProvenance(String txHash, String from, String to, int blockTime, String quantity, int nftId) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO provenance (txHash, nftId, fromAddress, toAddress, blockTime, quantity) VALUES (?,?,?,?,?,?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, txHash);
			prepStmt.setInt(2, nftId);
			prepStmt.setString(3, from);
			prepStmt.setString(4, to);
			prepStmt.setInt(5, blockTime);
			prepStmt.setString(6, quantity);
			prepStmt.executeUpdate();
			ResultSet generatedKeys = prepStmt.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepStmt.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public static int insertSale(String txId, int nftId, double amount) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO sale (txId, nftId, amount) VALUES (?,?,?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, txId);
			prepStmt.setInt(2, nftId);
			prepStmt.setDouble(3, amount);
			prepStmt.executeUpdate();
			ResultSet generatedKeys = prepStmt.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepStmt.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public static int insertAttribute(String value, int nftId, int attributeTypeId) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO attribute (value, nftId, attributeTypeId) VALUES (?,?,?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, value);
			prepStmt.setInt(2, nftId);
			prepStmt.setInt(3, attributeTypeId);
			prepStmt.executeUpdate();
			ResultSet generatedKeys = prepStmt.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepStmt.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public static int insertFakeId(int id) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO fakeIds (fakeId) VALUES (?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setInt(1, id);
			prepStmt.executeUpdate();
			ResultSet generatedKeys = prepStmt.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepStmt.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public static int insertAttributeType(String name) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO attributeType (name) VALUES (?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, name);
			prepStmt.executeUpdate();
			ResultSet generatedKeys = prepStmt.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepStmt.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public static int insertNft(String tokenId, int likes, String contractAddress) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO nft (tokenId, likes, contractAddress) VALUES (?,?,?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, tokenId);
			prepStmt.setInt(2, likes);
			prepStmt.setString(3, contractAddress);
			prepStmt.executeUpdate();
			ResultSet generatedKeys = prepStmt.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepStmt.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

//	public boolean updateCampaigns(int campaignId, String name, String ownedByUserId, String active, Date createdDate, Date updatedDate, ServletContext context) {
//		boolean retVar = false;
//		try {
//			Connection connection = DbPooledDataSource.getConnection(context);
//			String stmtStr = "UPDATE campaigns SET campaign_id = ? , name = ? , owned_by_user_id = ? , active = ? , created_date = ? , updated_date = ?  WHERE campaign_id = ?";
//			PreparedStatement stat = connection.prepareStatement(stmtStr);
//			stat.setInt(1, campaignId);
//			stat.setString(2, name);
//			stat.setString(3, ownedByUserId);
//			stat.setString(4, active);
//			stat.setDate(5, new java.sql.Date(createdDate.getTime()));
//			stat.setDate(6, new java.sql.Date(updatedDate.getTime()));
//			stat.executeUpdate();
//			retVar = true;
//			stat.close();
//			connection.close();
//		} catch (NumberFormatException nfe) {
//			nfe.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return retVar;
//	}
//
//	public boolean deleteCampaigns(int campaignId, ServletContext context) {
//		boolean retVar = false;
//		try {
//			Connection connection = DbPooledDataSource.getConnection(context);
//			String stmtStr = "DELETE FROM campaigns WHERE campaign_id = ?";
//			PreparedStatement stat = connection.prepareStatement(stmtStr);
//			stat.setInt(1, campaignId);
//			stat.executeUpdate();
//			retVar = true;
//			stat.close();
//			connection.close();
//		} catch (NumberFormatException nfe) {
//			nfe.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return retVar;
//	}
}
