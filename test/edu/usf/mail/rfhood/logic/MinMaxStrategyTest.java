package edu.usf.mail.rfhood.logic;

import edu.usf.mail.rfhood.state.GameBoard;
import edu.usf.mail.rfhood.state.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
     *  Expect a score of 1 for X in a terminal position where X wins.
     *  Expect a score of -1 for X in a terminal position where O wins.
     *  Expect a score of 0 for X in a terminal position draw game.
     *  Shouldn't matter if X is the current turn player or not.
     */
    @Test
    public void scoreGameBoard_correctlyRateTerminalPositionForX() {

        //test
        int xWinTurnPlayer = classUnderTest.scoreGameBoard(terminalGameBoardXWins, X, true);
        int xWinOpposingPlayer = classUnderTest.scoreGameBoard(terminalGameBoardXWins, X, false);
        int xLosesTurnPlayer = classUnderTest.scoreGameBoard(terminalGameBoardOWins, X, true);
        int xLosesOpposingPlayer = classUnderTest.scoreGameBoard(terminalGameBoardOWins, X, false);
        int xDrawsTurnPlayer = classUnderTest.scoreGameBoard(terminalGameBoardDraw, X, true);
        int xDrawsOpposingPlayer = classUnderTest.scoreGameBoard(terminalGameBoardDraw, X, false);

        //verify
        assertEquals( 1, xWinTurnPlayer);
        assertEquals( 1, xWinOpposingPlayer );
        assertEquals( -1, xLosesTurnPlayer );
        assertEquals( -1, xLosesOpposingPlayer );
        assertEquals( 0, xDrawsTurnPlayer );
        assertEquals( 0, xDrawsOpposingPlayer );

    }

    /**
     *  Expect a score of 1 for O in a terminal position where O wins.
     *  Expect a score of -1 for O in a terminal position where X wins.
     *  Expect a score of 0 for O in a terminal position draw game.
     *  Shouldn't matter if O is the current turn player or not.
     */
    @Test
    public void scoreGameBoard_correctlyRateTerminalPositionForO() {

        //test
        int oWinTurnPlayer = classUnderTest.scoreGameBoard(terminalGameBoardOWins, O, true);
        int oWinOpposingPlayer = classUnderTest.scoreGameBoard(terminalGameBoardOWins, O, false);
        int oLosesTurnPlayer = classUnderTest.scoreGameBoard(terminalGameBoardXWins, O, true);
        int oLosesOpposingPlayer = classUnderTest.scoreGameBoard(terminalGameBoardXWins, O, false);
        int oDrawsTurnPlayer = classUnderTest.scoreGameBoard(terminalGameBoardDraw, O, true);
        int oDrawsOpposingPlayer = classUnderTest.scoreGameBoard(terminalGameBoardDraw, O, false);

        //verify
        assertEquals( 1, oWinTurnPlayer);
        assertEquals( 1, oWinOpposingPlayer );
        assertEquals( -1, oLosesTurnPlayer );
        assertEquals( -1, oLosesOpposingPlayer );
        assertEquals( 0, oDrawsTurnPlayer );
        assertEquals( 0, oDrawsOpposingPlayer );
    }


}
