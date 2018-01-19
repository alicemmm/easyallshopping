package com.lomoasia.easyallshopping.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lomoasia.easyallshopping.R;

/**
 * Created by asia on 2018/1/18.
 */

public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
