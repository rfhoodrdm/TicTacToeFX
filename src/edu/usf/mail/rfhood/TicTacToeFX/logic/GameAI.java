package edu.usf.mail.rfhood.TicTacToeFX.logic;

import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.DifficultyOutOfBoundsException;
import edu.usf.mail.rfhood.TicTacToeFX.logic.exception.GameAIException;
import edu.usf.mail.rfhood.TicTacToeFX.state.GameState;

public class GameAI {

    /* **************************************************************
                                Fields and Constants
       ************************************************************** */

    public static final int MIN_AI_LEVEL = 0;
    public static final int MAX_AI_LEVEL = 2;

    private Strategy randomStrategy;
    private Strategy oneMoveStrategy;
    private Strategy minMaxStrategy;

    /* **************************************************************
                                Constructor/Initialization
       ************************************************************** */

    public GameAI() {
        //initialize the various strategies available.
        randomStrategy = new RandomStrategy();
        oneMoveStrategy = new OneMoveStrategy();
        minMaxStrategy = new MinMaxStrategy();
    }

    /* **************************************************************
                                API
        ************************************************************** */

    public void makeComputerMove( GameState gameState ) throws GameAIException {
        //find out the difficulty level of the game, then defer to the appropriate strategy to make the computer move.
        int difficultyLevel = gameState.getGameAILevel();
        switch (difficultyLevel) {
            case 2:
                minMaxStrategy.makeComputerMove(gameState);
                break;
            case 1:
                oneMoveStrategy.makeComputerMove(gameState);
                break;
            case 0:
                randomStrategy.makeComputerMove(gameState);
                break;

            default:    //error state: the difficulty level is set out of bounds.
                throw new DifficultyOutOfBoundsException();
        }
    }

}
