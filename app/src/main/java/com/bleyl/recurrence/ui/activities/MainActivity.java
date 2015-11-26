package com.bleyl.recurrence.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.adapters.ViewPageAdapter;
import com.bleyl.recurrence.interfaces.RecyclerCallback;
import com.bleyl.recurrence.receivers.BootReceiver;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements RecyclerCallback {

    @Bind(R.id.tabs) PagerSlidingTabStrip mPagerSlidingTabStrip;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.fab_button) FloatingActionButton mFloatingActionButton;

    private boolean mFabIsHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), getApplicationContext());
        mViewPager.setAdapter(adapter);

        mPagerSlidingTabStrip.setViewPager(mViewPager);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        SharedPreferences sharedPreferences = getSharedPreferences("first_run_preferences", Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && sharedPreferences.getBoolean("FirstRun", true)) {
            sharedPreferences.edit().putBoolean("FirstRun", false).apply();
            Intent intent = new Intent().setClass(this, BootReceiver.class);
            sendBroadcast(intent);
        }
    }

    @OnClick(R.id.fab_button)
    public void fabClicked() {
        Intent intent = new Intent(this, CreateEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void hideFab() {
        mFloatingActionButton.hide();
        mFabIsHidden = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFabIsHidden) {
            mFloatingActionButton.show();
            mFabIsHidden = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent preferenceIntent = new Intent(this, PreferenceActivity.class);
                startActivity(preferenceIntent);
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}