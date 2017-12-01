package com.haibeey.vote.datas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by haibeey on 11/22/2017.
 */

public class jsonHandler {
    public  String getStringFromJsonObject (JSONObject jsonObject,String name) throws JSONException{
        return jsonObject.getString(name);
    }

    public JSONArray getArrayFromJsonObject(JSONObject jsonObject, String name) throws  JSONException{
        return jsonObject.getJSONArray(name);
    }

    public  JSONObject getObjectFromJsonObject(JSONObject jsonObject,String name) throws  JSONException{
        return jsonObject.getJSONObject(name);
    }

    public String getStringFromJsonArray(JSONArray jsonArray,int index) throws  JSONException{
        return jsonArray.getString(index);
    }

    public JSONArray getArrayFromJsonArray(JSONArray jsonArray,int index) throws JSONException{
        return jsonArray.getJSONArray(index);
    }

    public JSONObject getObjectFromJsonArray(JSONArray jsonArray,int index) throws JSONException{
        return jsonArray.getJSONObject(index);
    }

}
