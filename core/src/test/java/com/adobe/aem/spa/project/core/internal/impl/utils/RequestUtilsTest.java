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

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.wcm.api.Page;

import static com.adobe.aem.spa.project.core.internal.impl.utils.RequestUtils.getJsonExportURL;
import static com.adobe.aem.spa.project.core.internal.impl.utils.RequestUtils.getURL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestUtilsTest {

    // Mock paths
    private static final String CONTEXT_PATH = "/context/path";

    private static final String PAGE_PATH = "/path/to/page";

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private Page page;

    @BeforeEach
    void beforeEach() {
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(page.getPath()).thenReturn(PAGE_PATH);
    }

    @Test
    void testGetURLWithoutVanityUrl() {
        // Without vanity URL
        String vanityUrl = "";
        when(page.getVanityUrl()).thenReturn(vanityUrl);
        assertEquals(CONTEXT_PATH + PAGE_PATH + ".html", getURL(request, page));
    }

    @Test
    void testGetURLWithVanityUrl() {
        // With vanity URL
        String vanityUrl = "/vanity/url";
        when(page.getVanityUrl()).thenReturn(vanityUrl);
        assertEquals(CONTEXT_PATH + vanityUrl, getURL(request, page));
    }

    @Test
    void testGetJsonExportURL() {
        assertNull(getJsonExportURL(""));
        assertEquals("some/path.model.json", getJsonExportURL("some/path"));
        assertEquals("path/with/some.model.json", getJsonExportURL("path/with/some.more.selectors.html"));
    }
}
