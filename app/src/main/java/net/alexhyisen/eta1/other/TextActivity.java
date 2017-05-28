package net.alexhyisen.eta1.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import net.alexhyisen.eta1.R;

public class TextActivity extends AppCompatActivity implements ToolbarOwner{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("text", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        Utility.setupToolbar(this,true);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle((String) getIntent().getExtras().get(Intent.EXTRA_TITLE));

        String content = (String) getIntent().getExtras().get(Intent.EXTRA_TEXT);
        //Maybe I should use a shared preference to store indent value.
        String indent = "        ";
        content = indent + content;
        //may cost a lot of time, hope it isn't troublesome.
        content = content.replace("\n", "\n" + indent);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Should I override the original meaning of UpButton, which is back to its parent?
                //There should be no exact parent activity of this view activity,
                //as its only usage is to show the view and then return back.
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
