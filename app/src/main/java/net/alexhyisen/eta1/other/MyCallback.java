package net.alexhyisen.eta1.other;

/**
 * Created by Alex on 2017/4/29.
 * Consumer<T> in Java 8
 */

@FunctionalInterface
public interface MyCallback<T> {
    void accept(T data);
}
