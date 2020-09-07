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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import com.day.cq.wcm.api.Page;

import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.JSON_EXPORT_SUFFIX;

public class RequestUtils {
    private RequestUtils() {
    }

    /**
     * Given a {@link Page}, this method returns the correct URL, taking into account that the provided page might provide a vanity URL
     *
     * @param request The current request, used to determine the server's context path
     * @param page    The page
     * @return The URL of the page identified by the provided path, or the original path if this doesn't identify a {@link Page}
     */
    @NotNull
    public static String getURL(@NotNull SlingHttpServletRequest request, @NotNull Page page) {
        String contextPath = request.getContextPath();
        String contextPathNotNull = contextPath == null ? "" : contextPath;
        String vanityURL = page.getVanityUrl();
        return StringUtils.isEmpty(vanityURL) ? contextPathNotNull + page.getPath() + ".html" : contextPathNotNull + vanityURL;
    }

    /**
     * Returns a model URL for the given page URL
     *
     * @param url Page URL
     * @return Model URL
     */
    public static String getJsonExportURL(@NotNull String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        int dotIndex = url.indexOf('.');

        if (dotIndex < 0) {
            dotIndex = url.length();
        }

        return url.substring(0, dotIndex) + JSON_EXPORT_SUFFIX;
    }

    /**
     * Returns a model URL for the given page URL
     *
     * @param slingRequest The current servlet request
     * @param page         Page for which to get the model URL
     * @return Model URL
     */
    public static String getPageJsonExportUrl(@NotNull SlingHttpServletRequest slingRequest, @NotNull com.day.cq.wcm.api.Page page) {
        return RequestUtils.getJsonExportURL(RequestUtils.getURL(slingRequest, page));
    }
}
