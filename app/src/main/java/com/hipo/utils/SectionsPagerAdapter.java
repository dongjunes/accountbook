package com.hipo.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.hipo.fragment.ChartFragment;
import com.hipo.fragment.ListFragment;
import com.hipo.fragment.MapFragment;
import com.hipo.fragment.SettingFragment;
import com.hipo.model.pojo.UserVo;

/**
 * Created by dongjune on 2017-04-25.
 */

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private UserVo vo;
    private ListFragment listFragment;

    public SectionsPagerAdapter(FragmentManager fm, UserVo vo) {
        super(fm);
        this.vo = vo;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return listFragment = ListFragment.newInstance(position + 1, vo);
            case 1:
                return ChartFragment.newInstance(position + 2);
            case 2:
                return MapFragment.newInstance(position + 3);
            case 3:
                return SettingFragment.newInstance(position + 4);
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public void reflashList() {
        Log.d("되냐?", "ㅇㅇ");
        listFragment.reflashing();
    }

}
