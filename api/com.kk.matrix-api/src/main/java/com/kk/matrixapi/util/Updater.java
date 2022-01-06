package com.kk.matrixapi.util;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kk.matrixapi.NiftysAvatarClient;
import com.kk.matrixapi.NiftysWebClient;
import com.kk.matrixapi.db.QueryDoer;
import com.kk.matrixapi.model.json.Attribute;
import com.kk.matrixapi.model.json.Nft;
import com.kk.matrixapi.model.json.RarityComposite;

public class Updater implements Runnable {
	
	public static ArrayList<Integer> currentlyRunningStartingIds = new ArrayList<Integer>();
	
	NiftysAvatarClient niftysAggregatorClient;

	NiftysWebClient niftysWebClient;

	private int startId;
	private int endId;

	public Updater(int startId, int endId, NiftysWebClient niftysWebClient, NiftysAvatarClient niftysAggregatorClient) {
		this.startId = startId;
		this.endId = endId;
		this.niftysAggregatorClient = niftysAggregatorClient;
		this.niftysWebClient = niftysWebClient;
	}
	
	@Override
	public void run() {
		
		Updater.currentlyRunningStartingIds.add(this.startId);
		
		long startTime = System.currentTimeMillis();

		for(int i = startId; i <= endId; i ++) {
			
			try {

				Nft blueAvatarNft = null;
				Nft redAvatarNft = null;
				Nft baseAvatarNft = null;
				try {
					blueAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.BLUE_ID, i);
				} catch (Exception e) {/*ignore*/}
				try {
					redAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.RED_ID, i);
				} catch (Exception e) {/*ignore*/}
				try {
					baseAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.BASE_ID, i);
				} catch (Exception e) {/*ignore*/}

				Nft jsonData = null;

				if (blueAvatarNft != null) {
					jsonData = blueAvatarNft;
					jsonData.contractAddress = "0x423e540cb46db0e4df1ac96bcbddf78a804647d8";
				} else if (redAvatarNft != null) {
					jsonData = redAvatarNft;
					jsonData.contractAddress = "0x28e4b03bc88b59d25f3467b2252b66d4b2c43286";
				} else {
					jsonData = baseAvatarNft;
					jsonData.contractAddress = "0x39ceaa47306381b6d79ad46af0f36bc5332386f2";
				}
				
//				List<OffersComposite> offers = niftysWebClient.getOffers(i);
				
				if (jsonData.tokenId == null) {
					jsonData.tokenId = "" + i;
				}
				
				int existingNftId = QueryDoer.getNftId(jsonData.tokenId);
				
				if (existingNftId == 0) {
					existingNftId = QueryDoer.insertNft(jsonData.tokenId, jsonData.likes, jsonData.contractAddress, jsonData.image);
				} else {
					QueryDoer.updateNft(existingNftId, jsonData.likes, jsonData.image);
				}

		        Gson gg = new GsonBuilder().setPrettyPrinting().create();
	            Util.writeToFile(".." + File.separator + ".." + File.separator + "json" + File.separator + "indiv_avatars" + File.separator + jsonData.tokenId + ".json", gg.toJson(jsonData));
				
				for(Attribute attr : jsonData.attributes) {
		        	int attrTypeId = QueryDoer.getAttributeTypeId(attr.trait_type);
		        	if (attrTypeId == 0) {
		        		attrTypeId = QueryDoer.insertAttributeType(attr.trait_type);
		        	}
		        	int attrId = QueryDoer.getAttributeId(attr.value, existingNftId, attrTypeId);
		        	if (attrId == 0) {
			        	QueryDoer.insertAttribute(attr.value, existingNftId, attrTypeId);
		        	}
				}
				
			} catch (Exception e) {
				if (!e.getMessage().contains("404 Not Found")) {
					e.printStackTrace();
				}
			}

		}
		
		long endTime = System.currentTimeMillis();
		long timeItTook = endTime - startTime;

		Updater.currentlyRunningStartingIds.remove(Updater.currentlyRunningStartingIds.indexOf(this.startId));
		
		System.out.println("Took: " + timeItTook + " ms");
	}

}
