package com.kk.matrixapi.model.db;

public class AttributeComposite {

	private int attributeId;
	private int attributeTypeId;
	private String attributeValue;
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

	public String getAttributeValueId(){
		return this.attributeValue;
	}
	public void setAttributeValueId(String attributeValue){
		this.attributeValue = attributeValue;
	}

	public int getNftId(){
		return this.nftId;
	}
	public void setNftId(int nftIdIn){
		this.nftId = nftIdIn;
	}

	public AttributeComposite(int attributeId, int attributeTypeId, String attributeValue, int nftId){
		this.attributeId = attributeId;
		this.attributeTypeId = attributeTypeId;
		this.attributeValue = attributeValue;
		this.nftId = nftId;
	}
	public AttributeComposite(){}

}
