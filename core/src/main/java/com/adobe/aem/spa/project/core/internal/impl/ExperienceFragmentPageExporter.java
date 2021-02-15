package com.adobe.aem.spa.project.core.internal.impl;


import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.hierarchy.HierarchyNodeExporter;

/**
 * Interface that allows experience fragment pages (that cannot inherit the core component page) to be exported as model JSON
 * so that the SPA editor can function properly.
 */
public interface ExperienceFragmentPageExporter extends HierarchyNodeExporter, ContainerExporter {

}
