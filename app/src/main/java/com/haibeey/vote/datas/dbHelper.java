package com.haibeey.vote.datas;

import android.content.ContentValues;
import android.content.Context;
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
        db.execSQL("create table Topics (id integer primary key, title text,count  text,date text,by text)");
        db.execSQL("create table Choice (id integer primary key ,title text ,name text,count text,by text)");
        db.execSQL("create table User (id integer primary key ,title text ,count text)");

        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertTopic(String title,String date,int count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("date",date);
        values.put("by",by);
        values.put("count",String.valueOf(count));

        db.insert("Topics",null,values);

        db.close();
        Log.i(LogTag,"new topic "+title+" inserted succesfully");
    }

    public  void insertInChoice(String title,String name,String count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("count",String.valueOf(count));
        values.put("name",name);
        values.put("by",by);

        db.insert("Choice",null,values);

        db.close();
        Log.i(LogTag,"new choice "+title+" inserted succesfully");
    }

    public  void insertInUser(String title,String count){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("count",String.valueOf(count));

        db.insert("User",null,values);

        db.close();
    }

    public void updateTopic(String title,String date,int count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("date",date);
        values.put("by",by);
        values.put("count",String.valueOf(count));

        db.update("Topics",values,"title=? and by=?",new String[]{title,by});

        db.close();
        Log.i(LogTag,"new topic "+title+" updated succesfully");
    }

    public  void updateChoice(String title,String name,int count,String by){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("by",by);
        values.put("name",name);
        values.put("count",String.valueOf(count));

        db.update("Choice",values,"name=? and title=? and by=?",new String[]{name,title,by});

        db.close();
        Log.i(LogTag,"new choice "+title+" choice succesfully");
    }

    public  void updateUser(String title,int count){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("title",title);
        values.put("count",String.valueOf(count));

        db.update("Choice",values,"title=?",new String[]{title});

        db.close();
    }

    public ArrayList<dataHolder> getDataFromTopic() {
        //select all data from db
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select * from Topics", null);

        ArrayList<dataHolder> arrayList = new ArrayList<>();

        cur.moveToFirst();
        //get all the topic so far but without corresponding options
        while (!cur.isAfterLast()) {
            dataHolder curdata=new dataHolder(cur.getString(1),cur.getString(2),cur.getString(3),cur.getString(4),null);
            arrayList.add(curdata);
            cur.moveToNext();
        }
        cur.close();

        //this now fill the topic and there corresponding option
        for(int choice=0;choice<arrayList.size();choice++){
            dataHolder curHolder=arrayList.get(choice);

            cur = db.query("Choice",new String[]{"name","count"},"title=? and by=?",new  String[]{curHolder.getTitle(),curHolder.getBy()},null,null,null);

            cur.moveToFirst();

            ArrayList choice_=new ArrayList<>();
            //updating the array list
            while(!cur.isAfterLast()){
                choice_.add(new String[]{cur.getString(0),cur.getString(1)});
                curHolder.setChoices(choice_);
                cur.moveToNext();
            }

            cur.close();
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
        cur.close();

        return  arrayList;
    }

    public boolean topicInTopic(String title){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.query("Topics",new String[]{"title"},"title=?",new  String[]{title},null,null,null);

        boolean result=cur.getCount()>=1;

        cur.close();

        return  result;
    }

    public boolean topicInUser(String title){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.query("User",new String[]{"title"},"title=?",new  String[]{title},null,null,null);

        boolean result=cur.getCount()>=1;

        cur.close();

        return  result;
    }

}
