package edu.usf.mail.rfhood.logic;

import edu.usf.mail.rfhood.state.GameBoard;
import edu.usf.mail.rfhood.state.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static edu.usf.mail.rfhood.logic.MinMaxStrategy.MIN_OR_MAX.MAX;
import static edu.usf.mail.rfhood.logic.MinMaxStrategy.MIN_OR_MAX.MIN;
import static edu.usf.mail.rfhood.state.GameState.POSITIONS.*;
import static edu.usf.mail.rfhood.state.GameState.POSITION_STATE.O;
import static edu.usf.mail.rfhood.state.GameState.POSITION_STATE.X;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinMaxStrategyTest {

    /* **************************************************************
                                FIELDS AND CONSTANTS
       ************************************************************** */
    MinMaxStrategy classUnderTest;

    //terminal game board states.
    GameBoard terminalGameBoardXWins;       //end-of-game state where X wins.
    GameBoard terminalGameBoardDraw;        //end-of-game state where no one wins.
    GameBoard terminalGameBoardOWins;       //end-of-game state where O wins.

    //game board states for games several moves in
    GameBoard midGameBoardOToWin;          //mid-game board position, with O winning on 1 of 2 possible moves.
    GameBoard midGameBoardXHasAdvantage;   //mid-game board position, with X having a path to victory.

    //Early game boards.
    GameBoard gameStartBoard;               //reference to a blank board with no moves on it.


    /* **************************************************************
                                TEST SETUP
        ************************************************************** */

    @BeforeEach
    public void setupTest() {

        //initialize strategy
        classUnderTest = new MinMaxStrategy();

        // setup terminal position for test.
        //     O |   | X
        //    ---+---+---
        //     O | X |
        //    ---+---+---
        //     X |   |
        terminalGameBoardXWins = new GameBoard();

        // X positions.
        terminalGameBoardXWins.setPositionState(TOP_RIGHT,   X );
        terminalGameBoardXWins.setPositionState(MID_CENTER,  X );
        terminalGameBoardXWins.setPositionState(BOTTOM_LEFT, X );

        // O positions.
        terminalGameBoardXWins.setPositionState( TOP_LEFT, O );
        terminalGameBoardXWins.setPositionState( MID_LEFT, O );

        //     X |   |
        //    ---+---+---
        //     O | O | O
        //    ---+---+---
        //       |   | X
        terminalGameBoardOWins = new GameBoard();

        // X positions.
        terminalGameBoardOWins.setPositionState(MID_LEFT,   O );
        terminalGameBoardOWins.setPositionState(MID_CENTER,  O );
        terminalGameBoardOWins.setPositionState(MID_RIGHT, O );

        // O positions.
        terminalGameBoardOWins.setPositionState( TOP_LEFT, X );
        terminalGameBoardOWins.setPositionState( BOTTOM_RIGHT, X );

        // set up draw position for test.
        //     O | X | O
        //    ---+---+---
        //     O | X | X
        //    ---+---+---
        //     X | O | X
        terminalGameBoardDraw = new GameBoard();

        // X positions.
        terminalGameBoardDraw.setPositionState(TOP_CENTER,      X);
        terminalGameBoardDraw.setPositionState(MID_CENTER,      X);
        terminalGameBoardDraw.setPositionState(MID_RIGHT,       X);
        terminalGameBoardDraw.setPositionState(BOTTOM_LEFT,     X);
        terminalGameBoardDraw.setPositionState(BOTTOM_RIGHT,    X);

        // O positions.
        terminalGameBoardDraw.setPositionState(TOP_LEFT,         O);
        terminalGameBoardDraw.setPositionState(TOP_RIGHT,        O);
        terminalGameBoardDraw.setPositionState(MID_LEFT,         O);
        terminalGameBoardDraw.setPositionState(BOTTOM_CENTER,    O);


        // Mid-game position, with X as the turn player having a path to victory.
        //     X | O | O
        //    ---+---+---
        //     O |   | X
        //    ---+---+---
        //     X |   |
        midGameBoardXHasAdvantage = new GameBoard();

        // X positions.
        midGameBoardXHasAdvantage.setPositionState(TOP_LEFT,      X);
        midGameBoardXHasAdvantage.setPositionState(MID_RIGHT,     X);
        midGameBoardXHasAdvantage.setPositionState(BOTTOM_LEFT,   X);

        // O positions.
        midGameBoardXHasAdvantage.setPositionState(TOP_CENTER,       O);
        midGameBoardXHasAdvantage.setPositionState(TOP_RIGHT,        O);
        midGameBoardXHasAdvantage.setPositionState(MID_LEFT,         O);


        // Mid-game position, with O to win as the turn player in 1 of 2 possible moves.
        //     X | O | O
        //    ---+---+---
        //       | X | O
        //    ---+---+---
        //     X | X |
        midGameBoardOToWin = new GameBoard();

        // X positions.
        midGameBoardOToWin.setPositionState(TOP_LEFT,      X);
        midGameBoardOToWin.setPositionState(MID_CENTER,    X);
        midGameBoardOToWin.setPositionState(BOTTOM_LEFT,   X);
        midGameBoardOToWin.setPositionState(BOTTOM_CENTER, X);

        // O positions.
        midGameBoardOToWin.setPositionState(TOP_CENTER,       O);
        midGameBoardOToWin.setPositionState(TOP_RIGHT,        O);
        midGameBoardOToWin.setPositionState(MID_RIGHT,        O);


        //blank, starting position game board.
        //       |   |
        //    ---+---+---
        //       |   |
        //    ---+---+---
        //       |   |
        gameStartBoard = new GameBoard();
    }


    /* **************************************************************
                                TESTS
        ************************************************************** */

    /**
     * Test of correct setup.
     */
    @Test
    public void amIAlive() {
        //do nothing.
    }

    /**
     *  If turn player wins, and we're maxing values, then return 1.
     *  If opposing player wins, and we're mining values, then return -1.
     *  If turn player loses, and we're mining values, then return 1;
     *  If opposing player loses, and we're maxing values, then return -1;
     */
    @Test
    public void scoreGameBoard_correctlyRateTerminalPositions() {

        //test
        int xPlayerXVictoryMaxing = classUnderTest.scoreGameBoard(terminalGameBoardXWins, X, MAX);
        int xPlayerXVictoryMining = classUnderTest.scoreGameBoard(terminalGameBoardXWins, X, MIN);
        int xPlayerOVictoryMaxing = classUnderTest.scoreGameBoard(terminalGameBoardOWins, X, MAX);
        int xPlayerOVictoryMining = classUnderTest.scoreGameBoard(terminalGameBoardOWins, X, MIN);

        int oPlayerXVictoryMaxing = classUnderTest.scoreGameBoard(terminalGameBoardXWins, O, MAX);
        int oPlayerXVictoryMining = classUnderTest.scoreGameBoard(terminalGameBoardXWins, O, MIN);
        int oPlayerOVictoryMaxing = classUnderTest.scoreGameBoard(terminalGameBoardOWins, O, MAX);
        int oPlayerOVictoryMining = classUnderTest.scoreGameBoard(terminalGameBoardOWins, O, MIN);

        int xPlayerDrawGameMaxing = classUnderTest.scoreGameBoard(terminalGameBoardDraw, X, MAX);
        int xPlayerDrawGameMining = classUnderTest.scoreGameBoard(terminalGameBoardDraw, X, MIN);
        int oPlayerDrawGameMaxing = classUnderTest.scoreGameBoard(terminalGameBoardDraw, O, MAX);
        int oPlayerDrawGameMining = classUnderTest.scoreGameBoard(terminalGameBoardDraw, O, MIN);

        //verify
        assertEquals( 1, xPlayerXVictoryMaxing);
        assertEquals( -1, xPlayerXVictoryMining );
        assertEquals( -1, xPlayerOVictoryMaxing );
        assertEquals( 1, xPlayerOVictoryMining );

        assertEquals( -1, oPlayerXVictoryMaxing);
        assertEquals( 1, oPlayerXVictoryMining );
        assertEquals( 1, oPlayerOVictoryMaxing );
        assertEquals( -1, oPlayerOVictoryMining );

        assertEquals( 0, xPlayerDrawGameMaxing );
        assertEquals( 0, xPlayerDrawGameMining );
        assertEquals( 0, oPlayerDrawGameMaxing );
        assertEquals( 0, oPlayerDrawGameMining );

    }

    /**
     * For a position which favors an X victory, should score highly for an X move when maxing,
     * and conversely, should score low for an X move when mining.
     */
    @Test
    public void scoreGameBoard_correctlyRateGameWithXPathToVictory() {

        //test
        int scoreForXMoveWhenMaxing = classUnderTest.scoreGameBoard(midGameBoardXHasAdvantage, X, MAX );
        int scoreForXMoveWhenMining = classUnderTest.scoreGameBoard(midGameBoardXHasAdvantage, X, MIN );

        //verify
        assertEquals( 1, scoreForXMoveWhenMaxing );
        assertEquals( -1, scoreForXMoveWhenMining );

    }


    /**
     * Expect to score 1 in a position as O with O as the turn player, and a possible way for O to win.
     * Expect to score -1 in a position as X with O as the turn player, and a possible way for O to win.
     */
    @Test
    public void scoreGameBoard_correctlyRateLateGameWithOToWin() {

        //test
        int oScoreOToMoveWithVictoryPath = classUnderTest.scoreGameBoard(midGameBoardOToWin, O, MAX);
        int xScoreOToMoveWithVictoryPath = classUnderTest.scoreGameBoard(midGameBoardOToWin, X, MIN);

        //verify
        assertEquals( 1, oScoreOToMoveWithVictoryPath );
        assertEquals( -1, xScoreOToMoveWithVictoryPath );
    }


    /**
     * No matter who goes first or which player is scoring a blank game board, the result should always be 0.
     * This is because the expected result for a perfect game of tic tac toe is always a draw.
     */
    @Test
    public void scoreGameBoard_BlankGameBoardShouldBeDrawGame() {

        //test
        int xScoreXToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, X, MAX);
        int xScoreOToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, X, MIN);
        int oScoreOToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, O, MAX);
        int oScoreXToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, O, MIN);

        //verify
        assertEquals( 0, xScoreXToMoveFirstBlankGame );
        assertEquals( 0, xScoreOToMoveFirstBlankGame );
        assertEquals( 0, oScoreOToMoveFirstBlankGame );
        assertEquals( 0, oScoreXToMoveFirstBlankGame );

    }


}
