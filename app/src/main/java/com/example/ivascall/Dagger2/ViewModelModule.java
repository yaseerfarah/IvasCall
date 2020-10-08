package com.example.ivascall.Dagger2;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;

import com.example.ivascall.Data.FirebaseAuthMethod;
import com.example.ivascall.Data.FirestoreMethod;
import com.example.ivascall.Data.SharedPreferencesMethod;
import com.example.ivascall.ViewModel.ContactsViewmodel;
import com.example.ivascall.ViewModel.ViewModelFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

/**
 * Created by DELL on 5/16/2019.
 */
@Module
public class ViewModelModule {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }





    @Provides
    ViewModelFactory viewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> providerMap) {

        return new ViewModelFactory(providerMap);
    }




    @Provides
    @Singleton
    @IntoMap
    @ViewModelKey(ContactsViewmodel.class)
    ViewModel contactsViewMole(Context context, FirestoreMethod firestoreMethod, SharedPreferencesMethod sharedPreferencesMethod, FirebaseAuthMethod firebaseAuthMethod) {
        return new ContactsViewmodel(context,sharedPreferencesMethod,firestoreMethod,firebaseAuthMethod);
    }

}
