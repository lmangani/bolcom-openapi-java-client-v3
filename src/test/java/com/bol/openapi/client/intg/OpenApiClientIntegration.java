package com.bol.openapi.client.intg;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ParseException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bol.openapi.client.OpenApiClient;
import com.bol.openapi.client.exception.BasicApiException;
import com.bol.openapi.client.request.ListResultRequest;
import com.bol.openapi.client.request.ListResultRequest.SortingMethod;
import com.bol.openapi.client.request.ListResultRequest.Type;
import com.bol.openapi.client.request.ProductRecommendationsRequest;
import com.bol.openapi.client.request.SearchResultsRequest;
import com.bol.openapi.openapi_3_0.BasketItem;
import com.bol.openapi.openapi_3_0.BasketResponse;
import com.bol.openapi.openapi_3_0.ListResultResponse;
import com.bol.openapi.openapi_3_0.ProductRecommendationsResponse;
import com.bol.openapi.openapi_3_0.SearchResultsResponse;
import com.bol.openapi.openapi_3_0.SessionResponse;

public class OpenApiClientIntegration {
    private static final String SECRET_ACCESS_KEY = "";
    private static final String ACCESS_KEY_ID = "";
    private URI uri;
    private OpenApiClient openApiClient;
    @Before
    public void setUp() throws Exception {
        Assert.assertFalse("Oeps, ACCESS_KEY_ID not assigned!!!111oneone!", StringUtils.isEmpty(ACCESS_KEY_ID));
        Assert.assertFalse("Oeps, SECRET_ACCESS_KEY not assigned!!!111oneone!", StringUtils.isEmpty(SECRET_ACCESS_KEY));
        uri = new URI("https://openapi.bol.com");
        openApiClient = new OpenApiClient(new DefaultHttpClient(), uri, ACCESS_KEY_ID, SECRET_ACCESS_KEY);
    }

    @Test
    public void testRecommendations() throws IOException, JAXBException, URISyntaxException, BasicApiException{
        ProductRecommendationsRequest request = new ProductRecommendationsRequest(1001004011817640L);
        request.setIncludeAllOffers(true).setIncludeAttributes(true).setIncludeProducts(true).setNrProducts(2).setOffset(0);
        ProductRecommendationsResponse resp = openApiClient.getProductRecommendations(request );
        Assert.assertNotNull(resp);
        Assert.assertEquals(2, resp.getProduct().size());
    }
    
    private String getAnonymousSession() throws ParseException, URISyntaxException, JAXBException, BasicApiException, IOException{
        SessionResponse session = openApiClient.getAnonymousSession();
        return session != null ? session.getSessionId() : null;
    }
    
    @Test
    public void testSearchResult() throws IOException, JAXBException, URISyntaxException, BasicApiException{
        SearchResultsRequest req = new SearchResultsRequest("harry");
        req.setCategoryId("8299");//only in books
        req.setIncludeAttributes(false);
        req.setIncludeCategories(true);
        req.setIncludeProducts(true);
        req.setIncludeRefinements(false);
        req.setNrProducts(10);
        req.setOffset(0L);
        req.setRefinementIds(Arrays.asList("7419","1283","1285","7373","5260"));//with some refinements
        req.setSortingAscending(false);
        req.setSortingMethod(com.bol.openapi.client.request.SearchResultsRequest.SortingMethod.SALES_RANKING);


        SearchResultsResponse resp = openApiClient.search(req);
        Assert.assertNotNull(resp);
        Assert.assertEquals(10, resp.getProduct().size());
        Assert.assertEquals(0, resp.getRefinementGroup().size());
        Assert.assertNull(resp.getProduct().get(0).getAttributes());
    }

    @Test
    public void testGetList() throws IOException, JAXBException, URISyntaxException, BasicApiException{
        ListResultRequest req = new ListResultRequest(Type.TOPLIST_DEFAULT, "8299");//Books
        req.setIncludeAttributes(false);
        req.setIncludeCategories(true);
        req.setIncludeProducts(true);
        req.setIncludeRefinements(false);
        req.setNrProducts(10);
        req.setOffset(0L);
        req.setRefinementIds(Arrays.asList("7419","1283","1285","7373","5260"));//with some refinements
        req.setSortingAscending(false);
        req.setSortingMethod(SortingMethod.SALES_RANKING);


        ListResultResponse resp = openApiClient.getList(req);
        Assert.assertNotNull(resp);
        Assert.assertEquals(10, resp.getProduct().size());
        Assert.assertEquals(0, resp.getRefinementGroup().size());
        Assert.assertNull(resp.getProduct().get(0).getAttributes());
    }
    
    @Test
    public void testBasketOperations() throws ParseException, URISyntaxException, JAXBException, BasicApiException, IOException{
        String session = getAnonymousSession();
        Assert.assertFalse(StringUtils.isEmpty(session));
        
        BasketResponse basket = openApiClient.getBasket(session);
        Assert.assertNotNull(basket);
        
        //Add offer to basket
        long offerId = 1001004011817640L;
        boolean response = openApiClient.addItemToBasket(session, offerId, 1, "10.10.10.10");
        Assert.assertTrue(response);
        
        //Check if the added item is in our basket
        basket = openApiClient.getBasket(session);
        Assert.assertNotNull(basket);
        
        List<BasketItem> basketItems = basket.getBasket().getBasketItem();
        Assert.assertNotNull(basketItems);
        Assert.assertFalse(basketItems.isEmpty());
        
        String basketItemId = null;
        boolean containsProduct = false;
        for (BasketItem basketItem : basketItems) {
            if(basketItem.getProduct().getId() == offerId){
                containsProduct = true;
                basketItemId = basketItem.getId();
                break;
            }
        }
        Assert.assertTrue(containsProduct);
        
        //Changing the quantity
        boolean quantityChanged = openApiClient.changeBasketItemQuantity(session, basketItemId , 2);
        Assert.assertTrue(quantityChanged);
        
        //Checking if the quantity is 2
        //Check if the added item is in our basket
        basket = openApiClient.getBasket(session);
        Assert.assertNotNull(basket);
        
        basketItems = basket.getBasket().getBasketItem();
        Assert.assertNotNull(basketItems);
        Assert.assertFalse(basketItems.isEmpty());
        
        int quantity = 0;
        for (BasketItem basketItem : basketItems) {
            if(basketItem.getProduct().getId() == offerId){
                quantity = basketItem.getQuantity();
                break;
            }
        }
        Assert.assertEquals(2, quantity);
        
        //Now deleting the item
        boolean itemDeleted = openApiClient.removeBasketItemFromBasket(session, basketItemId);
        Assert.assertTrue(itemDeleted);
        
        //Check if the item is really deleted (Assertion)
        basket = openApiClient.getBasket(session);
        Assert.assertNotNull(basket);
        
        basketItems = basket.getBasket().getBasketItem();
        Assert.assertNotNull(basketItems);
        
        containsProduct = false;
        for (BasketItem basketItem : basketItems) {
            if(basketItem.getProduct().getId() == offerId){
                containsProduct = true;
                break;
            }
        }
        Assert.assertFalse(containsProduct);
        
    }
    
}
