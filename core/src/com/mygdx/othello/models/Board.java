package com.mygdx.othello.models;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a board of the game Othello
 */
public class Board {

    /** ArrayList of all cells on the board */
    private ArrayList<Cell> cells;

    /** The width and/or height of the board*/
    private int boardSize;

    /** Variable used to memorize the cells to flip after the checking of a player's legal moves */
    private ArrayList<Cell> tempFlips1 = new ArrayList<Cell>();

    /** Variable used to memorize the cells to flip after the checking of a player's legal moves */
    private ArrayList<Cell> tempFlips2 = new ArrayList<Cell>();

    /** Variable used to store the last cells effectively flipped */
    private ArrayList<Cell> cellsToFlip;

    /** Number of cells on the board */
    private int totalBoardSize;

    /** Player turn */
    private int playerTurn;

    /** Boolean indicating if AI mode is ON or not */
    private boolean AI;

    /** Time left for black player in Timed Mode */
    private float timeBlack;

    /** Time left for white player in Time Mode */
    private float timeWhite;

    /** Score */
    private ArrayList<Integer> score = new ArrayList<Integer>();

    /** Preferences of the game */
    private Preferences prefs;

    /**
     * Creates a board of boardSize x boardSize dimensions
     * The board is represented by an ArrayList of Cells, each cell taking a value among {0,1,2}
     * according to its state.
     * 0 : if the cell is empty ; 1 : if a player 1's pown is on it, etc...
     * @param boardSize, width and/or height of the board
     * @param AI, a boolean indicating if the game is played versus an AI or not
     */
    public Board(int boardSize, boolean AI) {
        if ((boardSize%2 != 0) || boardSize == 0) {
            throw new IllegalArgumentException("Board size has to be larger than 0 and an even number.");
        }
        totalBoardSize = (int) Math.pow(boardSize, 2);

        this.AI = AI;

        ArrayList<Integer> startingValues = new ArrayList<Integer>(Arrays.asList(1, 2, 2, 1));

        for (int i = 0; i < 2; i++){        /* Initializing scores*/
            score.add(i,0);
        }

        cells = new ArrayList<Cell>();
        this.boardSize = boardSize;

        for (int i = 0; i < totalBoardSize; i++) {
            cells.add(new Cell(0, i));
        }

        ArrayList<Integer> temp = findMiddleSquares();
        for (int i = 0; i < temp.size(); i++) {
            cells.get(temp.get(i)).setValue(startingValues.get(i));
        }

        //Intialize preferences
        prefs = Gdx.app.getPreferences("My preferences");

        if (prefs.getFloat("time") != 0) {
            timeWhite = prefs.getFloat("time")*60;
            timeBlack = prefs.getFloat("time")*60;
        }

        for (int i=0; i<(int)prefs.getFloat("handicap"); i++) {
            if (i==0) {
                cells.get(0).setValue(2);
            } else if (i==1) {
                cells.get(this.boardSize - 1).setValue(2);
            } else if (i==2) {
                cells.get(this.totalBoardSize - this.boardSize).setValue(2);
            } else if (i==3) {
                cells.get(this.totalBoardSize - 1).setValue(2);
            }
        }

        this.playerTurn = 1;

    }

    /**
     * Constructor used to load an already saved party
     * Creates a board of boardSize x boardSize dimensions
     * The board is represented by an ArrayList of Cells, each cell taking a value among {0,1,2}
     * according to its state.
     * 0 : if the cell is empty ; 1 : if a player 1's pown is on it, etc...
     * @param gameData, load a GamaData instance containing all major information on a game
     */
    public Board(GameData gameData) {
        AI = gameData.isAI();
        boardSize = gameData.getBoardSize();
        totalBoardSize = gameData.getBoardSize() * gameData.getBoardSize();
        cells = new ArrayList<Cell>();
        for (int i=0; i<totalBoardSize; i++) {
            cells.add(new Cell(gameData.getCellsValue()[i],i));
        }

        timeBlack = gameData.getTimeBlack(); timeWhite = gameData.getTimeWhite();

        ArrayList<Integer> temp = findMiddleSquares();
        if (gameData.getCellsValue()[temp.get(0)]==0) {
            ArrayList<Integer> startingValues = new ArrayList<Integer>(Arrays.asList(1, 2, 2, 1));
            for (int i = 0; i < temp.size(); i++) {
                cells.get(temp.get(i)).setValue(startingValues.get(i));
            }
        }

        cellsToFlip = new ArrayList<Cell>();
        for (int i = 0; i<totalBoardSize; i++) {
            if (gameData.getLastFlips()[i] == 1) { cellsToFlip.add(cells.get(i)); }
        }

        playerTurn = gameData.getPlayerTurn();
        for (int i = 0; i < 2; i++){        /* Initializing scores*/
            score.add(i,0);
        }
    }

