package com.example.ivascall.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ivascall.Interface.InternetStatus;
import com.example.ivascall.POJO.UserInfo;
import com.example.ivascall.Util.NetworkReceiver;
import com.example.ivascall.R;
import com.example.ivascall.ViewModel.ContactsViewmodel;
import com.example.ivascall.ViewModel.ViewModelFactory;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements InternetStatus, HasSupportFragmentInjector {





    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelFactory viewModelFactory;
    ContactsViewmodel contactsViewmodel;


    public static boolean isLogin;
    public static boolean isOnline;
    private NetworkReceiver networkReceiver;


    @Override
    protected void onStart() {
        super.onStart();
        networkReceiver=new NetworkReceiver(this);
        IntentFilter netFilter=new IntentFilter();
        netFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver,netFilter);
        contactsViewmodel.getUserInfoLiveData().observe(this, new Observer<List<UserInfo>>() {
            @Override
            public void onChanged(List<UserInfo> userInfos) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkReceiver);
        contactsViewmodel.getUserInfoLiveData().removeObservers(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AndroidInjection.inject(this);

        contactsViewmodel= ViewModelProviders.of(this,viewModelFactory).get(ContactsViewmodel.class);


        isOnline=false;
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                (MainActivity.this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS},
                    1);
        }else {
            contactsViewmodel.getContacts();
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode==1) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               contactsViewmodel.getContacts();
            }
        }

    }

    @Override
    public void Connect() {
        isOnline=true;
    }

    @Override
    public void notConnect() {
        isOnline=false;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
