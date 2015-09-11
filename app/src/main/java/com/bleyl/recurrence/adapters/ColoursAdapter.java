package com.bleyl.recurrence.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.ui.activities.CreateEditActivity;

public class ColoursAdapter extends RecyclerView.Adapter<ColoursAdapter.ViewHolder>{

    private int mRowLayout;
    private Context mContext;
    private String[] mColoursArray;
    private String[] mColourNames;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mColourView;
        public TextView mColourText;
        private View mView;

        public ViewHolder(final View view) {
            super(view);
            mView = view;
            mColourView = (ImageView) view.findViewById(R.id.colour_circle);
            mColourText = (TextView) view.findViewById(R.id.colour_name);
        }
    }

    public ColoursAdapter(Context context, int rowLayout, String[] coloursArray, String[] colourNames) {
        mContext = context;
        mRowLayout = rowLayout;
        mColoursArray = coloursArray;
        mColourNames = colourNames;
    }

    @Override
    public int getItemCount() {
        return mColoursArray.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final String colourName = mColourNames[position];
        final int colour = Color.parseColor(mColoursArray[position]);
        viewHolder.mColourText.setText(colourName);
        GradientDrawable bgShape = (GradientDrawable) viewHolder.mColourView.getDrawable();
        bgShape.setColor(colour);

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CreateEditActivity) mContext).colourSelected(colourName, colour, position);
            }
        });
    }
}