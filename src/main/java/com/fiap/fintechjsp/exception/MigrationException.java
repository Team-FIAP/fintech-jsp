package com.fiap.fintechjsp.exception;

public class MigrationException extends RuntimeException {
    public MigrationException(String filename, String errorMessage) {
        super("Erro ao aplicar a migration " + filename + ": "  + errorMessage);
    }

    public MigrationException(String message) {
        super(message);
    }

    public MigrationException() {
        super();
    }

    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationException(Throwable cause) {
        super(cause);
    }

    protected MigrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
