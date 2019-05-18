package com.example.root.mapsexample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String,String> getDuration(JSONArray googleDirectionsJason){
        HashMap<String,String> googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance = "";

        try {
            duration = googleDirectionsJason.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJason.getJSONObject(0).getJSONObject("distance").getString("text");
            googleDirectionsMap.put("duration", duration);
            googleDirectionsMap.put("distance", distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  googleDirectionsMap;

    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String,String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity="-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";


        try {
            if (!googlePlaceJson.isNull("name")) {

                placeName = googlePlaceJson.getString("name");

            }
            if(!googlePlaceJson.isNull("vicinity")){

                vicinity = googlePlaceJson.getString("vicinity");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlaceMap.put("place_name",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }

    private List<HashMap<String,String>>getPlaces(JSONArray jsonArray)
    {

        int count = jsonArray.length();
        List<HashMap<String ,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for(int i = 0;i<count;i++){
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    public List<HashMap<String,String>> parse(String jsonData)
    {

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    public String[] parseDirections(String jsonData){

        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  getPaths(jsonArray);
    }

    public String[] getPaths(JSONArray googleStepsJason){
        int count = googleStepsJason.length();
        String[] polylines = new String[count];

        for(int i=0;i<count;i++)
        {
            try {
                polylines[i] = getPath(googleStepsJason.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    public String getPath(JSONObject googlePathJason){
        String polyline = "";
        try {
            polyline =  googlePathJason.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;

    }

}
