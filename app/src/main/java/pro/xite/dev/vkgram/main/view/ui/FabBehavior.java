package pro.xite.dev.vkgram.main.view.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class FabBehavior extends FloatingActionButton.Behavior {

    private boolean stillAnimating = false;

    public FabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        if(stillAnimating) return;

        if(dyConsumed < 0) {
            stillAnimating = true;
            child.animate().rotation(-180)
                           .translationX(getLayoutMarginEnd(child) - 32)
                           .setDuration(800)
                           .withEndAction(() -> stillAnimating = false)
                    .start();
        }
        if(dyConsumed > 0) {
            stillAnimating = true;
            child.animate().rotation(180)
                           .translationX(child.getWidth() + getLayoutMarginEnd(child))
                           .setDuration(1200)
                           .withEndAction(() -> stillAnimating = false)
                    .start();
        }
    }

    private int getLayoutMarginEnd(View v) {
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        return layoutParams.getMarginEnd();
    }
}
