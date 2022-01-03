package com.kk.matrixapi.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class Scarcity {

	private int scarcityId;
	private int attributeTypeId;
	private String attributeValue;
	private String attributeTypeName;
	private int count;
	private double ratio;

	public int getScarcityId(){
		return this.scarcityId;
	}
	public void setScarcityId(int scarcityIdIn){
		this.scarcityId = scarcityIdIn;
	}

	public int getAttributeTypeId(){
		return this.attributeTypeId;
	}
	public void setAttributeTypeId(int attributeTypeIdIn){
		this.attributeTypeId = attributeTypeIdIn;
	}

	public String getAttributeValue(){
		return this.attributeValue;
	}
	public void setAttributeValue(String attributeValueIn){
		this.attributeValue = attributeValueIn;
	}

	public String getAttributeTypeName(){
		return this.attributeTypeName;
	}
	public void setAttributeTypeName(String attributeTypeNameIn){
		this.attributeTypeName = attributeTypeNameIn;
	}

	public int getCount(){
		return this.count;
	}
	public void setCount(int countIn){
		this.count = countIn;
	}

	public double getRatio(){
		return this.ratio;
	}
	public void setRatio(double ratioIn){
		this.ratio = ratioIn;
	}

	public Scarcity(int scarcityId, int attributeTypeId, String attributeValue, String attributeTypeName, int count, double ratio){
		this.scarcityId = scarcityId;
		this.attributeTypeId = attributeTypeId;
		this.attributeValue = attributeValue;
		this.attributeTypeName = attributeTypeName;
		this.count = count;
		this.ratio = ratio;
	}
	public Scarcity(){}

	public static ArrayList<Scarcity> getScarcitys(){
		ArrayList<Scarcity> elems = new ArrayList<Scarcity>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM scarcity ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new Scarcity ( 
					rs.getInt(1)
					, rs.getInt(2)
					, rs.getString(3)
					, rs.getString(4)
					, rs.getInt(5)
					, rs.getDouble(6)

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

	public int insertScarcity(int scarcityId, int attributeTypeId, String attributeValue, String attributeTypeName, int count, double ratio) {
		int retVar = 0;
		try {
			Connection connectionScarcity = DbPooledDataSource.getConnection();
			String insertScarcitystmtStr = "INSERT INTO scarcity (scarcityId, attributeTypeId, attributeValue, attributeTypeName, count, ratio) VALUES (?,?,?,?,?,?)";
			PreparedStatement prepScarcityStat = connectionScarcity.prepareStatement(insertScarcitystmtStr);
			prepScarcityStat.setInt(1, scarcityId);
			prepScarcityStat.setInt(2, attributeTypeId);
			prepScarcityStat.setString(3, attributeValue);
			prepScarcityStat.setString(4, attributeTypeName);
			prepScarcityStat.setInt(5, count);
			prepScarcityStat.setDouble(6, ratio);
			prepScarcityStat.executeUpdate();
			ResultSet generatedKeys = prepScarcityStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepScarcityStat.close();
			connectionScarcity.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean updateScarcity(int scarcityId, int attributeTypeId, String attributeValue, String attributeTypeName, int count, double ratio) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE scarcity SET scarcityId = ? , attributeTypeId = ? , attributeValue = ? , attributeTypeName = ? , count = ? , ratio = ?  WHERE scarcityId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, scarcityId);
			stat.setInt(2, attributeTypeId);
			stat.setString(3, attributeValue);
			stat.setString(4, attributeTypeName);
			stat.setInt(5, count);
			stat.setDouble(6, ratio);
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

	public boolean deleteScarcity(int scarcityId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM scarcity WHERE scarcityId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, scarcityId);
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
