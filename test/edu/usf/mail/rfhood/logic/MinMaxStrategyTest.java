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

    /**
     * Expect to score a 1 for a position for X with X as the turn player, and a possible way for X to force a win.
     * Expect to score a -1 for a position for O with X as the turn player, and a possible way for X to force a win.
     */
    @Test
    public void scoreGameBoard_correctlyRateGameWithXPathToVictory() {

        //test
        int xScoreXToMoveWithVictoryPath = classUnderTest.scoreGameBoard(midGameBoardXHasAdvantage, X, true );
        int oScoreXToMoveWithVictoryPath = classUnderTest.scoreGameBoard(midGameBoardXHasAdvantage, O, false);

        //verify
        assertEquals( 1, xScoreXToMoveWithVictoryPath );
        assertEquals( -1, oScoreXToMoveWithVictoryPath );

    }


    /**
     * Expect to score 1 in a position as O with O as the turn player, and a possible way for O to win.
     * Expect to score -1 in a position as X with O as the turn player, and a possible way for O to win.
     */
    @Test
    public void scoreGameBoard_correctlyRateLateGameWithOToWin() {

        //test
        int oScoreOToMoveWithVictoryPath = classUnderTest.scoreGameBoard(midGameBoardOToWin, O, true);
        int xScoreOToMoveWithVictoryPath = classUnderTest.scoreGameBoard(midGameBoardOToWin, X, false);

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
        int xScoreXToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, X, true);
        int xScoreOToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, X, false);
        int oScoreOToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, O, true);
        int oScoreXToMoveFirstBlankGame = classUnderTest.scoreGameBoard(gameStartBoard, O, false);

        //verify
        assertEquals( 0, xScoreXToMoveFirstBlankGame );
        assertEquals( 0, xScoreOToMoveFirstBlankGame );
        assertEquals( 0, oScoreOToMoveFirstBlankGame );
        assertEquals( 0, oScoreXToMoveFirstBlankGame );

    }


}
