package com.banksaku.banksaku.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.banksaku.banksaku.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText editTextEmail;
    private EditText editTextNewPassword;
    private EditText editTextConfirmNewPassword;
    private Button buttonChange;

    private FirebaseUser user;
    private String email;
    private String[] name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Get Data User
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

        //Set Up Navigation Drawer
        setUpNavigationDrawer();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setText(email);
        editTextEmail.setEnabled(false);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        buttonChange = findViewById(R.id.buttonChange);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChange();
            }
        });
    }

    private void saveChange(){
        String email = editTextEmail.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();
        String confirmPassword = editTextConfirmNewPassword.getText().toString();

        if (email.equals("") || newPassword.equals("") || confirmPassword.equals("")){
            Toast.makeText(this, "Form harus diisi semua", Toast.LENGTH_SHORT).show();
        }else {
            if ((newPassword.length()<6 || confirmPassword.length()<6)){
                Toast.makeText(this, "Minimal password 6 karakter", Toast.LENGTH_SHORT).show();
            }else {
                if (!newPassword.equals(confirmPassword)){
                    Toast.makeText(this, "Password baru tidak sama", Toast.LENGTH_SHORT).show();
                }else {
                    //Update Data User
                    updateDataUser();
                }
            }
        }
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
            //This Activity
        } else if (id == R.id.nav_help) {
            makeIntent(HelpActivity.class);
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
        //Split Email When Found Integer / Number
        name = email.split("@");
        textViewName.setText(name[0].toUpperCase());
        //Change Email Havigation Drawer
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        textViewEmail.setText(email);
    }

    private void updateDataUser(){
        String confirmPassword = editTextConfirmNewPassword.getText().toString();

        user.updatePassword(confirmPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingActivity.this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(SettingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void makeIntent(Class destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
        finish();
    }
}
