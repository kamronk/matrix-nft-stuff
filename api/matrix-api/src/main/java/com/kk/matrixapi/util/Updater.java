package com.kk.matrixapi.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kk.matrixapi.NiftysAvatarClient;
import com.kk.matrixapi.NiftysWebClient;
import com.kk.matrixapi.db.DbPooledDataSource;
import com.kk.matrixapi.db.QueryDoer;
import com.kk.matrixapi.model.json.Attribute;
import com.kk.matrixapi.model.json.Nft;
import com.kk.matrixapi.model.json.offers.OffersComposite;

public class Updater implements Runnable {
	
	public static ArrayList<Integer> currentlyRunningStartingIds = new ArrayList<Integer>();
	
	NiftysAvatarClient niftysAggregatorClient;

	private int startId;
	private int endId;
	private List<Integer> ids;
	private boolean doRarity;
	private boolean onlyPilled;
	private Runnable nextTask;

	public Updater(int startId, int endId, NiftysAvatarClient niftysAggregatorClient) {
		this.startId = startId;
		this.endId = endId;
		this.niftysAggregatorClient = niftysAggregatorClient;
		Updater.currentlyRunningStartingIds.add(this.startId);
	}

	public Updater(List<Integer> ids, boolean onlyPilled, NiftysAvatarClient niftysAggregatorClient) {
		this.ids = ids;
		this.onlyPilled = onlyPilled;
		this.niftysAggregatorClient = niftysAggregatorClient;
		Updater.currentlyRunningStartingIds.add(ids.get(0));
	}

	public Updater(List<Integer> ids, boolean onlyPilled, Runnable nextTask, NiftysAvatarClient niftysAggregatorClient) {
		this.ids = ids;
		this.onlyPilled = onlyPilled;
		this.niftysAggregatorClient = niftysAggregatorClient;
		this.nextTask = nextTask;
		Updater.currentlyRunningStartingIds.add(ids.get(0));
	}
	public Updater(boolean doRarity, NiftysAvatarClient niftysAggregatorClient) {
		this.doRarity = doRarity;
		this.niftysAggregatorClient = niftysAggregatorClient;
		Updater.currentlyRunningStartingIds.add(-3);
	}
	
