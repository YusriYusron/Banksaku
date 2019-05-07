package com.banksaku.banksaku.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.banksaku.banksaku.R;
import com.banksaku.banksaku.adapter.TotalTransactionAdapter;
import com.banksaku.banksaku.adapter.TransactionAdapter;
import com.banksaku.banksaku.model.transaction.TotalTransaction;
import com.banksaku.banksaku.model.transaction.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner spinnerDate,spinnerTransaction;
    private RecyclerView recyclerViewTransaction, recyclerViewTotalTransaction;
    private ProgressDialog progressDialog;
    private TextView textViewNotDataFound;

    private ArrayList<TotalTransaction> dataTotalTransaction;
    private ArrayList<Transaction> dataTransaction;

    private FirebaseUser user;
    private String uId;
    private String email;
    private String[] name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Setup User
        user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        email = user.getEmail();

        //Setup Progress Dialog
        progressDialog = new ProgressDialog(this);
        textViewNotDataFound = findViewById(R.id.textViewNotDataFound);
        recyclerViewTransaction = findViewById(R.id.recyclerViewTransaction);
        recyclerViewTotalTransaction = findViewById(R.id.recyclerViewTotalTransaction);

        dataTotalTransaction = new ArrayList<>();
        dataTransaction = new ArrayList<>();

        //Set Up Navigation Drawer
        setUpNavigationDrawer();
        //Set Up Spinner
        setUpSpinnerDate();
        //Set Up RecyclerView Transaction
        setUpRecyclerViewTransaction();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // This Activity
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_chart) {

        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_plan) {
            makeIntent(PlanActivity.class);
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

    private void setUpNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeIntent(AddTransactionActivity.class);
            }
        });

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

    private void setUpSpinnerDate() {
        spinnerDate = findViewById(R.id.spinnerDate);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.date, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDate.setAdapter(adapter);
//        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                String selectedItem = adapterView.getItemAtPosition(position).toString();
//                if (selectedItem.equals("Januari")){
//                    setUpRecyclerViewTransaction("income","Total Pemasukan");
//                }else if (selectedItem.equals("Februari")){
//                    setUpRecyclerViewTransaction("expanse","Total Pengeluaran");
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    private void setUpRecyclerViewTransaction() {
        // Set Default Total Transaction
        defaultTotalTransaction();

        //Show Progress Dialog
        progressDialog.setMessage("Silahkan tunggu");
        progressDialog.show();

        //Get Root in Firebase
        final DatabaseReference databaseIncome = FirebaseDatabase.getInstance().getReference("income").child(uId);
        //Sync Offline Mode
        databaseIncome.keepSynced(true);

        final long[] price = {0};
        final String[] id = new String[1];
        databaseIncome.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Hide Progress Dialog
                progressDialog.dismiss();
                //Get data from Root Firebase
                if (dataSnapshot.exists()){
                    textViewNotDataFound.setVisibility(View.GONE);
                    for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()){
                        Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                        price[0] += transaction.getPrice();
                        id[0] = transaction.getId();
                        //Add to ArrayList from Firebase
                        dataTransaction.add(transaction);
                    }
                    TransactionAdapter adapter = new TransactionAdapter(HomeActivity.this, dataTransaction);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);

                    recyclerViewTransaction.setLayoutManager(layoutManager);
                    recyclerViewTransaction.setAdapter(adapter);

                    dataTotalTransaction.set(0,new TotalTransaction(id[0], "Total Pemasukan",price[0]));

                    TotalTransactionAdapter totalTransactionAdapter = new TotalTransactionAdapter(HomeActivity.this, dataTotalTransaction);
                    RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);

                    recyclerViewTotalTransaction.setLayoutManager(linearLayoutManager);
                    recyclerViewTotalTransaction.setAdapter(totalTransactionAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Get Root in Firebase
        final DatabaseReference databaseExpanse = FirebaseDatabase.getInstance().getReference("expanse").child(uId);
        databaseExpanse.keepSynced(true);

        final long[] priceExpanse = {0};
        databaseExpanse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Hide Progress Dialog
                progressDialog.dismiss();
                //Get data from Root Firebase
                if (dataSnapshot.exists()){
                    textViewNotDataFound.setVisibility(View.GONE);
                    for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()){
                        Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                        priceExpanse[0] += transaction.getPrice();
//                        id[0] = transaction.getId();
                        //Add to ArrayList from Firebase
                        dataTransaction.add(transaction);
                    }
                    TransactionAdapter adapter = new TransactionAdapter(HomeActivity.this, dataTransaction);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);

                    recyclerViewTransaction.setLayoutManager(layoutManager);
                    recyclerViewTransaction.setAdapter(adapter);

                    dataTotalTransaction.set(1,new TotalTransaction(id[0], "Total Pengeluaran",priceExpanse[0]));

                    TotalTransactionAdapter totalTransactionAdapter = new TotalTransactionAdapter(HomeActivity.this, dataTotalTransaction);
                    RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);

                    recyclerViewTotalTransaction.setLayoutManager(linearLayoutManager);
                    recyclerViewTotalTransaction.setAdapter(totalTransactionAdapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Get Root in Firebase
        final DatabaseReference databasePlan = FirebaseDatabase.getInstance().getReference("plan").child(uId);
        databasePlan.keepSynced(true);

        final long[] pricePlan = {0};
        databasePlan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Hide Progress Dialog
                progressDialog.dismiss();
                //Get data from Root Firebase
                if (dataSnapshot.exists()){
                    textViewNotDataFound.setVisibility(View.GONE);
                    for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()){
                        Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                        pricePlan[0] += transaction.getPrice();
                        id[0] = transaction.getId();
                        //Add to ArrayList from Firebase
//                        dataTransaction.add(transaction);
                    }
//                    TransactionAdapter adapter = new TransactionAdapter(HomeActivity.this, dataTransaction);
//                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);
//
//                    recyclerViewTransaction.setLayoutManager(layoutManager);
//                    recyclerViewTransaction.setAdapter(adapter);

                    dataTotalTransaction.set(2,new TotalTransaction(id[0], "Total Rencana Anggaran",pricePlan[0]));

                    TotalTransactionAdapter totalTransactionAdapter = new TotalTransactionAdapter(HomeActivity.this, dataTotalTransaction);
                    RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);

                    recyclerViewTotalTransaction.setLayoutManager(linearLayoutManager);
                    recyclerViewTotalTransaction.setAdapter(totalTransactionAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void defaultTotalTransaction(){
        String[] arrayTotalTransaction = {"Total Pemasukan","Total Pengeluaran","Total Rencana Anggaran"};
        for (int i = 0; i < arrayTotalTransaction.length; i++) {
            dataTotalTransaction.add(new TotalTransaction(i+"",arrayTotalTransaction[i],0));
        }

        TotalTransactionAdapter adapter = new TotalTransactionAdapter(this,dataTotalTransaction);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewTotalTransaction.setLayoutManager(linearLayoutManager);
        recyclerViewTotalTransaction.setAdapter(adapter);

    }

    private void makeIntent(Class destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
        finish();
    }
}