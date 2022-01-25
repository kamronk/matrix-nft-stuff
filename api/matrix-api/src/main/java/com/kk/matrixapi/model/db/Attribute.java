package com.kk.matrixapi.model.db;

import com.kk.matrixapi.db.DbPooledDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.Date;

public class Attribute {

	private int attributeId;
	private int attributeTypeId;
	private int attributeValueId;
	private int nftId;

	public int getAttributeId(){
		return this.attributeId;
	}
	public void setAttributeId(int attributeIdIn){
		this.attributeId = attributeIdIn;
	}

	public int getAttributeTypeId(){
		return this.attributeTypeId;
	}
	public void setAttributeTypeId(int attributeTypeIdIn){
		this.attributeTypeId = attributeTypeIdIn;
	}

	public int getAttributeValueId(){
		return this.attributeValueId;
	}
	public void setAttributeValueId(int attributeValueIdIn){
		this.attributeValueId = attributeValueIdIn;
	}

	public int getNftId(){
		return this.nftId;
	}
	public void setNftId(int nftIdIn){
		this.nftId = nftIdIn;
	}

	public Attribute(int attributeId, int attributeTypeId, int attributeValueId, int nftId){
		this.attributeId = attributeId;
		this.attributeTypeId = attributeTypeId;
		this.attributeValueId = attributeValueId;
		this.nftId = nftId;
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
					, rs.getInt(2)
					, rs.getInt(3)
					, rs.getInt(4)

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

	public int insertAttribute(int attributeId, int attributeTypeId, int attributeValueId, int nftId) {
		int retVar = 0;
		try {
			Connection connectionAttribute = DbPooledDataSource.getConnection();
			String insertAttributestmtStr = "INSERT INTO attribute (attributeId, attributeTypeId, attributeValueId, nftId) VALUES (?,?,?,?)";
			PreparedStatement prepAttributeStat = connectionAttribute.prepareStatement(insertAttributestmtStr);
			prepAttributeStat.setInt(1, attributeId);
			prepAttributeStat.setInt(2, attributeTypeId);
			prepAttributeStat.setInt(3, attributeValueId);
			prepAttributeStat.setInt(4, nftId);
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

	public boolean updateAttribute(int attributeId, int attributeTypeId, int attributeValueId, int nftId) {
		boolean retVar = false;
		try {
			Connection connection = DbPooledDataSource.getConnection();
			String stmtStr = "UPDATE attribute SET attributeId = ? , attributeTypeId = ? , attributeValueId = ? , nftId = ?  WHERE attributeId = ?";
			PreparedStatement stat = connection.prepareStatement(stmtStr);
			stat.setInt(1, attributeId);
			stat.setInt(2, attributeTypeId);
			stat.setInt(3, attributeValueId);
			stat.setInt(4, nftId);
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
