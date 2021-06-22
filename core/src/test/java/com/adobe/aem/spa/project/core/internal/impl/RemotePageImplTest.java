package com.adobe.aem.spa.project.core.internal.impl;

import com.adobe.aem.spa.project.core.models.RemotePage;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RemotePageImplTest {

    class NotImplementedClass implements RemotePage{}

    @InjectMocks
    private RemotePageImpl page;


    @BeforeEach
    void beforeEach() throws IllegalAccessException {
        FieldUtils.writeField(page,"remoteSPAUrl",  "/dummy/url", true);
    }


    @Test
    void testGetRemoteSpaUrl() {
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
