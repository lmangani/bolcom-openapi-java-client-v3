package com.bol.openapi.client.request;


public class ProductRecommendationsRequest {
    private Long productId;
    private Boolean includeProducts;
    private Boolean includeAttributes;
    private Boolean includeAllOffers;
    private Integer nrProducts;
    private Integer offset;
    
    
    public ProductRecommendationsRequest(long productId){
        this.productId = productId;
    }
    
    public Long getProductId() {
        return productId;
    }

    public Boolean getIncludeProducts() {
        return includeProducts;
    }

    public Boolean getIncludeAttributes() {
        return includeAttributes;
    }

    public Boolean getIncludeAllOffers() {
        return includeAllOffers;
    }

    public Integer getNrProducts() {
        return nrProducts;
    }

    public Integer getOffset() {
        return offset;
    }

    public ProductRecommendationsRequest setProductId(long productId) {
        this.productId = productId;
        return this;
    }

    public ProductRecommendationsRequest setIncludeProducts(boolean includeProducts) {
        this.includeProducts = includeProducts;
        return this;
    }

    public ProductRecommendationsRequest setIncludeAttributes(boolean includeAttributes) {
        this.includeAttributes = includeAttributes;
        return this;
    }

    public ProductRecommendationsRequest setIncludeAllOffers(boolean includeAllOffers) {
        this.includeAllOffers = includeAllOffers;
        return this;
    }

    public ProductRecommendationsRequest setNrProducts(int nrProducts) {
        this.nrProducts = nrProducts;
        return this;
    }

    public ProductRecommendationsRequest setOffset(int offset) {
        this.offset = offset;
        return this;
    }

}
