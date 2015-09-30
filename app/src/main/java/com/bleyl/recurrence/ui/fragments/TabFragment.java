package com.bleyl.recurrence.ui.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.enums.NotificationsType;
import com.bleyl.recurrence.interfaces.RecyclerCallback;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.adapters.NotificationAdapter;
import com.bleyl.recurrence.ui.activities.ViewActivity;

import java.util.List;

public class TabFragment extends Fragment {

    private static Activity sActivity;
    private RecyclerView mRecyclerView;
    private TextView mEmptyText;
    private NotificationAdapter mNotificationAdapter;
    private List<Notification> mNotificationList;
    private NotificationsType mNotificationsType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        sActivity = getActivity();
        LocalBroadcastManager.getInstance(sActivity).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mEmptyText = (TextView) view.findViewById(R.id.empty_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(sActivity);
        mRecyclerView.setLayoutManager(layoutManager);

        mNotificationsType = (NotificationsType) this.getArguments().get("TYPE");
        if (mNotificationsType == NotificationsType.INACTIVE) {
            mEmptyText.setText(getResources().getString(R.string.no_inactive));
        }

        mNotificationList = getListData();
        mNotificationAdapter = new NotificationAdapter(sActivity, R.layout.item_notification_list, mNotificationList);
        mRecyclerView.setAdapter(mNotificationAdapter);

        if (mNotificationAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    public List<Notification> getListData() {
        DatabaseHelper database = DatabaseHelper.getInstance(sActivity.getApplicationContext());
        List<Notification> notificationList = database.getNotificationList(mNotificationsType);
        database.close();
        return notificationList;
    }

    public void updateList() {
        mNotificationList.clear();
        mNotificationList.addAll(getListData());
        mNotificationAdapter.notifyDataSetChanged();

        if (mNotificationAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }
    }

    public void startViewerActivity(View view, Notification notification) {
        Intent intent = new Intent(sActivity, ViewActivity.class);
        intent.putExtra("NOTIFICATION_ID", notification.getId());

        // Add shared element transition animation if on Lollipop or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CardView cardView = (CardView) view.findViewById(R.id.card_view);

            TransitionSet setExit = new TransitionSet();
            Transition transition = new Fade();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            transition.excludeTarget(android.R.id.navigationBarBackground, true);
            transition.excludeTarget(R.id.fab_button, true);
            transition.excludeTarget(R.id.recycler_view, true);
            transition.setDuration(400);
            setExit.addTransition(transition);

            sActivity.getWindow().setSharedElementsUseOverlay(false);
            sActivity.getWindow().setReenterTransition(null);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(sActivity, cardView, "cardTransition");
            ActivityCompat.startActivity(sActivity, intent, options.toBundle());

            ((RecyclerCallback) sActivity).hideFab();
        } else {
            view.getContext().startActivity(intent);
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
        super.onResume();
        updateList();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(sActivity).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }
}