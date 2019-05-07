package com.banksaku.banksaku;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.banksaku.banksaku.activity.CallActivity;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context, CallActivity.class);
        context.startActivity(intent);
    }
}
