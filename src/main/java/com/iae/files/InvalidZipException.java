package com.iae.files;

public class InvalidZipException extends Exception {

    public InvalidZipException(String message) {
        super(message);
    }

    public InvalidZipException(String message, Throwable cause) {
        super(message, cause);
    }
}
