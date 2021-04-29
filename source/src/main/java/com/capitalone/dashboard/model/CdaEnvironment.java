package com.capitalone.dashboard.model;

/**
 * Represents a CDA environment by ID and name.
 */
public class CdaEnvironment {
    private String id;
    private String name;

    public CdaEnvironment(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
