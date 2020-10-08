package com.example.ivascall.View;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivascall.R;
import com.example.ivascall.ViewModel.ContactsViewmodel;
import com.example.ivascall.ViewModel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


/**
 * A simple {@link Fragment} subclass.
 */
public class Splash extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;
    private ContactsViewmodel contactsViewmodel;
    public Splash() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidSupportInjection.inject(this);
        contactsViewmodel= ViewModelProviders.of(this,viewModelFactory).get(ContactsViewmodel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        NavController navController= Navigation.findNavController(view);

        Handler handler=new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!contactsViewmodel.isLogin()){
                    navController.navigate(R.id.action_splash_to_login2);
                }else {
                    navController.navigate(R.id.action_splash_to_userList);
                }


            }
        },2000);


    }
}
