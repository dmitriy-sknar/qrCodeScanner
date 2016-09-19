package com.ioLab.qrCodeScanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created by disknar on 10.08.2016.
 */
public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public final static String POSITION = "POSITION";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//         Inflate tab_layout and setup Views.
        View rootView =  inflater.inflate(R.layout.tab_layout, null);
        tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

//         Set an Apater for the View Pager
        viewPager.setAdapter(new AppFragmentPagerAdapter(getChildFragmentManager(), getContext()));

        //Todo verify work without runnable
//         The setupWithViewPager dose't works without the runnable .
//         Maybe a Support Library Bug .
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
        }
    }

}