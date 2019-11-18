package com.mygdx.othello.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.mygdx.othello.controllers.AbstractController;
import com.mygdx.othello.controllers.GameController;
import com.mygdx.othello.controllers.GameStateManager;
import com.mygdx.othello.controllers.AIGameController;
import com.mygdx.othello.models.Board;
import com.mygdx.othello.models.Cell;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class manages the game display, i.e. the board, the pawns, the score, etc..
 */
public class GameScreen extends AbstractScreen {
    public static final int BOARD_SIZE = 8;

    /** Used for the basic renders : lines, font, etc... */
    private ShapeRenderer sr;

    /** The game model */
    private Board board;

    /** Container for the mainTable and input processor */
    private Stage mainStage;

    /** Container for the boardTable & the optionTable */
    private Table mainTable;

    /** Container for the end game dialog box */
    private Stage dialogStage;

    /** Used for the layout of the cell_buttons */
    private Table boardTable;

    /** Use for the layout of the 'option' buttons */
    private Table optionTable;

    /** Boolean if AI game */
    private boolean isAI;

    /** TextButton Style for the option Buttons : Main Menu and Rules */
    private TextButton.TextButtonStyle optionStyle;

    /** BitmapFont used in the option Buttons */
    private BitmapFont optionFont;

    /** Button Texture when not pressed */
    private Texture optionTextureNotPressed;

    /** Button Texture when pressed */
    private Texture optionTexturePressed;

    /** Atlas used to store texture and font used for the option Buttons */
    private TextureAtlas optionAtlas;

    /** Skin that store the optionAtlas */
    private Skin optionSkin;

    /** Exit button */
    private TextButton exitButton;

    /** Rules button */
    private TextButton rulesButton;

    /** Dark green texture used for the board cells */
    private Texture t1;

    /** ImageButton Style for the dark green cells */
    private ImageButton.ImageButtonStyle style1;

    /** Atlas that stores the dark green texture */
    private TextureAtlas at1;

    /** Stores the 1st atlas */
    private Skin skin1;

    /** Bright green texture used for the board cells */
    private Texture t2;

    /** ImageButton Style for the bright green cells */
    private ImageButton.ImageButtonStyle style2;

    /** Atlas that stores the bright green texture */
    private TextureAtlas at2;

    /** Stores the 2nd atlas */
    private Skin skin2;

    /** Array of buttons, the buttons are used for the cells display */
    private ArrayList<ImageButton> boardButtons;

    /** Overall background texture */
    private Texture background;

    /** Board background texture */
    private Texture boardBackground;

    /** Width = Height of the cell, depends of the boardSize */
    private int cellSize;

    /** Number of cells on each row||column of the board */
    private int boardSize;

    /** 'y' coordinate in pixels of the board upper limit */
    private int boardUpperLimit;

    /** 'y' coordinate in pixels of the board lower limit */
    private int boardLowerLimit;

    /** Relative distance in pixels used for the option buttons display on the screen */
    private int pointSize;

    /** Cell listener and option buttons listener */
    private GameController gameController;

    /** Padding between the bottom of the board and the lower edge of the device screen */
    private float verticalPadding;

    /** Padding between the sides the board and the side edges of the device screen */
    private float horizontalPadding;

    /** Relative distance in pixels used for the padding between the top of the board and the upper part of the display */
    private float paddingBoardOption;

    /** Preferences */
    private Preferences prefs;

    /** Memorize the colors to flip */
    private boolean isWhite;

    /** Memorize the current width of the pawn, the width will increase or decrease depending of its
     * previous value for the illusion of a flip motion
     */
    private float pownWidth;

    /** Memorize the previous width of the pawn */
    private float previousWidth;

    /** Current player ( 1 || 2 ) */
    private int currentPlayer;

    /** Previous player ( 1 || 2 ) */
    private int previousPlayer;

