package net.alexhyisen.eta1.other;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Alex on 2017/5/6.
 * A Promise to offer necessary functions that Utility.setupToolbar needs.
 */

public interface ToolbarOwner {
    View findViewById(@IdRes int id);

    void setSupportActionBar(@Nullable Toolbar toolbar);

    @Nullable
    ActionBar getSupportActionBar();
}
