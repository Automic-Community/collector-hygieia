package com.automic.hygieia.rest.model;

import lombok.Getter;

/**
 * <p>
 * State of deployment for each target.
 * <p>
 * 
 * @author nmt
 *
 */
public enum InstallationState {
	InProgress("In Progress"),
	Installed("Installed"),
	Invalid("Invalid"),
	Removed("Removed"),
	Replaced("Replaced");

    @Getter
    private String displayText;

	private InstallationState(final String displayText) {
		this.displayText = displayText;
	}

	public static InstallationState valueFrom(String valueString) {
		for (InstallationState state : InstallationState.values()) {
			if (state.toString().equals(valueString))
				return state;
		}
		return null;
	}
	
	public boolean isSuccess() {
		return Installed.equals(this);
	}
}
