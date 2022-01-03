package com.kk.matrixapi.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class Attribute {

	private int attributeId;
	private String value;
	private int nftId;
	private int attributeTypeId;
	private double scarcityRatio;

	public int getAttributeId(){
		return this.attributeId;
	}
	public void setAttributeId(int attributeIdIn){
		this.attributeId = attributeIdIn;
	}

	public String getValue(){
		return this.value;
	}
	public void setValue(String valueIn){
		this.value = valueIn;
	}

	public int getNftId(){
		return this.nftId;
	}
	public void setNftId(int nftIdIn){
		this.nftId = nftIdIn;
	}

	public int getAttributeTypeId(){
		return this.attributeTypeId;
	}
	public void setAttributeTypeId(int attributeTypeIdIn){
		this.attributeTypeId = attributeTypeIdIn;
	}

	public double getScarcityRatio(){
		return this.scarcityRatio;
	}
	public void setScarcityRatio(double scarcityRatioIn){
		this.scarcityRatio = scarcityRatioIn;
	}

	public Attribute(int attributeId, String value, int nftId, int attributeTypeId, double scarcityRatio){
		this.attributeId = attributeId;
		this.value = value;
		this.nftId = nftId;
		this.attributeTypeId = attributeTypeId;
		this.scarcityRatio = scarcityRatio;
	}
	public Attribute(){}

	public static ArrayList<Attribute> getAttributes(){
		ArrayList<Attribute> elems = new ArrayList<Attribute>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM attribute ";
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

	public int insertAttribute(int attributeId, String value, int nftId, int attributeTypeId, double scarcityRatio) {
		int retVar = 0;
		try {
			Connection connectionAttribute = DbPooledDataSource.getConnection();
			String insertAttributestmtStr = "INSERT INTO attribute (attributeId, value, nftId, attributeTypeId, scarcityRatio) VALUES (?,?,?,?,?)";
			PreparedStatement prepAttributeStat = connectionAttribute.prepareStatement(insertAttributestmtStr);
			prepAttributeStat.setInt(1, attributeId);
			prepAttributeStat.setString(2, value);
			prepAttributeStat.setInt(3, nftId);
			prepAttributeStat.setInt(4, attributeTypeId);
			prepAttributeStat.setDouble(5, scarcityRatio);
			prepAttributeStat.executeUpdate();
			ResultSet generatedKeys = prepAttributeStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepAttributeStat.close();
			connectionAttribute.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean updateAttribute(int attributeId, String value, int nftId, int attributeTypeId, double scarcityRatio) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE attribute value = ? , nftId = ? , attributeTypeId = ? , scarcityRatio = ?  WHERE attributeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setString(1, value);
			stat.setInt(2, nftId);
			stat.setInt(3, attributeTypeId);
			stat.setDouble(4, scarcityRatio);
			stat.setInt(5, attributeId);
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

	public boolean deleteAttribute(int attributeId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM attribute WHERE attributeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeId);
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
