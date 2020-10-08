package com.example.ivascall.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ivascall.POJO.UserInfo;
import com.example.ivascall.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.ivascall.View.RecipientCall.CALL;
import static com.example.ivascall.View.RecipientCall.IS_INCOMING;
import static com.example.ivascall.View.RecipientCall.R_ID;
import static com.example.ivascall.View.RecipientCall.R_Name;
import static com.example.ivascall.View.RecipientCall.TYPE;

public class UserCardViewAdapter extends RecyclerView.Adapter<UserCardViewAdapter.Pro_holder>  {




        private Context context;
        private List<UserInfo> userInfoList;
        private NavController navController;


        public UserCardViewAdapter(Context context,List<UserInfo> userInfoList,NavController navController) {
            this.context = context;
            this.userInfoList=userInfoList;
            this.navController=navController;


        }

        @NonNull
        @Override
        public Pro_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cardview, parent, false);
            return new Pro_holder(view);
        }



    @Override
        public void onBindViewHolder(@NonNull final Pro_holder holder, final int position) {
            holder.name.setText(userInfoList.get(holder.getAdapterPosition()).getName());
            holder.phone.setText(userInfoList.get(holder.getAdapterPosition()).getPhoneNumber());

            holder.container.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt(TYPE, CALL);
                bundle.putString(R_ID, userInfoList.get(holder.getAdapterPosition()).getPhoneNumber());
                bundle.putString(R_Name, userInfoList.get(holder.getAdapterPosition()).getName());
                bundle.putBoolean(IS_INCOMING, false);
                navController.navigate(R.id.action_userList_to_recipientCall, bundle);
            });

        }

        @Override
        public int getItemCount() {
            return userInfoList.size();
        }








        //////////////////////////////////////////////////////////
        public class Pro_holder extends RecyclerView.ViewHolder{
            ImageView imageView;
            TextView name,phone;
            RelativeLayout container ;

            public Pro_holder(View itemView) {
                super(itemView);
                imageView =(ImageView) itemView.findViewById(R.id.card_image);
                name=(TextView) itemView.findViewById(R.id.card_name);
                phone=(TextView) itemView.findViewById(R.id.card_phone);
                container=(RelativeLayout)itemView.findViewById(R.id.cardview);
            }
        }

    }


