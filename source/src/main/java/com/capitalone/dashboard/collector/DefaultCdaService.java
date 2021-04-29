package com.capitalone.dashboard.collector;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.automic.hygieia.rest.model.BaseEntityInfo;
import com.automic.hygieia.rest.model.InstalledPackageComponent;
import com.automic.hygieia.rest.request.GetApplicationsRequest;
import com.automic.hygieia.rest.request.GetComponentsRequest;
import com.automic.hygieia.rest.request.GetInstallationRequest;
import com.capitalone.dashboard.model.CdaApplication;
import com.google.api.client.util.Maps;

@Component
public class DefaultCdaService implements CdaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCdaService.class);

    private final CdaSettings cdaSettings;
    private IRestClient client;

    @Autowired
    public DefaultCdaService(CdaSettings cdaSettings, IRestClient client) {
        this.cdaSettings = cdaSettings;
        this.client = client;
    }

    @Override
    public List<BaseEntityInfo> getApplications(String instanceUrl) {
        return client.execute(new GetApplicationsRequest(cdaSettings.getUrl())).getData();
    }
    
    @Override
    public List<BaseEntityInfo> getComponents(List<String> applicationIds) {
    	GetComponentsRequest componentRequest = new GetComponentsRequest(cdaSettings.getUrl());
    	componentRequest.setIds(applicationIds.stream().collect(Collectors.joining(",")));
    	return client.execute(componentRequest).getData();
    }

    @Override
    public List<InstalledPackageComponent> getInstallation(List<String> applicationIds, List<String> componentIds) {
    	Map<String, String> parameters = Maps.newHashMap();
    	parameters.put("application.ids", applicationIds.stream().collect(Collectors.joining(",")));
    	parameters.put("component.ids", componentIds.stream().collect(Collectors.joining(",")));
    	GetInstallationRequest request = new GetInstallationRequest(cdaSettings.getUrl());
    	request.setParameterMap(parameters);
    	return client.execute(request).getData();
    }

    @Override
    public Map<CdaApplication, List<InstalledPackageComponent>> getEnvironmentComponents(List<CdaApplication> applications) {
    	List<String> applicationIds = applications.stream().map(app -> app.getApplicationId()).collect(Collectors.toList());
    	List<BaseEntityInfo> components = getComponents(applications.stream().map(app -> app.getApplicationId()).collect(Collectors.toList()));
    	
        Map<CdaApplication, List<InstalledPackageComponent>> envComponentsPerApp = Maps.newHashMap();
        List<InstalledPackageComponent> allInstallations = getInstallation(applicationIds, components.stream().map(c -> String.valueOf(c.getId())).collect(Collectors.toList()));
        Map<BaseEntityInfo, List<InstalledPackageComponent>> installationsPerApp = allInstallations.stream().collect(Collectors.groupingBy(InstalledPackageComponent::getApplication));
        for (Entry<BaseEntityInfo, List<InstalledPackageComponent>> e : installationsPerApp.entrySet()) {
        	CdaApplication cdaApp = new CdaApplication();
        	cdaApp.setApplicationName(e.getKey().getName());
        	cdaApp.setApplicationId(e.getKey().getId().toString());
        	
        	if (applications.contains(cdaApp)) {
        		CdaApplication originalApp = applications.get(applications.indexOf(cdaApp));
        		envComponentsPerApp.put(originalApp, e.getValue());	
        	}
        }
        if (envComponentsPerApp.isEmpty())
            LOGGER.info("No Environment data found, No components deployed");

        return envComponentsPerApp;
    }
    
}
