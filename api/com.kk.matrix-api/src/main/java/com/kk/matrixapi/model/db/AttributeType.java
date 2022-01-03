package com.kk.matrixapi.model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kk.matrixapi.db.DbPooledDataSource;

public class AttributeType {

	private int attributeTypeId;
	private String name;

	public int getAttributeTypeId(){
		return this.attributeTypeId;
	}
	public void setAttributeTypeId(int attributeTypeIdIn){
		this.attributeTypeId = attributeTypeIdIn;
	}

	public String getName(){
		return this.name;
	}
	public void setName(String nameIn){
		this.name = nameIn;
	}

	public AttributeType(int attributeTypeId, String name){
		this.attributeTypeId = attributeTypeId;
		this.name = name;
	}
	public AttributeType(){}

	public static ArrayList<AttributeType> getAttributeTypes(){
		ArrayList<AttributeType> elems = new ArrayList<AttributeType>();
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String selectQry = " SELECT * FROM attributeType ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectQry);
			while (rs.next()) {
				elems.add(new AttributeType ( 
					rs.getInt(1)
					, rs.getString(2)

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

	public int insertAttributeType(int attributeTypeId, String name) {
		int retVar = 0;
		try {
			Connection connectionAttributeType = DbPooledDataSource.getConnection();
			String insertAttributeTypestmtStr = "INSERT INTO attributeType (attributeTypeId, name) VALUES (?,?)";
			PreparedStatement prepAttributeTypeStat = connectionAttributeType.prepareStatement(insertAttributeTypestmtStr);
			prepAttributeTypeStat.setInt(1, attributeTypeId);
			prepAttributeTypeStat.setString(2, name);
			prepAttributeTypeStat.executeUpdate();
			ResultSet generatedKeys = prepAttributeTypeStat.getGeneratedKeys();
			if(generatedKeys.next()){
				retVar = generatedKeys.getInt(1);
			}
			generatedKeys.close();
			prepAttributeTypeStat.close();
			connectionAttributeType.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retVar;
	}

	public boolean updateAttributeType(int attributeTypeId, String name) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE attributeType SET attributeTypeId = ? , name = ?  WHERE attributeTypeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeTypeId);
			stat.setString(2, name);
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

	public boolean deleteAttributeType(int attributeTypeId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "DELETE FROM attributeType WHERE attributeTypeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeTypeId);
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
