package com.example.ivascall.View;


import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivascall.Adapter.UserCardViewAdapter;
import com.example.ivascall.Interface.InternetStatus;
import com.example.ivascall.POJO.UserInfo;
import com.example.ivascall.R;
import com.example.ivascall.Util.NetworkReceiver;
import com.example.ivascall.ViewModel.ContactsViewmodel;
import com.example.ivascall.ViewModel.ViewModelFactory;
import com.gturedi.views.CustomStateOptions;
import com.gturedi.views.StatefulLayout;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

import static com.example.ivascall.View.MainActivity.isLogin;
import static com.example.ivascall.View.MainActivity.isOnline;
import static com.example.ivascall.View.RecipientCall.GROUP;
import static com.example.ivascall.View.RecipientCall.IS_INCOMING;
import static com.example.ivascall.View.RecipientCall.R_ID;
import static com.example.ivascall.View.RecipientCall.TYPE;
import static com.example.ivascall.ViewModel.ContactsViewmodel.staticCall;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserList extends Fragment implements InternetStatus {

    @Inject
    ViewModelFactory viewModelFactory;
    private ContactsViewmodel contactsViewmodel;

    RecyclerView recyclerView;
    Button group;

    List<UserInfo> userInfoList=new ArrayList<>();
    UserCardViewAdapter userCardViewAdapter;

    StatefulLayout statefulLayout;

    NavController navController;
    MediaPlayer player;

    Vibrator vibrator;

    Dialog startDialog,callDialog;
    SinchCallListener sinchCallListener;
    SinchClient sinchClient;
    SinchCallClientListener sinchCallClientListener;


    private CustomStateOptions networkCustom=new CustomStateOptions().image(R.drawable.ic_cloud_off_black_24dp);
    private NetworkReceiver networkReceiver;


    private Observer userListObserver;


    public UserList() {
        // Required empty public constructor

        userListObserver=new Observer<List<UserInfo>>() {
            @Override
            public void onChanged(List<UserInfo> userInfos) {

                userInfoList.clear();
                userInfoList.addAll(userInfos);
                userCardViewAdapter.notifyDataSetChanged();

            }


        };
    }


    @Override
    public void onStart() {
        super.onStart();
        networkReceiver=new NetworkReceiver(this);
        IntentFilter netFilter=new IntentFilter();
        netFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(networkReceiver,netFilter);
        contactsViewmodel.getUserInfoLiveData().observe(this,userListObserver);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(networkReceiver);
        if (sinchCallListener!=null) {
            staticCall.removeCallListener(sinchCallListener);

        }
        sinchClient.getCallClient().removeCallClientListener(sinchCallClientListener);
        contactsViewmodel.getUserInfoLiveData().removeObservers(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
        contactsViewmodel= ViewModelProviders.of(this,viewModelFactory).get(ContactsViewmodel.class);

        sinchCallClientListener=new SinchCallClientListener();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController= Navigation.findNavController(view);
        recyclerView=(RecyclerView)view.findViewById(R.id.user_list);
        statefulLayout=(StatefulLayout)view.findViewById(R.id.stateful) ;
        group=(Button)view.findViewById(R.id.room);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));

        userCardViewAdapter=new UserCardViewAdapter(getContext(),userInfoList,navController);
        recyclerView.setAdapter(userCardViewAdapter);


        // Rington & Vibration

        vibrator=(Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE) ;


        // Dialog
        startDialog=new Dialog(getContext());
        startDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        startDialog.setCancelable(false);
        startDialog.setContentView(R.layout.dialog_add);

        callDialog=new Dialog(getContext());
        callDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        callDialog.setCancelable(false);
        callDialog.setContentView(R.layout.dialog_incoming_call);



        group.setOnClickListener(v -> {
            if (isOnline) {
               Toast.makeText(getContext(),"Not Supported Now",Toast.LENGTH_SHORT).show();
            }
        });







    }





    private void create_dialog(){



        final EditText name=(EditText) startDialog.findViewById(R.id.name_edit);

        Button done=(Button) startDialog.findViewById(R.id.add);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (name.getText().length()>0){
                    callerName=name.getText().toString();
                    isLogin=true;
                    userName.setText(callerName);
                    initSinchClint();
                    startDialog.dismiss();
                }else {
                    Toast.makeText(getContext(),"Write the Name",Toast.LENGTH_SHORT).show();
                }
*/

            }
        });
        startDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        startDialog.show();





    }


    private void createInComingCallDialog(){



        final TextView name=(TextView) callDialog.findViewById(R.id.name_call);
        final Button answer=(Button) callDialog.findViewById(R.id.call_answer);
        final Button end=(Button) callDialog.findViewById(R.id.call_end);

        // Ringtone call

        long[] pattern = {0, 500, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE));
        }else {
            vibrator.vibrate(pattern,0);
        }

        player = MediaPlayer.create(getContext(),R.raw.ring);
        player.setLooping(true);


        player.start();

        name.setText(contactsViewmodel.findUserByPhone(staticCall.getRemoteUserId()).getName());
        answer.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putBoolean(IS_INCOMING,true);
            bundle.putString(R_ID,staticCall.getRemoteUserId());
            navController.navigate(R.id.action_userList_to_recipientCall,bundle);
            player.stop();
            vibrator.cancel();
            callDialog.dismiss();

        });

        end.setOnClickListener(v -> {
            try {
                staticCall.hangup();

            }catch (Exception e){

                Log.e("ERROR Hangup",e.getMessage());
            }
            player.stop();
            vibrator.cancel();
            callDialog.dismiss();
        });

        callDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        callDialog.show();





    }










    @Override
    public void Connect() {
        statefulLayout.showContent();

        sinchClient=contactsViewmodel.getSinchClient();

            try {
                staticCall.hangup();

            }catch (Exception e){

                Log.e("ERROR Hangup",e.getMessage());
            }

            sinchClient.startListeningOnActiveConnection();
            sinchClient.getCallClient().addCallClientListener(sinchCallClientListener);


    }

    @Override
    public void notConnect() {

        if (startDialog.isShowing()){
            startDialog.dismiss();
        }
        if (callDialog.isShowing()){
            try {
                staticCall.hangup();

            }catch (Exception e){

                Log.e("ERROR Hangup",e.getMessage());
            }
            player.stop();
            vibrator.cancel();
            callDialog.dismiss();
        }
        if (sinchClient!=null){
            sinchClient.stopListeningOnActiveConnection();

        }
        statefulLayout.showCustom(networkCustom.message("Oooopss...  Check your Connection"));



    }






    //////////////////////////////// SinchCallClientListener

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            staticCall = incomingCall;
            Toast.makeText(getContext(), "incoming call", Toast.LENGTH_SHORT).show();
            sinchCallListener=new SinchCallListener();
            staticCall.addCallListener(sinchCallListener);

            /// callHome.answer();
            //callHome.addCallListener(new SinchCallListener());
            createInComingCallDialog();

        }




    }


//////////////////////////  SinchCallListener

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            player.stop();
            vibrator.cancel();
            callDialog.dismiss();
            //getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {

            //getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }







}