    /**
     * Initialize the Game Screen
     * @param gsm
     * @param board
     */
    public GameScreen(GameStateManager gsm, Board board) {
        //Loading of the game
        super(gsm);
        this.board = board;
        this.isAI = board.isAI();
        board.updateScore();
        isWhite = board.getPlayerTurn() == 2;
        prefs = Gdx.app.getPreferences("My preferences");

        //Used for the powns/grid renderer
        sr = new ShapeRenderer();

        //Set the useful board dimensions/parameters
        horizontalPadding = 0.05f*cam.viewportWidth;
        verticalPadding = 0.05f*cam.viewportHeight;
        paddingBoardOption = 0.05f*cam.viewportHeight;
        this.cellSize = (int)((cam.viewportWidth-2*horizontalPadding)/this.board.getBoardSize());
        this.boardSize = this.board.getBoardSize() * cellSize;
        this.pointSize = (int)((cam.viewportWidth-2*horizontalPadding)/8);

        //Container for the cells_buttons and all actors on the screens
        this.mainStage = new Stage(new FillViewport(cam.viewportWidth, cam.viewportHeight));

        this.mainTable = new Table(); mainTable.setFillParent(true);
        mainTable.setDebug(false);
        mainTable.padBottom(verticalPadding+horizontalPadding).padTop(verticalPadding);
        mainTable.padLeft(horizontalPadding).padRight(horizontalPadding);

        this.optionTable = new Table(); optionTable.setDebug(false);
        this.boardTable = new Table();

        mainTable.add(optionTable).top().width(this.boardSize).padBottom(paddingBoardOption+horizontalPadding).expand();
        mainTable.row();
        mainTable.add(boardTable).width(this.boardSize).height(this.boardSize);

        //Input settings
        if (isAI) {
            this.gameController = new AIGameController(gsm, this, this.board);
        }  else {
            this.gameController = new GameController(gsm, this, this.board);
        }

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(mainStage);
        inputMultiplexer.addProcessor(gameController);
        Gdx.input.setInputProcessor(inputMultiplexer);
        //Cancel the back key android paradigm
        Gdx.input.setCatchBackKey(true);

        //Used to display the end game dialog box
        this.dialogStage = new Stage(new FillViewport(cam.viewportWidth, cam.viewportHeight));

        //Board cells
        this.boardButtons = new ArrayList<ImageButton>();

        //Setting initial variables for flip animation
        pownWidth = 2*cellSize/3;
        previousWidth = 2*cellSize/3;

        //Background texture
        background = new Texture("Stone_bright_lv1.jpg");

        //Board background/Framework texture
        boardBackground = new Texture("wood_board_bg.jpg");

        //Design of the 'option' buttons
        optionStyle = new TextButton.TextButtonStyle();
        optionFont = new BitmapFont();
        optionFont.getData().setScale(4f);
        optionTexturePressed = new Texture("WoodenButton_dark.png");
        optionTextureNotPressed = new Texture("WoodenButton.png");
        optionAtlas = new TextureAtlas();
        optionAtlas.addRegion("Pressed", optionTexturePressed, 0, 0,
                optionTexturePressed.getWidth(), optionTexturePressed.getHeight());
        optionAtlas.addRegion("NotPressed", optionTextureNotPressed, 0, 0,
                optionTextureNotPressed.getWidth(), optionTextureNotPressed.getHeight());
        optionSkin = new Skin();
        optionSkin.addRegions(optionAtlas);
        optionStyle.font = optionFont;
        optionStyle.up = optionSkin.getDrawable("NotPressed"); optionStyle.down = optionSkin.getDrawable("Pressed");
        optionStyle.checked = optionSkin.getDrawable("NotPressed");

        //Display of the Exit button
        exitButton = new TextButton("Main Menu", optionStyle);
        exitButton.getLabel().setFontScale(3f);
        exitButton.addListener(gameController);
        optionTable.add(exitButton).expand().top().left().width(3.25f*pointSize).height(1.5f*pointSize);

        //Display of the rules button
        rulesButton = new TextButton("Rules", optionStyle);
        rulesButton.getLabel().setFontScale(3f);
        rulesButton.addListener(gameController);
        optionTable.add(rulesButton).expand().top().right().width(3.25f*pointSize).height(1.5f*pointSize);

        //Design of the first category of button
        style1 = new ImageButton.ImageButtonStyle();
        t1 = new Texture("green_carpet_square.jpg");
        at1 = new TextureAtlas();
        at1.addRegion("button1", t1, 0, 0,  cellSize, cellSize);
        skin1 = new Skin(); skin1.addRegions(at1);
        style1.up = skin1.getDrawable("button1"); style1.down = skin1.getDrawable("button1");
        style1.checked = skin1.getDrawable("button1");

        //Design of the second category of button
        style2 = new ImageButton.ImageButtonStyle();
        t2 = new Texture("green_carpet_square_bright.jpg");
        at2 = new TextureAtlas();
        at2.addRegion("button2", t2, 0, 0,  cellSize, cellSize);
        skin2 = new Skin(); skin2.addRegions(at2);
        style2.up = skin2.getDrawable("button2"); style2.down = skin2.getDrawable("button2");
        style2.checked = skin2.getDrawable("button2");

        //Display - layout of the cells
        boardButtons.add(new ImageButton(style1));
        boardButtons.get(0).addListener(gameController);
        boardTable.add(boardButtons.get(0));

        boolean reverse = false;
        for (int i=1; i<board.getCells().size(); i++) {
            if (i%board.getBoardSize() == 0) {
                reverse = !reverse;
            }
            if (i%2 == 0 && !reverse) {
                boardButtons.add(new ImageButton(style1));
            } else if (i%2 == 0 && reverse){
                boardButtons.add(new ImageButton(style2));
            } else if (i%2 == 1 && !reverse) {
                boardButtons.add(new ImageButton(style2));
            } else if (i%2 == 1 && reverse){
                boardButtons.add(new ImageButton(style1));
            }
            boardButtons.get(i).addListener(gameController);
            if (i%board.getBoardSize() == 0 ) { boardTable.row(); }
            boardTable.add(boardButtons.get(i));
        }

        ArrayList<Cell> activeCells = new ArrayList<Cell>();
        activeCells.addAll(board.getActivePlayerCells(1));
        activeCells.addAll(board.getActivePlayerCells(2));
        for (Cell c : activeCells) {
            boardButtons.get(c.getPos()).setDisabled(true);
        }


        mainStage.addActor(mainTable);

        this.boardLowerLimit = (int)(cam.viewportHeight - verticalPadding -horizontalPadding);
        this.boardUpperLimit = boardLowerLimit - boardSize;

    }

