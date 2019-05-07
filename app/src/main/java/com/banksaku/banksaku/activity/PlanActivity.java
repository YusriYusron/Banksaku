package com.banksaku.banksaku.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.banksaku.banksaku.R;
import com.banksaku.banksaku.adapter.PlanAdapter;
import com.banksaku.banksaku.model.transaction.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlanActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView recyclerViewPlan;
    private TextView textViewNotDataFound;
    private Button buttonDonePlan,buttonCanclePlan;

    private FirebaseUser user;
    private String email;
    private String[] name;
    private String uId;

    private Transaction getData;
    private String transactionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Get Data User
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        uId = user.getUid();

        getData = getIntent().getParcelableExtra("transaction");

        textViewNotDataFound = findViewById(R.id.textViewNotDataFound);
//        buttonDonePlan = findViewById(R.id.buttonDonePlan);
//        buttonDonePlan.setOnClickListener(this);
//        buttonCanclePlan = findViewById(R.id.buttonCancelPlan);
//        buttonCanclePlan.setOnClickListener(this);

        //Set Up Navigation Drawer
        setUpNavigationDrawer();
        //Set Up RecyclerView Transaction
        setUpRecyclerViewTransaction();

        if (getData != null){
            transactionId = getData.getId();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonDonePlan){

        }else if (view.getId() == R.id.buttonCancelPlan){
            deleteData();
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
            //This Activity
        } else if (id == R.id.nav_setting){
            makeIntent(SettingActivity.class);
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

    private void setUpRecyclerViewTransaction() {
        //Transaction Recycler View
        recyclerViewPlan = findViewById(R.id.recyclerViewPlan);
        final ArrayList<Transaction> dataPlan = new ArrayList<>();

        //Get Root in Firebase
        final DatabaseReference databasePlan = FirebaseDatabase.getInstance().getReference("plan").child(uId);
        databasePlan.keepSynced(true);

        databasePlan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Get data from Root Firebase
                if (dataSnapshot.exists()){
                    textViewNotDataFound.setVisibility(View.GONE);
                    for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()){
                        Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                        //Add to ArrayList from Firebase
                        dataPlan.add(transaction);
                    }
                    PlanAdapter adapter = new PlanAdapter(PlanActivity.this, dataPlan);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PlanActivity.this);

                    recyclerViewPlan.setLayoutManager(layoutManager);
                    recyclerViewPlan.setAdapter(adapter);
                }else {
                    textViewNotDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    private void deleteData(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("plan").child(uId).child(transactionId);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.removeValue();
                if (databaseReference.removeValue().isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Berhasil hapus data", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Gagal hapus data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    private void makeIntent(Class destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
        finish();
    }
}
