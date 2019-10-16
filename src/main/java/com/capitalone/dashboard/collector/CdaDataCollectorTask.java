package com.capitalone.dashboard.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.automic.hygieia.rest.model.BaseEntityInfo;
import com.automic.hygieia.rest.model.InstalledPackageComponent;
import com.capitalone.dashboard.model.CdaApplication;
import com.capitalone.dashboard.model.CdaCollector;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.model.EnvironmentComponent;
import com.capitalone.dashboard.model.EnvironmentStatus;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.CdaApplicationRepository;
import com.capitalone.dashboard.repository.CdaCollectorRepository;
import com.capitalone.dashboard.repository.ComponentRepository;
import com.capitalone.dashboard.repository.ConfigurationRepository;
import com.capitalone.dashboard.repository.EnvironmentComponentRepository;
import com.capitalone.dashboard.repository.EnvironmentStatusRepository;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Collects {@link EnvironmentComponent} and {@link EnvironmentStatus} data from
 * {@link CdaApplication}s.
 */
@Component
public class CdaDataCollectorTask extends CollectorTask<CdaCollector> {

    private final CdaCollectorRepository collectorRepository;
    private final CdaApplicationRepository applicationRepository;
    private final CdaService cdaClient;
    private final CdaSettings cdaSettings;
    private final EnvironmentComponentRepository envComponentRepository;
    private final EnvironmentStatusRepository environmentStatusRepository;
	private final ConfigurationRepository configurationRepository;
    private final ComponentRepository dbComponentRepository;

    @Autowired
    public CdaDataCollectorTask(TaskScheduler taskScheduler,
                                CdaCollectorRepository collectorRepository,
                                CdaApplicationRepository applicationRepository,
                                EnvironmentComponentRepository envComponentRepository,
                                CdaSettings uDeploySettings, CdaService cdaClient,
                                EnvironmentStatusRepository environmentStatusRepository,
                                ConfigurationRepository configurationRepository,
                                ComponentRepository dbComponentRepository) {
        super(taskScheduler, "Cda");
        this.collectorRepository = collectorRepository;
        this.applicationRepository = applicationRepository;
        this.cdaSettings = uDeploySettings;
        this.cdaClient = cdaClient;
        this.envComponentRepository = envComponentRepository;
        this.environmentStatusRepository = environmentStatusRepository;
        this.dbComponentRepository = dbComponentRepository;
		this.configurationRepository = configurationRepository;
    }

    @Override
    public CdaCollector getCollector() {
		Configuration config = configurationRepository.findByCollectorName("Cda");
        if (config != null) {
            config.decryptOrEncrptInfo();
            for (Map<String, String> udeployServer : config.getInfo()) {
                cdaSettings.setUrl(udeployServer.get("url"));
                cdaSettings.setUsername(udeployServer.get("userName"));
                cdaSettings.setPassword(udeployServer.get("password"));
            }
        }
        return CdaCollector.prototype(cdaSettings.getUrl());
    }

    @Override
    public BaseCollectorRepository<CdaCollector> getCollectorRepository() {
        return collectorRepository;
    }

    @Override
    public String getCron() {
        return cdaSettings.getCron();
    }

    @Override
    public void collect(CdaCollector collector) {

    	String url = collector.getUrl();
        logBanner(url);

        long start = System.currentTimeMillis();

        clean(collector);

        addNewApplications(cdaClient.getApplications(url), collector);
        updateData(enabledApplications(collector));

        log("Finished", start);
    }

    /**
     * Clean up unused deployment collector items
     *
     * @param collector the {@link CdaCollector}
     */
    private void clean(CdaCollector collector) {
        deleteUnwantedJobs(collector);
        Set<ObjectId> uniqueIDs = new HashSet<>();
        for (com.capitalone.dashboard.model.Component comp : dbComponentRepository.findAll()) {
            if (comp.getCollectorItems() == null || comp.getCollectorItems().isEmpty()) continue;
            List<CollectorItem> itemList = comp.getCollectorItems().get(CollectorType.Deployment);
            if (itemList == null) continue;
            for (CollectorItem ci : itemList) {
                if (ci == null) continue;
                uniqueIDs.add(ci.getId());
            }
        }
        List<CdaApplication> appList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet< >();
        udId.add(collector.getId());
        for (CdaApplication app : applicationRepository.findByCollectorIdIn(udId)) {
            if (app != null) {
                app.setEnabled(uniqueIDs.contains(app.getId()));
                appList.add(app);
            }
        }
        applicationRepository.save(appList);
    }

    private void deleteUnwantedJobs(CdaCollector collector) {

        List<CdaApplication> deleteAppList = new ArrayList<>();
        Set<ObjectId> udId = new HashSet<>();
        udId.add(collector.getId());
        deleteAppList = applicationRepository.findByCollectorIdIn(udId).stream().filter(app -> !app.getCollectorId().equals(collector.getId())).collect(Collectors.toList());

        applicationRepository.delete(deleteAppList);

    }
    
