package com.kk.matrixapi;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.kk.matrixapi.db.QueryDoer;
import com.kk.matrixapi.model.Stats;
import com.kk.matrixapi.model.json.Nft;
import com.kk.matrixapi.model.json.offers.OffersComposite;
import com.kk.matrixapi.util.Updater;
import com.kk.matrixapi.util.Util;

@Configuration
@ComponentScan("com.kk.matrixapi")
@EnableScheduling
public class SpringConfig {
	
	@Autowired
	NiftysAvatarClient webClient;

	//once per hour
//	@Scheduled(fixedDelay = 3600000)
	public void findOffers() {
		List<Integer> blueStatIds = QueryDoer.getAttributeValueIdsForContract(Util.BLUE_CONTRACT);
		List<Integer> redStatIds = QueryDoer.getAttributeValueIdsForContract(Util.RED_CONTRACT);
		List<Stats> heavyHittersBlue = QueryDoer.getBestStatsAvatars(blueStatIds, 64);
		List<Stats> heavyHittersRed = QueryDoer.getBestStatsAvatars(redStatIds, 64);
		
//		for(Stats aviStats : heavyHittersBlue) {
			List<OffersComposite> offers = NiftysWebClient.getOffers();//(Util.BLUE_CONTRACT, aviStats.getTokenId());
//			if (offers.size() > 0) {
//				System.out.println("Has offers!");
//			}
//		}
	}

	//once per day
//	@Scheduled(fixedDelay = 86400000)
	public void updateTraits() {
		
		Nft myNft = webClient.getTraits(NiftysAvatarClient.RED_ID, 76977);
		
		String ownerAddress = NiftysWebClient.findOwnerWallet(Util.RED_CONTRACT, 76977);
		
		//https://explorer.palm.io/token/0x39ceaa47306381b6d79ad46af0f36bc5332386f2/instance/90233/token-transfers?type=JSON
		
		int numberOfAvatars = QueryDoer.getNumberOfAvatars();
		
		if (numberOfAvatars == 0) {
			//get all avatars as a fresh start, ~7 hours
			Updater.getAllAvatars(webClient, NiftysAvatarClient.BASE_ID);
			System.out.println("Done BASE_ID " + new Date());
			Updater.getAllAvatars(webClient, NiftysAvatarClient.RED_ID);
			System.out.println("Done RED_ID " + new Date());
			Updater.getAllAvatars(webClient, NiftysAvatarClient.BLUE_ID);
			System.out.println("Done BLUE_ID " + new Date());
		} else {
			
			//get missing base numbers, get those bases
			List<Integer> missingBaseIds = QueryDoer.getMissingBases();
			List<Integer> unpilledIds = QueryDoer.getUnpilledTokenIds();
			
			if (missingBaseIds.size() == 0 && unpilledIds.size() == 0) {
				QueryDoer.updateNftTokenIdInt();
				missingBaseIds = QueryDoer.getMissingBases();
				unpilledIds = QueryDoer.getUnpilledTokenIds();
			}

			//run these in reverse order of instantiation
			Runnable rarityProcess = new Updater(true, webClient);
			Runnable unpilledCheckProcess = new Updater(unpilledIds, false, rarityProcess, webClient);
			Runnable missingBaseCheckProcess = new Updater(missingBaseIds, false, unpilledCheckProcess, webClient);
			
			//start first thread
			if (missingBaseIds.size() == 0) {
				new Thread(unpilledCheckProcess).start();
			} else {
				new Thread(missingBaseCheckProcess).start();
			}

			System.out.println("Done scheduling tasks");

			while(Updater.currentlyRunningStartingIds.size() != 0) {
				try {
					System.out.println("Still working " + (new Date()) + " " + Updater.currentlyRunningStartingIds.size() + " threads left");
					Thread.sleep(45 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//build json
			
			
			
		}
		
		//old scanning method, kept for c/p examples
//		int chunks = 1;
//		for(int i = 0; i < chunks; i++) {
//			int totalToDo = 100000 / chunks;
//			int startId = (i * totalToDo) + 1;
//			int endId = (i * totalToDo) + totalToDo;
//			Runnable r = new Updater(startId, endId, niftysWebClient, webClient);
//			new Thread(r).start();
//		}
//
//		System.out.println("Done scheduling tasks");
//		
//		
//		System.out.println("Done processing tasks");
		
//		int numberOfAvatars = QueryDoer.getNumberOfAvatars();
//		QueryDoer.updateNftTokenIdInt();
//		QueryDoer.updateAttributeCountsView();
//		
//		
//		QueryDoer.updateScarcity(numberOfAvatars);
//		
//		
//		QueryDoer.updateRarity();

//		System.out.println("Done updating rarity");
		
		//create json files

//        Gson gg = new GsonBuilder().setPrettyPrinting().create();
//
//    	List<Scarcity> scars = Scarcity.getScarcitys();
//    	List<RarityComposite> ranks = QueryDoer.getRanks();
//    	
//        Util.writeToFile(".." + File.separator + ".." + File.separator + "json" + File.separator + "scarcity.json", gg.toJson(scars));
//        Util.writeToFile(".." + File.separator + ".." + File.separator + "json" + File.separator + "ranks.json", gg.toJson(ranks));
//        Util.writeToFile(".." + File.separator + ".." + File.separator + "docs" + File.separator + "scarcity.json", 
//        		"var scarcity = " + gg.toJson(scars) + ";");
//        Util.writeToFile(".." + File.separator + ".." + File.separator + "docs" + File.separator + "ranks.json", 
//        		"var ranks = " + gg.toJson(ranks) + ";");
//
//		System.out.println("JSON files created");
		
		
	}
}
