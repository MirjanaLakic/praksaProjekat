package com.example.android.moneymanagerapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.fragments.DailyFragment;
import com.example.android.fragments.MonthlyFragment;
import com.example.android.fragments.WeeklyFragment;
import com.example.android.fragments.YearlyFragment;

public class Pager extends FragmentStatePagerAdapter {

    int tabCount;

    public Pager(FragmentManager fragmentManager, int tabCount){
        super(fragmentManager);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                DailyFragment daily = new DailyFragment();
                return daily;
            case 1:
                WeeklyFragment weekly = new WeeklyFragment();
                return weekly;
            case 2:
                MonthlyFragment monthly = new MonthlyFragment();
                return monthly;
            case 3:
                YearlyFragment yearly = new YearlyFragment();
                return yearly;
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
