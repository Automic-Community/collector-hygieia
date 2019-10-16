package com.automic.hygieia.rest.model;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

public class InstalledPackageComponent {
	@Getter
    @SerializedName("id")
    private long id;
    
    @Getter
    @SerializedName("application")
    private BaseEntityInfo application;
    
    @Getter
    @SerializedName("component")
    private BaseEntityInfo component;
    
    @Getter
    @SerializedName("deployment_target")
    private BaseEntityInfo deploymentTarget;
    
    @Getter
    @SerializedName("package")
    private BaseEntityInfo deploymentPackage;
    
    @Getter
    private Execution execution;
    
    @Getter
    @SerializedName("artifact")
    private Artifact artifact;
    
    @Getter
    @SerializedName("start_time")
    private Date startTime;
    
    @Getter
    @SerializedName("end_time")
    private Date endTime;
    
    @Getter
    @SerializedName("deactivated_time")
    private Date deactivatedTime;
    
    @Getter
    @SerializedName("state")
    private InstallationState installationState;
    
    @Getter
    @SerializedName("is_current_installation")
    private boolean isCurrentInstallation;
    
    @Getter
    @SerializedName("is_active")
    private boolean isActive;

	public boolean canOpenEnvironment() {
		return execution != null && execution.getAraProfile() != null && execution.getAraEnvironment().getId() != null;
	}

	public boolean canOpenMonitor() {
		return execution != null && execution.getProcessRunId() > 0;
	}
	
	public String getStatusText() {
	    if (installationState == null) return "Unknown";
	    switch (installationState) {
	        case InProgress:
	            return "In Progess";
	        case Installed:
                return isActive ? "Installed" : "Installed (Replaced)";
	        case Invalid:
                return isActive ? "Invalid" : "Invalid (Replaced)";
	        case Removed:
                return "Removed";
            default:
                return "Unknown";
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
		InstalledPackageComponent other = (InstalledPackageComponent) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