	@Override
	public void run() {
		
		long startTime = System.currentTimeMillis();
		

		System.out.println("Started - " + new Date());
		
		if (doRarity) {

			List<Integer> redAttrValueIds = QueryDoer.getAttributeValueIdsForContract(Util.RED_CONTRACT);
			List<Integer> blueAttrValueIds = QueryDoer.getAttributeValueIdsForContract(Util.BLUE_CONTRACT);
			List<Integer> baseAttrValueIds = QueryDoer.getAttributeValueIdsForContract(Util.BASE_CONTRACT);
			
			QueryDoer.dropRebuildTempScarcity(redAttrValueIds, Util.RED_CONTRACT);
			QueryDoer.dropRebuildTempScarcity(blueAttrValueIds, Util.BLUE_CONTRACT);
			QueryDoer.dropRebuildTempScarcity(baseAttrValueIds, Util.BASE_CONTRACT);
			
			QueryDoer.updateAttributeValueScarcityDropTemps();
			
			//get ranks
			//get rarest attributes for each rank
			
			//create files
			
			//get ranks
			
			//rarity checker json clob

			System.out.println("Done Rarity - " + new Date());
			
		} else if (ids != null && ids.size() > 0) {
			for(Integer tokenId : ids) {
				
				try {

					Nft jsonData = onlyPilled ? getPilledNft(tokenId) : getNft(tokenId);
					
					if (jsonData == null) 
						continue;
										
					if (jsonData.tokenId == null) {
						jsonData.tokenId = "" + tokenId;
					}
					
					saveNft(jsonData);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			QueryDoer.updateNftTokenIdInt();
			
			System.out.println("Done Updating " + (onlyPilled ? "Pilled" : "ALL") + " - " + new Date());
			
			if (nextTask != null) {
				
				
				
				new Thread(nextTask).start();
			}
		} else {
			for(int i = startId; i <= endId; i ++) {
				
				try {

					Nft jsonData = getNft(i);
	
					if (jsonData == null) {
						//try one more time, else let exception be thrown from here
						jsonData = getNft(i);
						if (jsonData == null) {
							continue;
						}
//						throw new Exception("Could not find token #" + i);
					}
					
//					List<OffersComposite> offers = niftysWebClient.getOffers(i);
					
					if (jsonData.tokenId == null) {
						jsonData.tokenId = "" + i;
					}
					
					saveNft(jsonData);
					
					//write eac json clob to disk
//			        Gson gg = new GsonBuilder().setPrettyPrinting().create();
//			        Util.writeToFile(".." + File.separator + ".." + File.separator + "json" + File.separator + "indiv_avatars" + File.separator + jsonData.tokenId + ".json", gg.toJson(jsonData));
	
					if (jsonData.contractAddress.equals(Util.RED_CONTRACT) || jsonData.contractAddress.equals(Util.BLUE_CONTRACT)) {
						try {
							Nft baseAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.BASE_ID, i);
							baseAvatarNft.contractAddress = Util.BASE_CONTRACT;
							baseAvatarNft.tokenId = "" + i;
							saveNft(baseAvatarNft);
						} catch (Exception e) {/*ignore*/}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		
		long endTime = System.currentTimeMillis();
		long timeItTook = endTime - startTime;

		if (ids != null && ids.size() > 0) {
			Updater.currentlyRunningStartingIds.remove(Updater.currentlyRunningStartingIds.indexOf(ids.get(0)));
		} else if (doRarity){
			Updater.currentlyRunningStartingIds.remove(Updater.currentlyRunningStartingIds.indexOf(-3));
		} else {
			Updater.currentlyRunningStartingIds.remove(Updater.currentlyRunningStartingIds.indexOf(this.startId));
		}
		
		System.out.println("Took: " + timeItTook + " ms");
	}
	
	public static void saveNft(Nft jsonData) {
		int existingNftId = QueryDoer.getNftId(jsonData.tokenId, jsonData.contractAddress);
		
		if (existingNftId == 0) {
			existingNftId = QueryDoer.insertNft(jsonData.tokenId, jsonData.likes, jsonData.contractAddress, jsonData.image);
		} else {
			QueryDoer.updateNft(existingNftId, jsonData.likes, jsonData.image);
		}
		
//		System.out.println("Saving " + jsonData.tokenId + " db pk " + existingNftId);
		
		for(Attribute attr : jsonData.attributes) {
        	int attrTypeId = QueryDoer.getAttributeTypeId(attr.trait_type, jsonData.contractAddress);
        	if (attrTypeId == 0) {
        		attrTypeId = QueryDoer.insertAttributeType(attr.trait_type, jsonData.contractAddress);
        	}

        	int attrValueId = QueryDoer.getAttributeValueId(attrTypeId, attr.value);
        	if (attrValueId == 0) {
        		attrValueId = QueryDoer.insertAttributeValue(attrTypeId, attr.value);
        	}
        	
        	int attrId = QueryDoer.getAttributeId(attrValueId, existingNftId, attrTypeId);
        	if (attrId == 0) {
	        	QueryDoer.insertAttribute(attrValueId, existingNftId, attrTypeId);
        	}
		}
	}
	
	private Nft getNft(int tokenId) {
		Nft returnedNft = null;
		try {
			Nft blueAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.BLUE_ID, tokenId);
			blueAvatarNft.contractAddress = Util.BLUE_CONTRACT;
			returnedNft = blueAvatarNft;
		} catch (Exception e) {/*ignore*/}
		if (returnedNft == null) {
			try {
				Nft redAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.RED_ID, tokenId);
				redAvatarNft.contractAddress = Util.RED_CONTRACT;
				returnedNft = redAvatarNft;
			} catch (Exception e) {/*ignore*/}
		}
		if (returnedNft == null) {
			try {
				Nft baseAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.BASE_ID, tokenId);
				baseAvatarNft.contractAddress = Util.BASE_CONTRACT;
				returnedNft = baseAvatarNft;
			} catch (Exception e) {/*ignore*/}
		}
		return returnedNft;
	}
	
	private Nft getPilledNft(int tokenId) {
		Nft returnedNft = null;
		try {
			Nft redAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.RED_ID, tokenId);
			redAvatarNft.contractAddress = Util.RED_CONTRACT;
			returnedNft = redAvatarNft;
		} catch (Exception e) {/*ignore*/}
		if (returnedNft == null) {
			try {
				Nft blueAvatarNft = niftysAggregatorClient.getTraits(NiftysAvatarClient.BLUE_ID, tokenId);
				blueAvatarNft.contractAddress = Util.BLUE_CONTRACT;
				returnedNft = blueAvatarNft;
			} catch (Exception e) {/*ignore*/}
		}
		return returnedNft;
	}

	public static void getAllAvatars(NiftysAvatarClient webClient, String baseId) {

		try {
			Connection connection = DbPooledDataSource.getConnection();
			String insertStr = "INSERT INTO attribute (attributeValueId, nftId, attributeTypeId) VALUES (?,?,?)";
			PreparedStatement prepStmt = connection.prepareStatement(insertStr);
			prepStmt = connection.prepareStatement(insertStr);
			for (int i = 0; i < 100000; i++) {
				try {
					Nft jsonData = webClient.getTraits(baseId, i);
					switch(baseId) {
						case NiftysAvatarClient.BASE_ID:
							jsonData.contractAddress = Util.BASE_CONTRACT;
							break;
						case NiftysAvatarClient.RED_ID:
							jsonData.contractAddress = Util.RED_CONTRACT;
							break;
						case NiftysAvatarClient.BLUE_ID:
							jsonData.contractAddress = Util.BLUE_CONTRACT;
							break;
					}
					jsonData.tokenId = "" + i;
					
					int existingNftId = QueryDoer.getNftId(jsonData.tokenId, jsonData.contractAddress);
					
					if (existingNftId == 0) {
						existingNftId = QueryDoer.insertNft(jsonData.tokenId, jsonData.likes, jsonData.contractAddress, jsonData.image);
					} else {
						QueryDoer.updateNft(existingNftId, jsonData.likes, jsonData.image);
					}
										
					for(Attribute attr : jsonData.attributes) {
			        	int attrTypeId = QueryDoer.getAttributeTypeId(attr.trait_type, jsonData.contractAddress);
			        	if (attrTypeId == 0) {
			        		attrTypeId = QueryDoer.insertAttributeType(attr.trait_type, jsonData.contractAddress);
			        	}

			        	int attrValueId = QueryDoer.getAttributeValueId(attrTypeId, attr.value);
			        	if (attrValueId == 0) {
			        		attrValueId = QueryDoer.insertAttributeValue(attrTypeId, attr.value);
			        	}

						prepStmt.setInt(1, attrValueId);
						prepStmt.setInt(2, existingNftId);
						prepStmt.setInt(3, attrTypeId);
						prepStmt.addBatch();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (i % 1000 == 0)
					prepStmt.executeBatch();
			}
			prepStmt.executeBatch();
			prepStmt.close();
			connection.close();
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
