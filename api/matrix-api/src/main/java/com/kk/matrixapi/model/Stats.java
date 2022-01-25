package com.kk.matrixapi.model;

public class Stats {
	private int tokenId;
	private int totalStats;
	public int getTokenId() {
		return tokenId;
	}
	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}
	public int getTotalStats() {
		return totalStats;
	}
	public void setTotalStats(int totalStats) {
		this.totalStats = totalStats;
	}
	public Stats(int tokenId, int totalStats) {
		super();
		this.tokenId = tokenId;
		this.totalStats = totalStats;
	}
}
