package pds.PersistentClasses;

import java.util.ArrayList;
import java.util.List;

import pds.UtilClasses.VersionStack;
import pds.UtilClasses.Head;
import pds.UtilClasses.Node;

public class PArray<E> {

    private int depth;
    private int bitsPerNode;

    private int mask;

    private int maxSize;
    private int nodeSize;
    
    private VersionStack<E> innerVersionStack;
    private VersionStack<E> outerVersionStack;

    private PArray<E> parent;

    public PArray() {
        this(3, 2);
    }

    public PArray(int depth, int bitsPerNode) {
        this.depth = depth;
        this.bitsPerNode = bitsPerNode;
        this.maxSize = (int) Math.pow(2, bitsPerNode * depth);
        this.nodeSize = (int) Math.pow(2, bitsPerNode);
        this.mask = this.nodeSize - 1;
        Head<E> head = new Head<>(this.bitsPerNode);
        this.innerVersionStack = new VersionStack<>(head);
        this.outerVersionStack = new VersionStack<>();
    }

    public PArray(PArray<E> other) {
        this(other.depth, other.bitsPerNode);
        this.innerVersionStack.copy(other.innerVersionStack);
        this.outerVersionStack.copy(other.outerVersionStack);
    }

    public void newVersion(Head<E> head) {
        if (this.parent != null) {
            parent.outerVersionStack.getUndo().push(this);
        }
        this.outerVersionStack.getUndo().push(null);
        this.outerVersionStack.getRedo().clear();
        this.innerVersionStack.newVersion(head);
    }

    public int getCurrentVersion() {
        return this.innerVersionStack.getCurrentVersion();
    }

    public int getVersionCount() {
        return this.innerVersionStack.getVersionCount();
    }

    public void undo() {
        if (!this.outerVersionStack.getUndo().isEmpty()) {
            Object peek = this.outerVersionStack.getUndo().peek();
            if (peek == null) {
                this.innerVersionStack.undo();
            } else {
                ((PArray) peek).undo();
            }
            this.outerVersionStack.getRedo().push(this.outerVersionStack.getUndo().pop());
        }
    }

    public void redo() {
        if (!this.outerVersionStack.getRedo().isEmpty()) {
            Object peek = this.outerVersionStack.getRedo().peek();
            if (peek == null) {
                this.innerVersionStack.redo();
            } else {
                ((PArray) peek).redo();
            }
            this.outerVersionStack.getUndo().push(this.outerVersionStack.getRedo().pop());
        }
    }

    public E get(int index) {
        Head<E> head = getHead();
        return get(head, index);
    }

    public void add(E value) {
        setParent(value);
        Head<E> head = new Head<>(this.bitsPerNode);
        head.copy(getHead());
        checkIfFull(head);
        newVersion(head);
        add(head, value);
    }

    public void add(int index, E value) {
        setParent(value);
        Head<E> prevHead = getHead();
        checkIfFull(prevHead);
        checkIndex(prevHead, index);
        Node<E> leafNode = copyPathToIncrement(prevHead, index);
        leafNode.set(index & this.mask, value);
        Head<E> newHead = getHead();
        for (int i = index; i < prevHead.size(); i++) {
            add(newHead, (E) get(prevHead, i));
        }
    }

    public void addAll(List<E> values) {
        Head<E> head = new Head<>(this.bitsPerNode);
        checkIfFull(head, values.size());
        head.copy(getHead());
        newVersion(head);
        for (int i = 0; i < values.size(); i++) {
            E value = values.get(i);
            setParent(value);
            add(head, value);
        }
    }

    public void set(int index, E value) {
        setParent(value);
        Head<E> prevHead = getHead();
        checkIfEmpty(prevHead);
        checkIndex(prevHead, index);
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.copy(getHead());
        newVersion(newHead);
        set(newHead, index, value);
    }

    public E pop() {
        Head<E> prevHead = getHead();
        checkIfEmpty(prevHead);
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.copy(prevHead);
        newHead.setSize(newHead.size() - 1);
        newVersion(newHead);
        Node<E> node = copyPath(newHead, newHead.size());
        E value = (E) node.pop();
        clearPath(newHead);
        return value;
    }

    public E remove(int index) {
        Head<E> prevHead = getHead();
        checkIndex(prevHead, index);
        checkIfEmpty(prevHead);
        Node<E> leafNode = copyPathToIncrement(prevHead, index);
        E value = (E) leafNode.pop();
        Head<E>newHead = getHead();
        newHead.setSize(newHead.size() - 1);
        for (int i = index + 1; i < prevHead.size(); i++) {
            add(newHead, (E) get(prevHead, i));
        }
        return value;
    }

    public void clear() {
        Head<E> head = new Head<>(this.bitsPerNode);
        newVersion(head);
    }

    private E get(Head<E> head, int index) {
        return (E) getLeafNodeValues(head, index)[index & this.mask];
    }

