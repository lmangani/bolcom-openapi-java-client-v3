package com.bol.openapi.client.exception;

/**
 * Basic API exception.
 */
public class BasicApiException extends Exception {

    private static final long serialVersionUID = -3465382844092838434L;
    private String status;

    /**
     * Constructs the basic API exception.
     */
    public BasicApiException() {
        super();
    }

    /**
     * Constructs the basic API exception.
     * 
     * @param throwable The throwable.
     */
    public BasicApiException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructs the basic API exception.
     * 
     * @param status The status.
     * @param throwable The throwable.
     */
    public BasicApiException(final String status, final Throwable throwable) {
        super(throwable);
        this.status = status;
    }

    /**
     * Constructs the basic API exception.
     * 
     * @param message The message.
     */
    public BasicApiException(final String message) {
        super(message);
    }

    /**
     * Constructs the basic API exception.
     * 
     * @param status The status.
     * @param message The message.
     */
    public BasicApiException(final String status, final String message) {
        super(message);
        this.status = status;
    }

    /**
     * Gets the status.
     * 
     * @return The status.
     */
    public String getStatus() {
        return status;
    }
}
