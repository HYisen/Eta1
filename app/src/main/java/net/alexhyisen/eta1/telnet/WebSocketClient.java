package net.alexhyisen.eta1.telnet;

import android.os.AsyncTask;
import android.util.Pair;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import net.alexhyisen.eta1.other.MyCallback;
import net.alexhyisen.eta1.other.msg.MsgType;

import java.io.IOException;


/**
 * Created by Alex on 2017/5/17.
 * sth uses WebSocket with the help of nv-websocket-client to communicate with the server.
 */

public class WebSocketClient implements Client{
    private MyCallback<Pair<MsgType, String>> handler;

    private WebSocket ws;

    public WebSocketClient(MyCallback<Pair<MsgType, String>> handler) {
        setHandler(handler);
    }

    @Override
    public void setHandler(MyCallback<Pair<MsgType, String>> handler) {
        this.handler = handler;
    }

    @Override
    public void send(String content) {
        ws.sendText(content);
    }

    @Override
    public void link(String host, int port) {
        AsyncTask<Void, Void, Exception> linkTask = new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    WebSocketFactory factory = new WebSocketFactory();
                    ws = factory.createSocket("ws://"+host+":"+port+"/ws");
                    ws.addListener(new WebSocketAdapter() {
                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception {
                            //There must be a better way.
                            new AsyncTask<String, Void, String>() {
                                @Override
                                protected String doInBackground(String... params) {
                                    return params[0];
                                }

                                @Override
                                protected void onPostExecute(String s) {
                                    handler.accept(new Pair<>(MsgType.SERVER, s));
                                }
                            }.execute(text);
                        }
                    });
                    ws.connect();
                } catch (IOException | WebSocketException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception e) {
                if (e == null) {
                    handler.accept(new Pair<>(MsgType.INFO, "link " + host + ":" + port));
                } else {
                    handler.accept(new Pair<>(MsgType.ERROR,
                            "failed to link " + host + ":" + port + "\n" + e.toString()));
                }
            }
        };
        System.out.println("going to link " + host + ":" + port);
        linkTask.execute();
    }

    @Override
    public void shutdown() {
        AsyncTask<Void, Void, Void> shutdownTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ws.disconnect();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                handler.accept(new Pair<>(MsgType.INFO,
                        "client has been shutdown"));
            }
        };
        shutdownTask.execute();
    }
}
