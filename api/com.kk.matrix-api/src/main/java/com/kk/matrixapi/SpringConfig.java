package com.kk.matrixapi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.gson.Gson;
import com.kk.matrixapi.db.QueryDoer;
import com.kk.matrixapi.model.db.Scarcity;
import com.kk.matrixapi.model.json.RarityComposite;
import com.kk.matrixapi.util.Updater;

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
		
		//create json files

    	List<Scarcity> scars = Scarcity.getScarcitys();

        Gson gg = new Gson();
        
        String scarsFileBody = gg.toJson(scars);
        
        try {
			BufferedWriter out = new BufferedWriter(new FileWriter("scarcity.json"));
			out.write(scarsFileBody);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

    	List<RarityComposite> ranks = QueryDoer.getRanks();
    	
        String ranksFileBody = gg.toJson(ranks);
        
        try {
			BufferedWriter out = new BufferedWriter(new FileWriter("ranks.json"));
			out.write(ranksFileBody);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
