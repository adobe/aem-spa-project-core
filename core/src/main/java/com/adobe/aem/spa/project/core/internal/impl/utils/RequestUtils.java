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
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.day.cq.wcm.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

import static com.adobe.aem.spa.project.core.internal.HierarchyConstants.JSON_EXPORT_SUFFIX;

public class RequestUtils {
    
    private static final Logger log = LoggerFactory.getLogger(RequestUtils.class);
    
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
    public static String getURL(@NotNull SlingHttpServletRequest request, @NotNull Page page, boolean isAuthor, boolean useExtension) {
        String contextPath = request.getContextPath();
        String contextPathNotNull = contextPath == null ? "" : contextPath;
        String vanityURL = page.getVanityUrl();
        
        String extensionSuffix = (useExtension) ? ".html" : "";
        
        if(StringUtils.isNotEmpty(vanityURL)){
            return contextPathNotNull + vanityURL;
        }else if(isAuthor){
            return mapPath(page.getPath(),request) + extensionSuffix;
        }else{
            return contextPathNotNull + page.getPath() + extensionSuffix;
        }
        
    }
    
    private static String mapPath(String path, @NotNull SlingHttpServletRequest request){
        ResourceResolver resourceResolver = request.getResourceResolver();
        String finalPath;
        if (shouldMapPath(request)) {
            String mappedPath = resourceResolver.map(path);
            finalPath = mappedPath;
        
            if (StringUtils.startsWith(mappedPath, "http")) {
                try {
                    finalPath = new URL(mappedPath).getPath();
                } catch (MalformedURLException e) {
                    log.error("error parsing path", e);
                }
            }
        } else {
            finalPath = path;
        }
        return finalPath;
    }
    
    private static boolean shouldMapPath(SlingHttpServletRequest request) {
        boolean isDispatcherRequest = request.getHeader("Server-Agent") != null &&
                request.getHeader("Server-Agent").equals("Communique-Dispatcher");
        boolean isNotYetMapped = request.getRequestURI().startsWith("/content") && request.getRequestURI().startsWith("/conf");
        return isDispatcherRequest && isNotYetMapped;
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
    public static String getPageJsonExportUrl(@NotNull SlingHttpServletRequest slingRequest, @NotNull com.day.cq.wcm.api.Page page, boolean isAuthor, boolean useExtension) {
        return RequestUtils.getJsonExportURL(RequestUtils.getURL(slingRequest, page, isAuthor,useExtension));
    }
}
