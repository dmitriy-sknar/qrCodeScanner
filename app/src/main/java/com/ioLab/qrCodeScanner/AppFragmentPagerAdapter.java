package com.ioLab.qrCodeScanner;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.num.barcodescannerproject.R;

/**
 * Created by disknar on 01.08.2016.
 */
public class AppFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    String tabOneTitle;
    String tabTwoTitle;
    private Context mContext;

    public AppFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        tabOneTitle = mContext.getResources().getString(R.string.tab_one_title);
        tabTwoTitle = mContext.getResources().getString(R.string.tab_two_title);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ScanFragment.newInstance(position);
            case 1:
                return HistoryFragment.newInstance(position);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return tabOneTitle;
            case 1:
                return tabTwoTitle;
            default:
                return null;
        }
    }
}