package com.kk.matrixapi.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kk.matrixapi.model.Stats;
import com.kk.matrixapi.model.db.AttributeType;
import com.kk.matrixapi.model.db.AttributeValue;
import com.kk.matrixapi.model.json.RarityComposite;
import com.kk.matrixapi.util.Util;

public class QueryDoer {

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

	public static ArrayList<Integer> getMissingBases(){
		ArrayList<Integer> elems = new ArrayList<Integer>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " select fakeId from fakeIds where fakeId not in (select distinct(tokenIdInt) from nft) ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(rs.getInt(1));
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

	public static ArrayList<Integer> getStatAttributeTypeIds(String contractAddress){
		ArrayList<Integer> elems = new ArrayList<Integer>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " select attributeTypeId from attributeType where name in ( \r\n" + 
					"'Charisma','Constitution','Dexterity','Intelligence','Strength','Wisdom' \r\n" + 
					") and contractAddress = ? ";
			PreparedStatement stat = connection.prepareStatement(selectQry);
			stat.setString(1, contractAddress);
			stat.executeUpdate();
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				elems.add(rs.getInt(1));
			}
			rs.close();
			stat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return elems;
	}

	public static ArrayList<Stats> getBestStatsAvatars(List<Integer> attrTypeIds, int minimumTotal){
		ArrayList<Stats> elems = new ArrayList<Stats>();
		String list = "";
		boolean first = true;
		for(int id : attrTypeIds) {
			if (first) {
				first = false;
				list += "" + id;
			} else 
				list += "," + id;
		}
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " select n.tokenIdInt, sum(av.value) as totalStats from attribute a\r\n" + 
					" join attributeValue av on a.attributeValueId = av.attributeValueId\r\n" + 
					" join nft n on n.nftId = a.nftId\r\n" + 
					" where av.attributeTypeId in (" + list + ")\r\n" + 
					" group by n.tokenIdInt having sum(av.value) > ?\r\n" + 
					" order by totalStats desc ";
			PreparedStatement stat = connection.prepareStatement(selectQry);
			stat.setInt(1, minimumTotal);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				elems.add(new Stats(rs.getInt(1), rs.getInt(1)));
			}
			rs.close();
			stat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return elems;
	}

	public static ArrayList<Integer> getUnpilledTokenIds(){
		ArrayList<Integer> elems = new ArrayList<Integer>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " select tokenIdInt from nft group by tokenIdInt having count(nftId) = 1 ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(rs.getInt(1));
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

	public static ArrayList<Integer> getExistingIdsForContract(String contractAddress){
		ArrayList<Integer> elems = new ArrayList<Integer>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = "select distinct(tokenIdInt) from nft where contractAddress = ? ";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, contractAddress);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				elems.add(rs.getInt(1));
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return elems;
	}

	public static ArrayList<Integer> getAttributeValueIdsForContract(String contractAddress){
		ArrayList<Integer> elems = new ArrayList<Integer>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = "select attributeTypeId from attributeType where contractAddress = ? ";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, contractAddress);
			ResultSet rs = prepStat.executeQuery();
			while (rs.next()) {
				elems.add(rs.getInt(1));
			}
			rs.close();
			prepStat.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return elems;
	}

	public static ArrayList<RarityComposite> getRanks(){
		ArrayList<RarityComposite> elems = new ArrayList<RarityComposite>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " select nft.tokenIdInt, avgRatio, minRatio, attributeName, nft.image from tempNftRarity t "
					+ " join nft on nft.nftId = t.nftId "
					+ " order by avgRatio ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			int rank = 1;
			while (rs.next()) {
//				elems.add(new RarityComposite ( 
//					rs.getInt(1)
//					, rank
//					, rs.getDouble(2)
//					, rs.getDouble(3)
//					, rs.getString(4)
//					, rs.getString(5)
//				));
				rank++;
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

//	public static ArrayList<Attribute> getAttributes(){
//		ArrayList<Attribute> elems = new ArrayList<Attribute>();
//		try {
//			Connection connection = DbPooledDataSource.getConnection();
//			String selectQry = " select a.nftId as nftId, sum(t.ratio) as ratioSum, sum(t.ratio) / count(a.attributeId) as avgRatio, a.scarcityRatio, v.value\n" + 
//					" from attribute a\n" + 
//					"	left join scarcity t on t.attributeTypeId = a.attributeTypeId and a.value = t.attributeValue\n" + 
//					"	left join attributeValue v on a.attributeValueId = v.attributeValueId and a.attributeTypeId = t.attributeTypeId\n" + 
//					" group by a.nftId\n" + 
//					" order by avgRatio, a.scarcityRatio ";
//			Statement stmt = connection.createStatement();
//			ResultSet rs = stmt.executeQuery(selectQry);
//			while (rs.next()) {
//				elems.add(new AttributeComposite ( 
//					rs.getInt(1)
//					, rs.getString(2)
//					, rs.getInt(3)
//					, rs.getInt(4)
//					, rs.getDouble(5)
//
//				));
//		}
//		rs.close();
//		stmt.close();
//		connection.close();
//		} catch (NumberFormatException nfe) {
//			nfe.printStackTrace();
//		} catch (SQLException se) {
//			se.printStackTrace();
//		}
//		return elems;
//	}

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

	public static int getAttributeId(int attrValueId, int nftId, int attributeTypeId){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT attributeId FROM attribute where attributeValueId = ? AND nftId = ? AND attributeTypeId = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setInt(1, attrValueId);
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
			System.out.println(">" + attrValueId + "<");
			System.out.println(">" + nftId + "<");
			System.out.println(">" + attributeTypeId + "<");
			se.printStackTrace();
		}
		return id;
	}

	private static List<AttributeType> cachedValues_AttributeType;
	public static int getAttributeTypeId(String typeName, String contractAddress){
		if (cachedValues_AttributeType == null) {
			cachedValues_AttributeType = new ArrayList<AttributeType>();
		}
		if (cachedValues_AttributeType.stream().anyMatch(f -> f.getName().equals(typeName) && f.getContractAddress().equals(contractAddress))){
			AttributeType obj = cachedValues_AttributeType.stream().filter(f -> f.getName().equals(typeName) && f.getContractAddress().equals(contractAddress)).findFirst().get();
			return obj.getAttributeTypeId();
		}
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT attributeTypeId FROM attributeType where name = ? AND contractAddress = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, typeName);
			prepStat.setString(2, contractAddress);
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
		if (id > 0)
			cachedValues_AttributeType.add(new AttributeType(id, typeName, contractAddress));
		return id;
	}

	private static List<AttributeValue> cachedValues_AttributeValue;
	public static int getAttributeValueId(int attributeTypeId, String value){
		if (cachedValues_AttributeValue == null) {
			cachedValues_AttributeValue = new ArrayList<AttributeValue>();
		}
		if (cachedValues_AttributeValue.stream().anyMatch(f -> f.getAttributeTypeId() == attributeTypeId && f.getValue().equals(value))){
			return cachedValues_AttributeValue.stream().filter(f -> f.getAttributeTypeId() == attributeTypeId && f.getValue().equals(value)).findFirst().get().getAttributeValueId();
		}
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT attributeValueId FROM attributeValue where attributeTypeId = ? AND value = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setInt(1, attributeTypeId);
			prepStat.setString(2, value);
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
		if (id > 0)
			cachedValues_AttributeValue.add(new AttributeValue(id, attributeTypeId, value, 0));
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

	public static int getNftId(String tokenId, String contractAddress){
		int id = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT nftId FROM nft where tokenId = ? AND contractAddress = ?";
			PreparedStatement prepStat = connection.prepareStatement(selectQry);
			prepStat.setString(1, tokenId);
			prepStat.setString(2, contractAddress);
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

	public static int insertAttributeValue(int attributeTypeId, String value) {
		int retVar = 0;
		try {
			Connection connectionAttributeValue = DbPooledDataSource.getConnection();
			String insertAttributeValuestmtStr = "INSERT INTO attributeValue (attributeTypeId, value) VALUES (?,?)";
			PreparedStatement prepAttributeValueStat = connectionAttributeValue.prepareStatement(insertAttributeValuestmtStr, Statement.RETURN_GENERATED_KEYS);
			prepAttributeValueStat.setInt(1, attributeTypeId);
			prepAttributeValueStat.setString(2, value);
			prepAttributeValueStat.executeUpdate();
			ResultSet generatedKeys = prepAttributeValueStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepAttributeValueStat.close();
			connectionAttributeValue.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
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

	public static int insertAttribute(int attributeValueId, int nftId, int attributeTypeId) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO attribute (attributeValueId, nftId, attributeTypeId) VALUES (?,?,?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setInt(1, attributeValueId);
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

	public static int insertAttributeType(String name, String contractAddress) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO attributeType (name, contractAddress) VALUES (?, ?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, name);
			prepStmt.setString(2, contractAddress);
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

	public static int insertNft(String tokenId, int likes, String contractAddress, String image) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO nft (tokenId, likes, contractAddress, image) VALUES (?,?,?,?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, tokenId);
			prepStmt.setInt(2, likes);
			prepStmt.setString(3, contractAddress);
			prepStmt.setString(4, image);
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

	public static int updateNft(int nftId, int likes, String image) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "update nft set likes = ?, image = ? where nftId = ?";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setInt(1, likes);
			prepStmt.setString(2, image);
			prepStmt.setInt(3, nftId);
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

	public static int updateNftRedImage(int nftId, String image) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "update nft set redImage = ? where nftId = ?";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, image);
			prepStmt.setInt(2, nftId);
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

	public static int updateNftBlueImage(int nftId, String image) {
		int retVar = 0;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "update nft set blueImage = ? where nftId = ?";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, image);
			prepStmt.setInt(2, nftId);
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

	public static boolean updateRarity() {
		return executeStmts("drop table IF EXISTS tempNftRarity; "
				+ "create table tempNftRarity (nftId int, ratioSum double, avgRatio double, minRatio double, attributeName varchar(500)) " + 
						"select a.nftId as nftId, sum(t.ratio) as ratioSum, sum(t.ratio) / count(a.attributeId) as avgRatio " + 
						"from attribute a " + 
							"left join scarcity t on t.attributeTypeId = a.attributeTypeId and a.value = t.attributeValue " + 
						"group by a.nftId " + 
						"order by avgRatio;" + 
				"UPDATE tempNftRarity r " + 
					"SET r.minRatio = (select a.scarcityRatio from attribute a where a.nftId = r.nftId order by a.scarcityRatio limit 1); " +
				"UPDATE tempNftRarity r " + 
					"SET r.attributeName = (select a.value from attribute a where a.nftId = r.nftId order by a.scarcityRatio limit 1); ");
	}

	public static boolean updateAttributeValueScarcityDropTemps() {
		return executeStmts(
				" UPDATE attributeValue av\r\n" + 
				" INNER JOIN tempScarcityBase t ON t.attributeValueId = av.attributeValueId\r\n" + 
				" SET av.scarcityRatio = t.scarcityRatio;" +

				" UPDATE attributeValue av\r\n" + 
				" INNER JOIN tempScarcityBlue t ON t.attributeValueId = av.attributeValueId\r\n" + 
				" SET av.scarcityRatio = t.scarcityRatio;" +

				" UPDATE attributeValue av\r\n" + 
				" INNER JOIN tempScarcityRed t ON t.attributeValueId = av.attributeValueId\r\n" + 
				" SET av.scarcityRatio = t.scarcityRatio;" +

				" drop table tempScarcityRed;" +

				" drop table tempScarcityBlue;" +

				" drop table tempScarcityBase;"
			);
	}

	public static boolean dropRebuildTempScarcity(List<Integer> attrValueIds, String contractAddress) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String tableName = "tempScarcityBase";
			switch(contractAddress) {
				case Util.RED_CONTRACT:
					tableName = "tempScarcityRed";
					break;
				case Util.BLUE_CONTRACT:
					tableName = "tempScarcityBlue";
					break;
			}
			
			PreparedStatement stat1 = connection.prepareStatement("drop table " + tableName);
			stat1.executeUpdate();
			stat1.close();
			
			PreparedStatement stat = connection.prepareStatement("create table " + tableName + " as\r\n" + 
					" select \r\n" + 
					"	count(a.attributeId) as attCount, \r\n" + 
					"    count(a.attributeId) / (\r\n" + 
					"				select count(distinct(tokenIdInt)) from nft \r\n" + 
					"                where contractAddress = ?\r\n" + 
					"                ) as scarcityRatio,\r\n" + 
					"    a.attributeValueId\r\n" + 
					" from attribute a \r\n" + 
					" where a.attributeTypeId in (" + String.join(",", (CharSequence[]) attrValueIds.toArray()) + ")\r\n" + 
					" group by a.attributeValueId\r\n" + 
					" order by attCount desc ");
			stat.setString(1, contractAddress);
			stat.executeUpdate();
			stat.close();
			
			retVar = true;
			connection.close();
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
			
			PreparedStatement stat1 = connection.prepareStatement("truncate scarcity;");
			stat1.executeUpdate();
			stat1.close();
			
			PreparedStatement stat = connection.prepareStatement("insert into scarcity (attributeTypeId, attributeValue, attributeTypeName, count, ratio) " + 
					"select attributeTypeId, value as attributeValue, name as attributeTypeName, numOfThis as count, numOfThis / ? as ratio from attributeCounts ");
			stat.setInt(1, numberOfAvatars);
			stat.executeUpdate();
			stat.close();
			
			retVar = true;
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public static boolean updateAttributeCountsView() {
		return executeStmts("drop table IF EXISTS attributeCounts; "
				+ " CREATE table attributeCounts AS " + 
						"select a.value, at.attributeTypeId, at.name, count(a.attributeId) as numOfThis " + 
						"from attribute a inner join attributeType at on at.attributeTypeId = a.attributeTypeId " + 
						"group by a.value, at.attributeTypeId, a.attributeTypeId " + 
						"order by numOfThis asc");
	}
	
	public static boolean updateNftTokenIdInt() {
		return executeStmts("UPDATE nft SET tokenIdInt = CONVERT(tokenId, SIGNED INTEGER)");
	}

	public static boolean executeStmts(String sql) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			for(String stmt : sql.split(";")) {
				if (stmt.trim().length() > 1) {
					PreparedStatement stat = connection.prepareStatement(stmt);
					stat.executeUpdate();
					stat.close();
				}
			}
			retVar = true;
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

}
