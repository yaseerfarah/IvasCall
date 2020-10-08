package com.example.ivascall.View;


import android.app.Activity;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivascall.Interface.InternetStatus;
import com.example.ivascall.Util.NetworkReceiver;
import com.example.ivascall.R;
import com.example.ivascall.ViewModel.ContactsViewmodel;
import com.example.ivascall.ViewModel.ViewModelFactory;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


import static com.example.ivascall.ViewModel.ContactsViewmodel.staticCall;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipientCall extends Fragment implements InternetStatus {

    @Inject
    ViewModelFactory viewModelFactory;
    private ContactsViewmodel contactsViewmodel;

    public static final String TYPE="Type";
    public static final String R_ID="r";
    public static final String R_Name="Name";
    public static final String IS_INCOMING="is_incoming";
    public static final int CALL=1;
    public static final int GROUP=2;

    private Activity activity;
    private NetworkReceiver networkReceiver;


    TextView recipientName,status;
    Button endCall;


    String recipientId;
    String recipientN;
    int type;
    boolean is_incoming;
    SinchCallListener sinchCallListener;
    SinchClient sinchClient;

    public RecipientCall() {
        // Required empty public constructor
    }




    @Override
    public void onStart() {
        super.onStart();
        networkReceiver=new NetworkReceiver(this);
        IntentFilter netFilter=new IntentFilter();
        netFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(networkReceiver,netFilter);
    }



    @Override
    public void onStop() {
        getActivity().unregisterReceiver(networkReceiver);
        staticCall.removeCallListener(sinchCallListener);
        super.onStop();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidSupportInjection.inject(this);
        contactsViewmodel= ViewModelProviders.of(this,viewModelFactory).get(ContactsViewmodel.class);


        is_incoming=getArguments().getBoolean(IS_INCOMING);
        if (!is_incoming) {
            type = getArguments().getInt(TYPE);
            recipientId=getArguments().getString(R_ID);
            recipientN=getArguments().getString(R_Name);


        }else {
            recipientId=getArguments().getString(R_ID);
            //call=callHome;
        }
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipient_call, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sinchClient=contactsViewmodel.getSinchClient();
        recipientName=(TextView)view.findViewById(R.id.person_name);
        status=(TextView)view.findViewById(R.id.status);
        endCall=(Button) view.findViewById(R.id.call_end);
        sinchCallListener=new SinchCallListener();




        endCall.setOnClickListener(v -> {

            staticCall.hangup();
            //getActivity().onBackPressed();


        });

}

     private void initConnection(){
         if (is_incoming){

             recipientName.setText(contactsViewmodel.findUserByPhone(recipientId).getName());
             staticCall.answer();
             staticCall.addCallListener(sinchCallListener);



         }else {


             if (sinchClient != null) {

                 switch (type) {

                     case CALL:
                         recipientName.setText(recipientN);
                         staticCall = sinchClient.getCallClient().callUser(recipientId);
                         staticCall.addCallListener(sinchCallListener);
                         status.setText("Calling ...");
                         break;

                     case GROUP:
                         recipientName.setText(recipientN+"  "+"Group");
                         staticCall = sinchClient.getCallClient().callConference(recipientId);
                         staticCall.addCallListener(sinchCallListener);
                         status.setText("Calling ...");
                         break;
                 }

             } else {
                 Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
             }

         }


     }







    @Override
    public void Connect() {
        initConnection();
    }

    @Override
    public void notConnect() {
        try {
            activity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        }catch (Exception e){

        }
        activity.onBackPressed();
    }






//////////////////////////  SinchCallListener

    private class SinchCallListener implements CallListener ,InternetStatus {
        @Override
        public void onCallEnded(Call endedCall) {


            try {
                activity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

            }catch (Exception e){

            }
            activity.onBackPressed();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            try {
                activity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            }catch (Exception e){

            }

            status.setText("Connected");
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            status.setText("Calling ...");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }


        @Override
        public void Connect() {



        }

        @Override
        public void notConnect() {

        }
    }




}
