package com.example.katia.mylocations.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by katia on 17/12/2016.
 */

public class LocationAnimationUtils {


    public static void setViewAlphaAnimation(View view){
        ObjectAnimator leftView = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.play(leftView);
        animatorSet.start();
    }
    public static void setClickImageScaleAnimation(View view){
        ObjectAnimator downView = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.7f);
        ObjectAnimator freeView = ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(downView,freeView);
        animatorSet.start();
    }
    public static void setClickImageAlphaAnimation(final ImageView view, final int newResource){
        ObjectAnimator downView = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.1f);
        final ObjectAnimator freeView = ObjectAnimator.ofFloat(view, "alpha", 0.1f, 1.0f);
        freeView.setDuration(150);
        downView.setDuration(150);
        downView.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                freeView.start();
                view.setImageResource(newResource);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        downView.start();
    }


    /**
     *
     * @param imageContainer View Group should contain ImageView children, each ImageView should be set to GONE visibility
     * @return
     */
    public static AnimatorSet setSlideShowAnimation (ViewGroup imageContainer){

        final ViewGroup container = imageContainer;
        ObjectAnimator goneView = ObjectAnimator.ofFloat(container, "alpha", 1.0f, 0.0f);
        ObjectAnimator delayView = ObjectAnimator.ofFloat(container, "alpha", 1.0f, 1.0f);
        ObjectAnimator vsibleView = ObjectAnimator.ofFloat(container, "alpha", 0.0f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(vsibleView, delayView, goneView);
        goneView.setDuration(350);
        delayView.setDuration(3500);
        goneView.setDuration(350);
        animatorSet.setStartDelay(400);
        animatorSet.addListener(new Animator.AnimatorListener() {
            private boolean mCanceled;

            @Override
            public void onAnimationStart(Animator animation) {
                mCanceled = false;
                boolean set = false;
                for (int i = 0; i < container.getChildCount() - 1; i++) {
                    if (container.getChildAt(i).getVisibility() == View.VISIBLE) {
                        container.getChildAt(i).setVisibility(View.GONE);
                        container.getChildAt(i + 1).setVisibility(View.VISIBLE);
                        set = true;
                        break;
                    }
                }
                if (!set) {
                    if(container.getChildCount()>0) {
                        container.getChildAt(0).setVisibility(View.VISIBLE);
                    }
                    if (container.getChildCount()>1)
                        container.getChildAt(container.getChildCount() - 1).setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCanceled)
                    animation.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {mCanceled = true;}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
        return animatorSet;
    }
}
