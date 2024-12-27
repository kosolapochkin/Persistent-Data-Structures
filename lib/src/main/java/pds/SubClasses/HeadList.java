package pds.SubClasses;

import java.util.LinkedList;

public class HeadList<E> extends Head<E> {

    protected int width;
    protected Integer first;
    protected Integer last;
    protected LinkedList<Integer> emptyIndexes;

    public HeadList(int bitsPerNode) {
        super(bitsPerNode);
        this.bitsPerNode = bitsPerNode;
        this.first = null;
        this.last = null;
        this.emptyIndexes = new LinkedList<>();
    }

    public void clone(HeadList<E> other) {
        this.size = other.getSize();
        this.width = other.getWidth();
        this.bitsPerNode = other.bitsPerNode;
        this.root = new Node<>(this.bitsPerNode);
        this.root.clone(other.root);      
        this.first = other.first;
        this.last = other.last;
        this.emptyIndexes = other.emptyIndexes;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getLast() {
        return last;
    }

    public void setLast(Integer last) {
        this.last = last;
    }

    public int getSize() {
        return this.size;
    }

    public Node<E> getRoot() {
        return this.root;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer pollEmptyIndex() {
        if (this.emptyIndexes.size() == 0) {
            return this.width;
        } else {
            return this.emptyIndexes.poll();
        }
    }

    public void addEmptyIndex(Integer index) {
        this.emptyIndexes.add(index);
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public LinkedList<Integer> getEmptyIndexes() {
        return emptyIndexes;
    }

    public void setEmptyIndexes(LinkedList<Integer> emptyIndexes) {
        this.emptyIndexes = emptyIndexes;
    }
}