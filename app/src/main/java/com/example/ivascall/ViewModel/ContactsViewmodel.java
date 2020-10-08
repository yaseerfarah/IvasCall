package com.example.ivascall.ViewModel;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.ivascall.Data.FirebaseAuthMethod;
import com.example.ivascall.Data.FirestoreMethod;
import com.example.ivascall.Data.ListenFirestoreItem;
import com.example.ivascall.Data.SharedPreferencesMethod;
import com.example.ivascall.Interface.MyCallback;
import com.example.ivascall.Interface.MyListener;
import com.example.ivascall.POJO.Contact;
import com.example.ivascall.POJO.DocumentSnapListener;
import com.example.ivascall.POJO.UserInfo;
import com.example.ivascall.View.Home;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.ivascall.Constant.APP_KEY;
import static com.example.ivascall.Constant.APP_SECRET;
import static com.example.ivascall.Constant.EMAIL;
import static com.example.ivascall.Constant.ENVIRONMENT;
import static com.example.ivascall.Constant.IS_LOGIN;
import static com.example.ivascall.Constant.NAME;
import static com.example.ivascall.Constant.PHONE_NUMBER;
import static com.example.ivascall.Constant.USER_COLLECTION_REFRENCE;
import static com.example.ivascall.View.MainActivity.isOnline;

public class ContactsViewmodel extends ViewModel {

    public static Call staticCall;

    Context context;
    MediatorLiveData<List<UserInfo>> userInfoLiveData ;

    List<UserInfo> userInfoList;
    List<Contact> contactsList;

    SharedPreferencesMethod sharedPreferencesMethod;
    FirestoreMethod firestoreMethod;
    FirebaseAuthMethod firebaseAuthMethod;

    boolean isLogin;
    UserInfo loggedUserInfo;

    SinchClient sinchClient;


    @Inject
    public ContactsViewmodel(Context context, SharedPreferencesMethod sharedPreferencesMethod, FirestoreMethod firestoreMethod, FirebaseAuthMethod firebaseAuthMethod) {
        this.sharedPreferencesMethod = sharedPreferencesMethod;
        this.firestoreMethod = firestoreMethod;
        this.userInfoLiveData=new MediatorLiveData();
        this.userInfoList=new ArrayList<>();
        this.contactsList=new ArrayList<>();
        this.context=context;

        this.firebaseAuthMethod=firebaseAuthMethod;

       // getContacts();


        isLogin=getIsLogin();
        if (isLogin){
            loggedUserInfo=getUserInfo();

        }else {
            loggedUserInfo=null;
        }


    }



    public LiveData<List<UserInfo>> getUserInfoLiveData(){
        return userInfoLiveData;
    }


    public UserInfo getLoggedUserInfo(){
        return loggedUserInfo;
    }

    public boolean isLogin() {
        return isLogin;
    }



    public void addNewUser(String name, String phone,String email, MyListener myListener){

        UserInfo userInfo=new UserInfo(name,phone,email);

        firestoreMethod.getInfoByID(USER_COLLECTION_REFRENCE, phone, UserInfo.class, new MyCallback() {
            @Override
            public void onCallback(List list, String actionType, DocumentSnapshot lastResult) {

                if (list!=null){
                    // already in database
                   myListener.onFailure("Phone Number already Exits  ");

                }else {
                    // not in Database
                    firebaseAuthMethod.signUpNewUser(name, phone,email, new MyListener() {
                        @Override
                        public void onSuccess() {
                            firestoreMethod.addToDatabase(USER_COLLECTION_REFRENCE, userInfo, phone, new MyListener() {
                                @Override
                                public void onSuccess() {
                                    sharedPreferencesMethod.setString(NAME,name);
                                    sharedPreferencesMethod.setString(PHONE_NUMBER,phone);
                                    sharedPreferencesMethod.setString(EMAIL,email);
                                    sharedPreferencesMethod.setBoolean(IS_LOGIN,true);
                                    loggedUserInfo=userInfo;
                                    isLogin=true;
                                    myListener.onSuccess();
                                    userAddSource();
                                }

                                @Override
                                public void onFailure(String e) {
                                    myListener.onFailure(e);
                                }
                            });

                        }

                        @Override
                        public void onFailure(String e) {
                            myListener.onFailure(e);
                        }
                    });


                }

            }

            @Override
            public void onFailure(String e) {
                myListener.onFailure(e);
            }
        });





    }




