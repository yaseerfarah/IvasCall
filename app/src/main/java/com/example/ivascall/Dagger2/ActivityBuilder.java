package com.example.ivascall.Dagger2;

import com.example.ivascall.View.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;


/**
 * Created by DELL on 5/18/2019.
 */
@Module
public abstract class ActivityBuilder {


    @ContributesAndroidInjector(modules = MainActivityFragments.class)
    abstract MainActivity contributeMainActivity();






}
