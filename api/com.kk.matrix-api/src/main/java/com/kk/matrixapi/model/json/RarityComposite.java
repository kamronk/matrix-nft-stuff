package com.kk.matrixapi.model.json;

public class RarityComposite {
	
	public int tokenId;
	public int rank;
	public double avgRatio;
	public double rarestAttributeRatio;
	public String rarestAttributeValue;
	public String image;
	
	public RarityComposite(int tokenId, int rank, double avgRatio, double rarestAttributeRatio,
			String rarestAttributeValue, String image) {
		super();
		this.tokenId = tokenId;
		this.rank = rank;
		this.avgRatio = avgRatio;
		this.rarestAttributeRatio = rarestAttributeRatio;
		this.rarestAttributeValue = rarestAttributeValue;
		this.image = image;
	}
	
}