    /**
     * Return the Board instance of the game
     * @return a Board
     */
    public Board getBoard() { return board; }

    /**
     * @return a boolean, indicating if the Game is on AI mode or not
     */
    public boolean getIsAI() {
        return isAI;
    }

    /**
     * Method used for animation purposes. Changes the color of a pown that has been flipped.
     * @param white, true if the current rendering color is white
     * @return a Color
     */
    public Color changeColoR(boolean white){
        if(white){
            return Color.WHITE;
        } else{
            return Color.BLACK;
        }
    }

    /**
     * Method used for animation purposes
     * Changes the width of a pown for the illusion of a flip motion.
     */
    public void changedWidth() {
        if (pownWidth < 2 * cellSize / 3 && pownWidth > 0 && previousWidth > pownWidth) {
            previousWidth = pownWidth;
            pownWidth -= 4f;
        }
        else if (pownWidth < 0){
            previousWidth = pownWidth;
            pownWidth += 4f;
            isWhite = !isWhite;
        }
        else if (pownWidth < 2 * cellSize / 3 && pownWidth > 0 && previousWidth < pownWidth) {
            previousWidth = pownWidth;
            pownWidth += 4f;
        }

        else if (pownWidth>=2 * cellSize / 3){
            previousWidth = pownWidth;
            pownWidth = 2 * cellSize / 3;
        }
    }

    public Stage getDialogStage() { return dialogStage; }

    public int getPointSize() { return pointSize; }

    public ArrayList<ImageButton> getBoardButtons() { return boardButtons; }

    @Override
    public AbstractController getController() {
        return this.gameController;
    }

