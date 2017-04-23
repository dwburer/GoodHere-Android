package com.danielburer.goodhere;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabProfileFragmentAuthenticated extends Fragment {

    static ProfileFragmentListener profileListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        profileListener = (ProfileFragmentListener) args.get("listener");
        return inflater.inflate(R.layout.tab_profile_fragment_authenticated, container, false);
    }
}
