package com.example.myapplication;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Flower {
    private String score;
    private String name;
    private String baike_info;
    private Baike_info baikeInfo = new Baike_info();

    public Baike_info getBaikeInfo() {
        return baikeInfo;
    }


    public void setBaikeInfo(String Baike_info) throws JSONException {
        // Log.d("TAG", "getBaikeInfo: ");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(Baike_info);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        //  Log.d("TAG", "getBaikeInfo: "+jsonObject.getString("image_url"));
        baikeInfo.setImage_url(jsonObject.getString("image_url"));
        baikeInfo.setBaike_url(jsonObject.getString("baike_url"));
        baikeInfo.setDescription(jsonObject.getString("description"));
    }


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaike_info() {
        return baike_info;
    }

    public void setBaike_info(String baike_info) {
        try {
            setBaikeInfo(baike_info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.baike_info = baike_info;
    }
}
