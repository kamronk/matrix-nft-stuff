package com.kk.matrixapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kk.matrixapi.model.json.TokenTransferResponse;
import com.kk.matrixapi.model.json.offers.OffersComposite;

//https://niftys.com/api/offers/nft/0x39ceaa47306381b6d79ad46af0f36bc5332386f2/73702?status=CREATED

public interface NiftysWebClient {

    private static String readFromUrl(String url){
        String responseBody = "";
        try {
            URL siteUrl = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(siteUrl.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                responseBody += inputLine;
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    public static String findOwnerWallet(String contractAddress, int tokenId){
    	String currentOwnerAddy = null;
		try {
	    	String responseBody = readFromUrl("https://explorer.palm.io/token/" + contractAddress + "/instance/" + tokenId + "/token-transfers?type=JSON");
	        ObjectMapper mapper = new ObjectMapper();
	        TokenTransferResponse result = mapper.readValue(responseBody, TokenTransferResponse.class);
			String mostRecentItem = result.getItems()[0];
			currentOwnerAddy = mostRecentItem.split("data-address-hash=\"")[2].split("\"")[0];
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return currentOwnerAddy;
    }
    
    public static List<OffersComposite> getOffers(){
    	    	
		try {
	        StringBuilder result = new StringBuilder();
	        URL url = new URL("https://niftys.com/api/offers/nft/0x423e540cb46db0e4df1ac96bcbddf78a804647d8/55898?status=CREATED");

	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
//	        conn.setRequestProperty("", "");
	        conn.addRequestProperty("authority", "niftys.com");
	        conn.addRequestProperty("method", "GET");
	        conn.addRequestProperty("path", "/api/offers/nft/0x423e540cb46db0e4df1ac96bcbddf78a804647d8/55898?status=CREATED");
	        conn.addRequestProperty("scheme", "https");
	        conn.addRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
	        conn.addRequestProperty("accept-encoding", "gzip, deflate, br");
	        conn.addRequestProperty("accept-language", "en-US,en;q=0.9");
	        conn.addRequestProperty("cache-control", "max-age=0");
	        conn.addRequestProperty("if-none-match", "W/\\\"1066-/6XmvebdeG0Yjemw3xJC4x/SuBA\\\"");
	        conn.addRequestProperty("sec-ch-ua", "\"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\"");
	        conn.addRequestProperty("sec-ch-ua-mobile", "?0");
	        conn.addRequestProperty("sec-fetch-dest", "document");
	        conn.addRequestProperty("sec-fetch-mode", "navigate");
	        conn.addRequestProperty("sec-fetch-site", "none");
	        conn.addRequestProperty("sec-fetch-user", "?1");
	        conn.addRequestProperty("upgrade-insecure-requests", "1");
	        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");

	        try (BufferedReader reader = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream()))) {
	            for (String line; (line = reader.readLine()) != null; ) {
	                result.append(line);
	            }
	        } catch (IOException e) {
				e.printStackTrace();
			}
	        
			System.out.println(result.toString());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        return null;
        
        

	}
    
}
