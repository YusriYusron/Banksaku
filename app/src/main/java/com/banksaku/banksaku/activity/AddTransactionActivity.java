package com.banksaku.banksaku.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.banksaku.banksaku.Alarm;
import com.banksaku.banksaku.R;
import com.banksaku.banksaku.adapter.AddTransactionAdapter;
import com.banksaku.banksaku.model.transaction.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageViewArrowBack,imageViewDelete;
    private EditText editTextAmount,editTextNote,editTextReminderDate,editTextReminderTime;
    private TextView titleToolbar,textViewDate,textViewSelectDate,textViewReminder;
    private Spinner spinnerCategory;
    private Button buttonAddTransaction;

    private ArrayAdapter<CharSequence> adapter;
    private Transaction getData;

    private Calendar calendar;
    private int day, month, year, hour, minute;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uId = user.getUid();

    private String transactionId;
    private String[] root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getData = getIntent().getParcelableExtra("transaction");

        if (getData == null){
            setContentView(R.layout.activity_add_transaction);

            tabLayout = findViewById(R.id.tabLayoutAddTransaction);
            viewPager = findViewById(R.id.viewPagerAddTransaction);

            AddTransactionAdapter adapter = new AddTransactionAdapter(getSupportFragmentManager());

            tabLayout.setupWithViewPager(viewPager);
            viewPager.setAdapter(adapter);
        }else {
            setContentView(R.layout.fragment_add_transaction);
            transactionId = getData.getId();
            //Split String "income-1" -> income
            root = transactionId.split("-");

            titleToolbar = findViewById(R.id.titleToolbar);
            titleToolbar.setText("Ubah Transaksi");

            editTextAmount = findViewById(R.id.editTextAmount);
            editTextAmount.setText(getData.getPrice()+"");

            spinnerCategory = findViewById(R.id.spinnerCategory);

            textViewDate = findViewById(R.id.textViewDate);
            textViewSelectDate = findViewById(R.id.textViewSelectDate);
            editTextNote = findViewById(R.id.editTextNote);
            editTextReminderDate = findViewById(R.id.editTextReminderDate);
            editTextReminderDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePickerDialogReminder();
                }
            });
            editTextReminderTime = findViewById(R.id.editTextReminderTime);
            editTextReminderTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timePickerDialog();
                }
            });
            textViewReminder = findViewById(R.id.textViewReminder);
            buttonAddTransaction = findViewById(R.id.buttonAddTransaction);
            buttonAddTransaction.setText("Ubah");
            buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateData();
                }
            });
            imageViewDelete = findViewById(R.id.imageViewDelete);
            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteData();
                }
            });

            if (!root[0].equals("plan")){
                editTextReminderDate.setVisibility(View.GONE);
                editTextReminderTime.setVisibility(View.GONE);
                textViewReminder.setVisibility(View.GONE);
            }else {
                editTextReminderDate.setVisibility(View.VISIBLE);
                editTextReminderTime.setVisibility(View.VISIBLE);
                textViewReminder.setVisibility(View.VISIBLE);
                editTextReminderDate.setText(getData.getReminderDate());
                editTextReminderTime.setText(getData.getReminderTime());
            }

            //Setup Spinner Category
            setUpSpinnerCategory();
            spinnerCategory.setSelection(adapter.getPosition(getData.getCategory()));
            textViewDate.setText(getData.getTime());
            editTextNote.setText(getData.getNote());
            textViewSelectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePickerDialog();
                }
            });

            //Set up Calendar When Edit Text Clicking
            calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            hour = calendar.get(Calendar.HOUR);
            minute = calendar.get(Calendar.MINUTE);
        }

        imageViewArrowBack = findViewById(R.id.imageViewArrowBack);
        imageViewArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeIntent(HomeActivity.class);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (root[0].equals("plan")){
            makeIntent(PlanActivity.class);
        }else {
            makeIntent(HomeActivity.class);
        }
        super.onBackPressed();
    }

    private void setUpSpinnerCategory() {
        adapter = ArrayAdapter.createFromResource(this, R.array.category_transaction, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(adapter);
    }

    private void datePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                textViewDate.setText(day + "/" + month + "/" + year);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateData(){
        String category = spinnerCategory.getSelectedItem().toString();
        String note = editTextNote.getText().toString();
        String date = textViewDate.getText().toString();
        long price = Long.parseLong(editTextAmount.getText().toString().trim().replace(",",""));


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(root[0]).child(uId).child(transactionId);

        Transaction transaction = null;
        if (root[0].equals("plan")){
            //Plan Transaction
            String reminderDate = editTextReminderDate.getText().toString().trim();
            String reminderTime = editTextReminderTime.getText().toString().trim();

            if (reminderDate.equals("") || reminderTime.equals("")){
                Toast.makeText(this, "Form harus diisi semua", Toast.LENGTH_SHORT).show();
            }else {
                if (category.equals("Makanan")){
                    transaction = new Transaction(transactionId,category,note,date,price,R.drawable.banksaku_category_food,uId,reminderDate,reminderTime);
                }else if (category.equals("Pakaian")){
                    transaction = new Transaction(transactionId,category,note,date,price,R.drawable.banksaku_category_dress,uId,reminderDate,reminderTime);
                }else if (category.equals("Belanja")){
                    transaction = new Transaction(transactionId,category,note,date,price,R.drawable.banksaku_category_shopping,uId,reminderDate,reminderTime);
                }
            }

            Calendar current = Calendar.getInstance();
            Calendar target = Calendar.getInstance();
            target.set(year,month,day,hour,minute,00);

            if (target.compareTo(current) <= 0){
                Toast.makeText(this, "Tanggal / Waktu salah", Toast.LENGTH_SHORT).show();
            }else {
                setAlarm(target);
                Toast.makeText(this, year+"-"+month+"-"+day+" "+hour+":"+minute, Toast.LENGTH_LONG).show();
            }
        }else {
            if (category.equals("Makanan")){
                transaction = new Transaction(transactionId,category,note,date,price,R.drawable.banksaku_category_food,uId);
            }else if (category.equals("Pakaian")){
                transaction = new Transaction(transactionId,category,note,date,price,R.drawable.banksaku_category_dress,uId);
            }else if (category.equals("Belanja")){
                transaction = new Transaction(transactionId,category,note,date,price,R.drawable.banksaku_category_shopping,uId);
            }
        }

        databaseReference.setValue(transaction);
        Toast.makeText(this, "Berhasil Ubah Data", Toast.LENGTH_SHORT).show();
        makeIntent(HomeActivity.class);
    }

    private void deleteData(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(root[0]).child(uId).child(transactionId);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.removeValue();

                Toast.makeText(AddTransactionActivity.this, "Berhasil hapus data", Toast.LENGTH_SHORT).show();
                makeIntent(HomeActivity.class);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    private void datePickerDialogReminder() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;
                editTextReminderDate.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void timePickerDialog() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                editTextReminderTime.setText(hour + ":" + minute);
            }
        };

        TimePickerDialog dialog = new TimePickerDialog(this, listener, hour, minute, true);
        dialog.show();
    }

    private void setAlarm(Calendar target){
        Toast.makeText(this, target.getTime()+"", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this.getBaseContext(), Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getBaseContext(),1,intent,0);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getBaseContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,target.getTimeInMillis(),pendingIntent);
    }

    private void makeIntent(Class destination){
        Intent intent = new Intent(AddTransactionActivity.this, destination);
        startActivity(intent);
        finish();
    }
}
