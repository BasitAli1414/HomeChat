package com.bashoo.homechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class FriendsProfile extends AppCompatActivity {

    private TextView namefp, statusfp, totalfp;
    private ImageView profileImageView;
    private Button sendRequestBtn, declineReq;
    private ProgressDialog progressDialog;


    DatabaseReference userdatabaseRef;
    DatabaseReference friendReqdatabase;
    DatabaseReference friendAcceptdatabase;
    DatabaseReference notificationDatabase;
    private String current_userId;

    private String request_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_profile);

        namefp = findViewById(R.id.nameView_FPActivity);
        statusfp = findViewById(R.id.statusView_FPActivity);
        totalfp = findViewById(R.id.showfriendsView_FPActivity);
        profileImageView = findViewById(R.id.imageView_FPActivity);
        sendRequestBtn = findViewById(R.id.sendReqButton_FPActivity);
        declineReq = findViewById(R.id.declineReqButton_FPActivity);


        progressDialog = new ProgressDialog(FriendsProfile.this);
        progressDialog.setTitle("Loading user data...");
        progressDialog.setMessage("Please wait while we load the user data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        String openProfileuserId = getIntent().getStringExtra("user_id");

        //setting request type
        request_type = "not_friend";
        //new database inside users which id Friend_req..
        friendReqdatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        userdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(openProfileuserId);
        friendAcceptdatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        current_userId = FirebaseAuth.getInstance().getUid().toString();


        userdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String display_name = snapshot.child("name").getValue().toString();
                String display_status = snapshot.child("status").getValue().toString();
                String display_image = snapshot.child("image").getValue().toString();

                namefp.setText(display_name);
                statusfp.setText(display_status);

                Picasso.with(FriendsProfile.this).load(display_image).placeholder(R.drawable.profile).into(profileImageView);

//     ======================== Friend List / Request Features ================================

                friendReqdatabase.child(current_userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if (snapshot.hasChild(openProfileuserId)) {

                            String req_type = snapshot.child(openProfileuserId).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                sendRequestBtn.setText("Accept Friend Request");
                                request_type = "req_received";

                                declineReq.setVisibility(View.VISIBLE);
                                declineReq.setEnabled(true);

                                declineReq.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (request_type.equals("req_received")) {
                                            friendReqdatabase.child(current_userId).child(openProfileuserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    friendReqdatabase.child(openProfileuserId).child(current_userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @SuppressLint("ResourceAsColor")
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            sendRequestBtn.setText("Send Friend Request");
                                                            request_type = "not_friend";

                                                            declineReq.setVisibility(View.INVISIBLE);
                                                            declineReq.setEnabled(false);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });


                            } else if (req_type.equals("sent")) {

                                sendRequestBtn.setText("Cancel Friend Request");
                                request_type = "req_sent";

                                declineReq.setVisibility(View.INVISIBLE);
                                declineReq.setEnabled(false);
                            }
                        } else {

                            friendAcceptdatabase.child(current_userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.hasChild(openProfileuserId)) {

                                        sendRequestBtn.setText("Unfriend this Person");
                                        request_type = "friend";

                                        declineReq.setVisibility(View.INVISIBLE);
                                        declineReq.setEnabled(false);
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//     ======================== Sending the friend request ================================
        // sending the request user
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checking the friend or not inside request filed

                if (request_type.equals("not_friend")) {
                    //sending the request and saving the id and value inside database
                    //user01 --> user02_id --> request_type = sent
                    friendReqdatabase.child(current_userId).child(openProfileuserId).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                //user02 --> user01_id --> request_type = recived
                                friendReqdatabase.child(openProfileuserId).child(current_userId).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", current_userId);
                                        notificationData.put("type", "request");

                                        notificationDatabase.child(openProfileuserId).push().setValue(notificationData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        sendRequestBtn.setText("Cancel Friend Request");
                                                        request_type = "req_sent";

                                                        declineReq.setVisibility(View.INVISIBLE);
                                                        declineReq.setEnabled(false);

                                                    }
                                                });
                                    }
                                });
                            } else {
                                Toast.makeText(FriendsProfile.this, "Faild Sending Request", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }


                //     ======================== Canceling the friend request ================================

                if (request_type.equals("req_sent")) {
                    friendReqdatabase.child(current_userId).child(openProfileuserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendReqdatabase.child(openProfileuserId).child(current_userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("ResourceAsColor")
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendRequestBtn.setText("Send Friend Request");
                                    request_type = "not_friend";

                                    declineReq.setVisibility(View.INVISIBLE);
                                    declineReq.setEnabled(false);
                                }
                            });
                        }
                    });
                }


                //   ======================= Aceepting the friend request ========================

                if (request_type.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    friendAcceptdatabase.child(openProfileuserId).child(current_userId).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendReqdatabase.child(current_userId).child(openProfileuserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendReqdatabase.child(openProfileuserId).child(current_userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @SuppressLint("ResourceAsColor")
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            sendRequestBtn.setText("Unfriend this Person");
                                            request_type = "friend";

                                            declineReq.setVisibility(View.INVISIBLE);
                                            declineReq.setEnabled(false);
                                        }
                                    });
                                }
                            });
                        }
                    });
//
                    friendAcceptdatabase.child(current_userId).child(openProfileuserId).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendReqdatabase.child(openProfileuserId).child(current_userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendReqdatabase.child(openProfileuserId).child(current_userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @SuppressLint("ResourceAsColor")
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            sendRequestBtn.setText("Unfriend this Person");
                                            request_type = "friend";

                                            declineReq.setVisibility(View.INVISIBLE);
                                            declineReq.setEnabled(false);
                                        }
                                    });
                                }
                            });
                        }
                    });

                }


                //   ======================= Unfriend  ========================

                if (request_type.equals("friend")) {

                    friendAcceptdatabase.child(current_userId).child(openProfileuserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendAcceptdatabase.child(openProfileuserId).child(current_userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("ResourceAsColor")
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendRequestBtn.setText("Send Friend Request");
                                    request_type = "not_friend";

                                    declineReq.setVisibility(View.INVISIBLE);
                                    declineReq.setEnabled(false);
                                }
                            });
                        }
                    });

                }
            }
        });

    }

}