    public void signIn(String phone,String email, MyListener myListener){

        firestoreMethod.getInfoByID(USER_COLLECTION_REFRENCE, phone, UserInfo.class, new MyCallback() {
            @Override
            public void onCallback(List list, String actionType, DocumentSnapshot lastResult) {

                if (list!=null){
                    // already in database
                    UserInfo userInfo= (UserInfo) list.get(0);

                    if (userInfo.getEmail().trim().matches(email.trim()))
                    {
                        firebaseAuthMethod.signInUser( phone,email, new MyListener() {
                            @Override
                            public void onSuccess() {
                                sharedPreferencesMethod.setString(NAME,userInfo.getName());
                                sharedPreferencesMethod.setString(PHONE_NUMBER,userInfo.getPhoneNumber());
                                sharedPreferencesMethod.setString(EMAIL,userInfo.getEmail());
                                sharedPreferencesMethod.setBoolean(IS_LOGIN,true);
                                loggedUserInfo=userInfo;
                                isLogin=true;
                                myListener.onSuccess();
                                userAddSource();
                            }

                            @Override
                            public void onFailure(String e) {
                                myListener.onFailure(e);
                            }
                        });
                    }else {
                        myListener.onFailure("Invalid Email ");
                    }




                }else {
                    // not in Database
                    myListener.onFailure("Invalid Email Or Phone Number ");
                }

            }

            @Override
            public void onFailure(String e) {
                myListener.onFailure(e);
            }
        });

    }


    public UserInfo findUserByPhone(String phone){

        for (UserInfo userInfo:userInfoList){
            if (userInfo.getPhoneNumber().trim().matches(phone.trim())){
                return userInfo;

            }
        }
        return null;
    }




    private boolean getIsLogin(){

        return sharedPreferencesMethod.getBoolean(IS_LOGIN);
    }


    private UserInfo getUserInfo(){

        String name=sharedPreferencesMethod.getString(NAME);
        String phoneNumber=sharedPreferencesMethod.getString(PHONE_NUMBER);
        String email=sharedPreferencesMethod.getString(EMAIL);

        return new UserInfo(name,phoneNumber,email);

    }


    private void userAddSource(){


        userInfoList.clear();

        userInfoLiveData.addSource(new ListenFirestoreItem(USER_COLLECTION_REFRENCE, context), new Observer<List<DocumentSnapListener>>() {
            @Override
            public void onChanged(List<DocumentSnapListener> documentSnapListeners) {

                if (documentSnapListeners.size()>0) {

                    for (DocumentSnapListener documentSnapListener:documentSnapListeners) {

                        switch (documentSnapListener.getActionType()) {

                            case ListenFirestoreItem.addedTag:

                                UserInfo userInfo = documentSnapListener.getDocumentSnapshot().toObject(UserInfo.class) != null ? documentSnapListener.getDocumentSnapshot().toObject(UserInfo.class) : new UserInfo();
                                if (!userInfo.getPhoneNumber().trim().matches(loggedUserInfo.getPhoneNumber().trim()))
                                {
                                    for (Contact contact:contactsList){
                                        if (removeAllUnnecessaryChar(contact.getPhoneNumber()).trim().matches(userInfo.getPhoneNumber().trim())){
                                            userInfoList.add(userInfo);
                                            break;
                                        }
                                    }

                                }


                        }
                    }

                }
                userInfoLiveData.postValue(userInfoList);
            }


        });


    }

///////////////////////////////////////////////////////////// Sinch


    public SinchClient getSinchClient(){

        if (isOnline&&sinchClient==null){
            initSinchClint();
            return sinchClient;
        }else {
            return sinchClient;
        }




    }

    private void initSinchClint(){
        try {
            sinchClient = Sinch.getSinchClientBuilder()
                    .context(context)
                    .userId(loggedUserInfo.getPhoneNumber())
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT)
                    .build();

            sinchClient.setSupportCalling(true);
           // sinchClient.startListeningOnActiveConnection();
            sinchClient.start();
        }catch (Exception e){
            Log.e("ERROR Sinch Client",e.getMessage());
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }




    //////////////////////////////// get Contacts Array


    public void getContacts(){

        Observable<Contact> contactObservable= Observable.create(emitter -> {

            try {
                Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                while (phones.moveToNext())
                {
                    String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    if (!emitter.isDisposed()){
                        emitter.onNext(new Contact(name,phoneNumber));
                    }

                }
                if (!emitter.isDisposed()){
                    emitter.onComplete();
                }
                phones.close();



            }catch (Exception e){

                if (!emitter.isDisposed()){
                    Log.e("ERROR CONTACTS",e.getMessage());
                    emitter.onError(e);
                }

            }

        });


        contactObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<Contact>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Contact contact) {
                        contactsList.add(contact);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                        if (isLogin) {
                            userAddSource();
                        }
                        Toast.makeText(context,contactsList.get(0).getPhoneNumber(),Toast.LENGTH_SHORT).show();

                    }
                });

    }



    private String removeAllUnnecessaryChar(String s){

        return s.replaceAll("\\s+","").replaceAll("\\W+","");


    }



    @Override
    protected void onCleared() {
        super.onCleared();
       // userInfoList.clear();

    }
}
