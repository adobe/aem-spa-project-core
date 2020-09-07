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

import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.policies.ContentPolicy;

class ContentPolicyUtils {
    private ContentPolicyUtils() {
    }

    /**
     * Utility function which checks whether the provided content policy contains a property and whether its value is {@code true}
     *
     * @param contentPolicy The content policy which should be searched for the specified property
     * @param propertyName  Name of the JCR property to check
     * @return Boolean indicating whether the property is set to {@code true}. If the content policy or the property don't exist, the
     *         function will return {@code false}
     */
    static boolean propertyIsTrue(ContentPolicy contentPolicy, String propertyName) {
        if (contentPolicy != null) {
            ValueMap properties = contentPolicy.getProperties();
            return (properties != null && properties.get(propertyName, false));
        }
        return false;
    }
}
