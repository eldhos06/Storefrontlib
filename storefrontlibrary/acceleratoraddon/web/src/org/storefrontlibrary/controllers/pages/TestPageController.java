/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package org.storefrontlibrary.controllers.pages;


import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller for a category page
 */
@Controller
@RequestMapping(value = "/test/cc")
public class TestPageController
{

	@RequestMapping(value = "/getattrval", method = RequestMethod.GET)
	public String customCategory(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException
	{
		System.console().printf("Test Console", "Test Console");
		final StringBuffer x = new StringBuffer();
		x.append("sajfkashfkjakf");

		return "";
	}
}