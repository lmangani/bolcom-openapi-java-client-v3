package com.bol.openapi.client;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.bol.openapi.client.exception.BasicApiException;
import com.bol.openapi.client.exception.handler.ExceptionHandler;
import com.bol.openapi.client.request.ListResultRequest;
import com.bol.openapi.client.request.ProductRecommendationsRequest;
import com.bol.openapi.client.request.SearchResultsRequest;
import com.bol.openapi.client.util.AuthUtils;
import com.bol.openapi.openapi_3_0.BasketResponse;
import com.bol.openapi.openapi_3_0.ListResultResponse;
import com.bol.openapi.openapi_3_0.ObjectFactory;
import com.bol.openapi.openapi_3_0.ProductRecommendationsResponse;
import com.bol.openapi.openapi_3_0.ProductResponse;
import com.bol.openapi.openapi_3_0.SearchResultsResponse;
import com.bol.openapi.openapi_3_0.SessionResponse;

/**
 * Client for OpenAPI based on Apache HTTP client and JAXB.
 */
public class OpenApiClient {

    private HttpClient httpClient;
    private JAXBContext jaxbContext;
    private String accessKeyId;
    private String secretAccessKey;
    private URI uriPrefix;

    /**
     * Constructs the OpenAPI client.
     *
     * @param httpClient      The Apache HTTP client to interact with.
     * @param uri             The URI to connect to.
     * @param accessKeyId     The access key id.
     * @param secretAccessKey The secret access key.
     *
     * @throws JAXBException      When a new JAXB context cannot be instantiated.
     * @throws URISyntaxException When the URI contains a syntax error.
     */
    public OpenApiClient(final HttpClient httpClient, final URI uri, final String accessKeyId, final String secretAccessKey)
            throws JAXBException, URISyntaxException {
        jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        this.httpClient = httpClient;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.uriPrefix = new URI(uri.toString() + "/openapi/services/rest");
    }

    /**
     * Pings the OpenAPI server.
     *
     * @return True when the server pongs back.
     *
     * @throws IOException        When the HTTP execution fails.
     * @throws URISyntaxException When the URI contains a syntax error.
     */
    public boolean ping() throws IOException, URISyntaxException {
        boolean result = false;

        // Handle request
        final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + "/utils/v3/ping", null, null);
        final HttpGet httpGet = new HttpGet(uri);
        AuthUtils.handleRequest(httpGet, accessKeyId, secretAccessKey);

