/*
 * Copyright 2020 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.adobe.aem.spa.project.core.internal.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.aem.spa.project.core.internal.HierarchyConstants;
import com.adobe.aem.spa.project.core.models.Page;
import com.adobe.cq.export.json.hierarchy.type.HierarchyTypes;
import com.day.cq.wcm.api.designer.Style;

import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.JSON_EXPORT_SUFFIX;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.PN_STRUCTURE_PATTERNS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PageImplTest {

    private static final String CURRENT_PAGE_PATH = "/path/to/current/page";

    @Mock
    private com.day.cq.wcm.api.Page currentPage;

    @Mock
    private Style currentStyle;

    @Mock
    private ModelFactory modelFactory;

    @Mock
    private SlingHttpServletRequest request;

    @InjectMocks
    private PageImpl page;

    @BeforeEach
    void beforeEach() {
        // Current page
        when(currentPage.getPath()).thenReturn(CURRENT_PAGE_PATH);

        // Current style
        when(currentStyle.get(HierarchyConstants.PN_IS_ROOT, false)).thenReturn(false);

        // Root page
        page.setRootPage(currentPage);

        // Request
        RequestParameter requestParameter = mock(RequestParameter.class);
        when(request.getRequestParameter(eq(PN_STRUCTURE_PATTERNS.toLowerCase()))).thenReturn(requestParameter);
    }

    @Test
    void testGetExportedHierarchyType() {
        assertEquals(HierarchyTypes.PAGE, page.getExportedHierarchyType());
    }

    @Test
    void testGetExportedChildrenModelsIsNotNull() {
        // descendedPageModels is not null
        Map<String, ? extends Page> descendedPageModels = new HashMap<>();
        page.setDescendedPageModels(descendedPageModels);
        assertEquals(descendedPageModels, page.getExportedChildren());
    }

    @Test
    void testGetExportedChildrenModelsIsNull() {
        // descendedPageModels is null
        assertTrue(page.getExportedChildren().isEmpty());
    }

    @Test
    void testGetExportedPath() {
        assertEquals(CURRENT_PAGE_PATH, page.getExportedPath());
    }

    @Test
    void testGetHierarchyRootJsonExportUrlIsNotRoot() {
        // Is not root page
        assertEquals(CURRENT_PAGE_PATH + JSON_EXPORT_SUFFIX, page.getHierarchyRootJsonExportUrl());
    }

    @Test
    void testGetHierarchyRootJsonExportUrlIsRoot() {
        // Is root page
        when(currentStyle.get(HierarchyConstants.PN_IS_ROOT, false)).thenReturn(true);
        assertEquals(CURRENT_PAGE_PATH + JSON_EXPORT_SUFFIX, page.getHierarchyRootJsonExportUrl());
    }

    @Test
    void testGetHierarchyRootModelIsNotRoot() {
        // Is not root page
        when(modelFactory.getModelFromWrappedRequest(any(), any(), any())).thenReturn(page);
        assertEquals(page, page.getHierarchyRootModel());
    }

    @Test
    void testGetHierarchyRootModelIsRoot() {
        // Is root page
        when(currentStyle.get(HierarchyConstants.PN_IS_ROOT, false)).thenReturn(true);
        assertEquals(page, page.getHierarchyRootModel());
    }
}
