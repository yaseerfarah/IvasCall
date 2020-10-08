package com.example.ivascall.Data;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import javax.inject.Inject;



/**
 * Created by DELL on 7/31/2019.
 */

public class SharedPreferencesMethod {


    private SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferencesMethod(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }


    public void setBoolean(String name,boolean b){

        sharedPreferences.edit().putBoolean(name,b).apply();

    }


    public boolean getBoolean(String name){

        return sharedPreferences.getBoolean(name,false);
    }



    public void setString(String name,String value){

        sharedPreferences.edit().putString(name,value).apply();

    }



    public String getString(String name){

        return sharedPreferences.getString(name,"");
    }












}
