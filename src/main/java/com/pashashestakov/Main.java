package com.pashashestakov;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.json.JSONObject;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pasha on 10/17/2016.
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException, UnirestException, IOException {

        // getAccessToken();
        // getUserInfo();
        String response = retrieveFromAirBNB().toString();
        JsonParser jsonParser = new JsonParser();
        JsonArray results = jsonParser.parse(response)
                .getAsJsonObject().getAsJsonArray("search_results");
        int i = 0;
        File file = new File("C:\\results\\parkcity.txt");
        Writer output = new BufferedWriter(new FileWriter(file));
        for (JsonElement result : results) {
            JsonPrimitive address = result.getAsJsonObject().get("listing").getAsJsonObject().getAsJsonPrimitive("public_address");
            JsonPrimitive user = result.getAsJsonObject().get("listing").getAsJsonObject().get("user").getAsJsonObject().getAsJsonPrimitive("id");
            //System.out.print(++i+". ");
            System.out.print(address);
            //System.out.println(user);
            String userInfo = getHostInfo(user.toString());
            JsonParser jsonP = new JsonParser();
            JsonPrimitive hostResults = jsonP.parse(userInfo)
                    .getAsJsonObject()
                    .get("user")
                    .getAsJsonObject()
                    .getAsJsonPrimitive("first_name");
            System.out.print("Name: " + hostResults+ "\n");
            output.write(+ ++i + ". ");
            output.flush();
            output.write(address.toString());
            output.flush();
            output.write("\n Name: " + hostResults  + "\n");
            output.write("----------------------------------\n");
            output.flush();
        }
    }

    private static String getHostInfo(String id) {
        String url = "https://api.airbnb.com/v2/users/" + id;
        HttpRequest request = null;
        request = Unirest.get(url)
                .queryString("client_id", "3092nxybyb0otqw18e8nh5nty")
                .queryString("locale", "en-US")
                .queryString("currency", "USD")
                .queryString("_format", "v1_legacy_show")
        ;
        try {
            HttpResponse<JsonNode> airBnbResponse = request.asJson();
            return airBnbResponse.getBody().toString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void getUserInfo() {
        HttpRequest request = Unirest.get("https://api.airbnb.com/v1/account/active")
                .header("X-Airbnb-OAuth-Token", "cee7owhwxw312xhwbedkj1xqe")
                .queryString("client_id", "3092nxybyb0otqw18e8nh5nty")
                .queryString("locale", "en-US")
                .queryString("currency", "USD");
        try {
            HttpResponse<JsonNode> airBnbResponse = request.asJson();
            System.out.println(airBnbResponse.getBody().toString());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

    private static String getAccessToken() {
        HttpResponse response = null;
        try {
            response = Unirest.post("https://api.airbnb.com/v1/authorize")
                    .queryString("grant_type", "password")
                    .queryString("client_id", "3092nxybyb0otqw18e8nh5nty")
                    .queryString("locale", "en_US")
                    .queryString("currency", "USD")
                    .basicAuth("XXXXXXXXX", "XXXXXXX")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        String accessToken = response.getBody().toString();
        System.out.println(accessToken);
        return accessToken;
    }

    private static JSONObject retrieveFromAirBNB() throws UnirestException {
        HttpRequest request = Unirest.get("https://api.airbnb.com/v2/search_results")
                .header("X-Airbnb-Carrier-Country", "us")
                .queryString("client_id", "3092nxybyb0otqw18e8nh5nty")
                .queryString("location", "Park City, UT, United States")
                // .queryString("user_lng", "-73.98530667330833")
                .queryString("ib_add_photo_flow", "false")
                .queryString("suppress_facets", "false")
                .queryString("_limit", "40")
                .queryString("locale", "en-US")
                .queryString("currency", "USD");


        HttpResponse<JsonNode> airBnbResponse = request.asJson();

        return airBnbResponse.getBody().getObject();
    }

}
