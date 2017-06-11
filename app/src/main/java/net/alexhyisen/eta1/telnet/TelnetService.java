package net.alexhyisen.eta1.telnet;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import net.alexhyisen.eta1.other.MyCallback;
import net.alexhyisen.eta1.other.msg.Message;

//An IntentService is not suitable because manual rather than automatic shutdown is required.
public class TelnetService extends Service implements MyCallback<Message> {
    private final IBinder binder = new TelnetBinder();

    private final Client client = new WebSocketClient(this);

    //vacuum callback on default
    private MyCallback<Message> handler = v -> {
    };

    public class TelnetBinder extends Binder {
        public TelnetService getService() {
            return TelnetService.this;
        }
    }

    public TelnetService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        client.link(sp.getString("pref_host", "error_host"),
                Integer.valueOf(sp.getString("pref_port", "-1")));
    }

    @Override
    public void accept(Message data) {
        Log.d("Service", "accept " + data.getType() + " : " + data.getContent());
        handler.accept(data);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void send(String content) {
        client.send(content);
    }

    public String test(String content) {
        Toast.makeText(this, "test " + content, Toast.LENGTH_SHORT).show();
        return "reply of " + content;
    }

    public void setHandler(MyCallback<Message> handler) {
        this.handler = handler;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        client.shutdown();

        Log.d("TelnetService", "onDestroy");
    }
}
