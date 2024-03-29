package com.danielburer.goodhere;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final FragmentManager mFragmentManager;
    private int mNumOfTabs;

    private ProfilePageListener listener;
    private Fragment profileFragment;
    private Bundle profileFragmentArgs = new Bundle();
    private Context mContext;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mFragmentManager = fm;
        listener = new ProfilePageListener();
    }

    /**
     * This is basically just used to toggle between the default and authenticated
     * profile fragments. Can probably streamline or abstract for wider use cases.
     */
    public final class ProfilePageListener implements ProfileFragmentListener {
        public void onSwitchToNextFragment() {

            mFragmentManager.beginTransaction().remove(profileFragment).commit();
            if (profileFragment instanceof TabProfileFragmentDefault){
                profileFragment = new TabProfileFragmentAuthenticated(listener);
            }else {
                profileFragment = new TabProfileFragmentDefault(listener);
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
                boolean authenticated = sharedPref.getBoolean(mContext.getString(R.string.client_authenticated_key), false);

                if (profileFragment == null) {
                    profileFragment = !authenticated ? new TabProfileFragmentDefault(listener) : new TabProfileFragmentAuthenticated(listener);
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