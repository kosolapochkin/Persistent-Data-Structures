package pds.SubClasses.UndoRedoClasses;

import java.util.Stack;

public class UndoRedoStack<E> {

    private Stack<Object> undo;
    private Stack<Object> redo;

    public UndoRedoStack() {
        this.undo = new Stack<>();
        this.redo = new Stack<>();
    }
    
    public UndoRedoStack(Object head) {
        this();
        this.undo.push(head);
    }

    public void clone(UndoRedoStack other) {
        this.undo.addAll(other.getUndo());
        this.redo.addAll(other.getRedo());
    }

    public void newVersion(Object head) {
        this.undo.push(head);
        this.redo.clear();
    }
    
    public Object getCurrent() {
        if (!undo.isEmpty()) {
            return this.undo.peek();
        }
        return null;
    }

    public void undo() {
        if (!this.undo.isEmpty()) {
            this.redo.push(this.undo.pop());
        }
    }

    public void redo() {
        if (!this.redo.isEmpty()) {
            this.undo.push(this.redo.pop());
        }
    }

    public int getCurrentVersion() {
        return this.undo.size() - 1;
    }

    public int getVersionCount() {
        return this.undo.size() + this.redo.size();
    }

    public Stack<Object> getUndo() {
        return this.undo;
    }

    public Stack<Object> getRedo() {
        return this.redo;
    }
}
