package com.bleyl.recurrence.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
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

import com.bleyl.recurrence.activities.ViewActivity;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.utils.DateAndTimeUtil;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private Context context;
    private List<Reminder> reminderList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.notification_title) TextView title;
        @BindView(R.id.notification_time) TextView time;
        @BindView(R.id.notification_content) TextView content;
        @BindView(R.id.header_separator) TextView textSeparator;
        @BindView(R.id.notification_icon) ImageView icon;
        @BindView(R.id.notification_circle) ImageView circle;
        private View view;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    public interface RecyclerListener {
        void hideFab();
    }

    public ReminderAdapter(Context context, List<Reminder> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminderList.get(position).getDateAndTime());
        // Show header for item if it is the first in date group
        if (position > 0 && reminderList.get(position).getDate().equals(reminderList.get(position - 1).getDate()) ) {
            viewHolder.textSeparator.setVisibility(View.GONE);
        } else {
            String appropriateDate = DateAndTimeUtil.getAppropriateDateFormat(context, calendar);
            viewHolder.textSeparator.setText(appropriateDate);
            viewHolder.textSeparator.setVisibility(View.VISIBLE);
        }

        viewHolder.title.setText(reminderList.get(position).getTitle());
        viewHolder.content.setText(reminderList.get(position).getContent());
        viewHolder.time.setText(DateAndTimeUtil.toStringReadableTime(calendar, context));
        int iconResId = context.getResources().getIdentifier(reminderList.get(position).getIcon(), "drawable", context.getPackageName());
        viewHolder.icon.setImageResource(iconResId);
        GradientDrawable bgShape = (GradientDrawable) viewHolder.circle.getDrawable();
        bgShape.setColor(Color.parseColor(reminderList.get(position).getColour()));

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra("NOTIFICATION_ID", reminderList.get(viewHolder.getAdapterPosition()).getId());

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

                    ((Activity) context).getWindow().setSharedElementsUseOverlay(false);
                    ((Activity) context).getWindow().setReenterTransition(null);

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), cardView, "cardTransition");
                    ActivityCompat.startActivity(((Activity) context), intent, options.toBundle());

                    ((RecyclerListener) context).hideFab();
                } else {
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }
}