package VC.CodeGen;

import java.util.Stack;

/**
 * Frame class representing method frame information during code generation.
 */
public class Frame {

    private final boolean isMain;
    private int labelCounter;
    private int localVarIndex;
    private int currentStackSize;
    private int maximumStackSize;

    public final Stack<String> conStack = new Stack<>();
    public final Stack<String> brkStack = new Stack<>();
    public final Stack<String> scopeStart = new Stack<>();
    public final Stack<String> scopeEnd = new Stack<>();

    public Frame(boolean isMain) {
        this.isMain = isMain;
        this.labelCounter = 0;
        this.localVarIndex = 0;
        this.currentStackSize = 0;
        this.maximumStackSize = 0;
    }

    public boolean isMain() {
        return isMain;
    }

    /**
     * Returns the next available index for a local variable.
     */
    public int getNewIndex() {
        if (localVarIndex >= JVM.MAX_LOCALVARINDEX) {
            throw new IllegalStateException("Maximum local variable index (" +
                JVM.MAX_LOCALVARINDEX + ") reached.");
        }
        return localVarIndex++;
    }

    /**
     * Returns a new unique label for this frame.
     */
    public String getNewLabel() {
        return "L" + labelCounter++;
    }

    /**
     * Simulates pushing one operand onto the stack.
     */
    public void push() {
        push(1);
    }

    /**
     * Simulates pushing multiple operands onto the stack.
     */
    public void push(int count) {
        currentStackSize += count;
        if (currentStackSize < 0 || currentStackSize > JVM.MAX_OPSTACK) {
            throw new IllegalStateException("Operand stack overflow. Tried to push " + count +
                " operand(s). Stack size is now " + currentStackSize +
                ", limit is " + JVM.MAX_OPSTACK + ".");
        }
        maximumStackSize = Math.max(maximumStackSize, currentStackSize);
    }

    /**
     * Simulates popping one operand from the stack.
     */
    public void pop() {
        pop(1);
    }

    /**
     * Simulates popping multiple operands from the stack.
     */
    public void pop(int count) {
        currentStackSize -= count;
        if (currentStackSize < 0) {
            throw new IllegalStateException("Operand stack underflow. Tried to pop " + count +
                " operand(s). Stack size is now " + currentStackSize + ".");
        }
    }

    public int getMaximumStackSize() {
        return maximumStackSize;
    }

    public int getCurStackSize() {
        return currentStackSize;
    }
}

