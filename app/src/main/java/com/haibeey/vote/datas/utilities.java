package com.haibeey.vote.datas;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by haibeey on 11/22/2017.
 */

public class utilities {
    Activity activity;
    Context context;
    public utilities(Activity activity, Context context){
        this.context=context;
        this.activity=activity;
    }

    public boolean matchEmail(String email){
        return email.matches("[a-z 0-9 A-Z]+@[a-z]+.com");
    }

    public String createJSon(String[] ...params){
        String res="{";
        for(int i =0;i<params.length;i++){
            res+=params[i][0]+":"+params[i][1]+",";
        }
        res=res.substring(0,res.length()-1);
        res+="}";
        return res;
    }

    public boolean isConnected(){
        ConnectivityManager check= (ConnectivityManager)this.context.getSystemService(this.context.CONNECTIVITY_SERVICE);
        if(check!=null){
            NetworkInfo[] info=check.getAllNetworkInfo();
            if(info!=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()== NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }


    public static String getLocalIPAddress () {
        String ip=null;
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if(inetAddress.getHostAddress().matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+")){
                            ip=inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.i("SocketException ", ex.toString());
        }
        return ip;

    }
}
