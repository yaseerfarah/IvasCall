package com.example.ivascall.View;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ivascall.Interface.MyListener;
import com.example.ivascall.R;
import com.example.ivascall.ViewModel.ContactsViewmodel;
import com.example.ivascall.ViewModel.ViewModelFactory;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {


    @Inject
    ViewModelFactory viewModelFactory;
    private ContactsViewmodel contactsViewmodel;

    EditText phoneE,emailE;

    Button login,signUp;
    ProgressBar progressBar;
    MyListener myListener ;
    NavController navController;

    public Login() {
        // Required empty public constructor



    }


    @Override
    public void onStop() {
        super.onStop();

        myListener=null;


    }

    @Override
    public void onStart() {
        super.onStart();

        myListener=new MyListener() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.INVISIBLE);
                navController.navigate(R.id.action_login2_to_userList);

            }

            @Override
            public void onFailure(String e) {
                Log.e("Login ERROR",e);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(),e,Toast.LENGTH_SHORT).show();
            }
        };
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       signUp=(Button)view.findViewById(R.id.signup);
        phoneE=(EditText)view.findViewById(R.id.user_phone);
        emailE=(EditText) view.findViewById(R.id.user_email);
        login=(Button)view.findViewById(R.id.login);
        progressBar=(ProgressBar)view.findViewById(R.id.prog);
        navController= Navigation.findNavController(view);

        login.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (phoneE.getText().toString().trim().length()>5&&emailE.getText().length()>0&& Patterns.EMAIL_ADDRESS.matcher(emailE.getText().toString()).matches()){

                contactsViewmodel.signIn(phoneE.getText().toString(),emailE.getText().toString(),myListener);

            }else {
                Toast.makeText(getContext(),"Something Wrong Please Check Again",Toast.LENGTH_SHORT).show();

            }


        });

        signUp.setOnClickListener(v -> {
            navController.navigate(R.id.action_login2_to_signUp);
        });


    }
}
