package com.example.ivascall.Dagger2;

import com.example.ivascall.View.Login;
import com.example.ivascall.View.RecipientCall;
import com.example.ivascall.View.SignUp;
import com.example.ivascall.View.Splash;
import com.example.ivascall.View.UserList;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by DELL on 5/18/2019.
 */
@Module
public abstract class MainActivityFragments {


    @ContributesAndroidInjector()
    abstract Login contributeLoginFragment();


    @ContributesAndroidInjector()
    abstract SignUp contributeSignUpFragment();


    @ContributesAndroidInjector()
    abstract Splash contributeSplashFragment();

    @ContributesAndroidInjector()
    abstract UserList contributeUserListFragment();

    @ContributesAndroidInjector()
    abstract RecipientCall contributeRecipientCallFragment();






}
