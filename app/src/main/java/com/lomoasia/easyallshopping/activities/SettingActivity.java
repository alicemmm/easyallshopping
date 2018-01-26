package com.lomoasia.easyallshopping.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lomoasia.easyallshopping.R;
import com.lomoasia.easyallshopping.fragment.SettingsFragment;

/**
 * Created by asia on 2018/1/12.
 */

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(R.string.action_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_view, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.right_in, R.anim.stable);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stable, R.anim.right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
