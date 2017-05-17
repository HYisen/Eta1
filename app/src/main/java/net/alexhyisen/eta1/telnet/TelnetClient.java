package net.alexhyisen.eta1.telnet;

import android.os.AsyncTask;
import android.util.Pair;

import net.alexhyisen.eta1.other.MyCallback;
import net.alexhyisen.eta1.other.msg.MsgType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Alex on 2017/5/6.
 * A session module, rather than the handshake one.
 */

class TelnetClient implements Client {
    private MyCallback<Pair<MsgType, String>> handler;

    private Socket socket;
    private PrintWriter out;

    private AsyncTask<Void, String, IOException> listenTask;

    TelnetClient(MyCallback<Pair<MsgType, String>> handler) {
        setHandler(handler);
    }

    @Override
    public void setHandler(MyCallback<Pair<MsgType, String>> handler) {
        this.handler = handler;
    }

    @Override
    public void send(final String content) {
        AsyncTask<String, Void, Void> sendTask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                System.out.println("send " + content);
                out.println(params[0]);
                return null;
            }
        };
        sendTask.execute(content);
    }

    @Override
    public void link(final String host, final int port) {
        AsyncTask<Void, Void, IOException> linkTask = new AsyncTask<Void, Void, IOException>() {
            @Override
            protected IOException doInBackground(Void... params) {
                try {
                    socket = new Socket(host, port);
                    //socket.setKeepAlive(true);
                    out = new PrintWriter(socket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(IOException e) {
                if (e == null) {
                    handler.accept(new Pair<>(MsgType.INFO, "link " + host + ":" + port));
                    listen();
                } else {
                    handler.accept(new Pair<>(MsgType.ERROR,
                            "failed to link " + host + ":" + port + "\n" + e.toString()));
                }
            }
        };
        System.out.println("going to link " + host + ":" + port);
        linkTask.execute();
    }

    //It will automatically start listening once the link is established,
    //through which the order problem (listen() should after async link()) is overcome.
    private void listen() {
        System.out.println("launch listen task");
        listenTask = new AsyncTask<Void, String, IOException>() {
            @Override
            protected IOException doInBackground(Void... params) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("receive " + line);
                        publishProgress(line);
                    }
                } catch (SocketException e) {
                    if (e.toString().equals("java.net.SocketException: Socket closed")) {
                        //that's what will happen when task.cancel() is called.
                        System.out.println("listening is stopped as socket is closed");
                        return null;
                    } else {
                        e.printStackTrace();
                        return e;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return e;
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                handler.accept(new Pair<>(MsgType.SERVER, values[0]));
            }

            @Override
            protected void onPostExecute(IOException e) {
                if (e == null) {
                    handler.accept(new Pair<>(MsgType.INFO, "listening finished"));
                } else {
                    handler.accept(new Pair<>(MsgType.ERROR,
                            "exception while listening" + "\n" + e.toString()));
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
        };
        //the listen task will last for a quite long time (blocking) and thus should run in a parallel pool.
        listenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void shutdown() {
        if (socket != null) {
            listenTask.cancel(true);
            AsyncTask<Void, Void, IOException> shutdownTask = new AsyncTask<Void, Void, IOException>() {
                @Override
                protected IOException doInBackground(Void... params) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return e;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(IOException e) {
                    if (e == null) {
                        handler.accept(new Pair<>(MsgType.INFO,
                                "client has been shutdown"));
                    } else {
                        handler.accept(new Pair<>(MsgType.ERROR,
                                "failed to shutdown client" + "\n" + e.toString()));
                    }
                }
            };
            shutdownTask.execute();
        }
    }
}
