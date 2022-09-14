package com.adobe.aem.spa.project.core.internal.impl;

import com.adobe.aem.spa.project.core.models.RemotePage;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RemotePageImplTest {

    class NotImplementedClass implements RemotePage{}

    @InjectMocks
    private RemotePageImpl page;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    PageManager pageManager;

     @BeforeEach
     void beforeEach() {
         when(request.getResourceResolver()).thenReturn(resourceResolver);
         when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    }

    @Test
    void testDefaultRemoteSpaUrl() {
        assertEquals("", page.getRemoteSPAUrl());
    }

    @Test
    void testGetRemoteSpaUrl() throws IllegalAccessException {
        FieldUtils.writeField(page,"remoteSPAUrl",  "/dummy/url", true);
        // descendedPageModels is null
        assertEquals("/dummy/url", page.getRemoteSPAUrl());
    }

    @Test
    void testDefaultMethod(){
        try{
            new NotImplementedClass().getRemoteSPAUrl();
            assertTrue(false);
        }catch(UnsupportedOperationException ex){
            assertTrue(true);
        }

    }

}
