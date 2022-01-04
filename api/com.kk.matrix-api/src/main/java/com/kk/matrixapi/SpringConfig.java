package com.kk.matrixapi;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kk.matrixapi.db.QueryDoer;
import com.kk.matrixapi.model.db.Scarcity;
import com.kk.matrixapi.model.json.RarityComposite;
import com.kk.matrixapi.util.Updater;
import com.kk.matrixapi.util.Util;

@Configuration
@ComponentScan("com.kk.matrixapi")
@EnableScheduling
public class SpringConfig {
	
	@Autowired
	NiftysAggregatorClient webClient;
	
	@Autowired
	NiftysWebClient niftysWebClient;

	@Scheduled(fixedDelay = 86400000)
	public void updateTraits() {
		
		int chunks = 100;
		for(int i = 0; i < chunks; i++) {
			int totalToDo = 100000 / chunks;
			int startId = (i * totalToDo) + 1;
			int endId = (i * totalToDo) + totalToDo;
			Runnable r = new Updater(startId, endId, niftysWebClient, webClient);
			new Thread(r).start();
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

		System.out.println("Done processing tasks");
		
		int numberOfAvatars = QueryDoer.getNumberOfAvatars();
		QueryDoer.updateNftTokenIdInt();
		QueryDoer.updateAttributeCountsView();
		QueryDoer.updateScarcity(numberOfAvatars);
		QueryDoer.updateRarity();

		System.out.println("Done updating rarity");
		
		//create json files

        Gson gg = new GsonBuilder().setPrettyPrinting().create();

    	List<Scarcity> scars = Scarcity.getScarcitys();
    	List<RarityComposite> ranks = QueryDoer.getRanks();
    	
        Util.writeToFile(".." + File.separator + ".." + File.separator + "json" + File.separator + "scarcity.json", gg.toJson(scars));
        Util.writeToFile(".." + File.separator + ".." + File.separator + "json" + File.separator + "ranks.json", gg.toJson(ranks));
        Util.writeToFile(".." + File.separator + ".." + File.separator + "docs" + File.separator + "scarcity.json", 
        		"var scarcity = " + gg.toJson(scars) + ";");
        Util.writeToFile(".." + File.separator + ".." + File.separator + "docs" + File.separator + "ranks.json", 
        		"var ranks = " + gg.toJson(ranks) + ";");

		System.out.println("JSON files created");
		
		
	}
}
