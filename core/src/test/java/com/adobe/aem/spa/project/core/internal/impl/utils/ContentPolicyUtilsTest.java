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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.day.cq.wcm.api.policies.ContentPolicy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContentPolicyUtilsTest {
    private static final String PROPERTY_NAME = "propertyName";

    @Spy
    private ContentPolicy contentPolicy;

    @Mock
    private ValueMap properties;

    @BeforeEach
    void beforeEach() {
        when(contentPolicy.getProperties()).thenReturn(properties);
    }

    @Test
    void testPropertyIsTruePropertyMissingOrFalse() {
        // Property is missing or set to `false`
        when(properties.get(PROPERTY_NAME, false)).thenReturn(false);
        assertFalse(ContentPolicyUtils.propertyIsTrue(contentPolicy, PROPERTY_NAME));
    }

    @Test
    void testPropertyIsTruePropertyTrue() {
        // Property is set to `true`
        when(properties.get(PROPERTY_NAME, false)).thenReturn(true);
        assertTrue(ContentPolicyUtils.propertyIsTrue(contentPolicy, PROPERTY_NAME));
    }
}
