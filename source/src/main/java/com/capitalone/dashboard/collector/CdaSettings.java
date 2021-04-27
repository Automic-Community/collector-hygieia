package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Bean to hold settings specific to the Cda collector.
 */
@Component
@ConfigurationProperties(prefix = "cda")
public class CdaSettings {
	
	@Getter
	@Setter
    private String cron;
	
	@Getter
	@Setter
    private String username;
	
	@Getter
	@Setter
    private String password;
	
	@Getter
	@Setter
    private String url;
	
	@Getter
	@Setter
    private String uiUrl;
}
