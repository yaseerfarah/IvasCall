package com.example.ivascall.View;

import android.app.Activity;
import android.app.Application;

import com.example.ivascall.Dagger2.Component.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;



/**
 * Created by DELL on 5/18/2019.
 */

public class AppController extends Application implements HasActivityInjector
{

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerAppComponent.builder()
                .application(this)
                .appContext(getApplicationContext())
                .build()
                .inject(this);






    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