    @Override
    public void render(SpriteBatch sb) {
        //Background render
        sb.begin();
        sb.draw(background, 0,0 ); sb.draw(background, background.getWidth(), 0);
        sb.draw(background, 2*background.getWidth(), 0);
        sb.draw(background, 0, background.getHeight()); sb.draw(background, background.getWidth(), background.getHeight());
        sb.draw(background, 2*background.getWidth(), background.getHeight());
        sb.draw(background, 0, 2*background.getHeight()); sb.draw(background, background.getWidth(), 2*background.getHeight());
        sb.draw(background, 2*background.getWidth(), 2*background.getHeight());
        sb.draw(background, 0, 3*background.getHeight()); sb.draw(background, background.getWidth(), 3*background.getHeight());
        sb.draw(background, 2*background.getWidth(), 3*background.getHeight());
        sb.end();

        //Basic score display
        if (prefs.getBoolean("DisplayScore")) {
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(Color.BLACK);
            sr.circle(horizontalPadding + pointSize/2,
                    verticalPadding + exitButton.getHeight() + 0.65f*pointSize, pointSize/3);
            sr.setColor(Color.WHITE);
            sr.circle(cam.viewportWidth - horizontalPadding - pointSize/2,
                    verticalPadding + exitButton.getHeight() + 0.65f*pointSize, pointSize/3);
            sr.end();
            sb.begin();
            optionFont.setColor(Color.BLACK);
            optionFont.draw(sb, ""+board.getScore().get(0), horizontalPadding + 1.25f*pointSize,
                    cam.viewportHeight - verticalPadding - exitButton.getHeight() - 0.45f*pointSize);
            optionFont.setColor(Color.WHITE);
            optionFont.draw(sb, ""+board.getScore().get(1), cam.viewportWidth - horizontalPadding - 1.5f*pointSize,
                    cam.viewportHeight - verticalPadding - exitButton.getHeight() - 0.45f*pointSize);
            sb.end();
        }

        //Basic time display
        if (((int)(board.getTimeWhite()*60) > 0 && (int)(board.getTimeBlack()*60) > 0)
                && !this.getIsAI()) {
            sb.begin();
            optionFont.setColor(Color.BLACK);
            int mn = (int)(board.getTimeBlack()/60);
            int sec = (int)(board.getTimeBlack()%60);
            optionFont.draw(sb,mn+ "mn "+sec+" s", horizontalPadding + 0.15f*pointSize,
                    cam.viewportHeight - verticalPadding - exitButton.getHeight() - 1.2f*pointSize);
            optionFont.setColor(Color.WHITE);
            mn = (int)(board.getTimeWhite()/60);
            sec = (int)(board.getTimeWhite()%60);
            optionFont.draw(sb, mn+ "mn "+sec+" s", cam.viewportWidth - horizontalPadding - 2f*pointSize,
                    cam.viewportHeight - verticalPadding - exitButton.getHeight() - 1.2f*pointSize);
            sb.end();
        }

        //Board background render
        sb.begin();
        sb.draw(boardBackground,0, verticalPadding, cam.viewportWidth, boardSize+2*horizontalPadding);
        sb.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.BLACK);
        Gdx.gl.glLineWidth(8);
        sr.rect(2,boardUpperLimit-horizontalPadding, cam.viewportWidth-6, boardSize+2*horizontalPadding);
        sr.end();

        //Board cells render
        mainStage.draw();

