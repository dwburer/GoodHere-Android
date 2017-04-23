package com.danielburer.goodhere;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.io.Serializable;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mFragmentManager = fm;
        args.putSerializable("listener", listener);
    }

    private final class FirstPageListener implements FirstPageFragmentListener, Serializable {
        public void onSwitchToNextFragment() {

            mFragmentManager.beginTransaction().remove(profileFragment).commit();
            if (profileFragment instanceof TabProfileFragmentDefault){
                profileFragment = new TabProfileFragmentAuthenticated();
                profileFragment.setArguments(args);
            }else {
                profileFragment = new TabProfileFragmentDefault();
                profileFragment.setArguments(args);
            }
            notifyDataSetChanged();
        }
    }

    private FirstPageListener listener = new FirstPageListener();
    private final FragmentManager mFragmentManager;
    private Fragment profileFragment;
    private Bundle args = new Bundle();

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabSearchFragment tab1 = new TabSearchFragment();
                return tab1;
            case 1:
                TabNearbyFragment tab2 = new TabNearbyFragment();
                return tab2;
            case 2:
                if (profileFragment == null)
                {
                    profileFragment = new TabProfileFragmentDefault();
                    profileFragment.setArguments(args);
                }
                return profileFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object)
    {
        if (object instanceof TabProfileFragmentDefault &&
                profileFragment instanceof TabProfileFragmentAuthenticated) {
            return POSITION_NONE;
        }
        if (object instanceof TabProfileFragmentAuthenticated &&
                profileFragment instanceof TabProfileFragmentDefault) {
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
    }
}