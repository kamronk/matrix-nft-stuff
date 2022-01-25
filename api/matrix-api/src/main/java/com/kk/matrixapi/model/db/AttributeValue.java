package com.kk.matrixapi.model.db;

import com.kk.matrixapi.db.DbPooledDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.Date;

public class AttributeValue {

	private int attributeValueId;
	private int attributeTypeId;
	private String value;
	private double scarcityRatio;

	public int getAttributeValueId(){
		return this.attributeValueId;
	}
	public void setAttributeValueId(int attributeValueIdIn){
		this.attributeValueId = attributeValueIdIn;
	}

	public int getAttributeTypeId(){
		return this.attributeTypeId;
	}
	public void setAttributeTypeId(int attributeTypeIdIn){
		this.attributeTypeId = attributeTypeIdIn;
	}

	public String getValue(){
		return this.value;
	}
	public void setValue(String valueIn){
		this.value = valueIn;
	}

	public double getScarcityRatio(){
		return this.scarcityRatio;
	}
	public void setScarcityRatio(double scarcityRatioIn){
		this.scarcityRatio = scarcityRatioIn;
	}

	public AttributeValue(int attributeValueId, int attributeTypeId, String value, double scarcityRatio){
		this.attributeValueId = attributeValueId;
		this.attributeTypeId = attributeTypeId;
		this.value = value;
		this.scarcityRatio = scarcityRatio;
	}
	public AttributeValue(){}

	public static ArrayList<AttributeValue> getAttributeValues(){
		ArrayList<AttributeValue> elems = new ArrayList<AttributeValue>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM attributeValue ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new AttributeValue ( 
					rs.getInt(1)
					, rs.getInt(2)
					, rs.getString(3)
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

	public int insertAttributeValue(int attributeValueId, int attributeTypeId, String value, double scarcityRatio) {
		int retVar = 0;
		try {
			Connection connectionAttributeValue = DbPooledDataSource.getConnection();
			String insertAttributeValuestmtStr = "INSERT INTO attributeValue (attributeValueId, attributeTypeId, value, scarcityRatio) VALUES (?,?,?,?)";
			PreparedStatement prepAttributeValueStat = connectionAttributeValue.prepareStatement(insertAttributeValuestmtStr);
			prepAttributeValueStat.setInt(1, attributeValueId);
			prepAttributeValueStat.setInt(2, attributeTypeId);
			prepAttributeValueStat.setString(3, value);
			prepAttributeValueStat.setDouble(4, scarcityRatio);
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

	public boolean updateAttributeValue(int attributeValueId, int attributeTypeId, String value, double scarcityRatio) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE attributeValue SET attributeValueId = ? , attributeTypeId = ? , value = ? , scarcityRatio = ?  WHERE attributeValueId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeValueId);
			stat.setInt(2, attributeTypeId);
			stat.setString(3, value);
			stat.setDouble(4, scarcityRatio);
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

	public boolean deleteAttributeValue(int attributeValueId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM attributeValue WHERE attributeValueId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeValueId);
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
