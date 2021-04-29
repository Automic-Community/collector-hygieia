package com.capitalone.dashboard.model;

import com.capitalone.dashboard.model.CollectorItem;

public class CdaApplication extends CollectorItem {
    protected static final String APP_NAME = "applicationName";
    protected static final String APP_ID = "applicationId";
    
    public String getApplicationId() {
        return (String) getOptions().get(APP_ID);
    }
    
    public void setApplicationId(String id) {
        getOptions().put(APP_ID, id);
    }

    public String getApplicationName() {
        return (String) getOptions().get(APP_NAME);
    }

    public void setApplicationName(String name) {
        getOptions().put(APP_NAME, name);
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CdaApplication that = (CdaApplication) o;
        return getApplicationId().equals(that.getApplicationId()) && getApplicationName().equals(that.getApplicationName());
    }

    @Override
    public int hashCode() {
        int result = getApplicationName().hashCode();
        result = 31 * result + getApplicationId().hashCode();
        return result;
    }
}
