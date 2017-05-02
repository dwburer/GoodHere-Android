package com.danielburer.goodhere;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;

import java.io.Serializable;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final FragmentManager mFragmentManager;
    private int mNumOfTabs;

    private Fragment profileFragment;
    private Bundle profileFragmentArgs = new Bundle();
    private Context mContext;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mFragmentManager = fm;
    }

    /**
     * This is basically just used to toggle between the default and authenticated
     * profile fragments. Can probably streamline or abstract for wider use cases.
     */
    private final class ProfilePageListener implements ProfileFragmentListener, Serializable {
        public void onSwitchToNextFragment() {

            mFragmentManager.beginTransaction().remove(profileFragment).commit();
            if (profileFragment instanceof TabProfileFragmentDefault){
                profileFragment = new TabProfileFragmentAuthenticated();
                profileFragment.setArguments(profileFragmentArgs);
            }else {
                profileFragment = new TabProfileFragmentDefault();
                profileFragment.setArguments(profileFragmentArgs);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        switch (position) {
            case 0:
                return new TabSearchFragment();

            case 1:
                return new TabNearbyFragment();

            case 2:

                boolean authenticated = sharedPref.getBoolean(mContext.getString(R.string.client_authenticated_key), false);

                Log.d("adapter", authenticated + "");

                if (profileFragment == null) {
                    profileFragment = !authenticated ? new TabProfileFragmentDefault() : new TabProfileFragmentAuthenticated();
                    profileFragment.setArguments(profileFragmentArgs);
                }

                return profileFragment;
            default:
                return null;
        }
    }

    public void setContext(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}