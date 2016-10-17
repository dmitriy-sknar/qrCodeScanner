package com.ioLab.qrCodeScanner.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.ioLab.qrCodeScanner.R;

public class AppFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 3;
    private String tabOneTitle;
    private String tabTwoTitle;
    private String tabThreeTitle;
    private Context mContext;

    public SparseArray getSparseArray() {
        return sparseArray;
    }

    private SparseArray sparseArray = new SparseArray();

    public AppFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        tabOneTitle = mContext.getResources().getString(R.string.tab_one_title);
        tabTwoTitle = mContext.getResources().getString(R.string.tab_two_title);
        tabThreeTitle = mContext.getResources().getString(R.string.tab_one_title);

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ScanFragmentEmb.newInstance(position);
            case 1:
                return HistoryFragment.newInstance(position);
            case 2:
                return ScanFragment.newInstance(position);
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
            case 2:
                return tabThreeTitle;
            default:
                return null;
        }
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // get the tags set by FragmentPagerAdapter
        switch (position) {
            case 0:
                String firstTag = createdFragment.getTag();
                sparseArray.put(0, firstTag);
                break;
            case 1:
                String secondTag = createdFragment.getTag();
                sparseArray.put(1, secondTag);
                break;
            case 2:
                String thirdTag = createdFragment.getTag();
                sparseArray.put(2, thirdTag);
                break;
        }

        return createdFragment;
    }

}