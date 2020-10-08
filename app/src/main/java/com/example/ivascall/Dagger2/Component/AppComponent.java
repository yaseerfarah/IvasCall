package com.example.ivascall.Dagger2.Component;

import android.app.Application;
import android.content.Context;

import com.example.ivascall.Dagger2.ActivityBuilder;
import com.example.ivascall.Dagger2.RepositoryModule;
import com.example.ivascall.Dagger2.ViewModelModule;
import com.example.ivascall.View.AppController;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by DELL on 5/16/2019.
 */
@Singleton
@Component(modules = {ViewModelModule.class, ActivityBuilder.class, AndroidSupportInjectionModule.class, RepositoryModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder appContext(Context context);

        AppComponent build();

    }



    void inject(AppController appController);

}
