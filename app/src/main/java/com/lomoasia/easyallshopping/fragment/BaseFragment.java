package com.lomoasia.easyallshopping.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by asia on 2018/1/12.
 */

public class BaseFragment extends Fragment {

    private boolean isViewFirstAppear = true;
    protected Context context;
    protected Activity activity;

    public BaseFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
    }

    public String getFragmentName() {
        return getClass().getName();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                onViewAppear();
            } else {
                onViewDisappear();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onViewAppear();
        } else {
            onViewDisappear();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onViewDisappear();
    }

    public void onViewAppear() {
        if (isViewFirstAppear) {
            onViewFirstAppear();
            isViewFirstAppear = false;
        }
    }

    public void onViewFirstAppear() {
    }

    public void onViewDisappear() {
    }

}
