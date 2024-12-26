package pds.UtilClasses;

public class Head<E> {
    
    protected int size;
    protected int bitsPerNode;
    protected Node<E> root;

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

    public int getBitsPerNode() {
        return this.bitsPerNode;
    }

    public int getSize() {
        return size;
    }

    public void setBitsPerNode(int bitsPerNode) {
        this.bitsPerNode = bitsPerNode;
    }

    public void setRoot(Node<E> root) {
        this.root = root;
    }
}