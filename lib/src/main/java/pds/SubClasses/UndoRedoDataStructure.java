package pds.SubClasses;

public interface UndoRedoDataStructure {

    private void newVersion(Object object) {};
    private void setParent(Object object) {};
    void undo();
    void redo();
    int getCurrentVersion();
    int getVersionCount();

}