package pds.SubClasses.TrieClasses;

public class Node<E> {
    
    private Object[] content;
    private int size;
    private int count;

    public Node(int bits) {
        this.size = (int) Math.pow(2, bits);
        this.count = 0;
        this.content = new Object[this.size];
    }

    public void clone(Node<E> node) {
        this.size = node.size;
        this.count = node.count;
        this.content = node.content.clone();
    }

    public void partClone(Node<E> node, int index) {
        this.size = node.size;
        this.count = index + 1;
        for (int i = 0; i <= index; i++) {
            this.content[i] = node.content[i];
        }
    }

    public void add(Object element) {
        this.content[this.count] = element;
        this.count = this.count + 1;
    }

    public void set(int index, Object element) {
        this.content[index] = element;
    }

    public Object pop() {
        this.count = this.count - 1;
        Object element = this.content[this.count];
        this.content[this.count] = null;
        return element;
    }

    public Object[] get() {
        return this.content;
    }

    public Object get(int index) {
        return get()[index];
    }

    public int getCount() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public boolean isFull() {
        return this.count == this.size;
    }
}