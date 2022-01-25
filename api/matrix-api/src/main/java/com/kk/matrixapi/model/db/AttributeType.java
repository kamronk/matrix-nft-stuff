package com.kk.matrixapi.model.db;

import com.kk.matrixapi.db.DbPooledDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.Date;

public class AttributeType {

	private int attributeTypeId;
	private String name;
	private String contractAddress;

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

	public String getContractAddress(){
		return this.contractAddress;
	}
	public void setContractAddress(String contractAddressIn){
		this.contractAddress = contractAddressIn;
	}

	public AttributeType(int attributeTypeId, String name, String contractAddress){
		this.attributeTypeId = attributeTypeId;
		this.name = name;
		this.contractAddress = contractAddress;
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
					, rs.getString(3)

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

	public int insertAttributeType(int attributeTypeId, String name, String contractAddress) {
		int retVar = 0;
		try {
			Connection connectionAttributeType = DbPooledDataSource.getConnection();
			String insertAttributeTypestmtStr = "INSERT INTO attributeType (attributeTypeId, name, contractAddress) VALUES (?,?,?)";
			PreparedStatement prepAttributeTypeStat = connectionAttributeType.prepareStatement(insertAttributeTypestmtStr);
			prepAttributeTypeStat.setInt(1, attributeTypeId);
			prepAttributeTypeStat.setString(2, name);
			prepAttributeTypeStat.setString(3, contractAddress);
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

	public boolean updateAttributeType(int attributeTypeId, String name, String contractAddress) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE attributeType SET attributeTypeId = ? , name = ? , contractAddress = ?  WHERE attributeTypeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeTypeId);
			stat.setString(2, name);
			stat.setString(3, contractAddress);
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