    public boolean isAI() { return AI; }

    public void setAI(boolean mode) { this.AI = mode; }

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

    /**
     * Returns the position of the 4 powns in the middle of the board
     * @return an ArrayList of Integer, with the position of the 4 initial powns.
     */
    public ArrayList<Integer> findMiddleSquares() {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        int pos = totalBoardSize / 2 - boardSize / 2;
        temp.add(pos - 1);
        temp.add(pos);
        temp.add(pos + boardSize - 1);
        temp.add(pos + boardSize);
        return temp;
    }

    /**
     * Returns each cell of the designated player
     * @param player, Integer representing one of the player
     * @return an ArrayList including the position of each of the player's cell
     */
    public ArrayList<Cell> getActivePlayerCells(int player) {
        ArrayList<Cell> activeCells = new ArrayList<Cell>();
        for (Cell c : cells) {
            if (c.getValue() == player) {
                activeCells.add(c);
            }
        }
        return activeCells;
    }

    /** Returns the last flipped cells
     * @return en ArrayList<Cell>
     */
    public ArrayList<Cell> getLastFlips() { return cellsToFlip; }

    /**
     * Check if putting a powns in this cell will lead to a legal move horizontally-wise
     * Also fills the tempFlips variable with the cell to flip
     * @param c, a potential position for the active player's pown
     * @param player, int representing the opposing player
     * @return a boolean, true if this position will lead to a legal move horizontally-wise
     */
    private boolean checkHorizontal(Cell c, int player) {
        boolean seenOther = false;

        boolean firstSearch = false;
        boolean secondSearch = false;

        Cell cell = c;
        tempFlips1.clear();

        //Iterates along the row of c, on the left side of c
        while (true) {
            if (c.getPos() % boardSize == 0 || (c.getPos()-1)% boardSize == 0) {
                tempFlips1.clear();
                break;
            }
            cell = moveLeft(cell);
            if (cell == null) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips1.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == (player == 1 ? 2 : 1)){
                if (seenOther) {
                    firstSearch = true;
                }
                break;
            }
        }

        cell = c;
        tempFlips2.clear();

