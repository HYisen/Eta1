package net.alexhyisen.eta1.reader;

import android.support.annotation.Nullable;

import net.alexhyisen.eta1.other.MyCallback;

import java.util.Optional;

/**
 * Created by Alex on 2017/6/9.
 * A card used in CardView with a content TextView inside to show content,
 * A handler on touch-like activity is optional.
 */

class Card {
    private final String text;
    @Nullable
    private final MyCallback<Void> handler;
    private final int textAlignment;

    Card(String text, @Nullable MyCallback<Void> handler, int textAlignment) {
        this.text = text;
        this.handler = handler;
        this.textAlignment = textAlignment;
    }

    public String getText() {
        return text;
    }

    int getTextAlignment() {
        return textAlignment;
    }

    Optional<MyCallback<Void>> getHandler() {
        return Optional.ofNullable(handler);
    }
}
