package com.bleyl.recurrence.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bleyl.recurrence.R;

public class ContributionAdapter extends RecyclerView.Adapter<ContributionAdapter.ViewHolder> {

    private int mRowLayout;
    private Context mContext;
    private String[] mContributorNames;
    private String[] mContributionTypes;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mType;

        public ViewHolder(final View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.contributor_name);
            mType = (TextView) view.findViewById(R.id.contribution_type);
        }
    }

    public ContributionAdapter(Context context, int rowLayout, String[] contributorNames, String[] contribution_types) {
        mContext = context;
        mRowLayout = rowLayout;
        mContributorNames = contributorNames;
        mContributionTypes = contribution_types;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mName.setText(mContributorNames[position]);
        viewHolder.mType.setText(mContext.getResources().getString(R.string.translator) + " " + mContributionTypes[position]);
    }

    @Override
    public int getItemCount() {
        return mContributorNames.length;
    }
}