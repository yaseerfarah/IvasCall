package com.example.ivascall.Interface;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

/**
 * Created by DELL on 5/5/2019.
 */

public interface MyCallback {

    void onCallback(List list, String actionType, DocumentSnapshot lastResult);
    void onFailure(String e);
}
