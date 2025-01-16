package pds.SubClasses.UndoRedoClasses;

import java.util.ArrayList;
import java.util.List;

public class UndoRedoDataStructure {

    protected UndoRedoStack<?> versions;
    protected UndoRedoStack<?> changes;
    protected UndoRedoDataStructure parent;

    protected void newVersion(Object head) {
        if (this.parent != null) {
            parent.changes.getUndo().push(this);
        }
        this.changes.getUndo().push(null);
        this.changes.getRedo().clear();
        this.versions.newVersion(head);
    }

    protected void setParent(Object object) {
        if (isPersistent(object)) {
            ((UndoRedoDataStructure) object).parent = this;
        }
    }
    
    public void undo() {
        if (!this.changes.getUndo().isEmpty()) {
            Object peek = this.changes.getUndo().peek();
            if (peek == null) {
                this.versions.undo();
            } else {
                ((UndoRedoDataStructure) peek).undo();
            }
            this.changes.getRedo().push(this.changes.getUndo().pop());
        }
    }
    
    public void redo() {
        if (!this.changes.getRedo().isEmpty()) {
            Object peek = this.changes.getRedo().peek();
            if (peek == null) {
                this.versions.redo();
            } else {
                ((UndoRedoDataStructure) peek).redo();
            }
            this.changes.getUndo().push(this.changes.getRedo().pop());
        }
    }
    
    public int getCurrentVersion() {
        return this.versions.getCurrentVersion();
    }

    public int getVersionCount() {
        return this.versions.getVersionCount();
    }

    private boolean isPersistent(Object object) {
        if (object instanceof UndoRedoDataStructure) {
            return true;
        }
        return false;
    }

    public List<Object> toList() {
        return new ArrayList<>();
    }
}