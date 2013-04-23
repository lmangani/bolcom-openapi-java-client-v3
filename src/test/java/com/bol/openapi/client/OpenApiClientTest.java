package com.bol.openapi.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.bol.openapi.client.exception.BasicApiException;
import com.bol.openapi.client.request.ListResultRequest;
import com.bol.openapi.client.request.SearchResultsRequest;
import com.bol.openapi.client.util.TestUtils;
import com.bol.openapi.openapi_3_0.ListResultResponse;
import com.bol.openapi.openapi_3_0.ProductRecommendationsResponse;
import com.bol.openapi.openapi_3_0.ProductResponse;
import com.bol.openapi.openapi_3_0.SearchResultsResponse;

public class OpenApiClientTest {

    private final HttpClient httpClient = Mockito.mock(HttpClient.class);
    private URI uri;
    private OpenApiClient openApiClient;

    @Before
    public void setUp() throws Exception {
        uri = new URI("http://localhost:8082");
        openApiClient = new OpenApiClient(httpClient, uri, "accessKeyId", "secretAccessKey");
    }

    @Test
    public void testPingGood() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        Assert.assertTrue(openApiClient.ping());
    }

    @Test
    public void testPingBad() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_BAD_GATEWAY, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        Assert.assertFalse(openApiClient.ping());
    }

    @Test
    public void testSearchWithCategoryId() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final SearchResultsResponse searchResultsResponse = new SearchResultsResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(searchResultsResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final SearchResultsRequest searchResultsRequest = new SearchResultsRequest("test");
        searchResultsRequest.setCategoryId("0");
        final SearchResultsResponse searchResultsResponse = openApiClient.search(searchResultsRequest);
        Assert.assertNotNull(searchResultsResponse);
    }

    @Test
    public void testSearchWithRefinements() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final SearchResultsResponse searchResultsResponse = new SearchResultsResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(searchResultsResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final SearchResultsRequest searchResultsRequest = new SearchResultsRequest("test");
        final List<String> refinementIds = new ArrayList<String>();
        refinementIds.add("123");
        refinementIds.add("456");
        refinementIds.add("678");
        searchResultsRequest.setRefinementIds(refinementIds);
        final SearchResultsResponse searchResultsResponse = openApiClient.search(searchResultsRequest);
        Assert.assertNotNull(searchResultsResponse);
    }
    
    @Test
    public void testSearchWithEmptyRefinements() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final SearchResultsResponse searchResultsResponse = new SearchResultsResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(searchResultsResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final SearchResultsRequest searchResultsRequest = new SearchResultsRequest("test");        
        searchResultsRequest.setRefinementIds(new ArrayList<String>());
        final SearchResultsResponse searchResultsResponse = openApiClient.search(searchResultsRequest);
        Assert.assertNotNull(searchResultsResponse);
    }

    @Test
    public void testSearchWithAllParameters() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final SearchResultsResponse searchResultsResponse = new SearchResultsResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(searchResultsResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final SearchResultsRequest searchResultsRequest = new SearchResultsRequest("test");
        searchResultsRequest.setCategoryId("0");
        searchResultsRequest.setIncludeCategories(true);
        searchResultsRequest.setIncludeProducts(true);
        searchResultsRequest.setIncludeRefinements(true);
        searchResultsRequest.setNrProducts(10);
        searchResultsRequest.setOffset(0L);
        searchResultsRequest.setSortingAscending(true);
        searchResultsRequest.setSortingMethod(SearchResultsRequest.SortingMethod.SALES_RANKING);
        final List<String> refinementIds = new ArrayList<String>();
        refinementIds.add("123");
        searchResultsRequest.setRefinementIds(refinementIds);
        final SearchResultsResponse searchResultsResponse = openApiClient.search(searchResultsRequest);
        Assert.assertNotNull(searchResultsResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSearchMissingTerm() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final com.bol.openapi.openapi_3_0.Error error = new com.bol.openapi.openapi_3_0.Error();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(error));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final SearchResultsRequest searchResultsRequest = new SearchResultsRequest("test");
        searchResultsRequest.setTerm(null);
        openApiClient.search(searchResultsRequest);
    }

    @Test(expected = BasicApiException.class)
    public void testSearchError() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final com.bol.openapi.openapi_3_0.Error error = new com.bol.openapi.openapi_3_0.Error();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(error));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final SearchResultsRequest searchResultsRequest = new SearchResultsRequest("test");
        openApiClient.search(searchResultsRequest);
    }

    @Test
    public void testGetList() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final ListResultResponse listResultResponse = new ListResultResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(listResultResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ListResultRequest listResultRequest = new ListResultRequest(ListResultRequest.Type.NEW, "0");
        final ListResultResponse listResultResponse = openApiClient.getList(listResultRequest);
        Assert.assertNotNull(listResultResponse);
    }

    @Test
    public void testGetListWithAllParameters() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final ListResultResponse listResultResponse = new ListResultResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(listResultResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ListResultRequest listResultRequest = new ListResultRequest(ListResultRequest.Type.NEW, "0");
        listResultRequest.setIncludeCategories(true);
        listResultRequest.setIncludeProducts(true);
        listResultRequest.setIncludeRefinements(true);
        listResultRequest.setNrProducts(10);
        listResultRequest.setOffset(0L);
        listResultRequest.setSortingAscending(true);
        listResultRequest.setSortingMethod(ListResultRequest.SortingMethod.SALES_RANKING);
        final List<String> refinementIds = new ArrayList<String>();
        refinementIds.add("123");
        listResultRequest.setRefinementIds(refinementIds);
        final ListResultResponse listResultResponse = openApiClient.getList(listResultRequest);
        Assert.assertNotNull(listResultResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListRequestMissingType() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final com.bol.openapi.openapi_3_0.Error error = new com.bol.openapi.openapi_3_0.Error();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(error));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ListResultRequest listResultRequest = new ListResultRequest(ListResultRequest.Type.NEW, "0");
        listResultRequest.setType(null);
        openApiClient.getList(listResultRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListRequestMissingCategoryId() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final com.bol.openapi.openapi_3_0.Error error = new com.bol.openapi.openapi_3_0.Error();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(error));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ListResultRequest listResultRequest = new ListResultRequest(ListResultRequest.Type.NEW, "0");
        listResultRequest.setCategoryId(null);
        openApiClient.getList(listResultRequest);
    }

    @Test(expected = BasicApiException.class)
    public void testListRequestError() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final com.bol.openapi.openapi_3_0.Error error = new com.bol.openapi.openapi_3_0.Error();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(error));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ListResultRequest listResultRequest = new ListResultRequest(ListResultRequest.Type.NEW, "0");
        openApiClient.getList(listResultRequest);
    }

    @Test
    public void testGetProduct() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final ProductResponse productResponse = new ProductResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(productResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ProductResponse productResponse = openApiClient.getProduct("1234567890", true);
        Assert.assertNotNull(productResponse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetProductMissingId() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final ProductResponse productResponse = new ProductResponse();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(productResponse));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ProductResponse productResponse = openApiClient.getProduct(null, false);
        Assert.assertNotNull(productResponse);
    }
    
    @Test(expected = BasicApiException.class)
    public void testGetProductError() throws Exception {
        {
            final ProtocolVersion protocolVersion = new ProtocolVersion("http", 1, 1);
            final StatusLine statusLine = new BasicStatusLine(protocolVersion, HttpStatus.SC_INTERNAL_SERVER_ERROR, "");
            final HttpResponse httpResponse = new BasicHttpResponse(statusLine);

            final com.bol.openapi.openapi_3_0.Error error = new com.bol.openapi.openapi_3_0.Error();
            final HttpEntity httpEntity = new StringEntity(TestUtils.serializeUsingJAXB(error));
            httpResponse.setEntity(httpEntity);

            Mockito.when(httpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        }

        final ProductResponse productResponse = openApiClient.getProduct("1234567890", false);
        Assert.assertNotNull(productResponse);
    }
   
}
