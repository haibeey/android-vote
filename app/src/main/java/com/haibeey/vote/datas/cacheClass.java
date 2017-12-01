package com.haibeey.vote.datas;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by haibeey on 11/22/2017.
 */

public class cacheClass {
    Context context;
    SharedPreferences pref;
    String name;
    SharedPreferences.Editor editor;
    public cacheClass(Context context,String name){
        this.context=context;
        this.name=name;
        pref=context.getSharedPreferences(name,Context.MODE_PRIVATE);
    }

    public void putString(String key,String value){
        editor=pref.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public void putInt(String key,int value){
        editor=pref.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public String getString(String key,String value){
        return pref.getString(key,value);
    }

    public int getInt(String key,int value){
        return pref.getInt(key,value);
    }


}
