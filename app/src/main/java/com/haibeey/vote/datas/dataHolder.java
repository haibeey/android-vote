package com.haibeey.vote.datas;

import java.util.ArrayList;
import java.util.jar.Pack200;

/**
 * Created by haibeey on 11/29/2017.
 */

public class dataHolder {
    String title;
    String count;
    String date;
    String by;
    ArrayList choices;
    public dataHolder(String title,String count,String date,String by,ArrayList choices){
        this.title=title;
        this.choices=choices;
        this.date=date;
        this.count=count;
        this.by=by;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public void setBy(String by){
        this.by=by;
    }

    public void setCount(String count){
        this.count=count;
    }

    public void setChoices(ArrayList choices){
        this.choices=choices;
    }

    public void  setDate(String date){
        this.date=date;
    }

    public String getTitle(){
        return title;
    }

    public String getBy(){
        return by;
    }

    public String getDate(){
        return date;
    }

    public String getCount(){
        return count;
    }

    public ArrayList getChoice(){
        return choices;
    }
}
