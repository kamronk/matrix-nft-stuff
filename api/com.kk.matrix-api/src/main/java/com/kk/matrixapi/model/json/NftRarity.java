package com.kk.matrixapi.model.json;

public class NftRarity {
	
	private int nftId;
	private double ratio;
	private double avgRatio;
	private double minRatio;
	private String rarestAttribute;
	
	public NftRarity(int nftId, double ratio, double avgRatio, double minRatio, String rarestAttribute) {
		super();
		this.nftId = nftId;
		this.ratio = ratio;
		this.avgRatio = avgRatio;
		this.minRatio = minRatio;
		this.rarestAttribute = rarestAttribute;
	}
	public int getNftId() {
		return nftId;
	}
	public void setNftId(int nftId) {
		this.nftId = nftId;
	}
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	public double getAvgRatio() {
		return avgRatio;
	}
	public void setAvgRatio(double avgRatio) {
		this.avgRatio = avgRatio;
	}
	public double getMinRatio() {
		return minRatio;
	}
	public void setMinRatio(double minRatio) {
		this.minRatio = minRatio;
	}
	public String getRarestAttribute() {
		return rarestAttribute;
	}
	public void setRarestAttribute(String rarestAttribute) {
		this.rarestAttribute = rarestAttribute;
	}

}
