package com.bashoo.homechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.annotation.KeepForSdkWithFieldsAndMethods;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabLayoutFragmentSection tabLayoutFragmentSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //toolbar
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home Chat");
//        toolbar.inflateMenu(R.menu.main_menu);

        //tabs
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        tabLayout = findViewById(R.id.main_tabLayout);

        tabLayoutFragmentSection = new TabLayoutFragmentSection(getSupportFragmentManager());
        viewPager.setAdapter(tabLayoutFragmentSection);

        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            goToStartActivity();
        }


    }

    private void goToStartActivity() {
        Intent intent = new Intent(MainActivity.this, StratActivity.class);
        startActivity(intent);
        finish();
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logoutBtn:
                FirebaseAuth.getInstance().signOut();
                goToStartActivity();
                break;
            case R.id.userlistMenu:
                Intent Userintent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(Userintent);
                finish();
                break;
            case R.id.accountMenu:
                Intent intent = new Intent(MainActivity.this, SettingProfile.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}