package com.nnkti.friendlocator.helpers;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by nnkti on 10/25/2017.
 */

public class FloatingActionButtonHelper implements OnClickListener {
    FloatingActionButton floatingActionButton;
    FabClickCallBack fabClickCallBack;

    public FloatingActionButtonHelper(FloatingActionButton floatingActionButton) {
        this.floatingActionButton = floatingActionButton;
        floatingActionButton.setOnClickListener(this);
    }

    public void hideFab() {
        if (floatingActionButton.isShown())
            floatingActionButton.hide();
    }

    public void showFab() {
        if (!floatingActionButton.isShown())
            floatingActionButton.show();
    }

    public void setFabOnClickListener(FabClickCallBack fabClickCallBack) {
        this.fabClickCallBack = fabClickCallBack;
    }

    @Override
    public void onClick(View v) {
        fabClickCallBack.fabClicked();
    }

    public interface FabClickCallBack {
        void fabClicked();
    }
}
