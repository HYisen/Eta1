package net.alexhyisen.eta1.other;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
