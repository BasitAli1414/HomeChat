package com.bashoo.homechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginBtn;
    private EditText  logEdtgmail , logEdtpass;
    private Toolbar toolbar;
    private ProgressDialog loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginProgress = new ProgressDialog(this);

        logEdtgmail = (EditText)findViewById(R.id.login_gmailEdt);
        logEdtpass = (EditText)findViewById(R.id.login_passwordEdt);
        loginBtn = (Button)findViewById(R.id.login_accountBtn);

        toolbar = (Toolbar)findViewById(R.id.login_toolbar);
        setActionBar(toolbar);
        getActionBar().setTitle("Login Account");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userGmail = logEdtgmail.getText().toString();
                String userPass = logEdtpass.getText().toString();

                if (!TextUtils.isEmpty(userGmail) || !TextUtils.isEmpty(userPass)){

                    loginProgress.setTitle("Logging In");
                    loginProgress.setMessage("Please wait...");
                    loginProgress.setCanceledOnTouchOutside(false);
                    loginMainActivity(userGmail , userPass);
                }
            }
        });
    }

    private void loginMainActivity(String userGmail, String userPass) {

            mAuth.signInWithEmailAndPassword(userGmail , userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        loginProgress.dismiss();
                        Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        loginProgress.hide();
                        Toast.makeText(LoginActivity.this , task.getException().getMessage() , Toast.LENGTH_LONG).show();
                    }
                }
            });

    }
}