package com.banksaku.banksaku.fragment;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.banksaku.banksaku.Alarm;
import com.banksaku.banksaku.R;
import com.banksaku.banksaku.activity.HomeActivity;
import com.banksaku.banksaku.model.transaction.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTransactionFragment extends Fragment {

    private int position;
    private EditText editTextAmount, editTextNote, editTextReminderDate, editTextReminderTime;
    private Spinner spinnerCategory;
    private TextView textViewReminder,textViewDate, textViewSelectDate;
    private Button buttonAddTransaction;
    private Toolbar toolbarTransaction;

    private Calendar calendar;
    private int day, month, year, hour, minute;

    private DatabaseReference databaseReference;
    private String id;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        AddTransactionFragment addTransaction = new AddTransactionFragment();
        addTransaction.setArguments(bundle);
        return addTransaction;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewSelectDate = view.findViewById(R.id.textViewSelectDate);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        editTextNote = view.findViewById(R.id.editTextNote);
        editTextReminderDate = view.findViewById(R.id.editTextReminderDate);
        editTextReminderTime = view.findViewById(R.id.editTextReminderTime);
        textViewReminder = view.findViewById(R.id.textViewReminder);
        buttonAddTransaction = view.findViewById(R.id.buttonAddTransaction);
        toolbarTransaction = view.findViewById(R.id.toolbarTransaction);
        toolbarTransaction.setVisibility(View.GONE);

        //Set up Spinner Category
        setUpSpinnerCategory();
        // Show Transaction
        showAddTransaction();

        //Set up Calendar When Edit Text Clicking
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        textViewSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog();
            }
        });

        editTextReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogReminder();
            }
        });

        editTextReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog();
            }
        });

        return view;
    }

    private void showAddTransaction() {
        if (position == 0) {
            contentGone();
            buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addTransaction("income");
                }
            });
        } else if (position == 1) {
            contentGone();
            buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addTransaction("expanse");
                }
            });
        } else if (position == 2) {
            buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addTransaction("plan");
                }
            });
        }
    }

    private void setUpSpinnerCategory() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.category_transaction, android.R.layout.simple_spinner_item);
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

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
        if (position == 2){
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }else {
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        dialog.show();
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

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
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

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, hour, minute, true);
        dialog.show();
    }

    private void addTransaction(final String root){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uId = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference(root).child(uId);
        final String textPrice = editTextAmount.getText().toString().trim();

        final String category = spinnerCategory.getSelectedItem().toString().trim();
        final String date = textViewDate.getText().toString().trim();
        final String note = editTextNote.getText().toString().trim();

        if (textPrice.equals("") || category.equals("") || date.equals("dd/mm/yyyy")|| note.equals("")){
            Toast.makeText(getContext(), "Form harus diisi semua", Toast.LENGTH_SHORT).show();
        }else {
//            String id = databaseReference.push().getKey();
            final long price = Long.parseLong(textPrice.replace(",",""));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        id = root+"-"+(dataSnapshot.getChildrenCount()+1);
                    }else {
                        id = root+"-"+(1);
                    }

                    Transaction transaction = null;
                    if (position == 2){
                        //Plan Transaction
                        String reminderDate = editTextReminderDate.getText().toString().trim();
                        String reminderTime = editTextReminderTime.getText().toString().trim();

                        if (reminderDate.equals("") || reminderTime.equals("")){
                            Toast.makeText(getContext(), "Form harus diisi semua", Toast.LENGTH_SHORT).show();
                        }else {
                            if (category.equals("Makanan")){
                                transaction = new Transaction(id,category,note,date,price,R.drawable.banksaku_category_food,uId,reminderDate,reminderTime);
                            }else if (category.equals("Pakaian")){
                                transaction = new Transaction(id,category,note,date,price,R.drawable.banksaku_category_dress,uId,reminderDate,reminderTime);
                            }else if (category.equals("Belanja")){
                                transaction = new Transaction(id,category,note,date,price,R.drawable.banksaku_category_shopping,uId,reminderDate,reminderTime);
                            }
                        }

                        Calendar current = Calendar.getInstance();
                        Calendar target = Calendar.getInstance();
                        target.set(year,month,day,hour,minute,00);

                        if (target.compareTo(current) <= 0){
                            Toast.makeText(getContext(), "Tanggal / Waktu salah", Toast.LENGTH_SHORT).show();
                        }else {
                            setAlarm(target);
                            Toast.makeText(getContext(), year+"-"+month+"-"+day+" "+hour+":"+minute, Toast.LENGTH_LONG).show();
                        }

                    }else {
                        if (category.equals("Makanan")){
                            transaction = new Transaction(id,category,note,date,price,R.drawable.banksaku_category_food,uId);
                        }else if (category.equals("Pakaian")){
                            transaction = new Transaction(id,category,note,date,price,R.drawable.banksaku_category_dress,uId);
                        }else if (category.equals("Belanja")){
                            transaction = new Transaction(id,category,note,date,price,R.drawable.banksaku_category_shopping,uId);
                        }
                    }

                    databaseReference.child(id).setValue(transaction);
                    Toast.makeText(getContext(), "Berhasil menambahkan data", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setAlarm(Calendar target){
        Toast.makeText(getActivity(), target.getTime()+"", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity().getBaseContext(), Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getBaseContext(),1,intent,0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,target.getTimeInMillis(),pendingIntent);
    }

    private void contentGone(){
        editTextReminderDate.setVisibility(View.GONE);
        editTextReminderTime.setVisibility(View.GONE);
        textViewReminder.setVisibility(View.GONE);
    }
}
