package com.example.grandmasicecream;

import android.app.Activity;

import com.example.grandmasicecream.models.Extra;
import com.example.grandmasicecream.models.IceCream;
import com.example.grandmasicecream.models.Item;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSONLoader {

    private static String loadJSON(URL url){
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream stream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");

            }

            connection.disconnect();

            return buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Extra> loadExtrasFromJson(Activity activity) {
        String extrasString = null;

        try {
            extrasString = loadJSON(new URL("https://raw.githubusercontent.com/udemx/hr-resources/master/extras.json"));

            JSONParser jsonParser = new JSONParser();
            Object extrasJsonObject = jsonParser.parse(extrasString);
            JSONArray extrasJson = (JSONArray) extrasJsonObject;

            List<Extra> extras = new ArrayList<>();
            if (extrasJson != null) {
                for (Object object : extrasJson) {
                    JSONObject extraJson = (JSONObject) object;
                    Extra extra = new Extra( extraJson.get("type").toString() );
                    if (extraJson.get("required") != null) {
                        extra.setRequired(Boolean.parseBoolean(extraJson.get("required").toString()));
                    }
                    JSONArray itemsJson = (JSONArray) extraJson.get("items");
                    extra.setItems(itemsFromJSONArray(itemsJson));

                    extras.add(extra);
                }
            }
            Collections.sort(extras, (s1, s2) -> Boolean.compare(s2.getRequired(), s1.getRequired()));
            return extras;

        } catch(Exception e) {
            System.out.println("ERROR: " + e);
            return  null;
        }
    }

    private static List<Item> itemsFromJSONArray(JSONArray itemsJson) {
        List<Item> items = new ArrayList<>();
        for (Object object : itemsJson){
            JSONObject itemJson = (JSONObject) object;
            Item item = new Item(
                    Long.parseLong(itemJson.get("id").toString()),
                    itemJson.get("name").toString(),
                    Double.parseDouble(itemJson.get("price").toString())
            );
            items.add(item);
        }
        return items;
    }

    public static List<IceCream> loadIceCreamsFromJson(Activity activity) {
        String iceCreamsString = null;
        try {
            iceCreamsString = loadJSON(new URL("https://raw.githubusercontent.com/udemx/hr-resources/master/icecreams.json"));

            List<IceCream> iceCreams = new ArrayList<>();

            JSONParser jsonParser = new JSONParser();
            Object object = jsonParser.parse(iceCreamsString);
            JSONObject iceCreamsJson = (JSONObject) object;

            if (iceCreamsJson != null) {
                JSONArray iceCreamTypes = ((JSONArray) iceCreamsJson.get("iceCreams"));

                for (int i = 0; i < iceCreamTypes.size(); i++) {
                    JSONObject iceCream = (JSONObject) iceCreamTypes.get(i);
                    String imgUrl;
                    Object url;
                    if ((url = iceCream.get("imageUrl")) != null) {
                        imgUrl = url.toString();
                    } else {
                        imgUrl = null;
                    }

                    iceCreams.add(new IceCream(
                            Long.parseLong(iceCream.get("id").toString()),
                            iceCream.get("name").toString(),
                            iceCream.get("status").toString(),
                            imgUrl
                    ));
                }

                Collections.sort(iceCreams, (s1, s2) -> s1.getStatus().compareTo(s2.getStatus()));
            }
            return iceCreams;
        } catch(Exception e) {
            System.out.println("ERROR: " + e);
            return null;
        }
    }
}
