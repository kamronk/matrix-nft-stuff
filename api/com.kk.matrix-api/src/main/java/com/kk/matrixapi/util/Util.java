package com.kk.matrixapi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Util {

    private static int writeToFile(String fullfilepath, String fileBody){
        int retVar = 1;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fullfilepath));
            out.write(fileBody);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            retVar = 0;
        }
        return retVar;
    }

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
}
