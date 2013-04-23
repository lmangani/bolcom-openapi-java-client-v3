package com.bol.openapi.client.exception.handler;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.bol.openapi.client.exception.BasicApiException;

public class ExceptionHandlerTest {    
    
    @Test
    public void testHandleBasicApiException() {
        final com.bol.openapi.openapi_3_0.Error error = new com.bol.openapi.openapi_3_0.Error();
        error.setMessage("message");
        error.setStatus("status");
        final BasicApiException basicApiException = ExceptionHandler.handleBasicApiException(error);
        Assert.assertEquals("message", basicApiException.getMessage());
        Assert.assertEquals("status", basicApiException.getStatus());
    }
    
    @Test
    public void testExceptionHandlerPrivateConstructor() throws Exception {        
        final ExceptionHandler exceptionHandler = Whitebox.invokeConstructor(ExceptionHandler.class);
        Assert.assertNotNull(exceptionHandler);
    }
}
