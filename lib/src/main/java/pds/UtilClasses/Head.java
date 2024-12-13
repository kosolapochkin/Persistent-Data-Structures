package pds.UtilClasses;

public class Head<E> {
    
    private int size;
    private int bitsPerNode;
    private Node<E> root;

    public Head(int bitsPerNode) {
        this.size = 0;
        this.bitsPerNode = bitsPerNode;
        this.root = new Node<>(bitsPerNode);
    }

    public void copy(Head<E> other) {
        this.size = other.size;
        this.bitsPerNode = other.bitsPerNode;
        this.root = new Node<>(this.bitsPerNode);
        this.root.copy(other.root);
    }

    public void partCopy(Head<E> other, int index) {
        this.size = other.size;
        this.bitsPerNode = other.bitsPerNode;
        this.root = new Node<>(this.bitsPerNode);
        this.root.partCopy(other.root, index);
    }

    public int size() {
        return this.size;
    }

    public Node<E> getRoot() {
        return this.root;
    }

    public void setSize(int size) {
        this.size = size;
    }
}