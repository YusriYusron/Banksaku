package com.banksaku.banksaku.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.banksaku.banksaku.fragment.AddTransactionFragment;

public class AddTransactionAdapter extends FragmentPagerAdapter {

    private String[] tabLayoutTitle = {"Pemasukan","Pengeluaran","Rencana"};

    public AddTransactionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return AddTransactionFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return tabLayoutTitle.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabLayoutTitle[position];
    }
}
