package com.bleyl.recurrence.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.adapters.ReminderAdapter;
import com.bleyl.recurrence.utils.ReminderConstants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TabFragment extends Fragment {

    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.empty_text) TextView mEmptyText;
    @Bind(R.id.empty_view) LinearLayout mLinearLayout;
    @Bind(R.id.empty_icon) ImageView mImageView;

    private Activity mActivity;
    private ReminderAdapter mReminderAdapter;
    private List<Reminder> mReminderList;
    private int mRemindersType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        mActivity = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ButterKnife.bind(this, view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);

        mRemindersType = this.getArguments().getInt("TYPE");
        if (mRemindersType == ReminderConstants.INACTIVE) {
            mEmptyText.setText(R.string.no_inactive);
            mImageView.setImageResource(R.drawable.ic_notifications_off_black_empty);
        }

        mReminderList = getListData();
        mReminderAdapter = new ReminderAdapter(mActivity, R.layout.item_notification_list, mReminderList);
        mRecyclerView.setAdapter(mReminderAdapter);

        if (mReminderAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    public List<Reminder> getListData() {
        DatabaseHelper database = DatabaseHelper.getInstance(mActivity.getApplicationContext());
        List<Reminder> reminderList = database.getNotificationList(mRemindersType);
        database.close();
        return reminderList;
    }

    public void updateList() {
        mReminderList.clear();
        mReminderList.addAll(getListData());
        mReminderAdapter.notifyDataSetChanged();

        if (mReminderAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        updateList();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(messageReceiver);
        super.onPause();
    }
}