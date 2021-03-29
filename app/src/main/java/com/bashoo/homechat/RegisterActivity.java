package com.bashoo.homechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.spec.RSAKeyGenParameterSpec;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference rDatabaseRef;
    private FirebaseAuth mAuth;
    private Button rBtnacc;
    private EditText rEdtname , rEdtgmail , rEdtpass;
    private Toolbar toolbar;
    private ProgressDialog rProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rProgress = new ProgressDialog(this);

        rEdtname = (EditText)findViewById(R.id.reg_nameEdt);
        rEdtgmail = (EditText)findViewById(R.id.reg_gmailEdt);
        rEdtpass = (EditText)findViewById(R.id.reg_passwordEdt);
        rBtnacc = (Button)findViewById(R.id.reg_accountBtn);

        toolbar = (Toolbar)findViewById(R.id.reg_toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle("Create Account");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        rBtnacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = rEdtname.getText().toString();
                String gmail = rEdtgmail.getText().toString();
                String pass = rEdtpass.getText().toString();

                if (!name.isEmpty() & !gmail.isEmpty() & !pass.isEmpty()){
                    rProgress.setTitle("Registering User");
                    rProgress.setMessage("Please wait while we create your account !");
                    rProgress.setCanceledOnTouchOutside(false);
                    rProgress.show();
                    UserAccount(name , gmail , pass);
                }
            }
        });

    }

    private void UserAccount(String name, String gmail, String pass) {

        mAuth.createUserWithEmailAndPassword(gmail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //saving user info in database
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = current_user.getUid();
                            rDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap<String,String> dataMap = new HashMap<>();

                            dataMap.put("name" , name);
                            dataMap.put("status", "Hi I'm using Home Chat App.");
                            dataMap.put("image" , "default");
                            dataMap.put("thumb_image", "defualt");

                            rDatabaseRef.setValue(dataMap);

                            // Sign in success, update UI with the signed-in user's information
                            rProgress.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            rProgress.hide();
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}