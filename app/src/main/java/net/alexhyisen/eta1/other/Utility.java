package net.alexhyisen.eta1.other;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import net.alexhyisen.eta1.R;

/**
 * Created by Alex on 2017/5/6.
 * functions for
 * Do not Repeat Yourself
 * principle
 */

public class Utility {
    public static void setupEditText(EditText orig, final int IME_ACTION, final MyCallback<TextView> handler) {
        orig.setOnEditorActionListener((v,actionID,event)->{
            boolean handled = false;
            if (actionID == IME_ACTION) {
                handler.accept(v);
                handled = true;
            }
            return handled;
        });
    }

    public static void setupToolbar(ToolbarOwner activity) {
        setupToolbar(activity,true);
    }

    //If you want to enable the up button, the parent of the activity should be declared in manifests.
    public static void setupToolbar(ToolbarOwner activity, boolean enableUpButton) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (enableUpButton) {
            ActionBar actionBar = activity.getSupportActionBar();
            assert actionBar!=null;
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
