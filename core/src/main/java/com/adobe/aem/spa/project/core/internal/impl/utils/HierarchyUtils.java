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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.spa.project.core.internal.HierarchyConstants;
import com.adobe.aem.spa.project.core.internal.impl.HierarchyComponentContextWrapper;
import com.adobe.aem.spa.project.core.models.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_COMPONENT_CONTEXT;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_CURRENT_PAGE;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_HIERARCHY_ENTRY_POINT_PAGE;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.ATTR_IS_CHILD_PAGE;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.PN_IS_ROOT;
import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.PN_STRUCTURE_PATTERNS;
import static com.adobe.aem.spa.project.core.models.Page.PN_STRUCTURE_DEPTH;

public class HierarchyUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchyUtils.class);

    private HierarchyUtils() {
    }

    /**
     * Creates a new request wrapping the {@code request} from the parameters. <br>
     * The new request is created to set the
     * <ul>
     * <li>componentcontext - includes the {@link Page} from the {@code page} parameter and the context from {@code request}</li>
     * <li>currentPage - {@link Page} from {@code page} parameter</li>
     * <li>entryPointPage - {@link Page} from {@code entryPage} parameter</li>
     * </ul>
     * attributes in order to ensure that the references to the page are accurate for the hierarchical structure.
     *
     * @param request   Request to be wrapped
     * @param page      Page to be referenced as statically containing the current page content
     * @param entryPage Page that is the entry point of the request
     * @return A {@link SlingHttpServletRequestWrapper} containing the given page and request
     */
    public static SlingHttpServletRequest createHierarchyServletRequest(@NotNull SlingHttpServletRequest request,
            @NotNull com.day.cq.wcm.api.Page page, @Nullable com.day.cq.wcm.api.Page entryPage) {
        SlingHttpServletRequest wrapperRequest = new SlingHttpServletRequestWrapper(request);

        ComponentContext componentContext = (ComponentContext) request.getAttribute(ATTR_COMPONENT_CONTEXT);

        // When traversing child pages, the currentPage must be updated
        HierarchyComponentContextWrapper componentContextWrapper = new HierarchyComponentContextWrapper(componentContext, page);
        wrapperRequest.setAttribute(ATTR_COMPONENT_CONTEXT, componentContextWrapper);
        wrapperRequest.setAttribute(ATTR_CURRENT_PAGE, page);
        wrapperRequest.setAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE, entryPage);

        return wrapperRequest;
    }

    /**
     * Returns the request's entry point attribute value
     *
     * @param request Request to get the entry point attribute from
     * @return Entry point attribute value
     */
    public static com.day.cq.wcm.api.Page getEntryPoint(@NotNull SlingHttpServletRequest request) {
        return (com.day.cq.wcm.api.Page) request.getAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE);
    }

    /**
     * Optionally adds a page that is the entry point of a site model request, even when was not added because of the root structure
     * configuration
     *
     * @param request        Request
     * @param currentPage    Current style
     * @param descendedPages List of descendants
     */
    public static void addEntryPointPage(SlingHttpServletRequest request, com.day.cq.wcm.api.Page currentPage,
            @NotNull List<com.day.cq.wcm.api.Page> descendedPages) {
        // Child pages are only added to the root page
        if (Boolean.TRUE.equals(request.getAttribute(ATTR_IS_CHILD_PAGE))) {
            return;
        }

        com.day.cq.wcm.api.Page entryPointPage = HierarchyUtils.getEntryPoint(request);

        if (entryPointPage == null) {
            return;
        }

        // Filter the root page
        if (entryPointPage.getPath().equals(currentPage.getPath())) {
            return;
        }

        // Filter duplicates
        if (descendedPages.contains(entryPointPage)) {
            return;
        }

        descendedPages.add(entryPointPage);
    }

    /**
     * Returns the page structure patterns to filter the descendants to be exported. The patterns can either be stored on the template
     * policy of the page or provided as a request parameter
     *
     * @param request      Request
     * @param currentStyle Current style
     * @return List of page structure patterns
     */
    @NotNull
    public static List<Pattern> getStructurePatterns(@NotNull SlingHttpServletRequest request, Style currentStyle) {
        RequestParameter pageFilterParameter = request.getRequestParameter(PN_STRUCTURE_PATTERNS.toLowerCase());

        String rawPageFilters = null;

        if (pageFilterParameter != null) {
            rawPageFilters = pageFilterParameter.getString();
        }

        if (currentStyle != null && StringUtils.isBlank(rawPageFilters)) {
            rawPageFilters = currentStyle.get(PN_STRUCTURE_PATTERNS, String.class);
        }

        if (StringUtils.isBlank(rawPageFilters)) {
            return Collections.emptyList();
        }

        String[] pageFilters = rawPageFilters.split(",");

        List<Pattern> pageFilterPatterns = new ArrayList<>();
        for (String pageFilter : pageFilters) {
            pageFilterPatterns.add(Pattern.compile(pageFilter));
        }

        return pageFilterPatterns;
    }

    /**
     * Returns the root page which the current page is part of
     *
     * @param resource    Resource
     * @param currentPage Current page
     * @return Root page
     */
    public static com.day.cq.wcm.api.Page getRootPage(Resource resource, com.day.cq.wcm.api.Page currentPage) {
        com.day.cq.wcm.api.Page tempRootPage = currentPage;

        ContentPolicyManager contentPolicyManager = resource.getResourceResolver().adaptTo(ContentPolicyManager.class);
        if (contentPolicyManager == null) {
            LOGGER.error("Error determining SPA root page: Cannot adapt resource resolver to ContentPolicyManager class");
            return null;
        }

        while (tempRootPage != null) {
            Template template = tempRootPage.getTemplate();
            if (template != null && template.hasStructureSupport()) {
                Resource pageContentResource = tempRootPage.getContentResource();
                if (pageContentResource != null) {
                    ContentPolicy contentPolicy = contentPolicyManager.getPolicy(pageContentResource);
                    if (ContentPolicyUtils.propertyIsTrue(contentPolicy, PN_IS_ROOT)) {
                        // Is the root page to return it
                        LOGGER.debug("Found SPA root page: {}", tempRootPage.getPath());
                        return tempRootPage;
                    }
                }
            }

            // Is not the root page to move up the tree
            tempRootPage = tempRootPage.getParent();
        }

        // Root page not found
        LOGGER.error("SPA root page not found, returning null");
        return null;
    }

    /**
     * Traverses the tree of descendants of the page. Descendants that
     * <ul>
     * <li>Are not deeper than the defined depth</li>
     * <li>Have a path that matches one of defined structurePattern</li>
     * </ul>
     * will be returned in a flat list
     *
     * @param page              Page from which to extract descended pages
     * @param slingRequest      Request
     * @param structurePatterns Patterns to filter descended pages
     * @param depth             Depth of the traversal
     * @return Flat list of matching descendants
     */
    @NotNull
    public static List<com.day.cq.wcm.api.Page> getDescendants(com.day.cq.wcm.api.Page page, SlingHttpServletRequest slingRequest,
            List<Pattern> structurePatterns, int depth) {
        // By default the depth is 0 meaning we do not expose descendants
        // If the value is set as a positive number it is going to be exposed until the counter is brought down to 0
        // If the value is set to a negative value all descendants will be exposed (full traversal tree - aka infinity)
        // Descendants pages do not expose their child pages
        if (page == null || depth == 0 || Boolean.TRUE.equals(slingRequest.getAttribute(HierarchyConstants.ATTR_IS_CHILD_PAGE))) {
            return Collections.emptyList();
        }

        List<com.day.cq.wcm.api.Page> pages = new ArrayList<>();
        Iterator<com.day.cq.wcm.api.Page> childPagesIterator = page.listChildren();

        if (childPagesIterator == null || !childPagesIterator.hasNext()) {
            return Collections.emptyList();
        }

        // we are about to explore one lower level down the tree
        depth--;

        boolean noPageFilters = structurePatterns.isEmpty();

        while (childPagesIterator.hasNext()) {
            com.day.cq.wcm.api.Page childPage = childPagesIterator.next();
            boolean found = noPageFilters;

            for (Pattern pageFilterPattern : structurePatterns) {
                if (pageFilterPattern.matcher(childPage.getPath()).find()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                continue;
            }

            pages.add(childPage);

            pages.addAll(getDescendants(childPage, slingRequest, structurePatterns, depth));
        }

        return pages;
    }

    @Nullable
    protected static Page getDescendantModel(com.day.cq.wcm.api.Page childPage, SlingHttpServletRequest slingRequestWrapper,
            ModelFactory modelFactory) {
        Resource childPageContentResource = childPage.getContentResource();

        if (childPageContentResource == null) {
            return null;
        }

        // Try to pass the templated content resource
        TemplatedResource templatedResource = childPageContentResource.adaptTo(TemplatedResource.class);

        if (templatedResource != null) {
            childPageContentResource = templatedResource;
        }

        SlingHttpServletRequest wrapperRequest = HierarchyUtils.createHierarchyServletRequest(slingRequestWrapper, childPage, null);

        return modelFactory.getModelFromWrappedRequest(wrapperRequest, childPageContentResource, Page.class);
    }

    /**
     * Returns all descended page models of the currentPage plus the entryPoint page (even if was excluded based on rules enforced by
     * filterPatterns or traversalDepth)
     *
     * @param request      Request
     * @param currentPage  Current page
     * @param currentStyle Current style
     * @param modelFactory Model factory
     * @return {@link Map} containing the page models with their corresponding paths (as keys)
     */
    @NotNull
    public static Map<String, Page> getDescendantsModels(SlingHttpServletRequest request, com.day.cq.wcm.api.Page currentPage,
            Style currentStyle, ModelFactory modelFactory, boolean isAuthor, boolean useExtension) {
        int pageTreeTraversalDepth = StyleUtils.getPageTreeDepth(currentStyle, PN_STRUCTURE_DEPTH);

        List<Pattern> pageFilterPatterns = HierarchyUtils.getStructurePatterns(request, currentStyle);

        // Setting the child page to true to prevent child pages to expose their own child pages
        SlingHttpServletRequest slingRequestWrapper = new SlingHttpServletRequestWrapper(request);

        Map<String, Page> itemWrappers = new LinkedHashMap<>();

        List<com.day.cq.wcm.api.Page> descendants = HierarchyUtils.getDescendants(currentPage, slingRequestWrapper, pageFilterPatterns,
            pageTreeTraversalDepth);

        HierarchyUtils.addEntryPointPage(request, currentPage, descendants);

        // Add a flag to inform the model of the descendant page that it is not the root of the returned hierarchy
        slingRequestWrapper.setAttribute(HierarchyConstants.ATTR_IS_CHILD_PAGE, true);
        
        for (com.day.cq.wcm.api.Page childPage : descendants) {
            Page descendantModel = getDescendantModel(childPage, slingRequestWrapper, modelFactory);
            if (descendantModel != null) {
                final String path = (isAuthor) ?  childPage.getPath() : RequestUtils.getURL(request, childPage, isAuthor, useExtension );
                itemWrappers.put(path, descendantModel);
            }
        }

        return itemWrappers;
    }
}