        //Iterates along the row of c, on the right side of c
        while (true) {
            if ((c.getPos() + 1) % boardSize == 0 || (c.getPos()+2) % boardSize ==0) {
                tempFlips2.clear();
                break;
            }
            cell = moveRight(cell);
            if (cell == null) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips2.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == (player==1 ? 2 : 1)){
                if (seenOther) {
                    secondSearch = true;
                }
                break;
            }
        }
        return firstSearch || secondSearch;
    }

    /**
     * Check if putting a powns in this cell will lead to a legal move vertically-wise
     * Also fills the tempFlips variable with the cell to flip
     * @param c, a potential position for the active player's pown
     * @param player, int representing the opposing player
     * @return a boolean, true if this position will lead to a legal move vertically-wise
     */
    private boolean checkVertical(Cell c, int player) {
        boolean seenOther = false;

        boolean firstSearch = false;
        boolean secondSearch = false;

        Cell cell = c;
        tempFlips1.clear();

        //Iterates along the column of c, below c
        while (true) {
            if (c.getPos() >= (totalBoardSize - 2*boardSize) && c.getPos() < totalBoardSize) {
                tempFlips1.clear();
                break;
            }
            cell = moveDown(cell);
            if (cell == null) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips1.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == (player==1 ? 2 : 1)){
                if (seenOther) {
                    firstSearch = true;
                }
                break;
            }
        }

        cell = c;
        tempFlips2.clear();

        //Iterates along the column of c, above c
        while (true) {
            if (c.getPos() >= 0 && c.getPos() < 2*boardSize) {
                tempFlips2.clear();
                break;
            }
            cell = moveUp(cell);
            if (cell == null) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips2.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == (player==1 ? 2 : 1)) {
                if (seenOther) {
                    secondSearch = true;
                }
                break;
            }
        }
        return firstSearch || secondSearch;
    }

    /**
     * Check if putting a powns in this cell will lead to a legal along the left diagonal
     * Also fills the tempFlips variable with the cell to flip
     * @param c, a potential position for the active player's pown
     * @param player, int representing the opposing player
     * @return a boolean, true if this position will lead to a legal move along the left diagonal
     */
    private boolean checkDiagonalLeft(Cell c, int player) {
        boolean seenOther = false;

        boolean firstSearch = false;
        boolean secondSearch = false;

        Cell cell = c;
        tempFlips1.clear();

        //Iterate along the top left diagonal of c
        while (true) {
            if ((c.getPos() >= 0 && c.getPos() < boardSize) || (c.getPos() % boardSize == 0)) {
                tempFlips1.clear();
                break;
            }
            cell = moveUpLeft(cell);
            if (cell == null) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips1.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == (player==1 ? 2 : 1)) {
                if (seenOther) {
                    firstSearch = true;
                }
                break;
            }
        }

        cell = c;
        tempFlips2.clear();

        //Iterates along the down right diagonal of c
        while (true) {
            if ((c.getPos() >= (totalBoardSize - boardSize)) ||  ((c.getPos() + 1) % boardSize == 0)){
                tempFlips2.clear();
                break;
            }
            cell = moveDownRight(cell);
            if (cell == null) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips2.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == (player==1 ? 2 : 1)){
                if (seenOther) {
                    secondSearch = true;
                }
                break;
            }
        }
        return firstSearch || secondSearch;
    }

    /**
     * Check if putting a powns in this cell will lead to a legal move along the right diagonal
     * Also fills the tempFlips variable with the cell to flip
     * @param c, a potential position for the active player's pown
     * @param player, int representing the opposing player
     * @return a boolean, true if this position will lead to a legal move along the right diagonal
     */
    private boolean checkDiagonalRight(Cell c, int player) {
        boolean seenOther = false;

        boolean firstSearch = false;
        boolean secondSearch = false;

        Cell cell = c;
        tempFlips1.clear();

        //Iterates along the down left diagonal of c
        while (true) {
            if ((c.getPos() >= (totalBoardSize - boardSize)) || (c.getPos() % boardSize == 0)) {
                tempFlips1.clear();
                break;
            }
            cell = moveDownLeft(cell);
            if (cell == null) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips1.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips1.clear();
                break;
            } else if (cell.getValue() == (player==1 ? 2 : 1)) {
                if (seenOther) {
                    firstSearch = true;
                }
                break;
            }
        }

        cell = c;
        tempFlips2.clear();

        //Iterates along the top right diagonal of c
        while (true) {
            if ((c.getPos() >= 0 && c.getPos() < boardSize) || ((c.getPos() + 1) % boardSize == 0)){
                tempFlips2.clear();
                break;
            }
            cell = moveUpRight(cell);
            if (cell == null) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == player) {
                seenOther = true;
                tempFlips2.add(cell);
            } else if (cell.getValue() == 0) {
                tempFlips2.clear();
                break;
            } else if (cell.getValue() == (player==1 ? 2 : 1)) {
                if (seenOther) {
                    secondSearch = true;
                }
                break;
            }
        }
        return firstSearch || secondSearch;
    }


    /**
     * Returns the Cell just after c in terms of 'absolute' position
     * If the working cell is the last cell on the row (no cell on the right) : returns null
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveRight(Cell c) {
        if ((c.getPos() + 1)%boardSize != 0) {
            return cells.get(c.getPos() + 1);
        } else {
            return null;
        }
    }

    /**
     * Return the Cell just before c in terms of 'absolute' position
     * If the working cell is the first cell of the row (no cell on the left) : return null
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveLeft(Cell c) {
        if (c.getPos()%boardSize != 0) {
            return cells.get(c.getPos() - 1);
        } else {
            return null;
        }
    }

    /**
     * Returns the Cell above c on the board
     * If the working cell is on the first row (no cell on the top) : returns null
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveUp(Cell c) {
        if (c.getPos() >= boardSize) {
            return cells.get(c.getPos() - boardSize);
        } else {
            return null;
        }
    }

    /**
     * Returns the cell below c on the board
     * If the working cell is on the last row (no cell below) : returns null
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveDown(Cell c) {
        if (c.getPos() < totalBoardSize - boardSize) {
            return cells.get(c.getPos() + boardSize);
        } else {
            return null;
        }
    }

    /**
     * Returns the cell at the bottom right of c on the board
     * Returns null if no cell can answer the query
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveDownRight(Cell c) {
        if (c.getPos() < totalBoardSize - boardSize - 1 && (c.getPos() + 1)%boardSize != 0) {
            return cells.get(c.getPos() + boardSize + 1);
        } else {
            return null;
        }
    }

    /**
     * Returns the cell at the bottom left of c on the board
     * Returns null if no cell can answer the query
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveDownLeft(Cell c) {
        if (c.getPos() < totalBoardSize - boardSize - 1 && c.getPos()%boardSize != 0) {
            return cells.get(c.getPos() + boardSize - 1);
        } else {
            return null;
        }
    }

    /**
     * Returns the cell at the top right of c on the board
     * Returns null if no cell can answer the query
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveUpRight(Cell c) {
        if (c.getPos() >= boardSize && (c.getPos() + 1)%boardSize != 0) {
            return cells.get(c.getPos() - boardSize + 1);
        } else {
            return null;
        }
    }

    /**
     * Returns the cell at the top left of c on the board
     * Returns null if no cell can answer the query
     * @param c, the working Cell
     * @return a Cell
     */
    private Cell moveUpLeft(Cell c) {
        if (c.getPos() > boardSize && c.getPos()%boardSize !=0) {
            return cells.get(c.getPos() - boardSize - 1);
        } else {
            return null;
        }
    }

    /**
     * Returns all the legal moves the active player can do
     * @param player, the active player
     * @return an HashMap<Cell, ArrayList<Cell>>, mapping a potential new cell for the
     * active player to all the opposing player's cells to flip
     */
    public Map<Cell, ArrayList<Cell>> getLegalMoves(int player) {

        Map<Cell, ArrayList<Cell>> legalMoves = new HashMap<Cell, ArrayList<Cell>>();
        ArrayList<Cell> flips = new ArrayList<Cell>();

        int otherPlayer = (player == 1) ? 2 : 1;

        for (Cell c : cells) {
            if (c.isEmpty()) {
                flips.clear();
                if (checkHorizontal(c, otherPlayer)) {
                    flips.addAll(tempFlips1);
                    flips.addAll(tempFlips2);
                }
                if (checkVertical(c, otherPlayer)) {
                    flips.addAll(tempFlips1);
                    flips.addAll(tempFlips2);
                }
                if (checkDiagonalLeft(c, otherPlayer)) {
                    flips.addAll(tempFlips1);
                    flips.addAll(tempFlips2);
                }
                if (checkDiagonalRight(c, otherPlayer)) {
                    flips.addAll(tempFlips1);
                    flips.addAll(tempFlips2);
                }

                if (!flips.isEmpty()) {
                    legalMoves.put(c, (ArrayList<Cell>) flips.clone());

                }
            }
        }
        return legalMoves;

    }

    /**
     * Places a disc at the designated position 'pos' if the move is legal
     * Returns true if a disc is placed, return false if not
     * @param pos, the desired 'absolute' position
     * @param player, the active player number
     * @param legalMoves, an HashMap with the legal moves as the keySet (Set<Cell>) and the Cells to
     *                    flip as the mapped values.
     * @return a boolean, true if a disc is placed, false in the other case
     */
    public boolean placeDisc(int pos, int player, Map<Cell, ArrayList<Cell>> legalMoves) {

        /*
        if (!legalMoves.keySet().stream().anyMatch(o -> o.getPos() == pos)) {
            throw new IllegalArgumentException("Invalid move.");
        }*/
        if (!legalMoves.keySet().contains(cells.get(pos))) {
            System.err.println("Invalid move.");
            return false;
        } else {

            for (Cell c : legalMoves.keySet()) {
                if (c.getPos() == pos) {
                    cellsToFlip = legalMoves.get(c);

                    for (Cell c2 : cellsToFlip) {
                        c2.setValue(player);
                    }
                }
            }
            cells.get(pos).setValue(player);
            updateScore();
            return true;
        }
    }

    /**
     * Returns the board with all cell's state
     * @return an ArrayList<Cell>
     */
    public ArrayList<Cell> getCells() {
        return cells;
    }

    /**
     * Returns the board's width and/or height
     * @return an Integer
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Returns the row number of c on the board
     * @param c, the working cell
     * @return an Integer
     */
    public int getRow(Cell c) {
        return c.getPos()/boardSize;
    }

    /**
     * Returns the column number of c on the board
     * @param c, the working cell
     * @return an Integer
     */
    public int getColumn(Cell c) {
        return c.getPos()%boardSize;
    }

    /**
     * Returns the 'absolute' position of a Cell as a function of its row number and column number
     * @param row, Integer equal to the row number of the Cell
     * @param col, Integer equal to the column number of the Cell
     * @return an Integer, the 'absolute' position of the Cell
     */
    public int getCellPos(int row, int col) {
        return row*getBoardSize() + col;
    }

    /**
     * Set the player turn
     * @param player turn
     */
    public void setPlayerTurn(int player) {
        playerTurn = player;
    }

    /**
     * Returns the player turn
     * @return an Integer, the player turn
     */
    public int getPlayerTurn() { return playerTurn; }

    /**
     * Iterates through the board and updates the players' score
     */
    public void updateScore() {
        int player1Score = 0;
        int player2Score = 0;
        for (Cell c : cells) {
            if (c.getValue() == 1) {
                player1Score ++;
                score.set(0,player1Score);
            }
            if (c.getValue() == 2) {
                player2Score ++;
                score.set(1,player2Score);
            }
        }
    }

    /**
     * Returns the players' score represented as an array with 2 elements
     * @return an ArrayList<Integer>
     */
    public ArrayList<Integer> getScore(){
        return score;
    }

    /**
     * Print the board as a String in the console
     * @return a String
     */

    @Override
    public String toString() {
        String boardString = "";
        int i = 1;
        for (Cell c : cells) {
            boardString += c.getValue();
            if (i%boardSize == 0) {
                boardString += "\n";
            }
            i++;
        }
        return boardString;
    }


    public static void main(String[] args) {
    }
}
