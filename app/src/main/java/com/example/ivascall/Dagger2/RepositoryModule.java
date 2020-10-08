package com.example.ivascall.Dagger2;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.ivascall.Data.FirebaseAuthMethod;
import com.example.ivascall.Data.FirestoreMethod;
import com.example.ivascall.Data.SharedPreferencesMethod;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.example.ivascall.Constant.USER_FILE_NAME;

/**
 * Created by DELL on 5/24/2019.
 */

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public FirestoreMethod firestoreMethod(){

       return new FirestoreMethod();
    }



    @Provides
    @Singleton
    public SharedPreferences sharedPreferences(Context context){

        return context.getSharedPreferences(USER_FILE_NAME,Context.MODE_PRIVATE);
    }


    @Provides
    @Singleton
    public FirebaseAuthMethod firebaseAuthMethod(){

        return new FirebaseAuthMethod();

    }


    @Provides
    @Singleton
    public SharedPreferencesMethod sharedPreferencesMethod(SharedPreferences sharedPreferences){

       return new SharedPreferencesMethod(sharedPreferences);

    }


}
