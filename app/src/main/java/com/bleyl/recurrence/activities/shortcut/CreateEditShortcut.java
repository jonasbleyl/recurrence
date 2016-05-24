package com.bleyl.recurrence.activities.shortcut;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;

import com.bleyl.recurrence.activities.CreateEditActivity;

public class CreateEditShortcut extends CreateEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        }, 300);
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();
    }
}