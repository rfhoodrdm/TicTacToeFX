package edu.usf.mail.rfhood.TicTacToeFX.logic.exception;

public class DifficultyOutOfBoundsException extends GameAIException {

    public DifficultyOutOfBoundsException() {
        super("The Game AI cannot make a move for the computer; the difficulty level was invalid. No Strategy could be selected.");
    }
}
