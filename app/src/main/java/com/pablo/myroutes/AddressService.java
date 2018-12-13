package com.pablo.myroutes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Paul on 10.12.2017.
 */

public class AddressService {
    static private String RequestUrl = "https://geocode-maps.yandex.ru/1.x/?format=json&geocode=";

    static private String geoCoderRequest(String param) throws Exception {
        URL url = new URL(RequestUrl + param);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();

        } catch (Exception e) {
            return "null";
        } finally {
            urlConnection.disconnect();
        }
    }

    static private String getAddressFromJson(String str) {
        String basicLocality = "Архангельск";
        try {
            JSONObject json = new JSONObject(str);
            JSONObject response = json.getJSONObject("response");
            JSONObject GeoObjectCollection = response.getJSONObject("GeoObjectCollection");
            JSONArray featureMembersArray = GeoObjectCollection.getJSONArray("featureMember");
            JSONObject featureMember = featureMembersArray.getJSONObject(0);
            JSONObject GeoObject = featureMember.getJSONObject("GeoObject");
            JSONObject locality = GeoObject.getJSONObject("metaDataProperty")
                    .getJSONObject("GeocoderMetaData")
                    .getJSONObject("Address")
                    .getJSONArray("Components")
                    .getJSONObject(4);
            if (locality.get("name").toString().equals(basicLocality)) {
                return GeoObject.getString("name");
            } else {
                return locality.get("name").toString() + ", " + GeoObject.getString("name");
            }

        } catch (JSONException ex) {
            //return ex.toString();
            return null;
        }
    }

    static public String getAddressFromCoords(Double longitude, Double latitude) throws Exception {
        return getAddressFromJson(geoCoderRequest(longitude.toString() + "," + latitude.toString()));
    }
}