package com.adobe.aem.spa.project.core.internal.impl;

import com.adobe.aem.spa.project.core.internal.impl.utils.HierarchyUtils;
import com.adobe.aem.spa.project.core.internal.impl.utils.RequestUtils;
import com.adobe.aem.spa.project.core.internal.impl.utils.StyleUtils;
import com.adobe.aem.spa.project.core.models.PageHierarchyRootExporter;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.Nullable;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {PageHierarchyRootExporter.class})
public class PageHierarchyRootExporterImpl implements PageHierarchyRootExporter {

    // Delegated to Page v1
    @ScriptVariable
    private com.day.cq.wcm.api.Page currentPage;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    private Style currentStyle;

    // Delegated to Page v2
    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Resource resource;

    private com.day.cq.wcm.api.Page rootPage;


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

    @Override
    public Page getRootPage() {

        if (rootPage == null) {
            setRootPage(HierarchyUtils.getRootPage(resource, currentPage));
        }

        return rootPage;
    }

    private boolean isRootPage() {
        return currentStyle != null && StyleUtils.isRootPage(currentStyle);
    }

    /**
     * Package-private setter for rootPage (required for tests)
     */
    void setRootPage(com.day.cq.wcm.api.Page rootPage) {
        this.rootPage = rootPage;
    }

}
