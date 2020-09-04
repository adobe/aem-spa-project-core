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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.aem.spa.project.core.internal.HierarchyConstants;
import com.day.cq.wcm.api.designer.Style;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StyleUtilsTest {

    private static final String PN_STRUCTURE_DEPTH = "structureDepth";

    private static final int STRUCTURE_DEPTH = 12;

    @Mock
    private Style style;

    @Test
    void testGetPageTreeDepthStyleIsNull() {
        // No style
        assertEquals(0, StyleUtils.getPageTreeDepth(null, PN_STRUCTURE_DEPTH));
    }

    @Test
    void testGetPageTreeDepthStyleIsNotNull() {
        // structureDepth set to 12
        when(style.get(anyString(), any())).thenReturn(STRUCTURE_DEPTH);
        assertEquals(STRUCTURE_DEPTH, StyleUtils.getPageTreeDepth(style, PN_STRUCTURE_DEPTH));
    }

    @Test
    void testGetStructureDepthStyleIsNull() {
        // No style
        assertNull(StyleUtils.getStructureDepth(null, PN_STRUCTURE_DEPTH));
    }

    @Test
    void testGetStructureDepthStyleIsNotNull() {
        // structureDepth set to 12
        when(style.get(anyString(), any())).thenReturn(STRUCTURE_DEPTH);
        assertEquals(STRUCTURE_DEPTH, (int) StyleUtils.getStructureDepth(style, PN_STRUCTURE_DEPTH));
    }

    @Test
    void testIsRootPageAttributeMissingOrFalse() {
        // `PN_IS_ROOT` property is missing or set to `false`
        when(style.get(HierarchyConstants.PN_IS_ROOT, false)).thenReturn(false);
        assertFalse(StyleUtils.isRootPage(style));
    }

    @Test
    void testIsRootPageAttributeTrue() {
        // `PN_IS_ROOT` set to `true`
        when(style.get(HierarchyConstants.PN_IS_ROOT, false)).thenReturn(true);
        assertTrue(StyleUtils.isRootPage(style));
    }
}
