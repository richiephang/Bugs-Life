package mavenproject3;

import java.util.Stack;

// class for undo redo feature
public class UndoRedoStack<E> extends Stack<E> {
    private Stack undoStack;
    private Stack redoStack;

    // constructs an empty UndoRedoStack
    public UndoRedoStack() {
        undoStack = new Stack();
        redoStack = new Stack();
    }

    // pushes and returns the given value on top of the stack
    public E push(E value) {
        super.push(value);
        undoStack.push("push");
        redoStack.clear();
        return value;
    }

    // pops and returns the value at the top of the stack
    public E pop() {
        E value = super.pop();
        undoStack.push(value);
        undoStack.push("pop");
        redoStack.clear();
        return value;
    }

    // returns whether or not an undo can be done
    public boolean canUndo() {
        boolean result=false;
        if(undoStack.size()<1){
            System.out.println("--Unable to undo--");
            result=false;
        }else{
            result= true;
        }
        return result;
    }
    
    // undoes the last stack push or pop command if canUndo method returns true
    public void undo() {
        if (canUndo()) {
  
        Object action = undoStack.pop();
        if (action.equals("push")) {
            E value = super.pop();
            redoStack.push(value);
            redoStack.push("push");
        } else {
            E value = (E) undoStack.pop();         
            super.push(value);
            redoStack.push("pop");
        }
        }
    }

    // returns whether or not a redo can be done
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    // redoes the last undone operation if canRedo method returns true
    public void redo() {
        
        if (!canRedo()) {
            System.out.println("--Unable to redo--");
            Object action = undoStack.pop();       
            E value = super.pop();
            redoStack.push(value);
            redoStack.push("push");
           
        }
        Object action = redoStack.pop();
        if (action.equals("push")) {
            E value = (E) redoStack.pop();
            super.push(value);
            undoStack.push("push");
        } else {
            E value = super.pop();
            undoStack.push(value);
            undoStack.push("pop");
        }
    }
}