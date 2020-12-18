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

package com.adobe.aem.spa.project.core.models;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.hierarchy.HierarchyNodeExporter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Defines the {@code Page} Sling Model used for the {@code /apps/spa/project/core/models/page} component
 */
@ConsumerType
public interface Page extends com.adobe.cq.wcm.core.components.models.Page, HierarchyNodeExporter {
    /**
     * Key for the depth of the tree of pages that is to be exported
     */
    String PN_STRUCTURE_DEPTH = "structureDepth";

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
     * Root page model of the current hierarchy of pages
     *
     * @return {@link Page}
     */
    @Nullable
    @JsonIgnore
    default Page getHierarchyRootModel() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedItemsOrder()
     */
    @NotNull
    @Override
    default String[] getExportedItemsOrder() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedItems()
     */
    @NotNull
    @Override
    default Map<String, ? extends ComponentExporter> getExportedItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedType()
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see HierarchyNodeExporter#getExportedHierarchyType
     */
    @Override
    default String getExportedHierarchyType() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see HierarchyNodeExporter#getExportedPath
     */
    @Override
    default String getExportedPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see HierarchyNodeExporter#getExportedChildren
     */
    @Override
    default Map<String, ? extends HierarchyNodeExporter> getExportedChildren() {
        throw new UnsupportedOperationException();
    }

    public String getRemoteSPAUrl();
}
