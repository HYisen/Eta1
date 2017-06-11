package net.alexhyisen.eta1.other;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2017/6/8.
 * something warp various messages to received in JSON
 */
public class Envelope {
    public enum EnvelopeType {
        CHAPTER,
        BOOK,
        SHELF,
    }

    private EnvelopeType type;
    private String name;
    private String[] content;

    public Envelope(EnvelopeType type, String name, String[] content) {
        this.type = type;
        this.name = name;
        this.content = content;
    }

    public Envelope(String json) {
        JsonReader reader = new JsonReader(new StringReader(json));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "type":
                        this.type = EnvelopeType.valueOf(reader.nextString());
                        break;
                    case "name":
                        this.name = reader.nextString();
                        break;
                    case "content":
                        List<String> array = new ArrayList<>();
                        reader.beginArray();
                        while (reader.hasNext()) {
                            array.add(reader.nextString());
                        }
                        reader.endArray();
                        this.content = array.toArray(new String[array.size()]);
                        break;
                    default:
                        Log.w("Net", "'A lenient JSON parser passed value " + name);
                        reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public EnvelopeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String[] getContent() {
        return content;
    }
}

