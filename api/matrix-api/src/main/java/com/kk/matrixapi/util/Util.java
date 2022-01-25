package com.kk.matrixapi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Util {

	public static final String BASE_CONTRACT = "0x39ceaa47306381b6d79ad46af0f36bc5332386f2";
	public static final String RED_CONTRACT = "0x28e4b03bc88b59d25f3467b2252b66d4b2c43286";
	public static final String BLUE_CONTRACT = "0x423e540cb46db0e4df1ac96bcbddf78a804647d8";

    public static int writeToFile(String fullfilepath, String fileBody){
        int retVar = 1;
        try {
            File file = new File(fullfilepath);
            FileOutputStream fileOutputStream = null;
            try {
              fileOutputStream = new FileOutputStream(file);
              fileOutputStream.write(fileBody.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
              e.printStackTrace();
              retVar = 0;
            } finally {
              if (fileOutputStream != null) {
                fileOutputStream.close();
              }
            }
        } catch (IOException e) {
            e.printStackTrace();
            retVar = 0;
        }
        return retVar;
    }
   
    public static String readFromUrl(String url){
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
}
