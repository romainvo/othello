package com.mygdx.othello.models;

import java.util.Objects;

/**
 * This class represents a Cell on a Othello Board
 */
public class Cell {

    /** int indicating the state of the cell, 0 = empty, 1 = black pawn, 2 = white pown */
    private int value;

    /** Absolute position of the Cell, 0 <= pos <= boardSize² */
    private int pos;

    /**
     * Initialize a Cell
     * @param value
     * @param pos
     */
    public Cell(int value, int pos) {
        this.value = value;
        this.pos = pos;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public int getPos() {
        return this.pos;
    }

    /**
     * Check if the Cell is empty
     * @return a boolean
     */
    public boolean isEmpty() {
        return value == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return pos == cell.pos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

    @Override
    public String toString() {

        return "Value: " + getValue() + " Pos: " + getPos();
    }

    public static void main(String[] args) {
        Cell c = null;
        System.out.println(20);
        System.out.println(c.getValue());
        System.out.println(30);
    }
}
