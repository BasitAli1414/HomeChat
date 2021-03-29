package com.bashoo.homechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.mbms.StreamingServiceInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeStatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference changeDBRef;
    private FirebaseUser firebaseUser;
    private EditText getStatus;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getStatus = findViewById(R.id.changeStatusEdt);

        String oldStatus = getIntent().getStringExtra("old_status");
        getStatus.setText(oldStatus);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid().toString();

        changeDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

    }

    public void newStatus(View view) {

        progressDialog = new ProgressDialog(ChangeStatusActivity.this);
        progressDialog.setTitle("Saving Changes");
        progressDialog.setMessage("Please wait while we save changes.");
        progressDialog.show();


        String status = getStatus.getText().toString();

        changeDBRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                try {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ChangeStatusActivity.this, "" + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}