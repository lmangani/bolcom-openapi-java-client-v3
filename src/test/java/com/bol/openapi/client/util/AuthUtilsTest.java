package com.bol.openapi.client.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicRequestLine;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class AuthUtilsTest {

    @Test
    public void testHandleRequestAndAccessKeyIdAndSecretAccessKey() throws Exception {
        final HttpRequest request = new HttpGet("/");

        AuthUtils.handleRequest(request, "accessKeyId", "secretAccessKey");

        Assert.assertEquals(1, request.getHeaders("Date").length);
        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Authorization").length);
    }

    @Test
    public void testHandleRequestAndAccessKeyIdAndSecretAccessKeyAndSessionId() throws Exception {
        final HttpRequest request = new HttpGet("/");

        AuthUtils.handleRequest(request, "accessKeyId", "secretAccessKey", "sessionId");

        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Session-ID").length);
        Assert.assertEquals(1, request.getHeaders("Date").length);
        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Authorization").length);
    }

    @Test
    public void testHandleRequestAndAccessKeyIdAndSecretAccessKeyAndSessionIdAndHttpParams() throws Exception {
        final HttpRequest request = new HttpGet("/");
        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add(new BasicNameValuePair("term", "test"));

        AuthUtils.handleRequest(request, "accessKeyId", "secretAccessKey", "sessionId", httpParams);

        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Session-ID").length);
        Assert.assertEquals(1, request.getHeaders("Date").length);
        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Authorization").length);
    }

    @Test
    public void testHandleRequestAndAccessKeyIdAndSecretAccessKeyAndSessionIdAndBodyAndHttpParams() throws Exception {
        final HttpRequest request = new HttpPost("/");
        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add(new BasicNameValuePair("term", "test"));

        AuthUtils.handleRequest(request, "accessKeyId", "secretAccessKey", "sessionId", "body", httpParams);

        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Session-ID").length);
        Assert.assertEquals(1, request.getHeaders("Content-Type").length);
        Assert.assertEquals(1, request.getHeaders("Content-MD5").length);
        Assert.assertEquals(1, request.getHeaders("Date").length);
        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Authorization").length);
    }

    @Test
    public void testHandleRequestAndAccessKeyIdAndSecretAccessKeyAndNoSessionIdAndNoBodyAndHttpParams() throws Exception {
        final HttpRequest request = new HttpPost("/");
        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add(new BasicNameValuePair("term", "test"));

        AuthUtils.handleRequest(request, "accessKeyId", "secretAccessKey", null, null, httpParams);

        Assert.assertEquals(0, request.getHeaders("X-OpenAPI-Session-ID").length);
        Assert.assertEquals(1, request.getHeaders("Content-Type").length);
        Assert.assertEquals(0, request.getHeaders("Content-MD5").length);
        Assert.assertEquals(1, request.getHeaders("Date").length);
        Assert.assertEquals(1, request.getHeaders("X-OpenAPI-Authorization").length);
    }

    @Test
    public void testCalculateHMAC256() throws Exception {
        final String stringToSign = AuthUtils.calculateHMAC256("stringToSign", "secretAccessKey");
        Assert.assertNotNull(stringToSign);
    }

    @Test
    public void testAuthUtilsPrivateConstructor() throws Exception {
        final AuthUtils authUtils = Whitebox.invokeConstructor(AuthUtils.class);
        Assert.assertNotNull(authUtils);
    }
    
    @Test
    public void testCreateStringToSign() throws Exception {
        final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
        final RequestLine requestLine = new BasicRequestLine("", "/", protocolVersion);
        final HttpRequest request = new BasicHttpRequest(requestLine);
        request.addHeader("X-OpenAPI-Date", "11-11-2011");
        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add(new BasicNameValuePair("term", "test"));
        httpParams.add(new BasicNameValuePair("term", "test2"));       
        
        final String stringToSign = AuthUtils.createStringToSign(request, httpParams);
        Assert.assertNotNull(stringToSign);
    }
    
    @Test
    public void testCreateStringToSignAlternativeFlow() throws Exception {
        final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
        final RequestLine requestLine = new BasicRequestLine("WHAZA", "/", protocolVersion);
        final HttpRequest request = new BasicHttpRequest(requestLine);
        request.addHeader("Date", "11-11-2011");
        request.addHeader("X-OpenAPI-Authorization", "zomg");
        final List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add(new BasicNameValuePair("term", null));      
        
        final String stringToSign = AuthUtils.createStringToSign(request, httpParams);
        Assert.assertNotNull(stringToSign);
    }
}
