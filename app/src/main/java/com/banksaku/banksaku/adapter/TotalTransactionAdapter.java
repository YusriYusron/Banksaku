package com.banksaku.banksaku.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.banksaku.banksaku.R;
import com.banksaku.banksaku.model.transaction.TotalTransaction;

import java.util.ArrayList;
import java.util.Locale;

public class TotalTransactionAdapter extends RecyclerView.Adapter<TotalTransactionAdapter.TotalTransactionViewHolder> {

    private Context context;
    private ArrayList<TotalTransaction> dataTotalTransaction;

    public TotalTransactionAdapter(Context context, ArrayList<TotalTransaction> dataTotalTransaction) {
        this.context = context;
        this.dataTotalTransaction = dataTotalTransaction;
    }

    @NonNull
    @Override
    public TotalTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_total_transaction_list,viewGroup,false);
        return new TotalTransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TotalTransactionViewHolder totalTransactionViewHolder, int i) {
        TotalTransaction totalTransaction = dataTotalTransaction.get(i);
        totalTransactionViewHolder.textViewTypeTransaction.setText(totalTransaction.getTypeTransaction());
        totalTransactionViewHolder.textViewTotalTransaction.setText(String.format(Locale.getDefault(),"%,d",totalTransaction.getTotalTransaction()));
    }

    @Override
    public int getItemCount() {
        return dataTotalTransaction.size();
    }

    public class TotalTransactionViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewTypeTransaction,textViewTotalTransaction;
        public TotalTransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTypeTransaction = itemView.findViewById(R.id.textViewTypeTransaction);
            textViewTotalTransaction = itemView.findViewById(R.id.textViewTotalTransaction);
        }
    }
}
