package com.bol.openapi.client.exception;

import org.junit.Assert;
import org.junit.Test;

public class BasicApiExceptionTest {

    @Test
    public void testBasicApiException() {
        final BasicApiException basicApiException = new BasicApiException();
        Assert.assertNotNull(basicApiException);
    }

    @Test
    public void testBasicApiExceptionThrowable() {
        final Throwable throwable = new ArithmeticException();
        final BasicApiException basicApiException = new BasicApiException(throwable);
        Assert.assertNotNull(basicApiException);
        Assert.assertTrue(basicApiException.getCause() instanceof ArithmeticException);
    }

    @Test
    public void testBasicApiExceptionStatusAndThrowable() {
        final Throwable throwable = new ArithmeticException();
        final BasicApiException basicApiException = new BasicApiException("status", throwable);
        Assert.assertNotNull(basicApiException);
        Assert.assertTrue(basicApiException.getCause() instanceof ArithmeticException);
        Assert.assertEquals("status", basicApiException.getStatus());
    }

    @Test
    public void testBasicApiExceptionMessage() {
        final BasicApiException basicApiException = new BasicApiException("message");
        Assert.assertNotNull(basicApiException);
        Assert.assertEquals("message", basicApiException.getMessage());
    }

    @Test
    public void testBasicApiExceptionStatusAndMessage() {
        final BasicApiException basicApiException = new BasicApiException("status", "message");
        Assert.assertNotNull(basicApiException);
        Assert.assertEquals("status", basicApiException.getStatus());
        Assert.assertEquals("message", basicApiException.getMessage());
    }
}
