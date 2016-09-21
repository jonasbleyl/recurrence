package com.bleyl.recurrence.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.bleyl.recurrence.R;
import com.bleyl.recurrence.adapters.IconsAdapter;
import com.bleyl.recurrence.database.DatabaseHelper;

public class IconPicker extends DialogFragment {

    public interface IconSelectionListener {
        void onIconSelection(DialogFragment dialog, String iconName, String iconType, int iconResId);
    }

    IconSelectionListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (IconSelectionListener) context;
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog_icons, null);

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.icons_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_columns)));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        DatabaseHelper database = DatabaseHelper.getInstance(getContext());
        recyclerView.setAdapter(new IconsAdapter(IconPicker.this, database.getIconList()));
        database.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Dialog);
        builder.setTitle(R.string.select_icon);
        builder.setView(dialogView);
        return builder.create();
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
