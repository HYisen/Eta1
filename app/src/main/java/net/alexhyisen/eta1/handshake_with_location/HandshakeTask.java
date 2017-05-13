package net.alexhyisen.eta1.handshake_with_location;

import android.os.AsyncTask;

import net.alexhyisen.eta1.other.MyCallback;

import java.io.IOException;

/**
 * Created by Alex on 2017/5/7.
 * An adapter for async requirement
 */
class HandshakeTask extends AsyncTask<String, Void, String> {
    private MyCallback<String> callback;
    private String content;
    private HandshakeClient handshakeClient;

    private String host;
    private int port;

    HandshakeTask(MyCallback<String> callback, String host, int port) {
        this.callback = callback;
        this.host = host;
        this.port = port;
        System.out.println(host + ":" + port);
    }

    @Override
    protected String doInBackground(String... params) {
        content = params[0];

        try {
            handshakeClient = new HandshakeClient(host, port);
            handshakeClient.send(content);
            content = handshakeClient.receive();
        } catch (IOException e) {
            e.printStackTrace();
            content = "Exception occurred.";
        }

        return content;
    }

    @Override
    protected void onPostExecute(String s) {
        callback.accept(s);
    }

    @Override
    protected void onCancelled() {
        try {
            handshakeClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
