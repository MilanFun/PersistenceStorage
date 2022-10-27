package ru.mipt1c.homework.task1;

public class ClosedStorageException extends RuntimeException {
    public ClosedStorageException() {

    }

    public ClosedStorageException(String message) {
        super(message);
    }

    public ClosedStorageException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ClosedStorageException(Throwable throwable) {
        super(throwable);
    }
}
