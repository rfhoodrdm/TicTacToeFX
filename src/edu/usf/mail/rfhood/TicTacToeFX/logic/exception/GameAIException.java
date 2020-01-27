package edu.usf.mail.rfhood.TicTacToeFX.logic.exception;

public abstract class GameAIException extends Exception {

    public GameAIException() {
    }

    public GameAIException(String message) {
        super(message);
    }

    public GameAIException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameAIException(Throwable cause) {
        super(cause);
    }

    public GameAIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
