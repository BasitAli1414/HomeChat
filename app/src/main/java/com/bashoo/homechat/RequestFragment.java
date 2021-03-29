package com.bashoo.homechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.lang.ref.ReferenceQueue;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private View mRequestView;
    private DatabaseReference requestDatabase;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRequestView = inflater.inflate(R.layout.fragment_request, container, false);

        mRecyclerView = (RecyclerView) mRequestView.findViewById(R.id.request_fragment_recylerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        return mRequestView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, RequestViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, RequestViewHolder>(
                        Users.class,
                        R.layout.single_request_layout,
                        RequestViewHolder.class,
                        requestDatabase
                ) {
                    @Override
                    protected void populateViewHolder(RequestViewHolder requestViewHolder, Users request, int i) {


//                        requestViewHolder.setDisplayName(request.getDisplay_name());
//                        requestViewHolder.setDisplayStatus(request.getDisplay_status());
//                        requestViewHolder.setDisplayThumbImage(request.getDisplay_thumb_image() , getContext());

                        requestViewHolder.setDisplayName(request.getName());
                        requestViewHolder.setDisplayStatus(request.getStatus());
                        requestViewHolder.setDisplayThumbImage(request.getThumb_image() , getContext());
           }
                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
 }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;

        }


        public void setDisplayName(String name) {
            TextView viewName = mView.findViewById(R.id.request_name_layout);
            viewName.setText(name);
        }

        public void setDisplayStatus(String status) {
            TextView viewStatus = mView.findViewById(R.id.request_status_layout);
            viewStatus.setText(status);
        }

        public void setDisplayThumbImage(String thumbImage , Context ctx){

            CircleImageView circularImageView = (CircleImageView) mView.findViewById(R.id.request_profile_layout);

            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.profile2).into(circularImageView);


        }

    }

}