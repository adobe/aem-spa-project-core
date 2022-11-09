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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import java.util.Set;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.aem.spa.project.core.internal.impl.utils.HierarchyUtils;
import com.adobe.aem.spa.project.core.internal.impl.utils.RequestUtils;
import com.adobe.aem.spa.project.core.internal.impl.utils.StyleUtils;
import com.adobe.aem.spa.project.core.models.Page;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.export.json.hierarchy.type.HierarchyTypes;
import com.adobe.cq.wcm.core.components.models.HtmlPageItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Page that allows the retrieval of the model in JSON format with hierarchical structures of more than one page. <br>
 * The content of the JSON export of the page's model is limited by two parameters (see {@link HierarchyUtils}):
 * <ul>
 * <li>filterPatterns - paths from which Pages are to be included</li>
 * <li>traversalDepth - number of levels to be included</li>
 * </ul>
 * However, there is also the possibility to use the Java API to get the model. If the {@link #getHierarchyRootModel()} function is used,
 * the {@link HierarchyUtils#createHierarchyServletRequest(SlingHttpServletRequest, com.day.cq.wcm.api.Page, com.day.cq.wcm.api.Page)}
 * function would wrap the request saving
 * <ul>
 * <li>The original request</li>
 * <li>The root page of the hierarchy</li>
 * <li>The entry point page - so the page for which the actual request was made</li>
 * </ul>
 * in order to provide the full hierarchy from the root, with all descendants' models of the root page (with respect to filterPatterns and
 * traversalDepth) plus the entry point page (even if was excluded based on rules enforced by filterPatterns or traversalDepth). <br>
 * Among other information, the exported structure would contain:
 * <ul>
 * <li>A flat map of all descendants' models identifiable by their paths ({@link PageImpl#getExportedChildren()} to :children)</li>
 * <li>A map of the content of the page ({@link PageImpl#getExportedItems()} to :items), together with the order
 * ({@link PageImpl#getExportedItemsOrder()} to :itemsOrder</li>
 * </ul>
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = { Page.class,
    ContainerExporter.class }, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl implements Page {

    static final String RESOURCE_TYPE = "spa-project-core/components/page";

    // Delegated to Page v1
    @ScriptVariable
    @Via(type = ResourceSuperType.class)
    private com.day.cq.wcm.api.Page currentPage;

    // Delegated to Page v1
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    @Via(type = ResourceSuperType.class)
    private Style currentStyle;

    // Delegated to Page v1
    @Inject
    private ModelFactory modelFactory;

    // Delegated to Page v2
    @Self
    @Via(type = ResourceSuperType.class)
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Resource resource;

    // "delegate" object with which methods from Page v1/v2 can be used
    @Self
    @Via(type = ResourceSuperType.class)
    protected com.adobe.cq.wcm.core.components.models.Page delegate;

    /**
     * {@link Map} containing the page models with their corresponding paths (as keys)
     */
    private Map<String, ? extends Page> descendedPageModels;

    private com.day.cq.wcm.api.Page rootPage;

    /**
     * Package-private setter for descendedPageModels (required for tests)
     */
    void setDescendedPageModels(Map<String, ? extends Page> descendedPageModels) {
        this.descendedPageModels = descendedPageModels;
    }

    /**
     * Package-private setter for rootPage (required for tests)
     */
    void setRootPage(com.day.cq.wcm.api.Page rootPage) {
        this.rootPage = rootPage;
    }

    @Nullable
    @Override
    public String getExportedHierarchyType() {
        return HierarchyTypes.PAGE;
    }

    @NotNull
    @Override
    public Map<String, ? extends Page> getExportedChildren() {
        if (descendedPageModels == null) {
            setDescendedPageModels(HierarchyUtils.getDescendantsModels(request, currentPage, currentStyle, modelFactory));
        }

        return descendedPageModels;
    }

    @NotNull
    @Override
    public String getExportedPath() {
        return currentPage.getPath();
    }

    @Nullable
    @Override
    public String getHierarchyRootJsonExportUrl() {
        if (isRootPage()) {
            return RequestUtils.getPageJsonExportUrl(request, currentPage);
        }

        if (rootPage == null) {
            setRootPage(HierarchyUtils.getRootPage(resource, currentPage));
        }

        if (rootPage != null) {
            return RequestUtils.getPageJsonExportUrl(request, rootPage);
        }
        return null;
    }

    /**
     * Returns the model of the root page which this page is a part of
     *
     * @return Root page model
     */
    @Nullable
    @Override
    public Page getHierarchyRootModel() {
        if (isRootPage()) {
            return this;
        }

        if (rootPage == null) {
            setRootPage(HierarchyUtils.getRootPage(resource, currentPage));
        }

        if (rootPage == null) {
            return null;
        }

        return modelFactory.getModelFromWrappedRequest(request, rootPage.getContentResource(), this.getClass());
    }

    private boolean isRootPage() {
        return currentStyle != null && StyleUtils.isRootPage(currentStyle);
    }

    // Delegated to Page v1

    @Override
    public String getLanguage() {
        return delegate.getLanguage();
    }

    // Delegated to Page v1
    @Override
    public Calendar getLastModifiedDate() {
        return delegate.getLastModifiedDate();
    }

    // Delegated to Page v1
    @Override
    @JsonIgnore
    public String[] getKeywords() {
        return delegate.getKeywords();
    }

    // Delegated to Page v1
    @Override
    public String getDesignPath() {
        return delegate.getDesignPath();
    }

    // Delegated to Page v1
    @Override
    public String getStaticDesignPath() {
        return delegate.getStaticDesignPath();
    }

    // Delegated to Page v1
    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    // Delegated to Page v1
    @Override
    public String getTemplateName() {
        return delegate.getTemplateName();
    }

    // Delegated to Page v1
    @Override
    @JsonIgnore
    public String[] getClientLibCategories() {
        return delegate.getClientLibCategories();
    }

    // Delegated to Page v1
    @NotNull
    @Override
    public String getExportedType() {
        return delegate.getExportedType();
    }

    // Delegated to Page v2
    @Nullable
    @Override
    public String getMainContentSelector() {
        return delegate.getMainContentSelector();
    }

    // Delegated to Page v2
    @Override
    @JsonIgnore
    public String[] getClientLibCategoriesJsBody() {
        return delegate.getClientLibCategoriesJsBody();
    }

    // Delegated to Page v2
    @Override
    @JsonIgnore
    public String[] getClientLibCategoriesJsHead() {
        return delegate.getClientLibCategoriesJsHead();
    }

    // Delegated to Page v2
    @Override
    public String getAppResourcesPath() {
        return delegate.getAppResourcesPath();
    }

    // Delegated to Page v2
    @Override
    public String getCssClassNames() {
        return delegate.getCssClassNames();
    }

    // Delegated to Page v2
    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        return delegate.getExportedItemsOrder();
    }

    // Delegated to Page v2
    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return delegate.getExportedItems();
    }

    // Delegated to Page v2
    @Nullable
    @Override
    public NavigationItem getRedirectTarget() {
        return delegate.getRedirectTarget();
    }

    // Delegated to Page v2
    @Override
    public boolean hasCloudconfigSupport() {
        return delegate.hasCloudconfigSupport();
    }

    @NotNull
    @Override
    public Set<String> getComponentsResourceTypes() {
        return delegate.getComponentsResourceTypes();
    }

    @Override
    public String getBrandSlug() {
		return delegate.getBrandSlug();
	}

    @Nullable
    @Override
    public  List<HtmlPageItem> getHtmlPageItems() {
        return delegate.getHtmlPageItems();
    }
}
