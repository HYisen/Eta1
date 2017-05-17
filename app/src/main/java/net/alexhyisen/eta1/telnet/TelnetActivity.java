package net.alexhyisen.eta1.telnet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import net.alexhyisen.eta1.R;
import net.alexhyisen.eta1.other.ToolbarOwner;
import net.alexhyisen.eta1.other.Utility;
import net.alexhyisen.eta1.other.msg.Message;
import net.alexhyisen.eta1.other.msg.MessageArrayAdapter;
import net.alexhyisen.eta1.other.msg.MsgType;

import java.util.ArrayList;
import java.util.List;

public class TelnetActivity extends AppCompatActivity implements ToolbarOwner {
    private EditText inputHostEditText;
    private EditText inputPortEditText;
    private EditText inputEditText;
    private ToggleButton connectToggleButton;

    private Client client;
    private List<Message> messages=new ArrayList<>();
    ArrayAdapter<Message> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_telnet);

        Utility.setupToolbar(this);
        inputHostEditText = (EditText) findViewById(R.id.inputHostEditText);
        inputPortEditText = (EditText) findViewById(R.id.inputPortEditText);
        inputEditText = (EditText) findViewById(R.id.inputEditText);
        connectToggleButton = (ToggleButton) findViewById(R.id.connectToggleButton);
        ListView msgListView = (ListView) findViewById(R.id.msgListView);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        inputHostEditText.setText(sp.getString("pref_host", "error_host"));
        inputPortEditText.setText(sp.getString("pref_port","-1"));

        Utility.setupEditText(inputEditText, EditorInfo.IME_ACTION_SEND, this::handleSendButtonAction);
        connectToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                System.out.println("button on");
                String host = inputHostEditText.getText().toString();
                int port = Integer.valueOf(inputPortEditText.getText().toString());
                client.link(host, port);
            } else {
                System.out.println("button off");
                client.shutdown();
            }
        });

        adapter = new MessageArrayAdapter(this, messages);
        adapter.setNotifyOnChange(true);//additional safety guarantee, on default true
        msgListView.setAdapter(adapter);


        client = new TelnetClient(data -> handleMsg(new Message(data.first, data.second)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void handleSendButtonAction(View view) {
        Editable text = inputEditText.getText();
        if ("WebSocket".equals(text.toString())) {
            pushMsg(new Message(MsgType.INFO, "switched to WebSocket mode"));
            client = new WebSocketClient(data -> handleMsg(new Message(data.first, data.second)));
        } else {
            pushMsg(new Message(MsgType.CLIENT,text.toString()));
            client.send(text.toString());
        }
        text.clear();
    }

    private void pushMsg(Message msg) {
        System.out.println("push "+msg.getType()+" msg "+msg.getContent());
        messages.add(0,msg);
        adapter.notifyDataSetChanged();
    }

    private void handleMsg(Message msg) {
        if ((msg.getType() == MsgType.INFO && msg.getContent().equals("listening finished")) ||
                (msg.getType() == MsgType.ERROR && msg.getContent().startsWith("exception while listening"))) {
            connectToggleButton.setChecked(false);
        }
        pushMsg(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.shutdown();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            pushMsg(new Message(MsgType.INFO,"reconstructed"));
        }
    }
}
