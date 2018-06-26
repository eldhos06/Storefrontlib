<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/addons/storefrontlibrary/responsive" %>


<template:page pageTitle="${pageTitle}">
	<cms:pageSlot position="Section1" var="feature">
        <cms:component component="${feature}" />
    </cms:pageSlot>
	<div class="row">
		<div class="col-xs-3">
			<cms:pageSlot position="ProductLeftRefinements" var="feature" element="div" class="search-grid-page-left-refinements-slot">
				<cms:component component="${feature}" element="div" class="search-grid-page-left-refinements-component"/>
			</cms:pageSlot>
		</div>
		<div class="col-sm-12 col-md-9">
			<cms:pageSlot position="ProductGridSlot" var="feature" element="div" class="search-grid-page-result-grid-slot">
				<cms:component component="${feature}" element="div" class="search-grid-page-result-grid-component"/>
			</cms:pageSlot>
		</div>
	</div>

	<storepickup:pickupStorePopup />

</template:page>