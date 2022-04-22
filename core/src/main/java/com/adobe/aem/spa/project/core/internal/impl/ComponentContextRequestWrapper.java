/* ************************************************************************
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2022 Adobe
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe and its suppliers, if any. The intellectual
 * and technical concepts contained herein are proprietary to Adobe
 * and its suppliers and are protected by all applicable intellectual
 * property laws, including trade secret and copyright laws.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe.
 **************************************************************************/
package com.adobe.aem.spa.project.core.internal.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;

import com.day.cq.wcm.api.components.ComponentContext;

/**
 * Custom request wrapper which allows to store {@link ComponentContext#CONTEXT_ATTR_NAME} attribute.
 */
public class ComponentContextRequestWrapper extends SlingHttpServletRequestWrapper {
    private final Object componentContext;

    public ComponentContextRequestWrapper(SlingHttpServletRequest wrappedRequest, HierarchyComponentContextWrapper componentContext) {
        super(wrappedRequest);
        this.componentContext = componentContext;
    }

    @Override
    public Object getAttribute(String name) {
        return ComponentContext.CONTEXT_ATTR_NAME.equals(name) ? this.componentContext : super.getAttribute(name);
    }

}
