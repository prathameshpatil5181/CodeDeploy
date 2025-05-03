package Utilities;

public class Pair<T,V> {
    private T key;
    private V value;

    public Pair() {
        this.key = null;
        this.value = null;
    }

    public Pair(T key, V value) {
        this.key = key;
        this.value = value;
    }

    public T getkey() {
        return key;
    }

    public V getvalue() {
        return value;
    }

    public void setkey(T key) {
        this.key = key;
    }

    public void setvalue(V value) {
        this.value = value;
    }
}
