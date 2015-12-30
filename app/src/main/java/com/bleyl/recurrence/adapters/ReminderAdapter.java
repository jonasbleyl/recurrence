package com.bleyl.recurrence.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.interfaces.RecyclerCallback;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.ui.activities.ViewActivity;
import com.bleyl.recurrence.utils.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private int mRowLayout;
    private Activity mActivity;
    private List<Reminder> mReminderList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.notification_title) TextView mTitle;
        @Bind(R.id.notification_time) TextView mTime;
        @Bind(R.id.notification_content) TextView mContent;
        @Bind(R.id.header_separator) TextView mTextSeparator;
        @Bind(R.id.notification_icon) ImageView mIcon;
        @Bind(R.id.notification_circle) ImageView mCircle;
        private View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
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
                Intent intent = new Intent(mActivity, ViewActivity.class);
                intent.putExtra("NOTIFICATION_ID", mReminderList.get(position).getId());

                // Add shared element transition animation if on Lollipop or later
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CardView cardView = (CardView) view.findViewById(R.id.notification_card);

                    TransitionSet setExit = new TransitionSet();
                    Transition transition = new Fade();
                    transition.excludeTarget(android.R.id.statusBarBackground, true);
                    transition.excludeTarget(android.R.id.navigationBarBackground, true);
                    transition.excludeTarget(R.id.fab_button, true);
                    transition.excludeTarget(R.id.recycler_view, true);
                    transition.setDuration(400);
                    setExit.addTransition(transition);

                    mActivity.getWindow().setSharedElementsUseOverlay(false);
                    mActivity.getWindow().setReenterTransition(null);

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mActivity, cardView, "cardTransition");
                    ActivityCompat.startActivity(mActivity, intent, options.toBundle());

                    ((RecyclerCallback) mActivity).hideFab();
                } else {
                    view.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }
}