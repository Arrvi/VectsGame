package eu.arrvi.common;

/**
 * Collection of 2 objects acting as key and value. Used as replacement of Map.Entry for lists. Key object is final.
 */
public class Pair<K, V> {
    final private K key;
    private V value;

    /**
     * Creates simple pair of objects.
     * 
     * @param key key object of pair
     * @param value value object of pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns key object
     * @return key of pair
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns value object 
     * @return value of pair
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets new value for this pair
     * @param value new value object
     */
    public void setValue(V value) {
        this.value = value;
    }

    /** 
     * Returns string representation of pair in format: `Pair&lt;key, value&gt;`
     */
    @Override
    public String toString() {
        return String.format("%s<%s, %s>", getClass().getName(), key.toString(), value.toString());
    }
}
