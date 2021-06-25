package com.adobe.aem.spa.project.core.internal.impl;

import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ForcedResourceType;

/**
 * JsonSerialize annotation is added to prevent errors being thrown in model.json export, since these don't work for this type of page.
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = {ExperienceFragmentPageExporter.class,
        ContainerExporter.class}, resourceType = {
        ExperienceFragmentPageImpl.XF_RESOURCE_TYPE
})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@JsonSerialize(as = ExperienceFragmentPageExporter.class)
public class ExperienceFragmentPageImpl extends PageImpl implements ExperienceFragmentPageExporter {

    static final String XF_RESOURCE_TYPE = "spa-project-core/components/xf-page";

    @Self
    @Via(type = ForcedResourceType.class, value = "core/wcm/components/page/v2/page")
    @Optional
    private com.adobe.cq.wcm.core.components.models.Page delegate;

    /**
     * Override the getDelegate with a forced resource type one. We cannot inherit the core resource type on a XF page.
     * @return
     */
    protected com.adobe.cq.wcm.core.components.models.Page getDelegate(){
        return this.delegate;
    }
}
