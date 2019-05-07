package com.banksaku.banksaku.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.banksaku.banksaku.R;
import com.banksaku.banksaku.activity.AddTransactionActivity;
import com.banksaku.banksaku.model.transaction.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private ArrayList<Transaction> dataTrasaction;

    public TransactionAdapter(Context context, ArrayList<Transaction> dataTrasaction) {
        this.dataTrasaction = dataTrasaction;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_list,viewGroup,false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder transactionViewHolder, final int i) {
        final Transaction transaction = dataTrasaction.get(i);
        transactionViewHolder.imageViewImage.setImageResource(transaction.getImage());
        transactionViewHolder.textViewTitle.setText(transaction.getCategory());
        transactionViewHolder.textViewNote.setText(transaction.getNote());
        transactionViewHolder.textViewTime.setText(transaction.getTime());
        transactionViewHolder.textViewPrice.setText(String.format(Locale.getDefault(),"%,d",transaction.getPrice()));
        transactionViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeIntentData(transaction,AddTransactionActivity.class);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataTrasaction.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewTitle,textViewNote,textViewTime,textViewPrice;
        final ImageView imageViewImage;
        final LinearLayout linearLayout;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleTransaction);
            textViewNote = itemView.findViewById(R.id.noteTransaction);
            textViewTime = itemView.findViewById(R.id.timeTransaction);
            textViewPrice = itemView.findViewById(R.id.priceTransaction);
            imageViewImage = itemView.findViewById(R.id.imageTransaction);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }

    private void makeIntentData(Transaction transaction, Class destination){
        Intent intent = new Intent(context,destination);
        intent.putExtra("transaction",transaction);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}