package com.bleyl.recurrence.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bleyl.recurrence.ui.fragment.ActiveTabFragment;
import com.bleyl.recurrence.ui.fragment.InactiveTabFragment;
import com.bleyl.recurrence.model.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.util.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private int mRowLayout;
    private Context mContext;
    private List<Notification> mNotificationList;
    private String mListType;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mContent;
        public TextView mTextSeparator;
        public View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.title);
            mContent = (TextView) view.findViewById(R.id.content);
            mTextSeparator = (TextView) view.findViewById(R.id.header_separator);
        }
    }

    public NotificationAdapter(Context context, int rowLayout, List<Notification> notificationList, String listType) {
        mContext = context;
        mRowLayout = rowLayout;
        mNotificationList = notificationList;
        mListType = listType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Show sticky header for item if it is the first in date group
        if (position > 0 && mNotificationList.get(position).getDate().equals(mNotificationList.get(position - 1).getDate()) ) {
            viewHolder.mTextSeparator.setVisibility(View.GONE);
        } else {
            viewHolder.mTextSeparator.setVisibility(View.VISIBLE);
            // Parse date and get appropriate date format
            Calendar DateAndTime = DateAndTimeUtil.parseDateAndTime(mNotificationList.get(position).getDateAndTime());
            String appropriateDate = DateAndTimeUtil.getAppropriateDateFormat(DateAndTime);
            if (appropriateDate.equals("TODAY")) {
                appropriateDate = mContext.getResources().getString(R.string.date_today);
            }
            viewHolder.mTextSeparator.setText(appropriateDate);
        }

        viewHolder.mTitle.setText(mNotificationList.get(position).getTitle());
        viewHolder.mContent.setText(mNotificationList.get(position).getContent());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListType.equals("ACTIVE")) {
                    ActiveTabFragment fragment = new ActiveTabFragment();
                    fragment.startViewerActivity(mContext, v, position);
                } else if (mListType.equals("INACTIVE")) {
                    InactiveTabFragment fragment = new InactiveTabFragment();
                    fragment.startViewerActivity(mContext, v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }
}