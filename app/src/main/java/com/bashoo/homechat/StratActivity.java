package com.bashoo.homechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StratActivity extends AppCompatActivity {

    private Button regBtn , regBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strat);


        regBtn = (Button)findViewById(R.id.startButton);
        regBtnLogin = (Button)findViewById(R.id.startSignButton);


        regBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StratActivity.this , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StratActivity.this , RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}