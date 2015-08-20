package com.bleyl.recurrence.ui.fragment;

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

import com.bleyl.recurrence.model.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.adapter.NotificationAdapter;
import com.bleyl.recurrence.database.Database;
import com.bleyl.recurrence.ui.MainActivity;
import com.bleyl.recurrence.ui.ViewActivity;

import java.util.List;

public class ActiveTabFragment extends Fragment {

    private static Activity sParentActivity;
    private RecyclerView mRecyclerView;
    private TextView mEmptyText;
    private NotificationAdapter mNotificationAdapter;
    private List<Notification> mNotificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        sParentActivity = getActivity();
        LocalBroadcastManager.getInstance(sParentActivity).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mEmptyText = (TextView) view.findViewById(R.id.empty_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(sParentActivity);
        mRecyclerView.setLayoutManager(layoutManager);

        Database database = new Database(sParentActivity.getApplicationContext());
        mNotificationList = database.getActiveNotifications();
        mNotificationAdapter = new NotificationAdapter(sParentActivity, R.layout.item_notification_list, mNotificationList, "ACTIVE");
        database.close();
        mRecyclerView.setAdapter(mNotificationAdapter);

        if (mNotificationAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    };

    public void updateList() {
        Database database = new Database(sParentActivity.getApplicationContext());
        mNotificationList.clear();
        mNotificationList.addAll(database.getActiveNotifications());
        database.close();
        mNotificationAdapter.notifyDataSetChanged();

        if (mNotificationAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
        }
    }

    public void startViewerActivity(Context context, View view, int position) {
        Database database = new Database(context.getApplicationContext());
        List<Notification> notificationList = database.getActiveNotifications();
        database.close();

        Intent intent = new Intent(sParentActivity, ViewActivity.class);
        intent.putExtra("NOTIFICATION_ID", notificationList.get(position).getId());

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

            sParentActivity.getWindow().setSharedElementsUseOverlay(false);
            sParentActivity.getWindow().setReenterTransition(null);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(sParentActivity, cardView, "cardTransition");
            ActivityCompat.startActivity(sParentActivity, intent, options.toBundle());

            ((MainActivity)view.getContext()).hideFab();
        } else {
            view.getContext().startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(sParentActivity).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }
}