    /**
     * For each {@link CdaApplication}, update the current
     * {@link EnvironmentComponent}s and {@link EnvironmentStatus}.
     *
     * @param uDeployApplications list of {@link CdaApplication}s
     */
    private void updateData(List<CdaApplication> applications) {
    	
        Map<CdaApplication, List<InstalledPackageComponent>> compListPerApp = cdaClient.getEnvironmentComponents(applications);
        for (Entry<CdaApplication, List<InstalledPackageComponent>> entry : compListPerApp.entrySet()) {
			CdaApplication application = entry.getKey();
			List<EnvironmentComponent> compList = entry.getValue().stream().map(i -> convertToEnvironmentComponent(i, application.getId())).collect(Collectors.toList());
			if (!compList.isEmpty()) {
	            List<EnvironmentComponent> existingComponents = envComponentRepository.findByCollectorItemId(application.getId());
	            envComponentRepository.delete(existingComponents);
	            envComponentRepository.save(compList);
	        }
			
			List<EnvironmentStatus> statusList = entry.getValue().stream().map(i -> convertToEnvironmentStatus(i, application.getId())).collect(Collectors.toList());
			if (!statusList.isEmpty()) {
	        	for (CdaApplication app : applications) {
	        		List<EnvironmentStatus> existingStatuses = environmentStatusRepository.findByCollectorItemId(app.getId());
	                environmentStatusRepository.delete(existingStatuses);
	                environmentStatusRepository.save(statusList);
				}
	        }
		}

    }
    
    private EnvironmentStatus convertToEnvironmentStatus(InstalledPackageComponent i, ObjectId id) {
        EnvironmentStatus status = new EnvironmentStatus();
        status.setCollectorItemId(id);
//        status.setComponentID(i.getComponentID());
        status.setComponentName(i.getComponent().getName());
        status.setEnvironmentName(i.getDeploymentTarget().getName());
        if (i.getExecution() != null) {
        	status.setOnline(i.getExecution().getStatus().isFinished());
        }  else {
        	status.setOnline(false);	
        }
        
        if (i.getArtifact() != null) {
        	status.setResourceName(i.getArtifact().getName());	
        }

        return status;
	}

	private EnvironmentComponent convertToEnvironmentComponent(InstalledPackageComponent installation, ObjectId collectorId) {
    	EnvironmentComponent envComponent = new EnvironmentComponent();
    	envComponent.setCollectorItemId(collectorId);
    	envComponent.setEnvironmentID(String.valueOf(installation.getDeploymentTarget().getId()));
    	envComponent.setEnvironmentName(installation.getDeploymentTarget().getName());
//    	envComponent.setComponentID(String.valueOf(installation.getComponent().getId()));
    	envComponent.setComponentName(installation.getComponent().getName());
    	envComponent.setComponentVersion(installation.getDeploymentPackage().getName());
        envComponent.setDeployed(installation.getInstallationState().isSuccess());
        envComponent.setAsOfDate(installation.getStartTime().getTime());
		return envComponent;
	}
    

    private List<CdaApplication> enabledApplications(CdaCollector collector) {
        return applicationRepository.findEnabledApplications(collector.getId());
    }

    /**
     * Add any new {@link CdaApplication}s.
     *
     * @param applications list of {@link CdaApplication}s
     * @param collector    the {@link CdaCollector}
     */
    private void addNewApplications(List<BaseEntityInfo> applications, CdaCollector collector) {
        long start = System.currentTimeMillis();
        int count = 0;

        log("All apps", start, applications.size());
        for (BaseEntityInfo application : applications) {
        	CdaApplication existing = findExistingApplication(collector, application);

            if (existing == null) {
            	existing = new CdaApplication();
            	existing.setApplicationName(application.getName());
            	existing.setApplicationId(application.getId().toString());
            	existing.setCollectorId(collector.getId());
            	existing.setEnabled(true);
            	existing.setDescription(application.getName());
            	existing.setNiceName(application.getName());
                try {
                    applicationRepository.save(existing);
                } catch (org.springframework.dao.DuplicateKeyException ce) {
                    log("Duplicates items not allowed", 0);

                }
                count++;
            } else  {
				existing.setNiceName(application.getName());
				applicationRepository.save(existing);
            }

        }
        log("New apps", start, count);
    }

    private CdaApplication findExistingApplication(CdaCollector collector, BaseEntityInfo application) {
        return applicationRepository.findCdaApplication(collector.getId(), String.valueOf(application.getId()));
    }
    
    @SuppressWarnings("unused")
	private boolean changed(EnvironmentStatus status, EnvironmentStatus existing) {
        return existing.isOnline() != status.isOnline();
    }

    @SuppressWarnings("unused")
	private EnvironmentStatus findExistingStatus(
            final EnvironmentStatus proposed,
            List<EnvironmentStatus> existingStatuses) {

        return Iterables.tryFind(existingStatuses,
                new Predicate<EnvironmentStatus>() {
                    @Override
                    public boolean apply(EnvironmentStatus existing) {
                        return existing.getEnvironmentName().equals(
                                proposed.getEnvironmentName())
                                && existing.getComponentName().equals(
                                proposed.getComponentName())
                                && existing.getResourceName().equals(
                                proposed.getResourceName());
                    }
                }).orNull();
    }

    @SuppressWarnings("unused")
	private boolean changed(EnvironmentComponent component,
                            EnvironmentComponent existing) {
        return existing.isDeployed() != component.isDeployed()
                || existing.getAsOfDate() != component.getAsOfDate() || !existing.getComponentVersion().equalsIgnoreCase(component.getComponentVersion());
    }

    @SuppressWarnings("unused")
	private EnvironmentComponent findExistingComponent(
            final EnvironmentComponent proposed,
            List<EnvironmentComponent> existingComponents) {

        return Iterables.tryFind(existingComponents,
                new Predicate<EnvironmentComponent>() {
                    @Override
                    public boolean apply(EnvironmentComponent existing) {
                        return existing.getEnvironmentName().equals(
                                proposed.getEnvironmentName())
                                && existing.getComponentName().equals(
                                proposed.getComponentName());

                    }
                }).orNull();
    }
}
