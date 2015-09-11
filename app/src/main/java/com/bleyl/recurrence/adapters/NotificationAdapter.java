package com.bleyl.recurrence.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bleyl.recurrence.ui.fragments.TabFragment;
import com.bleyl.recurrence.models.Notification;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.utils.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private int mRowLayout;
    private Context mContext;
    private List<Notification> mNotificationList;
    private TypedArray mIconsArray;
    private String[] mColoursArray;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mContent;
        public TextView mTextSeparator;
        public ImageView mIcon;
        public ImageView mCircle;
        public View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.title);
            mContent = (TextView) view.findViewById(R.id.content);
            mTextSeparator = (TextView) view.findViewById(R.id.header_separator);
            mIcon = (ImageView) view.findViewById(R.id.image);
            mCircle = (ImageView) view.findViewById(R.id.circle);
        }
    }

    public NotificationAdapter(Context context, int rowLayout, List<Notification> notificationList, TypedArray iconsArray, String[] coloursArray) {
        mContext = context;
        mRowLayout = rowLayout;
        mNotificationList = notificationList;
        mIconsArray = iconsArray;
        mColoursArray = coloursArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Show header for item if it is the first in date group
        if (position > 0 && mNotificationList.get(position).getDate().equals(mNotificationList.get(position - 1).getDate()) ) {
            viewHolder.mTextSeparator.setVisibility(View.GONE);
        } else {
            // Parse date and get appropriate date format
            Calendar DateAndTime = DateAndTimeUtil.parseDateAndTime(mNotificationList.get(position).getDateAndTime());
            String appropriateDate = DateAndTimeUtil.getAppropriateDateFormat(mContext, DateAndTime);
            viewHolder.mTextSeparator.setText(appropriateDate);
            viewHolder.mTextSeparator.setVisibility(View.VISIBLE);
        }

        viewHolder.mTitle.setText(mNotificationList.get(position).getTitle());
        viewHolder.mContent.setText(mNotificationList.get(position).getContent());
        viewHolder.mIcon.setImageDrawable(mIconsArray.getDrawable(mNotificationList.get(position).getIconNumber()));
        Color.parseColor(mColoursArray[mNotificationList.get(position).getColourNumber()]);
        GradientDrawable bgShape = (GradientDrawable) viewHolder.mCircle.getDrawable();
        bgShape.setColor(Color.parseColor(mColoursArray[mNotificationList.get(position).getColourNumber()]));

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    TabFragment fragment = new TabFragment();
                    fragment.startViewerActivity(view, mNotificationList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }
}