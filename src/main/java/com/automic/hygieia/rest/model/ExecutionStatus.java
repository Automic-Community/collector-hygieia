package com.automic.hygieia.rest.model;

import lombok.Getter;

public enum ExecutionStatus {
	Abend("Abend"),
	Waiting("Waiting"),
    WaitingApproval("Waiting for approval"),
    WaitingStartTime("Waiting for start"),
    WaitingManualConfirm("Waiting for confirmation"),
    Active("Active"),
    Blocked("Blocked"),
    Rejected("Rejected"),
    Revoked("Revoked"),
    Canceled("Canceled"),
    Finished("Finished"),
    Failed("Failed");
	
    @Getter
	private String displayText;
	private ExecutionStatus(String displayText) {
		this.displayText = displayText;
	}
	
    public boolean isInProgress() {
        return this == Active || this == Blocked;
    }
    
    public boolean isFinished() {
        return this == Finished;
    }
    
	public static ExecutionStatus valueFrom(String string) {
		for (ExecutionStatus state : ExecutionStatus.values()) {
			if (state.toString().equals(string) || state.getDisplayText().equals(string))
				return state;
		}
		return null;
	}
}