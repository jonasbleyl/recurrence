package com.bleyl.recurrence.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bleyl.recurrence.R;

public class AnimationUtil {
    public static void shakeView(View v, Context ctx) {
        System.err.println(v);
        System.err.println(ctx);
        Animation shake = AnimationUtils.loadAnimation(ctx, R.anim.shake_view);
        v.startAnimation(shake);
    }
}