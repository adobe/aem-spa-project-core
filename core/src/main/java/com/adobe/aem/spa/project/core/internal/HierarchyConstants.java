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

package com.adobe.aem.spa.project.core.internal;

public class HierarchyConstants {

    private HierarchyConstants() {
    }

    /**
     * URL extension specific to the Sling Model exporter
     */
    public static final String JSON_EXPORT_SUFFIX = ".model.json";

    /**
     * Is the current model to be considered as a model root
     */
    public static final String PN_IS_ROOT = "isRoot";

    /**
     * Name of the request attribute which is used to flag the child pages. Optionally available as a request attribute
     */
    public static final String ATTR_IS_CHILD_PAGE = "com.adobe.aem.spa.project.core.models.Page.isChildPage";

    /**
     * Name of the request attribute that defines whether the page is an entry point of the request.
     */
    public static final String ATTR_HIERARCHY_ENTRY_POINT_PAGE = "com.adobe.aem.spa.project.core.models.Page.entryPointPage";

    /**
     * Request attribute key of the component context
     */
    public static final String ATTR_COMPONENT_CONTEXT = "com.day.cq.wcm.componentcontext";

    /**
     * Request attribute key of the current page
     */
    public static final String ATTR_CURRENT_PAGE = "currentPage";

    /**
     * List of Regexp patterns to filter the exported tree of pages
     */
    public static final String PN_STRUCTURE_PATTERNS = "structurePatterns";
}