        // Handle response
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse != null) {
            EntityUtils.consume(httpResponse.getEntity());

            result = httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        }

        return result;
    }

    /**
     * Searches for products.
     *
     * @param searchResultsRequest The search results request.
     *
     * @return The search results response.
     *
     * @throws IOException        When the HTTP execution fails.
     * @throws URISyntaxException When the URI contains a syntax error.
     * @throws BasicApiException  When an error response was received.
     * @throws JAXBException      When parsing the response fails.
     */
    public SearchResultsResponse search(final SearchResultsRequest searchResultsRequest)
            throws IOException, URISyntaxException, BasicApiException, JAXBException {
        if (searchResultsRequest.getTerm() == null) {
            throw new IllegalArgumentException("Search term is required");
        }

        SearchResultsResponse searchResultsResponse = null;

        // Prepare request
        final List<NameValuePair> queryParameters = new ArrayList<NameValuePair>();

        // Populate parameters
        queryParameters.add(new BasicNameValuePair("term", searchResultsRequest.getTerm()));
        if (searchResultsRequest.getCategoryId() != null || searchResultsRequest.getRefinementIds() != null) {
            final String combinedCategoryIdAndRefinementIds = combineCategoryAndRefinementIds(searchResultsRequest.getCategoryId(), searchResultsRequest.getRefinementIds());
            queryParameters.add(new BasicNameValuePair("categoryId", combinedCategoryIdAndRefinementIds));
        }
        if (searchResultsRequest.getSortingMethod() != null) {
            queryParameters.add(new BasicNameValuePair("sortingMethod", searchResultsRequest.getSortingMethod().value()));
        }
        if (searchResultsRequest.getSortingAscending() != null) {
            queryParameters.add(new BasicNameValuePair("sortingAscending", searchResultsRequest.getSortingAscending().toString()));
        }
        if (searchResultsRequest.getNrProducts() != null) {
            queryParameters.add(new BasicNameValuePair("nrProducts", searchResultsRequest.getNrProducts().toString()));
        }
        if (searchResultsRequest.getOffset() != null) {
            queryParameters.add(new BasicNameValuePair("offset", searchResultsRequest.getOffset().toString()));
        }
        if (searchResultsRequest.getIncludeProducts() != null) {
            queryParameters.add(new BasicNameValuePair("includeProducts", searchResultsRequest.getIncludeProducts().toString()));
        }
        if (searchResultsRequest.getIncludeCategories() != null) {
            queryParameters.add(new BasicNameValuePair("includeCategories", searchResultsRequest.getIncludeCategories().toString()));
        }
        if (searchResultsRequest.getIncludeRefinements() != null) {
            queryParameters.add(new BasicNameValuePair("includeRefinements", searchResultsRequest.getIncludeRefinements().toString()));
        }
        if (searchResultsRequest.getIncludeAttributes() != null) {
            queryParameters.add(new BasicNameValuePair("includeAttributes", searchResultsRequest.getIncludeAttributes().toString()));
        }
        if(searchResultsRequest.getListId() != null){
            queryParameters.add(new BasicNameValuePair("listId", searchResultsRequest.getListId()));
        }


        // Handle request
        final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + "/catalog/v3/searchresults/", URLEncodedUtils.format(queryParameters, "UTF-8"), null);
        final HttpGet httpGet = new HttpGet(uri);
        AuthUtils.handleRequest(httpGet, accessKeyId, secretAccessKey, null, queryParameters);

        // Handle response
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse != null) {
            final String entity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            final StringReader entityStream = new StringReader(entity);

            try {
                final Object object = jaxbContext.createUnmarshaller().unmarshal(entityStream);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    searchResultsResponse = (SearchResultsResponse) object;
                } else {
                    throw ExceptionHandler.handleBasicApiException((com.bol.openapi.openapi_3_0.Error) object);
                }
            } finally {
                entityStream.close();
            }
        }

        return searchResultsResponse;
    }

    /**
     * Gets the product or category/refinement results list.
     *
     * @param listResultRequest The list result request.
     *
     * @return The list result response.
     *
     * @throws IOException        When the HTTP execution fails.
     * @throws JAXBException      When parsing the response fails.
     * @throws URISyntaxException When the URI contains a syntax error.
     * @throws BasicApiException  When an error response was received.
     */
    public ListResultResponse getList(final ListResultRequest listResultRequest)
            throws IOException, JAXBException, URISyntaxException, BasicApiException {
        if (listResultRequest.getType() == null || listResultRequest.getCategoryId() == null) {
            throw new IllegalArgumentException("Type and category id are required");
        }

        ListResultResponse listResultResponse = null;

        final String combinedCategoryIdAndRefinementIds = combineCategoryAndRefinementIds(listResultRequest.getCategoryId(), listResultRequest.getRefinementIds());

        // Prepare request
        final List<NameValuePair> queryParameters = new ArrayList<NameValuePair>();

        // Populate parameters
        if (listResultRequest.getSortingMethod() != null) {
            queryParameters.add(new BasicNameValuePair("sortingMethod", listResultRequest.getSortingMethod().value()));
        }
        if (listResultRequest.getSortingAscending() != null) {
            queryParameters.add(new BasicNameValuePair("sortingAscending", listResultRequest.getSortingAscending().toString()));
        }
        if (listResultRequest.getNrProducts() != null) {
            queryParameters.add(new BasicNameValuePair("nrProducts", listResultRequest.getNrProducts().toString()));
        }
        if (listResultRequest.getOffset() != null) {
            queryParameters.add(new BasicNameValuePair("offset", listResultRequest.getOffset().toString()));
        }
        if (listResultRequest.getIncludeProducts() != null) {
            queryParameters.add(new BasicNameValuePair("includeProducts", listResultRequest.getIncludeProducts().toString()));
        }
        if (listResultRequest.getIncludeCategories() != null) {
            queryParameters.add(new BasicNameValuePair("includeCategories", listResultRequest.getIncludeCategories().toString()));
        }
        if (listResultRequest.getIncludeRefinements() != null) {
            queryParameters.add(new BasicNameValuePair("includeRefinements", listResultRequest.getIncludeRefinements().toString()));
        }
        if (listResultRequest.getIncludeAttributes() != null) {
            queryParameters.add(new BasicNameValuePair("includeAttributes", listResultRequest.getIncludeAttributes().toString()));
        }
        if(listResultRequest.getListId() != null){
            queryParameters.add(new BasicNameValuePair("listId", listResultRequest.getListId()));
        }

        // Handle request
        final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + "/catalog/v3/listresults/" + listResultRequest.getType().value() + "/" + URLEncoder.encode(combinedCategoryIdAndRefinementIds, "UTF-8"), URLEncodedUtils.format(queryParameters, "UTF-8"), null);
        final HttpGet httpGet = new HttpGet(uri);
        AuthUtils.handleRequest(httpGet, accessKeyId, secretAccessKey, null, queryParameters);

        // Handle response
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse != null) {
            final String entity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            final StringReader entityStream = new StringReader(entity);

            try {
                final Object object = jaxbContext.createUnmarshaller().unmarshal(entityStream);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    listResultResponse = (ListResultResponse) object;
                } else {
                    throw ExceptionHandler.handleBasicApiException((com.bol.openapi.openapi_3_0.Error) object);
                }
            } finally {
                entityStream.close();
            }
        }

        return listResultResponse;
    }

    /**
     * Gets the product.
     *
     * @param id The product id.
     *
     * @return The product response.
     *
     * @throws IOException        When the HTTP execution fails.
     * @throws JAXBException      When parsing the response fails.
     * @throws URISyntaxException When the URI contains a syntax error.
     * @throws BasicApiException  When an error response was received.
     */
    public ProductResponse getProduct(final String id, boolean includeAttributes)
            throws IOException, JAXBException, URISyntaxException, BasicApiException {
        if (id == null) {
            throw new IllegalArgumentException("Product id is required");
        }

        ProductResponse productResponse = null;
        final List<NameValuePair> queryParameters = new ArrayList<NameValuePair>();
        queryParameters.add(new BasicNameValuePair("includeCategories", ""+includeAttributes));
        // Handle request
        final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + "/catalog/v3/products/" + id, URLEncodedUtils.format(queryParameters, "UTF-8"), null);
        final HttpGet httpGet = new HttpGet(uri);
        AuthUtils.handleRequest(httpGet, accessKeyId, secretAccessKey, null, queryParameters);

        // Handle response
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse != null) {
            final String entity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            final StringReader entityStream = new StringReader(entity);

            try {
                final Object object = jaxbContext.createUnmarshaller().unmarshal(entityStream);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    productResponse = (ProductResponse) object;
                } else {
                    throw ExceptionHandler.handleBasicApiException((com.bol.openapi.openapi_3_0.Error) object);
                }
            } finally {
                entityStream.close();
            }
        }

        return productResponse;
    }

    /**
     * Combines the category and refinement ids.
     *
     * @param categoryId    The category id.
     * @param refinementIds The list of refinement ids.
     *
     * @return The combined string of category and refinement ids.
     */
    private String combineCategoryAndRefinementIds(final String categoryId, final List<String> refinementIds) {
        final StringBuilder sb = new StringBuilder();
        if (categoryId != null) {
            sb.append(categoryId);
        }
        if (refinementIds != null && !refinementIds.isEmpty()) {
            sb.append(' ');

            for (int i = 0; i < refinementIds.size(); i++) {
                sb.append(refinementIds.get(i));

                if (i + 1 < refinementIds.size()) {
                    sb.append(' ');
                }
            }
        }

        return sb.toString();
    }

    /**
     * Gets the product recommendations.
     *
     * @param id The product id.
     *
     * @return The product recommendations response.
     *
     * @throws IOException        When the HTTP execution fails.
     * @throws JAXBException      When parsing the response fails.
     * @throws URISyntaxException When the URI contains a syntax error.
     * @throws BasicApiException  When an error response was received.
     */
    public ProductRecommendationsResponse getProductRecommendations(ProductRecommendationsRequest request)
            throws IOException, JAXBException, URISyntaxException, BasicApiException {
        if (request == null || request.getProductId() == 0) {
            throw new IllegalArgumentException("Product id is required");
        }

        ProductRecommendationsResponse productRecommendationsResponse = null;
        
        final List<NameValuePair> queryParameters = new ArrayList<NameValuePair>();

        // Populate parameters
        if (request.getNrProducts() != null) {
            queryParameters.add(new BasicNameValuePair("nrProducts", ""+request.getNrProducts()));
        }
        if(request.getIncludeProducts() != null){
            queryParameters.add(new BasicNameValuePair("includeProducts", ""+request.getIncludeProducts()));
        }
        if(request.getIncludeAttributes()){
            queryParameters.add(new BasicNameValuePair("includeAttributes", ""+request.getIncludeAttributes()));
        }
        if(request.getIncludeAllOffers()){
            queryParameters.add(new BasicNameValuePair("includeAllOffers", ""+request.getIncludeAllOffers()));
        }
        if(request.getOffset() != null){
            queryParameters.add(new BasicNameValuePair("offset", ""+request.getOffset()));
        }

        // Handle request
        final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + "/catalog/v3/recommendations/" + request.getProductId(), URLEncodedUtils.format(queryParameters, "UTF-8"), null);
        final HttpGet httpGet = new HttpGet(uri);
        AuthUtils.handleRequest(httpGet, accessKeyId, secretAccessKey, null, queryParameters);

        // Handle response
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse != null) {
            final String entity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println(entity);
            final StringReader entityStream = new StringReader(entity);

            try {
                final Object object = jaxbContext.createUnmarshaller().unmarshal(entityStream);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    productRecommendationsResponse = (ProductRecommendationsResponse) object;
                } else {
                    throw ExceptionHandler.handleBasicApiException((com.bol.openapi.openapi_3_0.Error) object);
                }
            } finally {
                entityStream.close();
            }
        }

        return productRecommendationsResponse;
    }
    
    /**
     * Returns an anonymousSession, which can be use to manage an anonymous basket.
     * @return Session
     * @throws URISyntaxException
     * @throws JAXBException
     * @throws BasicApiException
     * @throws ParseException
     * @throws IOException
     */
    public SessionResponse getAnonymousSession() throws URISyntaxException, JAXBException, BasicApiException, ParseException, IOException{
        SessionResponse response = null;
        String path = "/auth/v3/session";
        final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + path, null, null);
        final HttpGet httpGet = new HttpGet(uri);
        AuthUtils.handleRequest(httpGet, accessKeyId, secretAccessKey);

        // Handle response
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse != null) {
            final String entity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println(entity);
            final StringReader entityStream = new StringReader(entity);

            try {
                final Object object = jaxbContext.createUnmarshaller().unmarshal(entityStream);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    response = (SessionResponse) object;
                } else {
                    throw ExceptionHandler.handleBasicApiException((com.bol.openapi.openapi_3_0.Error) object);
                }
            } finally {
                entityStream.close();
            }
        }

        return response;
    }
    
    /**
     * @param sessionId (anonymous)
     * @return Basket object
     * @throws URISyntaxException
     * @throws JAXBException
     * @throws BasicApiException
     * @throws ParseException
     * @throws IOException
     */
    public BasketResponse getBasket(String sessionId) throws URISyntaxException, JAXBException, BasicApiException, ParseException, IOException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(sessionId)){
            throw new IllegalArgumentException("session id is required");
        }
        BasketResponse response = null;
        String path = "/checkout/v3/baskets";

        final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + path, null, null);
        final HttpGet httpGet = new HttpGet(uri);
        AuthUtils.handleRequest(httpGet, accessKeyId, secretAccessKey, sessionId);

        // Handle response
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        if (httpResponse != null) {
            final String entity = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println(entity);
            final StringReader entityStream = new StringReader(entity);

            try {
                final Object object = jaxbContext.createUnmarshaller().unmarshal(entityStream);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    response = (BasketResponse) object;
                } else {
                    throw ExceptionHandler.handleBasicApiException((com.bol.openapi.openapi_3_0.Error) object);
                }
            } finally {
                entityStream.close();
            }
        }

        return response;
    }
    
    /**
     * Adds the offer with offerId to basket, associated with the @param sessionId
     * @param sessionId
     * @param offerId: 
     * @param quantity
     * @param ipAddress: client ipAddress
     * @return true if the item is added to basket, associated with the sessionId
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public boolean addItemToBasket(String sessionId, long offerId, int quantity, String ipAddress) throws URISyntaxException, ClientProtocolException, IOException{
        HttpResponse httpResponse = null;
        try {
            String path = String.format("/checkout/v3/baskets/%S/%s/%s", offerId, quantity, ipAddress);

            final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + path, null, null);
            final HttpPost http = new HttpPost(uri);
            AuthUtils.handleRequest(http, accessKeyId, secretAccessKey, sessionId);
            httpResponse = httpClient.execute(http);
            return httpResponse != null ? httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED : false;
        } finally {
            if (httpResponse != null && httpResponse.getEntity() != null) {
                try {
                    // LOG.info("CONSUMING");
                    EntityUtils.consume(httpResponse.getEntity());

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }

        }
    }
    
    /**
     * Changes the quantity of basketItem to given quantity
     * @param sessionId
     * @param basketItemId
     * @param quantity
     * @return true if the quantity of basketItem is changed.
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public boolean changeBasketItemQuantity(String sessionId, String basketItemId, int quantity) throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse httpResponse = null;
        try {

            String path = String.format("/checkout/v3/baskets/%s/%s", basketItemId, quantity);

            final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + path, null, null);
            final HttpPut http = new HttpPut(uri);
            AuthUtils.handleRequest(http, accessKeyId, secretAccessKey, sessionId);
            httpResponse = httpClient.execute(http);
            return httpResponse != null ? httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK : false;
        } finally {
            if (httpResponse != null && httpResponse.getEntity() != null) {
                try {
                    // LOG.info("CONSUMING");
                    EntityUtils.consume(httpResponse.getEntity());

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
    
    /**
     * Removes basketItem from basket that is associated with the sessionId
     * @param sessionId
     * @param basketItemId
     * @return true if the basketItem is removed
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public boolean removeBasketItemFromBasket(String sessionId, String basketItemId) throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse httpResponse = null;
        try {

            String path = String.format("/checkout/v3/baskets/%s", basketItemId);

            final URI uri = URIUtils.createURI(uriPrefix.getScheme(), uriPrefix.getHost(), uriPrefix.getPort(), uriPrefix.getPath() + path, null, null);
            final HttpDelete http = new HttpDelete(uri);
            AuthUtils.handleRequest(http, accessKeyId, secretAccessKey, sessionId);
            httpResponse = httpClient.execute(http);
            return httpResponse != null ? httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK : false;
        } finally {
            if (httpResponse != null && httpResponse.getEntity() != null) {
                try {
                    // LOG.info("CONSUMING");
                    EntityUtils.consume(httpResponse.getEntity());

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

}
