package com.bol.openapi.client.request;

import java.util.List;

/**
 * Request object for the search results request.
 */
public class SearchResultsRequest {

    private String term;
    private String categoryId;              // optional
    private List<String> refinementIds;     // optional    
    private Boolean includeProducts;        // optional
    private Boolean includeCategories;      // optional
    private Boolean includeRefinements;     // optional
    private Boolean includeAttributes;      // optional
    private SortingMethod sortingMethod;    // optional
    private Boolean sortingAscending;       // optional
    private Integer nrProducts;             // optional
    private Long offset;                    // optional
    private String listId;                  // optional
    
    /**
     * Constructs the search results request.
     * 
     * @param term The search term.
     */
    public SearchResultsRequest(final String term) {
        this.term = term;
    }
    
    public String getTerm() {
        return term;
    }

    public void setTerm(final String term) {
        this.term = term;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getRefinementIds() {
        return refinementIds;
    }

    public void setRefinementIds(final List<String> refinementIds) {
        this.refinementIds = refinementIds;
    }

    public Integer getNrProducts() {
        return nrProducts;
    }

    public void setNrProducts(final Integer nrProducts) {
        this.nrProducts = nrProducts;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(final Long offset) {
        this.offset = offset;
    }

    public SortingMethod getSortingMethod() {
        return sortingMethod;
    }

    public void setSortingMethod(final SortingMethod sortingMethod) {
        this.sortingMethod = sortingMethod;
    }

    public Boolean getSortingAscending() {
        return sortingAscending;
    }

    public void setSortingAscending(final Boolean sortingAscending) {
        this.sortingAscending = sortingAscending;
    }

    public Boolean getIncludeProducts() {
        return includeProducts;
    }

    public void setIncludeProducts(final Boolean includeProducts) {
        this.includeProducts = includeProducts;
    }

    public Boolean getIncludeCategories() {
        return includeCategories;
    }

    public void setIncludeCategories(final Boolean includeCategories) {
        this.includeCategories = includeCategories;
    }

    public Boolean getIncludeRefinements() {
        return includeRefinements;
    }

    public void setIncludeRefinements(final Boolean includeRefinements) {
        this.includeRefinements = includeRefinements;
    }

    public Boolean getIncludeAttributes() {
        return includeAttributes;
    }

    public void setIncludeAttributes(final Boolean includeAttributes) {
        this.includeAttributes = includeAttributes;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public enum SortingMethod {

        SALES_RANKING("sales_ranking"),
        PRICE("price"),
        TITLE("title"),
        PUBLISHING_DATE("publishing_date"),
        CUSTOMER_RATING("customer_rating");
        private final String value;

        SortingMethod(final String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public static SortingMethod fromValue(final String value) {
            for (SortingMethod c : SortingMethod.values()) {
                if (c.value.equals(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(value);
        }
    }
}
