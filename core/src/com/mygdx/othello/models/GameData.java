package com.mygdx.othello.models;

/**
 * This class manages the loading of the game data into minimalist variables
 * The values of the said variables are then extracted by the Board constructor to launch a game
 */
public class GameData {
    /** Save the current playerTurn : 1 || 2 */
    private int playerTurn;

    /** Save the board size */
    private int boardSize;

    /** Save the different cell values into an Array of Integers */
    private int[] cellsValue;

    /** Save the last flipped cells into an Array of Integers */
    private int[] lastFlips;

    /** Save the remaining time of the black player */
    private float timeBlack;

    /** Save the remaining time of the white player */
    private float timeWhite;

    /** Boolean about AI*/
    private boolean isAI;

    public int[] getLastFlips() {
        return lastFlips;
    }

    public void setLastFlips(int[] lastFlips) {
        this.lastFlips = lastFlips;
    }

    public float getTimeBlack() {
        return timeBlack;
    }

    public void setTimeBlack(float timeBlack) {
        this.timeBlack = timeBlack;
    }

    public float getTimeWhite() {
        return timeWhite;
    }

    public void setTimeWhite(float timeWhite) {
        this.timeWhite = timeWhite;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int[] getCellsValue() {
        return cellsValue;
    }

    public void setCellsValue(int[] cellsValue) {
        this.cellsValue = cellsValue;
    }

    /**
     * Check if the GamaData instance is empty, no game is saved on it
     * @return a boolean
     */
    public boolean isEmpty() {
        return boardSize==0;
    }
}
