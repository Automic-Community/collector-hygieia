package com.automic.hygieia.rest.model;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

public class Execution {
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Getter
    @SerializedName("id")
    private long id;
    
    @Getter
    @SerializedName("application")
    private BaseEntityInfo araApplication;
    
    @Getter
    @SerializedName("workflow")
    private BaseEntityInfo araWorkflow;
    
    @Getter
    @SerializedName("package")
    private BaseEntityInfo araPackage;
    
    @Getter
    @SerializedName("environment")
    private BaseEntityInfo araEnvironment;
    
    @Getter
    @SerializedName("owner")
    private BaseEntityInfo owner;
    
    @Getter
    @SerializedName("start_by")
    private BaseEntityInfo startBy;
    
    @Getter
    @SerializedName("deployment_profile")
    private BaseEntityInfo araProfile;
    
    @Getter
    @SerializedName("status")
    private ExecutionStatus status;
    
    @Getter
    @SerializedName("process_run_id")
    private long processRunId;
    
    @SerializedName("actual_to")
    private Date actualTo;
    
    @SerializedName("actual_from")
    private Date actualFrom;
    
    @Getter
    @SerializedName("planned_from")
    private Date plannedFrom;
    
    @Getter
    @SerializedName("actual_duration")
    private String isoDuration;
    
    @Getter
    @SerializedName("mode")
    private WorkflowMode mode;
    
    @Getter
    @SerializedName("is_archived")
    private boolean isArchived;
    
    @Getter
    @SerializedName("manual_confirmer")
    private String manualConfirmer;
    
    @Getter
    @SerializedName("needs_manual_start")
    private boolean needManualStart;
        
    @Getter
    @SerializedName("install_mode")
    private String installMode;
    
    public String getActualTo() {
        if (actualTo == null) {
            return "";
        }
        return SIMPLE_DATE_FORMAT.format(actualTo);
    }
    
    public String getActualFrom() {
        if (actualFrom == null) {
            return "";
        }
        return SIMPLE_DATE_FORMAT.format(actualFrom);
    }

    public void setDuration(Long durationInSeconds) {
        isoDuration = printIsoDuration(durationInSeconds);
    }

    public String getDuration() {
        Duration duration = parseIsoDuration(isoDuration);
        if (duration == null) {
            return "";
        }
        return DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss", true);
    }

    private String printIsoDuration(Long durationInSeconds) {
        if (durationInSeconds == null) {
            return "";
        }
        return Duration.ofSeconds(durationInSeconds).toString();
    }

    private Duration parseIsoDuration(String isoDuration) {
        if (Strings.isNullOrEmpty(isoDuration)) {
            return null;
        }

        try {
            return Duration.parse(isoDuration);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Execution other = (Execution) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public static enum WorkflowMode {
	    Install, Uninstall
	}
}
