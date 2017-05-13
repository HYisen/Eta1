package net.alexhyisen.eta1.other.msg;

import java.util.Calendar;

/**
 * Created by Alex on 2017/5/7.
 */

public class Message {
    private final MsgType type;
    private final String content;
    private final String time;


    public Message(MsgType type, String content) {
        this.type = type;
        this.content = content;
        time = Calendar.getInstance().getTime().toString();
    }

    public MsgType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}
