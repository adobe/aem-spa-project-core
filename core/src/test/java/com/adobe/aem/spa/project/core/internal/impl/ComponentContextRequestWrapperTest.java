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
import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.commons.WCMUtils;

import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_COMPONENT_CONTEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ComponentContextRequestWrapperTest {

    @Mock
    private ComponentContext originalContext;

    @Mock
    private HierarchyComponentContextWrapper childContext;

    private SlingHttpServletRequest originalRequest;

    @BeforeEach
    void beforeEach() {
        originalRequest = new MockSlingHttpServletRequest(MockOsgi.newBundleContext());
        originalRequest.setAttribute(ATTR_COMPONENT_CONTEXT, originalContext);
    }

    @Test
    void testContextIsSavedOnWrap() {
        // when
        SlingHttpServletRequest childRequest = new ComponentContextRequestWrapper(originalRequest, childContext);

        // then
        assertEquals(childContext, WCMUtils.getComponentContext(childRequest));
    }

    @Test
    void testWrappingDoesNotChangeOriginal() {
        // when
        new ComponentContextRequestWrapper(originalRequest, childContext);

        // then
        assertEquals(originalContext, WCMUtils.getComponentContext(originalRequest));
    }

    @Test
    void testContextIsImmutableAndPassedToOriginal() {
        // having
        HierarchyComponentContextWrapper newContext = mock(HierarchyComponentContextWrapper.class);
        SlingHttpServletRequest childRequest = new ComponentContextRequestWrapper(originalRequest, childContext);

        // when
        childRequest.setAttribute(ATTR_COMPONENT_CONTEXT, newContext);

        // then
        assertEquals(childContext, WCMUtils.getComponentContext(childRequest));
        assertEquals(newContext, WCMUtils.getComponentContext(originalRequest));
    }

    @Test
    void testChangingOriginalDoesNotAffectWrapper() {
        // having
        HierarchyComponentContextWrapper newContext = mock(HierarchyComponentContextWrapper.class);
        SlingHttpServletRequest childRequest = new ComponentContextRequestWrapper(originalRequest, childContext);

        // when
        originalRequest.setAttribute(ATTR_COMPONENT_CONTEXT, newContext);

        // then
        assertEquals(childContext, WCMUtils.getComponentContext(childRequest));
        assertEquals(newContext, WCMUtils.getComponentContext(originalRequest));
    }

}
