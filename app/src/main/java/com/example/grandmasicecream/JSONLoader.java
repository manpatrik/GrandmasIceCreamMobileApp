package com.example.grandmasicecream;

import android.app.Activity;

import com.example.grandmasicecream.models.Extra;
import com.example.grandmasicecream.models.IceCream;
import com.example.grandmasicecream.models.Item;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSONLoader {

    public static List<Extra> loadExtrasFromJson(Activity activity) {
        JSONArray extrasJson = null;
        try {
            InputStream inputStream = activity.getAssets().open("extras.json");
            JSONParser jsonParser = new JSONParser();
            Object object = jsonParser.parse(
                    new InputStreamReader(inputStream, "UTF-8"));
            extrasJson = (JSONArray) object;
        } catch(Exception e) {
            System.out.println("ERROR: " + e);
        }

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
        JSONObject iceCreamsJson = null;
        try {
            InputStream inputStream = activity.getAssets().open("icecreams.json");
            JSONParser jsonParser = new JSONParser();
            Object object = jsonParser.parse(
                    new InputStreamReader(inputStream, "UTF-8"));
            iceCreamsJson = (JSONObject) object;
        } catch(Exception e) {
            System.out.println("ERROR: " + e);
        }

        List<IceCream> iceCreams = new ArrayList<>();
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
    }
}
