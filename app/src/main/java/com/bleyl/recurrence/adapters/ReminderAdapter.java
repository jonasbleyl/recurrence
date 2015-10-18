package com.bleyl.recurrence.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bleyl.recurrence.ui.fragments.TabFragment;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.utils.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private int mRowLayout;
    private Activity mActivity;
    private List<Reminder> mReminderList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mTime;
        public TextView mContent;
        public TextView mTextSeparator;
        public ImageView mIcon;
        public ImageView mCircle;
        public View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.notification_title);
            mTime = (TextView) view.findViewById(R.id.notification_time);
            mContent = (TextView) view.findViewById(R.id.notification_content);
            mTextSeparator = (TextView) view.findViewById(R.id.header_separator);
            mIcon = (ImageView) view.findViewById(R.id.notification_icon);
            mCircle = (ImageView) view.findViewById(R.id.notification_circle);
        }
    }

    public ReminderAdapter(Activity activity, int rowLayout, List<Reminder> reminderList) {
        mActivity = activity;
        mRowLayout = rowLayout;
        mReminderList = reminderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Calendar calendar = DateAndTimeUtil.parseDateAndTime(mReminderList.get(position).getDateAndTime());
        // Show header for item if it is the first in date group
        if (position > 0 && mReminderList.get(position).getDate().equals(mReminderList.get(position - 1).getDate()) ) {
            viewHolder.mTextSeparator.setVisibility(View.GONE);
        } else {
            String appropriateDate = DateAndTimeUtil.getAppropriateDateFormat(mActivity, calendar);
            viewHolder.mTextSeparator.setText(appropriateDate);
            viewHolder.mTextSeparator.setVisibility(View.VISIBLE);
        }

        viewHolder.mTitle.setText(mReminderList.get(position).getTitle());
        viewHolder.mContent.setText(mReminderList.get(position).getContent());
        viewHolder.mTime.setText(DateAndTimeUtil.toStringReadableTime(calendar, mActivity));
        int iconResId = mActivity.getResources().getIdentifier(mReminderList.get(position).getIcon(), "drawable", mActivity.getPackageName());
        viewHolder.mIcon.setImageResource(iconResId);
        GradientDrawable bgShape = (GradientDrawable) viewHolder.mCircle.getDrawable();
        bgShape.setColor(Color.parseColor(mReminderList.get(position).getColour()));

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    TabFragment fragment = new TabFragment();
                    fragment.startViewerActivity(mActivity, view, mReminderList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }
}