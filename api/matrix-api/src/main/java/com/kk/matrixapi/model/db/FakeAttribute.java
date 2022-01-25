package com.kk.matrixapi.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class FakeAttribute {

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

	public FakeAttribute(int attributeId, String value, int nftId, int attributeTypeId, double scarcityRatio){
		this.attributeId = attributeId;
		this.value = value;
		this.nftId = nftId;
		this.attributeTypeId = attributeTypeId;
		this.scarcityRatio = scarcityRatio;
	}
	public FakeAttribute(){}

	public static ArrayList<FakeAttribute> getFakeAttributes(){
		ArrayList<FakeAttribute> elems = new ArrayList<FakeAttribute>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM fakeAttribute ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new FakeAttribute ( 
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

	public int insertFakeAttribute(int attributeId, String value, int nftId, int attributeTypeId, double scarcityRatio) {
		int retVar = 0;
		try {
			Connection connectionFakeAttribute = DbPooledDataSource.getConnection();
			String insertFakeAttributestmtStr = "INSERT INTO fakeAttribute (attributeId, value, nftId, attributeTypeId, scarcityRatio) VALUES (?,?,?,?,?)";
			PreparedStatement prepFakeAttributeStat = connectionFakeAttribute.prepareStatement(insertFakeAttributestmtStr);
			prepFakeAttributeStat.setInt(1, attributeId);
			prepFakeAttributeStat.setString(2, value);
			prepFakeAttributeStat.setInt(3, nftId);
			prepFakeAttributeStat.setInt(4, attributeTypeId);
			prepFakeAttributeStat.setDouble(5, scarcityRatio);
			prepFakeAttributeStat.executeUpdate();
			ResultSet generatedKeys = prepFakeAttributeStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepFakeAttributeStat.close();
			connectionFakeAttribute.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean updateFakeAttribute(int attributeId, String value, int nftId, int attributeTypeId, double scarcityRatio) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE fakeAttribute SET attributeId = ? , value = ? , nftId = ? , attributeTypeId = ? , scarcityRatio = ?  WHERE attributeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeId);
			stat.setString(2, value);
			stat.setInt(3, nftId);
			stat.setInt(4, attributeTypeId);
			stat.setDouble(5, scarcityRatio);
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

	public boolean deleteFakeAttribute(int attributeId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM fakeAttribute WHERE attributeId = ?";
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
