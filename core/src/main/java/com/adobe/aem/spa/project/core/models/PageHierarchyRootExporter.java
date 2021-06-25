package com.adobe.aem.spa.project.core.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Nullable;

/**
 * Exports the HierarchyRootUrl for the SPA application to fetch
 */
public interface PageHierarchyRootExporter {

    /**
     * URL to the root model of the App
     *
     * @return {@link String}
     */
    @Nullable
    @JsonIgnore
    default String getHierarchyRootJsonExportUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the root page of the SPA application
     * @return
     */
    @JsonIgnore
    default com.day.cq.wcm.api.Page getRootPage(){
        throw new UnsupportedOperationException();
    }

}
