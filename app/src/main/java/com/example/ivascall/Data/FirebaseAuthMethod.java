package com.example.ivascall.Data;

import androidx.annotation.NonNull;

import com.example.ivascall.Interface.MyListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthMethod {



    private FirebaseAuth firebaseAuth;


    public FirebaseAuthMethod() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


    public void signUpNewUser(String name, String phone,String email,  MyListener myListener){


        firebaseAuth.createUserWithEmailAndPassword(email,phone).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                myListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myListener.onFailure(e.getMessage());
            }
        });


    }




    public void signInUser( String phone,String email, MyListener myListener){


        firebaseAuth.signInWithEmailAndPassword(email,phone).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                myListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myListener.onFailure(e.getMessage());
            }
        });

    }



}
