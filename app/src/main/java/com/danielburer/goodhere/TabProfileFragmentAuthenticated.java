package com.danielburer.goodhere;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabProfileFragmentAuthenticated extends Fragment {

    static FirstPageFragmentListener firstPageListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        firstPageListener = (FirstPageFragmentListener) args.get("listener");
        return inflater.inflate(R.layout.tab_profile_fragment_authenticated, container, false);
    }

//    firstPageListener.onSwitchToNextFragment();
}
