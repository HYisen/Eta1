package net.alexhyisen.eta1.reader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.alexhyisen.eta1.R;
import net.alexhyisen.eta1.other.Envelope;
import net.alexhyisen.eta1.other.MyCallback;
import net.alexhyisen.eta1.other.Utility;
import net.alexhyisen.eta1.other.msg.Message;
import net.alexhyisen.eta1.other.msg.MsgType;
import net.alexhyisen.eta1.telnet.TelnetService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReaderActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    RecyclerView listView;

    private TelnetService service;
    private boolean bound = false;

    private static Float nextCardFontSize;
    private static Float textCardFontSize;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReaderActivity.this.service = ((TelnetService.TelnetBinder) service).getService();
            bound = true;
            Toast.makeText(ReaderActivity.this, "service connected", Toast.LENGTH_SHORT).show();
            ReaderActivity.this.service.setHandler(getReportCallback());

            manageNext();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            Toast.makeText(ReaderActivity.this, "service disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private void manageNext() {
        if (getIntent() != null && "envelope".equals(getIntent().getType())) {
            String json = getIntent().getStringExtra("json");
            String cmd = getIntent().getStringExtra("cmd");
            Envelope envelope = new Envelope(json);
            if (envelope.getType().equals(Envelope.EnvelopeType.CHAPTER)) {
                int pos = cmd.lastIndexOf('.');
                if (pos != -1) {
                    //whether there is a next chapter (whether it's the last one) is not checked.
                    retrieve(cmd.substring(0, pos + 1) +
                                    (Integer.parseInt(cmd.substring(pos + 1)) + 1),
                            ReaderActivity.this::setIntent);

                    addNextCard("Next", v -> initCards());
                }
            }
        }
    }

    private List<Card> data = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this, TelnetService.class), connection, Context.BIND_AUTO_CREATE);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        nextCardFontSize = Float.parseFloat(sp.getString("pref_next_card_font_size","bad float"));
        textCardFontSize = Float.parseFloat(sp.getString("pref_text_card_font_size","bad float"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reader);

        adapter = new CardArrayAdapter(data);

        listView = (RecyclerView) findViewById(R.id.card_list);
        Utility.setupRecyclerView(listView,
                this.getApplicationContext(), adapter);

        initCards();

//        addTextCard("Hello");
//        addNextCard("init content",v->initCards());
//        addNextCard("null next",null);
//        addNextCard("test telnet", v -> {
//            addTextCard(service.test("test"));
//        });
//        addNextCard("test reader", v -> {
//            addTextCard("clicked test reader");
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bound) {
            unbindService(connection);
            bound = false;
        }

        Log.d("Reader", "onDestroy");
    }

    private void initCards() {
        data.clear();
        adapter.notifyDataSetChanged();

        addNextCard("reconnect",v->{
            reconnect();
            Toast.makeText(this, "link reconnected", Toast.LENGTH_SHORT).show();
        });
        addNextCard("shelf", v -> retrieve("ls ."));

        if (getIntent() != null && "envelope".equals(getIntent().getType())) {
            String json = getIntent().getStringExtra("json");
            String cmd = getIntent().getStringExtra("cmd");
            Envelope envelope = new Envelope(json);
            String[] data = envelope.getContent();
            switch (envelope.getType()) {
                case CHAPTER:
                    addTextCard(envelope.getName());
                    listView.scrollToPosition(3);
                    String text = Arrays.stream(data)
                            .map(v -> "        " + v)
                            .collect(Collectors.joining("\n"));
                    addTextCard(text, View.TEXT_ALIGNMENT_TEXT_START);
                    //initCard() MUST occur before binding,
                    //in order to avoid multiple manageNext().
                    if (bound) {
                        manageNext();
                    }
                    break;
                case BOOK:
                    addTextCard(String.format("《%s》", envelope.getName()));
                    String bookId = cmd.substring(3);
                    for (int i = 0; i < data.length; i++) {
                        int index = i;
                        addTextCard(data[i], v -> retrieve("get " + bookId + "." + index));
                    }
                    break;
                case SHELF:
                    for (int i = 0; i < data.length; i++) {
                        int index = i;
                        addTextCard(data[i], v -> retrieve("ls " + index));
                    }
                    break;
            }
        }

//        Log.d("STEP", "00");
//        for (int k = 0; k != 10; ++k) {
//            addTextCard("text count " + k);
//        }
//        Log.d("STEP", "01");
//        addNextCard("test", v -> {
//            addTextCard(service.test("test"));
//        });
//        Log.d("STEP", "02");
//        addNextCard("next", null);
    }

    //The difference between addTextCard & addNextCard is designed to be the style of Card.
    //TextCard should be plain while NextCard visible, which is still unaccomplished.

    //missing my lovely default function parameter

    private void addTextCard(String text) {
        addTextCard(text, null);
    }

    private void addTextCard(String text, MyCallback<Void> handler) {
        addTextCard(text, handler, View.TEXT_ALIGNMENT_CENTER);
    }

    private void addTextCard(String text, int textAlignment) {
        addTextCard(text, null, textAlignment);
    }

    private void addTextCard(String text, MyCallback<Void> handler, int textAlignment) {
        data.add(new Card(text, handler, textAlignment,textCardFontSize));
        adapter.notifyDataSetChanged();
    }

    private void addNextCard(String text, MyCallback<Void> handler) {
        addNextCard(text, handler, View.TEXT_ALIGNMENT_CENTER);
    }

    private void addNextCard(String text, MyCallback<Void> handler, int textAlignment) {
        data.add(new Card(text, handler, textAlignment,nextCardFontSize));
        adapter.notifyDataSetChanged();
    }

    private void retrieve(String command) {
        retrieve(command, this::startActivity);
    }

    private void retrieve(String command, MyCallback<Intent> handler) {
        service.send(command);
        service.setHandler(v -> {
            if (v.getType().equals(MsgType.SERVER)) {
                Intent i = new Intent(this, ReaderActivity.class);
                i.putExtra("json", v.getContent());
                i.putExtra("cmd", command);
                i.setType("envelope");
                handler.accept(i);
            } else {
                getReportCallback().accept(v);
            }
            service.setHandler(getReportCallback());
        });
    }

    private void reconnect() {
        service.reconnect();
    }

    @NonNull
    private MyCallback<Message> getReportCallback() {
        return v -> addTextCard(v.getType() + " : " + v.getContent());
    }
}
