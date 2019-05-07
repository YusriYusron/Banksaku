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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.banksaku.banksaku.R;
import com.banksaku.banksaku.activity.AddTransactionActivity;
import com.banksaku.banksaku.activity.HomeActivity;
import com.banksaku.banksaku.activity.PlanActivity;
import com.banksaku.banksaku.model.transaction.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private Context context;
    private ArrayList<Transaction> dataPlan;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uId = user.getUid();

    public PlanAdapter(Context context, ArrayList<Transaction> dataPlan) {
        this.context = context;
        this.dataPlan = dataPlan;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan_list,viewGroup,false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder planViewHolder, int i) {
        final Transaction transaction = dataPlan.get(i);
        planViewHolder.imageViewImage.setImageResource(transaction.getImage());
        planViewHolder.textViewTitle.setText(transaction.getCategory());
        planViewHolder.textViewNote.setText(transaction.getNote());
        planViewHolder.textViewTime.setText(transaction.getTime());
        planViewHolder.textViewPrice.setText(String.format(Locale.getDefault(),"%,d",transaction.getPrice()));
        planViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeIntentData(transaction,AddTransactionActivity.class);
            }
        });
        planViewHolder.buttonDonePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeIntentData(transaction, PlanActivity.class);
            }
        });
        planViewHolder.buttonCancelPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData(transaction.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPlan.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewTitle,textViewNote,textViewTime,textViewPrice;
        final ImageView imageViewImage;
        final LinearLayout linearLayout;
        final Button buttonDonePlan,buttonCancelPlan;
        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titlePlan);
            textViewNote = itemView.findViewById(R.id.notePlan);
            textViewTime = itemView.findViewById(R.id.timePlan);
            textViewPrice = itemView.findViewById(R.id.pricePlan);
            imageViewImage = itemView.findViewById(R.id.imagePlan);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            buttonDonePlan = itemView.findViewById(R.id.buttonDonePlan);
            buttonCancelPlan = itemView.findViewById(R.id.buttonCancelPlan);
        }
    }

//    private void deleteData(String transactionId){
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("plan").child(uId).child(transactionId);
//        databaseReference.removeValue();
//        if (databaseReference.removeValue().isSuccessful()){
//            Toast.makeText(context, "Berhasil membatalkan rencana", Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show();
//        }
//    }
private void deleteData(String transactionId){
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("plan").child(uId).child(transactionId);

    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.delete);
    builder.setMessage(R.string.delete_message);
    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            databaseReference.removeValue();
            Toast.makeText(context, "Berhasil hapus data", Toast.LENGTH_SHORT).show();
            makeIntent(PlanActivity.class);
        }
    });
    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    }).create().show();
}

    private void makeIntentData(Transaction transaction, Class destination){
        Intent intent = new Intent(context,destination);
        intent.putExtra("transaction",transaction);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    private void makeIntent(Class destination){
        Intent intent = new Intent(context,destination);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