    private void add(Head<E> head, E value) {
        head.setSize(head.size() + 1);
        Node<E> node = copyPath(head, head.size() - 1);
        node.add(value);
    }

    private void set(Head<E> head, int index, E value) {
        Node<E> node = copyPath(head, index);
        node.set(index & this.mask, value);
    }

    private Object[] getLeafNodeValues(Head<E> head, int index) {
        return getLeafNode(head, index).get();
    }

    private Node<E> getLeafNode(Head<E> head, int index) {
        Node<E> node;
        checkIndex(head, index);
        node = head.getRoot();
        for (int level = this.bitsPerNode * (this.depth - 1); level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            node = (Node<E>) node.get(id);
        }
        return node;
    }

    private Node<E> copyPath(Head<E> head, int index) {
        Node<E> currentNode;
        Node<E> newNode;
        currentNode = head.getRoot();
        for (int level = (this.depth - 1) * this.bitsPerNode; level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            if (currentNode.isEmpty()) {
                newNode = new Node<>(this.bitsPerNode);
                currentNode.add(newNode);
            } else {
                if (id == currentNode.getCount()) {
                    newNode = new Node<>(this.bitsPerNode);
                    currentNode.add(newNode);
                } else {
                    newNode = new Node<>(this.bitsPerNode);

                    newNode.copy((Node<E>) currentNode.get(id));
                    currentNode.set(id, newNode);
                }
            }
            currentNode = newNode;
        }
        return currentNode;
    }

    private Node<E> copyPathToIncrement(Head<E> head, int index) {
        Node<E> newNode;
        int level = this.bitsPerNode * (depth - 1);
        Head<E> newHead = new Head<>(this.bitsPerNode);
        newHead.partCopy(head, (index >> level) & this.mask);
        newHead.setSize(index + 1);
        newVersion(newHead);
        Node<E> currentNode = newHead.getRoot();
        for (; level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & this.mask;
            int idNext = (index >> (level - this.bitsPerNode)) & this.mask;
            newNode = new Node(this.bitsPerNode);
            newNode.partCopy((Node<E>) currentNode.get(id), idNext);
            currentNode.set(id, newNode);
            currentNode = newNode;
        }
        return currentNode;
    }

    private void clearPath(Head<E> head) {
        Node<E> node;
        int index = head.size();
        List<Node<E>> pathNodes = new ArrayList<>();
        node = head.getRoot();
        pathNodes.add(node);
        for (int level = this.bitsPerNode * (this.depth - 1); level > 0; level -= this.bitsPerNode) {
            int id = (index >> level) & mask;
            node = (Node<E>) node.get(id);
            pathNodes.add(node);
        }
        for (int i = pathNodes.size() - 1; i > 0; --i) {
            if ((pathNodes.get(i) == null) || (pathNodes.get(i).getCount() == 0)) {
                pathNodes.get(i-1).pop();
            }
        }
    }

    public int size() {
        return getHead().size();
    }

    public int maxSize() {
        return this.maxSize;
    }
    
    public int getBitsPerNode() {
        return this.bitsPerNode;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getNodeSize() {
        return this.nodeSize;
    }

    private Head<E> getHead() {
        return (Head<E>) this.innerVersionStack.getCurrent();
    }

    private void setParent(Object object) {
        if (isPersistent(object)) {
            ((PArray) object).parent = this;
        }
    }

    private boolean isPersistent(Object object) {
        if (object instanceof PArray) {
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isFull() {
        return getHead().size() == this.maxSize;
    }

    private void checkIfFull(Head<E> head) {
        if (isFull()) {
            throw new IllegalStateException("Array is full");
        }
    }

    private void checkIfFull(Head<E> head, int delta) {
        if (head.size() + delta > this.maxSize) {
            throw new IllegalStateException("Array is full");
        }
    }
    
    
    public void checkIfEmpty(Head<E> head) {
        if (head.size() == 0) {
            throw new IllegalStateException("Array is empty");
        }
    }

    private void checkIndex(Head<E> head, int index) {
        if ((index < 0) || (index >= head.size())) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
    }

    public List<E> toList() {
        List<E> values;
        Head<E> head = getHead();
        if (head != null) {
            Object[] leafNodeValues;
            values = new ArrayList<>(head.size());
    
            for (int i = 0; i < head.size(); i = i + this.nodeSize) {
                leafNodeValues = getLeafNodeValues(head, i);
                for (int j = 0; j < this.nodeSize; j++) {
                    if (leafNodeValues[j] != null) {
                        Object value = leafNodeValues[j];
                        if (value instanceof PArray) {
                            value = ((PArray) value).toList();
                        }
                        values.add((E) value);
                    }
                }
            }
        } else {
            values = new ArrayList<>(0);
        }
        return values;  
    }

    public String toString() {
        return this.toList().toString();
    }
}