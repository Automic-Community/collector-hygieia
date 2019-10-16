package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.CdaApplication;
import com.capitalone.dashboard.repository.BaseCollectorItemRepository;

import java.util.List;

/**
 * Repository for {@link CdaApplication}s.
 */
public interface CdaApplicationRepository extends BaseCollectorItemRepository<CdaApplication> {

    /**
     * Find a {@link CdaApplication} by UDeploy instance URL and UDeploy application id.
     *
     * @param collectorId ID of the {@link com.capitalone.dashboard.model.CdaCollector}
     * @param applicationId UDeploy application ID
     * @return a {@link CdaApplication} instance
     */
    @Query(value="{ 'collectorId' : ?0, options.applicationId : ?1}")
    CdaApplication findCdaApplication(ObjectId collectorId, String applicationId);

    /**
     * Finds all {@link CdaApplication}.
     *
     * @param collectorId ID of the {@link com.capitalone.dashboard.model.CdaCollector}
     * @return list of {@link CdaApplication}s
     */
    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<CdaApplication> findEnabledApplications(ObjectId collectorId);
}
