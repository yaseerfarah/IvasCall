package com.example.ivascall.View;


import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import com.example.ivascall.Interface.InternetStatus;
import com.example.ivascall.Util.NetworkReceiver;
import com.example.ivascall.R;
import com.gturedi.views.CustomStateOptions;
import com.gturedi.views.StatefulLayout;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import static com.example.ivascall.Constant.APP_KEY;
import static com.example.ivascall.Constant.APP_SECRET;
import static com.example.ivascall.Constant.ENVIRONMENT;
import static com.example.ivascall.View.MainActivity.isLogin;
import static com.example.ivascall.View.MainActivity.isOnline;
import static com.example.ivascall.View.RecipientCall.CALL;
import static com.example.ivascall.View.RecipientCall.GROUP;
import static com.example.ivascall.View.RecipientCall.IS_INCOMING;
import static com.example.ivascall.View.RecipientCall.R_ID;
import static com.example.ivascall.View.RecipientCall.TYPE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment implements InternetStatus {
    private static SinchClient sinchClient;


    private static Call callHome;

    EditText recipientName;
    Button callB,group;
    TextView userName;
    StatefulLayout statefulLayout;

    NavController navController;
    MediaPlayer player;

    Vibrator vibrator;

    Dialog startDialog,callDialog;
    Uri ringtone;

    SinchCallListener sinchCallListener;
    private CustomStateOptions networkCustom=new CustomStateOptions().image(R.drawable.ic_cloud_off_black_24dp);
    private NetworkReceiver networkReceiver;

    private String callerName;
    public Home() {
        // Required empty public constructor
         //ringtone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

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
        if (sinchCallListener!=null) {
            callHome.removeCallListener(sinchCallListener);
        }
        super.onStop();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




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

        // assign View
        recipientName=(EditText)view.findViewById(R.id.recipientId);
        callB=(Button)view.findViewById(R.id.call);
        group=(Button)view.findViewById(R.id.room);
        userName=(TextView) view.findViewById(R.id.call_name);
        statefulLayout=(StatefulLayout)view.findViewById(R.id.stateful) ;
        navController= Navigation.findNavController(view);

        userName.setText(callerName);

        /*if (!isLogin&&isOnline){
            create_dialog();
        }*/

        callB.setOnClickListener(v -> {

            if (isOnline) {
                if (recipientName.getText().length()>0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(TYPE, CALL);
                    bundle.putString(R_ID, recipientName.getText().toString());
                    bundle.putBoolean(IS_INCOMING, false);
                    navController.navigate(R.id.action_home2_to_recipientCall, bundle);
                }else {
                    Toast.makeText(getContext(),"Write Recipient Name Please ",Toast.LENGTH_SHORT).show();
                }

            }
        });


        group.setOnClickListener(v -> {
            if (isOnline) {
                if (recipientName.getText().length()>0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(TYPE, GROUP);
                    bundle.putString(R_ID, recipientName.getText().toString());
                    bundle.putBoolean(IS_INCOMING, false);
                    navController.navigate(R.id.action_home2_to_recipientCall, bundle);
                }else {
                    Toast.makeText(getContext(),"Write Recipient Name Please ",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }





    private void create_dialog(){



        final EditText name=(EditText) startDialog.findViewById(R.id.name_edit);

        Button done=(Button) startDialog.findViewById(R.id.add);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length()>0){
               callerName=name.getText().toString();
               isLogin=true;
               userName.setText(callerName);
               initSinchClint();
               startDialog.dismiss();
                }else {
                    Toast.makeText(getContext(),"Write the Name",Toast.LENGTH_SHORT).show();
                }


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

        name.setText(callHome.getRemoteUserId());
        answer.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putBoolean(IS_INCOMING,true);
            bundle.putString(R_ID,callHome.getRemoteUserId());
            navController.navigate(R.id.action_home2_to_recipientCall,bundle);
            player.stop();
            vibrator.cancel();
            callDialog.dismiss();

        });

        end.setOnClickListener(v -> {
            try {
                callHome.hangup();

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


    private void initSinchClint(){
        try {
            sinchClient = Sinch.getSinchClientBuilder()
                    .context(getContext())
                    .userId(callerName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT)
                    .build();

            sinchClient.setSupportCalling(true);
            sinchClient.startListeningOnActiveConnection();
            sinchClient.start();
        }catch (Exception e){
            Log.e("ERROR",e.getMessage());
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }



        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
    }



    @Override
    public void Connect() {
        statefulLayout.showContent();
        if (sinchClient==null&&isLogin){
            initSinchClint();
        }else if (!isLogin){
            create_dialog();
        }else {

            try {
                callHome.hangup();

            }catch (Exception e){

                Log.e("ERROR Hangup",e.getMessage());
            }

            sinchClient.startListeningOnActiveConnection();

            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        }

    }

    @Override
    public void notConnect() {
        if (startDialog.isShowing()){
            startDialog.dismiss();
        }
        if (callDialog.isShowing()){
            try {
                callHome.hangup();

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
            callHome = incomingCall;
            Toast.makeText(getContext(), "incoming call", Toast.LENGTH_SHORT).show();
            sinchCallListener=new SinchCallListener();
           callHome.addCallListener(sinchCallListener);

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
