package pds.SubClasses.PHashMapClasses;

public class MapNode<K, V> {

    private K key;
    private V value;

    public MapNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public String toString() {
        return key.toString() + ":" + value.toString();
    }
}

