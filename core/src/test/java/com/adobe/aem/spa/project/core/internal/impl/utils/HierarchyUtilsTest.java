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

package com.adobe.aem.spa.project.core.internal.impl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import com.adobe.aem.spa.project.core.internal.HierarchyConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_COMPONENT_CONTEXT;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_CURRENT_PAGE;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_HIERARCHY_ENTRY_POINT_PAGE;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.PN_IS_ROOT;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.PN_STRUCTURE_PATTERNS;
import static org.apache.commons.collections.IteratorUtils.emptyIterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HierarchyUtilsTest {
    private static final int DEPTH = 5;

    @Mock
    private Page childPage;

    @Mock
    private com.adobe.aem.spa.project.core.models.Page childPageModel;

    @Mock
    private Resource childPageContentResource;

    @Mock
    private Page currentPage;

    @Mock
    private Page entryPage;

    @Mock
    private ModelFactory modelFactory;

    @Mock
    private Page page;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private RequestParameter requestParameter;

    @Mock
    private SlingHttpServletRequest requestWrapper;

    @Mock
    private Resource resource;

    private List<Pattern> structurePatterns = new ArrayList<>();

    @Mock
    private TemplatedResource templatedResource;

    @BeforeEach
    void beforeEach() {
        // Pages
        when(currentPage.getPath()).thenReturn("/path/to/current/page");
        when(entryPage.getPath()).thenReturn("/path/to/entry/page");
        when(request.getAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE)).thenReturn(entryPage);

        // Requests
        when(request.getRequestParameter(eq(PN_STRUCTURE_PATTERNS.toLowerCase()))).thenReturn(requestParameter);
    }

    @Test
    void testCreateHierarchyServletRequest() {
        // Mock request
        ComponentContext componentContext = spy(ComponentContext.class);
        when(request.getAttribute(ATTR_COMPONENT_CONTEXT)).thenReturn(componentContext);
        when(request.getAttribute(ATTR_CURRENT_PAGE)).thenReturn(page);

        SlingHttpServletRequest wrappedRequest = HierarchyUtils.createHierarchyServletRequest(request, page, entryPage);
        verify(request, times(1)).setAttribute(eq(ATTR_CURRENT_PAGE), eq(page));
        verify(request, times(1)).setAttribute(eq(ATTR_HIERARCHY_ENTRY_POINT_PAGE), eq(entryPage));
        assertEquals(componentContext, wrappedRequest.getAttribute(ATTR_COMPONENT_CONTEXT));
    }

    @Test
    void testGetEntryPointWhenNotSet() {
        // No entry point set
        when(request.getAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE)).thenReturn(null);
        assertNull(HierarchyUtils.getEntryPoint(request));
    }

    @Test
    void testGetEntryPointWhenSet() {
        // Entry point set
        when(request.getAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE)).thenReturn(entryPage);
        assertEquals(entryPage, HierarchyUtils.getEntryPoint(request));
    }

    @Test
    void testAddEntryPointPage() {
        // Mock pages
        List<Page> descendedPages = new ArrayList<>();

        HierarchyUtils.addEntryPointPage(request, currentPage, descendedPages);
        assertEquals(entryPage, descendedPages.get(0));
    }

    @Test
    void testGetStructurePatternsEmpty() {
        // Expect empty
        when(requestParameter.getString()).thenReturn("");
        assertTrue(HierarchyUtils.getStructurePatterns(request, null).isEmpty());
    }

    @Test
    void testGetStructurePatternsTwoElements() {
        // Expect two elements
        when(requestParameter.getString()).thenReturn("first,second");
        List<Pattern> patterns = HierarchyUtils.getStructurePatterns(request, null);
        assertEquals("first", patterns.get(0).pattern());
        assertEquals("second", patterns.get(1).pattern());
    }

    @Nested
    @DisplayName("Tests for `getRootPage` function")
    class TestGetRootPage {
        @Mock
        ContentPolicyManager contentPolicyManager;

        @Mock
        ResourceResolver resourceResolver;

        HashMap<String, Page> pages = new HashMap<>();

        HashMap<String, Template> templates = new HashMap<>();

        HashMap<String, Resource> pageContentResources = new HashMap<>();

        HashMap<String, ContentPolicy> pageContentPolicies = new HashMap<>();

        HashMap<String, ValueMap> properties = new HashMap<>();

        @BeforeEach
        void beforeEach() {
            when(resource.getResourceResolver()).thenReturn(resourceResolver);
            when(resourceResolver.adaptTo(ContentPolicyManager.class)).thenReturn(contentPolicyManager);

            // Create mocks for three pages in a hierarchy
            for (String pageName : new String[] { "current", "parent", "root" }) {
                // Mock attributes
                pages.put(pageName, mock(Page.class));
                templates.put(pageName, mock(Template.class));
                pageContentResources.put(pageName, mock(Resource.class));
                pageContentPolicies.put(pageName, mock(ContentPolicy.class));
                properties.put(pageName, mock(ValueMap.class));
                when(pages.get(pageName).getTemplate()).thenReturn(templates.get(pageName));
                when(pages.get(pageName).getContentResource()).thenReturn(pageContentResources.get(pageName));
                when(templates.get(pageName).hasStructureSupport()).thenReturn(true);
                when(contentPolicyManager.getPolicy(pageContentResources.get(pageName))).thenReturn(pageContentPolicies.get(pageName));
                when(pageContentPolicies.get(pageName).getProperties()).thenReturn(properties.get(pageName));
                when(properties.get(pageName).get(PN_IS_ROOT, false)).thenReturn(false);
            }

            // Mock hierarchical relationships
            when(pages.get("current").getParent()).thenReturn(pages.get("parent"));
            when(pages.get("parent").getParent()).thenReturn(pages.get("root"));
        }

        @Test
        void testGetRootPagePropertyMissingOrFalse() {
            // `PN_IS_ROOT` property is missing or set to `false`
            assertNull(HierarchyUtils.getRootPage(resource, pages.get("current")));
            assertNull(HierarchyUtils.getRootPage(resource, pages.get("parent")));
            assertNull(HierarchyUtils.getRootPage(resource, pages.get("root")));
        }

        @Test
        void testGetRootPagePropertyTrue() {
            // `PN_IS_ROOT` set to `true`
            when(properties.get("root").get(PN_IS_ROOT, false)).thenReturn(true);
            assertEquals(pages.get("root"), HierarchyUtils.getRootPage(resource, pages.get("current")));
            assertEquals(pages.get("root"), HierarchyUtils.getRootPage(resource, pages.get("parent")));
            assertEquals(pages.get("root"), HierarchyUtils.getRootPage(resource, pages.get("root")));
        }
    }

    @Test
    void testGetDescendantsNoPage() {
        // No page
        assertTrue(HierarchyUtils.getDescendants(null, request, structurePatterns, DEPTH).isEmpty());
    }

    @Test
    void testGetDescendantsDepth0() {
        // Depth 0
        assertTrue(HierarchyUtils.getDescendants(page, request, structurePatterns, 0).isEmpty());
    }

    @Test
    void testGetDescendantsIsChildPage() {
        // Is child page
        when(request.getAttribute(HierarchyConstants.ATTR_IS_CHILD_PAGE)).thenReturn(true);
        assertTrue(HierarchyUtils.getDescendants(page, request, structurePatterns, DEPTH).isEmpty());
        when(request.getAttribute(HierarchyConstants.ATTR_IS_CHILD_PAGE)).thenReturn(false);
    }

    @Test
    void testGetDescendantsHasNoChildren() {
        // Has no children
        when(page.listChildren()).thenReturn(emptyIterator());
        assertTrue(HierarchyUtils.getDescendants(page, request, structurePatterns, DEPTH).isEmpty());
    }

    @Test
    void testGetDescendantsHasChildren() {
        // Has children
        ArrayList<Page> children = new ArrayList<>();
        Page child1 = mock(Page.class);
        when(child1.getPath()).thenReturn("/path/to/child1");
        children.add(child1);
        Page child2 = mock(Page.class);
        when(child2.getPath()).thenReturn("/path/to/child2");
        children.add(child2);
        when(page.listChildren()).thenAnswer((Answer<Iterator<Page>>) i -> children.listIterator());
        assertEquals(children.size(), HierarchyUtils.getDescendants(page, request, structurePatterns, DEPTH).size());

        // Has children and page filter (structurePatterns)
        structurePatterns.add(Pattern.compile("^/path/to/.*$"));
        assertEquals(2, HierarchyUtils.getDescendants(page, request, structurePatterns, DEPTH).size());
        structurePatterns.set(0, Pattern.compile("child1"));
        assertEquals(1, HierarchyUtils.getDescendants(page, request, structurePatterns, DEPTH).size());
        structurePatterns.set(0, Pattern.compile("thisdoesntmatchanychild"));
        assertEquals(0, HierarchyUtils.getDescendants(page, request, structurePatterns, DEPTH).size());
        structurePatterns.clear();

        // Has grandchildren
        ArrayList<Page> grandChildren = new ArrayList<>();
        Page grandChild = mock(Page.class);
        when(grandChild.getPath()).thenReturn("/path/to/child2/grandchild");
        grandChildren.add(grandChild);
        when(child2.listChildren()).thenAnswer((Answer<Iterator<Page>>) i -> grandChildren.listIterator());
        assertEquals(3, HierarchyUtils.getDescendants(page, request, structurePatterns, DEPTH).size());

        // Has depth < actual children depth
        assertEquals(2, HierarchyUtils.getDescendants(page, request, structurePatterns, 1).size());
    }

    @Test
    void testGetDescendantModelContentResourceIsNull() {
        // contentResource is null
        assertNull(HierarchyUtils.getDescendantModel(childPage, requestWrapper, modelFactory));
    }

    @Test
    void testGetDescendantModelContentResourceIsNotNull() {
        // contentResource is not null
        when(childPage.getContentResource()).thenReturn(childPageContentResource);
        when(childPageContentResource.adaptTo(TemplatedResource.class)).thenReturn(templatedResource);
        when(modelFactory.getModelFromWrappedRequest(any(), any(), any())).thenReturn(childPageModel);
        assertEquals(childPageModel, HierarchyUtils.getDescendantModel(childPage, requestWrapper, modelFactory));
    }
}
