package com.capitalone.dashboard.model;

import java.util.HashMap;
import java.util.Map;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.model.CollectorType;

import lombok.Getter;

/**
 * Collector implementation for UDeploy that stores UDeploy server URLs.
 */
public class CdaCollector extends Collector {
	@Getter
    private String url;

    public static CdaCollector prototype(String url) {
        CdaCollector protoType = new CdaCollector();
        protoType.setName("Cda");
        protoType.setCollectorType(CollectorType.Deployment);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.url = url;
        
        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(CdaApplication.APP_NAME,"");
        allOptions.put(CdaApplication.APP_ID, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(CdaApplication.APP_NAME,"");
        protoType.setUniqueFields(uniqueOptions);
        return protoType;
    }
}
