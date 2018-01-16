package com.haibeey.vote.datas;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by haibeey on 11/23/2017.
 */

public class dbHelper extends SQLiteOpenHelper {
    private static String dbName="vote";
    private static String LogTag="DATABASE";
    public dbHelper(Context context){
        super(context,dbName,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //initial the databse
        db.execSQL("create table Topics (id integer, title text,count  text,date text,by text)");
        db.execSQL("create table Choice (id integer ,title text ,name text,count text,by text)");
        db.execSQL("create table User (id integer primary key ,title text ,count text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertTopic(int id,String title,String date,int count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id",id);
        values.put("title",title);
        values.put("date",date);
        values.put("by",by);
        values.put("count",String.valueOf(count));

        db.insert("Topics",null,values);

        Log.i(LogTag,"new topic "+title+" inserted succesfully");
    }

    public  void insertInChoice(int id,String title,String name,String count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("id",id);
        values.put("count",String.valueOf(count));
        values.put("name",name);
        values.put("by",by);

        db.insert("Choice",null,values);

        Log.i(LogTag,"new choice "+title+" inserted succesfully");
    }

    public  void insertInUser(String title,String count){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("count",String.valueOf(count));

        db.insert("User",null,values);

    }

    public void updateTopic(int id,String title,String date,int count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("id",id);
        values.put("date",date);
        values.put("by",by);
        values.put("count",String.valueOf(count));

        db.update("Topics",values,"title=? and by=? and date=?",new String[]{title,by});

        Log.i(LogTag,"new topic "+title+" updated succesfully");
    }

    public  void updateChoice(int id,String title,String name,int count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("id",id);
        values.put("title",title);
        values.put("by",by);
        values.put("name",name);
        values.put("count",String.valueOf(count));

        db.update("Choice",values,"name=? and title=? and by=?",new String[]{name,title,by});

        Log.i(LogTag,"new choice "+title+" choice succesfully");
    }

    public  void updateUser(String title,int count){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("count",String.valueOf(count));

        db.update("Choice",values,"title=?",new String[]{title});

    }

    public ArrayList<dataHolder> getDataFromTopic() {
        //select all data from db

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from Topics", null);

        ArrayList<dataHolder> arrayList = new ArrayList<>();

        cur.moveToFirst();
        //get all the topic so far but without corresponding options
        while (!cur.isAfterLast()) {
            dataHolder curdata=new dataHolder(cur.getInt(0),cur.getString(1),cur.getString(2),cur.getString(3),cur.getString(4),null);
            arrayList.add(curdata);
            cur.moveToNext();
        }

        //this now fill the topic and there corresponding option
        for(int choice=0;choice<arrayList.size();choice++){
            dataHolder curHolder=arrayList.get(choice);

            cur = db.query("Choice",new String[]{"name","count"},"id=? and title=? and by=?",new  String[]{String.valueOf(curHolder.getID()),curHolder.getTitle(),curHolder.getBy()},null,null,null);

            cur.moveToFirst();

            ArrayList choice_=new ArrayList<String[]>();
            //updating the array list
            while(!cur.isAfterLast()){
                choice_.add(new String[]{cur.getString(0),cur.getString(1)});
                curHolder.setChoices(choice_);
                cur.moveToNext();
            }

        }
        return arrayList;
    }

    public  ArrayList<String[]> getUserData(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.rawQuery("Select * from User", null);

        ArrayList<String[]> arrayList = new ArrayList<>();

        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            arrayList.add(new String[]{cur.getString(1),cur.getString(2),cur.getString(3)});
            cur.moveToNext();
        }

        return  arrayList;
    }

    public boolean topicInTopic(int id,String title){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.query("Topics",new String[]{"title"},"title=? and id=?",new  String[]{title,String.valueOf(id)},null,null,null);

        boolean result=cur.getCount()>=1;

        return  result;
    }

    public boolean topicInUser(String title){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.query("User",new String[]{"title"},"title=?",new  String[]{title},null,null,null);

        boolean result=cur.getCount()>=1;


        return  result;
    }

}
