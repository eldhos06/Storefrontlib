<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/addons/storefrontlibrary/responsive" %>

<div id="product-facet" class="hidden-sm hidden-xs product__facet js-product-facet">
    <nav:facetNavAppliedFilters pageData="${searchPageData}"/>
    <nav:facetNavRefinements pageData="${searchPageData}"/>
</div>