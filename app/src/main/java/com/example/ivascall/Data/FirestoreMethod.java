package com.example.ivascall.Data;



import androidx.annotation.NonNull;

import com.example.ivascall.Interface.MyCallback;
import com.example.ivascall.Interface.MyListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirestore;


/**
 * Created by DELL on 5/5/2019.
 */

public class FirestoreMethod {


    private FirebaseFirestore firebaseFirestore;




    public FirestoreMethod() {

        this.firebaseFirestore=FirebaseFirestore.getInstance();

    }






    public void getInfoEqualTo(String collectionRef, String columnName, String attributeName, final Class className, final MyCallback myCallback){


        CollectionReference collectionReference=firebaseFirestore.collection(collectionRef);
        final List list=new ArrayList();

        collectionReference.whereEqualTo(columnName,attributeName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                    list.add(documentSnapshot.toObject(className));

                }
                myCallback.onCallback(list,"Get",null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myCallback.onFailure(e.getMessage().toString());
                return ;
            }
        });

    }

////////


    public void getInfoByID(String collectionRef, String id, final Class className, final MyCallback myCallback){


        CollectionReference collectionReference=firebaseFirestore.collection(collectionRef);
        final List list=new ArrayList();

        collectionReference.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    list.add(documentSnapshot.toObject(className));
                    myCallback.onCallback(list, "Get", null);

                }else {
                    myCallback.onCallback(null,null,null);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myCallback.onFailure(e.getMessage().toString());
                return ;
            }
        });



    }


///////

    public void getInfo(String collectionRef,final Class className, final MyCallback myCallback){
        CollectionReference collectionReference=firebaseFirestore.collection(collectionRef);
        final List list=new ArrayList();

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                    list.add(documentSnapshot.toObject(className));

                }
                myCallback.onCallback(list,"Get",null);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myCallback.onFailure(e.getMessage().toString());
                return ;
            }
        });

    }


///////

    public void getInfo(String collectionRef,final Class className,int limit,DocumentSnapshot lastResult, final MyCallback myCallback){
        CollectionReference collectionReference=firebaseFirestore.collection(collectionRef);
        final List list=new ArrayList();

        Query query;
        if (lastResult==null){
            query=collectionReference.limit(limit);

        }else {
            query=collectionReference.startAt(lastResult).limit(limit);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                    list.add(documentSnapshot.toObject(className));

                }

                if(queryDocumentSnapshots.size()>0){
                    myCallback.onCallback(list,"Get",queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1));
                }else {
                    myCallback.onCallback(list,"Get",null);

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myCallback.onFailure(e.getMessage().toString());
                return ;
            }
        });

    }




    ///////







    public String getRandomID(String collectionRef){

        CollectionReference collectionReference=firebaseFirestore.collection(collectionRef);
        return collectionReference.document().getId().toString();
    }


    public void addToDatabase(String collectionRef, Object object, String id, final MyListener myListener){
        CollectionReference collectionReference=firebaseFirestore.collection(collectionRef);

        collectionReference.document(id).set(object).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                myListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myListener.onFailure(e.getMessage().toString());
            }
        });

    }









    public void addToDatabase(String collectionRef, Object object,final MyListener myListener){
        CollectionReference collectionReference=firebaseFirestore.collection(collectionRef);

        collectionReference.add(object).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                myListener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                myListener.onFailure(e.getMessage().toString());
            }
        });

    }










}
