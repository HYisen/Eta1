package net.alexhyisen.eta1.telnet;

import net.alexhyisen.eta1.other.MyCallback;
import net.alexhyisen.eta1.other.msg.Message;

/**
 * Created by Alex on 2017/5/17.
 * sth that can test msg and define method invoked when msg received
 */

interface Client {

    void send(final String content);

    void link(final String host, final int port);

    void shutdown();

    void setHandler(MyCallback<Message> handler);
}