        //Black powns render
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.BLACK);
        Gdx.gl.glLineWidth(3);

        ArrayList<Cell> cellsPlayer1 = board.getActivePlayerCells(1);
        ArrayList<Cell> cellsToFlip = board.getLastFlips();
        currentPlayer = board.getPlayerTurn();

        //pownWidth changes when player turn changes.
        //Will throw nullpointerexception until first player does first move.
        if (previousPlayer != currentPlayer) {
            pownWidth = 2 * cellSize / 3 - 0.1f;
        }

        if (cellsToFlip != null) {
            cellsPlayer1.removeAll(cellsToFlip);
        }

        for (Cell c : cellsPlayer1) {
            int tempRow = board.getRow(c);
            int tempCol = board.getColumn(c);
            sr.circle(horizontalPadding+tempCol*cellSize+0.5f*cellSize,
                    boardUpperLimit+tempRow*cellSize+0.5f*cellSize, cellSize/3);
        }
        sr.end();

        //White powns render
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);

        ArrayList<Cell> cellsPlayer2 = board.getActivePlayerCells(2);

        if (cellsToFlip != null) {
            cellsPlayer2.removeAll(cellsToFlip);
        }

        for (Cell c : cellsPlayer2) {
            int tempRow = board.getRow(c);
            int tempCol = board.getColumn(c);
            sr.circle(horizontalPadding+tempCol*cellSize+0.5f*cellSize,
                    boardUpperLimit+tempRow*cellSize+0.5f*cellSize, cellSize/3);
        }
        sr.end();

        //flip powns render
        changedWidth();
        previousPlayer = currentPlayer;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        Color color = changeColoR(isWhite);
        sr.setColor(color);

        if (cellsToFlip != null) {
            for (Cell c : cellsToFlip) {
                int tempRow = board.getRow(c);
                int tempCol = board.getColumn(c);
                sr.ellipse(horizontalPadding + tempCol * cellSize + cellSize / 2 - pownWidth / 2,
                        boardUpperLimit + tempRow * cellSize + cellSize / 6, pownWidth, 2 * cellSize / 3);
            }
        }
        sr.end();

        //Legal moves render
        if (!prefs.getBoolean("ShowLegalMoves") || (isAI && board.getPlayerTurn()==2)) {
        } else {
            int playerTurn = board.getPlayerTurn();
            if (playerTurn == 1) {
                sr.begin(ShapeRenderer.ShapeType.Filled);
                sr.setColor(Color.BLACK);
                Map<Cell, ArrayList<Cell>> legalMoves = board.getLegalMoves(playerTurn);
                for (Cell c : legalMoves.keySet()) {
                    int tempRow = board.getRow(c);
                    int tempCol = board.getColumn(c);
                    sr.circle(horizontalPadding + tempCol * cellSize + 0.5f * cellSize, boardUpperLimit + tempRow * cellSize + 0.5f * cellSize, cellSize/6.5f);
                }
                sr.end();
            } else {
                sr.begin(ShapeRenderer.ShapeType.Filled);
                sr.setColor(Color.WHITE);
                Map<Cell, ArrayList<Cell>> legalMoves = board.getLegalMoves(playerTurn);
                for (Cell c : legalMoves.keySet()) {
                    int tempRow = board.getRow(c);
                    int tempCol = board.getColumn(c);
                    sr.circle(horizontalPadding+tempCol*cellSize+0.5f*cellSize,boardUpperLimit+tempRow*cellSize+0.5f*cellSize, cellSize/6.5f);
                }
                sr.end();
            }
        }

        //Last flips render changes here
        if (prefs.getBoolean("DisplayLastFlips") && board.getLastFlips() != null) {
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(Color.PURPLE);

            for (Cell c : board.getLastFlips()) {
                int tempRow = board.getRow(c);
                int tempCol = board.getColumn(c);
                sr.circle(horizontalPadding + tempCol * cellSize + 0.5f * cellSize, boardUpperLimit + tempRow * cellSize + 0.5f * cellSize, cellSize/9f);
            }
            sr.end();
        }


        //Grid render, with black lines to help vizualize the cell limits
        sr.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(2);
        sr.setColor(Color.BLACK);

        for (int x = 0; x <= board.getBoardSize(); x++) {
            // draw vertical
            sr.line(horizontalPadding+cellSize*x, boardUpperLimit,
                    horizontalPadding+cellSize*x, boardLowerLimit);
        }
        for (int y = 0; y <= board.getBoardSize(); y++) {
            // draw horizontal
            sr.line(horizontalPadding, boardUpperLimit+ y * cellSize, horizontalPadding+boardSize, boardUpperLimit+ y * cellSize);
        }
        sr.end();

        dialogStage.draw();
    }

    @Override
    public void dispose() {
        sr.dispose();
        mainStage.dispose();
        optionFont.dispose();
        optionTexturePressed.dispose();
        optionTextureNotPressed.dispose();
        optionAtlas.dispose();
        optionSkin.dispose();
        t1.dispose();
        at1.dispose();
        skin1.dispose();
        t2.dispose();
        at2.dispose();
        skin2.dispose();
        background.dispose();
        boardBackground.dispose();
    }
}
