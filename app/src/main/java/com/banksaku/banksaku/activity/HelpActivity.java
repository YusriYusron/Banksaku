package com.banksaku.banksaku.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.banksaku.banksaku.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HelpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button buttonSendEmail;

    private FirebaseUser user;
    private String email;
    private String[] name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Get Data User
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

        buttonSendEmail = findViewById(R.id.buttonSendEmail);
        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
        //Set Up Navigation Drawer
        setUpNavigationDrawer();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_dashboard) {
            makeIntent(HomeActivity.class);
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_chart) {

        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_plan) {
            makeIntent(PlanActivity.class);
        } else if (id == R.id.nav_setting){
            makeIntent(SettingActivity.class);
        } else if (id == R.id.nav_help) {
            //This Activity
        } else if (id == R.id.nav_logout) {
            //Signout User
            FirebaseAuth.getInstance().signOut();
            makeIntent(LoginActivity.class);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void setUpNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get Navigation Drawer
        View headerView = navigationView.getHeaderView(0);
        //Change Name Navigation Drawer
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        email = user.getEmail();
        //Split Email When Found Integer / Number
        name = email.split("@");
        textViewName.setText(name[0].toUpperCase());
        //Change Email Havigation Drawer
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        textViewEmail.setText(email);
    }

    private void sendEmail(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"ibanksaku@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT,"Kritik dan Saran untuk Banksaku");
        startActivity(intent);
    }

    private void makeIntent(Class destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
        finish();
    }
}
