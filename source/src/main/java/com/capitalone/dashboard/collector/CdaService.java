package com.capitalone.dashboard.collector;

import java.util.List;
import java.util.Map;

import com.automic.hygieia.rest.model.BaseEntityInfo;
import com.automic.hygieia.rest.model.InstalledPackageComponent;
import com.capitalone.dashboard.model.CdaApplication;
import com.capitalone.dashboard.model.CdaEnvironment;
import com.capitalone.dashboard.model.EnvironmentComponent;

/**
 * Client for fetching information from CDA.
 */
public interface CdaService {

    /**
     * Fetches all {@link CdaApplication}s for a given instance URL.
     *
     * @param instanceUrl instance URL
     * @return list of {@link CdaApplication}s
     */
    List<BaseEntityInfo> getApplications(String instanceUrl);

    List<BaseEntityInfo> getComponents(List<String> applicationIds);
    
    List<InstalledPackageComponent> getInstallation(List<String> applicationIds, List<String> componentIds);
    
    /**
     * Fetches all {@link EnvironmentComponent}s for a given {@link CdaApplication} and {@link CdaEnvironment}.
     *
     * @param application a {@link CdaApplication}
     * @param environment an {@link CdaEnvironment}
     * @return list of {@link EnvironmentComponent}s
     */
    Map<CdaApplication, List<InstalledPackageComponent>> getEnvironmentComponents(List<CdaApplication> applications);

}
