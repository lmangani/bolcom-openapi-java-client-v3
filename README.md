Overview
===========================================================================
This library can be used to do the following requests:
-	Ping: Pings the OpenAPI server.
-	Search: Searches for products.
-	getList: Gets the product or category/refinement results list.
-	getProduct: Gets the product.
-	getProductRecommendations: Gets the product recommendations.
-	getAnonymousSession: Returns an anonymousSession, which can be use to manage an anonymous basket.
-	getBasket: Return the basket associated with given sessionId
-	addItemToBasket: Adds the offer with offerId to basket, associated with the sessionId.
-	changeBasketItemQuantity: Changes the quantity of basketItem to given quantity.
-	removeBasketItemFromBasket: Removes basketItem from basket that is associated with the sessionId
This library uses jaxb to convert the xml-responses to equivalent java objects.

Requirements
===========================================================================
-	JDK (6+)
-	Maven 2.x
-	Apache HttpClient 4.1.2/3
-	Apache CommonsLang 2.6
-	Apache CommonsCodec 1.5
-	JodaTime 2.0
-	JAXB 2.2.4-1/5
Howto:
===========================================================================

Basic examples:
---------------------------------------------------------------------------
Create a new instance of OpenApiClient. Its constructor requires 4 parameters
-	HttpClient: (for more information check: http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html) 
-	URL: bol.com openapi url: https://openapi.bol.com
-	AccessKeyId: user’s accessKeyId
-	SecretAccessKey: user’s secretAccessKey
OpenApiClient openApiClient = new OpenApiClient(new DefaultHttpClient(),new URI("https://openapi.bol.com"), ACCESS_KEY_ID, SECRET_ACCESS_KEY);
openApiClient.ping(); //ping bol.com openapi server.


Extended example:
----------------------------------------------------------------------------
Please check OpenApiClientIntegration test class for more examples
