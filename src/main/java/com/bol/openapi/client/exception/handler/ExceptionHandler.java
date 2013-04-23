package com.bol.openapi.client.exception.handler;

import com.bol.openapi.client.exception.BasicApiException;

/**
 * Handles errors and returns meaningful exceptions.
 */
public final class ExceptionHandler {
    
    private ExceptionHandler() {        
    }

    /**
     * Handles a basic API error as an exception.
     * 
     * @param error The API error.
     * 
     * @return The basic API exception.
     */
    public static BasicApiException handleBasicApiException(final com.bol.openapi.openapi_3_0.Error error) {
        return new BasicApiException(error.getStatus(), error.getMessage());
    }
}
