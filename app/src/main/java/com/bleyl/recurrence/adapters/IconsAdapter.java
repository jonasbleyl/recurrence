package com.bleyl.recurrence.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.dialogs.IconPicker;
import com.bleyl.recurrence.models.Icon;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder>{

    private int mRowLayout;
    private Context mContext;
    private IconPicker mIconPicker;
    private List<Icon> mIconList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.icon) ImageView mImageView;
        private View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }

    public IconsAdapter(Context context, IconPicker iconPicker, int rowLayout, List<Icon> iconList) {
        mContext = context;
        mIconPicker = iconPicker;
        mRowLayout = rowLayout;
        mIconList = iconList;
    }

    @Override
    public int getItemCount() {
        return mIconList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final String iconName = mIconList.get(position).getName();
        final int iconResId = mContext.getResources().getIdentifier(iconName, "drawable", mContext.getPackageName());
        viewHolder.mImageView.setImageResource(iconResId);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper database = DatabaseHelper.getInstance(mContext);
                mIconList.get(position).setUseFrequency(mIconList.get(position).getUseFrequency() + 1);
                database.updateIcon(mIconList.get(position));
                database.close();

                String name = mIconList.get(position).getName();
                if (!name.equals(mContext.getResources().getString(R.string.default_icon_value))) {
                    name = mContext.getResources().getString(R.string.custom_icon);
                } else {
                    name = mContext.getResources().getString(R.string.default_icon);
                }

                ((IconPicker.IconSelectionListener) mContext).onIconSelect(mIconPicker, name, iconResId);
            }
        });
    }
}