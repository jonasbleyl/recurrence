package com.bleyl.recurrence.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.ui.activities.CreateEditActivity;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder>{

    private int mRowLayout;
    private Context mContext;
    private TypedArray mIconsArray;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        private View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public IconsAdapter(Context context, int rowLayout, TypedArray iconsArray) {
        mContext = context;
        mRowLayout = rowLayout;
        mIconsArray = iconsArray;
    }

    @Override
    public int getItemCount() {
        return mIconsArray.length();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final Drawable icon = mIconsArray.getDrawable(position);
        viewHolder.mImageView.setImageDrawable(icon);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CreateEditActivity) mContext).iconSelected(icon, position);
            }
        });
    }
}