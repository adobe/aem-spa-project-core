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

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code RemotePage} Sling Model used for the {@code /apps/spa/project/core/models/remotepage} component
 */
@ConsumerType
public interface RemotePage extends Page {
    /**
     * @return the remote SPA page's URL, if one was set, or {@code null}
     */
     @Nullable
     default String getRemoteSPAUrl() {
         throw new UnsupportedOperationException();
     }
}
