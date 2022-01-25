package com.kk.matrixapi.model.json;

public class RarityComposite {
	
	public int tokenId;
	public int rank;
	public int totalStats;
	public double avgRatio;
	public double rarestAttributeRatio;
	public String rarestAttributeValue;
	public String image;
	public String pilledImage;

	public int typeSize;
	
	public RarityComposite(int tokenId, int rank, int typeSize, int totalStats, double avgRatio,
			double rarestAttributeRatio, String rarestAttributeValue, String image, String pilledImage) {
		super();
		this.tokenId = tokenId;
		this.rank = rank;
		this.typeSize = typeSize;
		this.totalStats = totalStats;
		this.avgRatio = avgRatio;
		this.rarestAttributeRatio = rarestAttributeRatio;
		this.rarestAttributeValue = rarestAttributeValue;
		this.image = image;
		this.pilledImage = pilledImage;
	}
	
}
