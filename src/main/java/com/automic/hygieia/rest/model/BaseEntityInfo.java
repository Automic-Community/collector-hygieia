package com.automic.hygieia.rest.model;

import lombok.Data;

@Data
public class BaseEntityInfo implements Comparable<BaseEntityInfo> {
    
    private Long id;
    
    private String name;

    public BaseEntityInfo() {}

    public BaseEntityInfo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        BaseEntityInfo other = (BaseEntityInfo) obj;
		if (!id.equals(other.id))
			return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int compareTo(BaseEntityInfo entity) {
        return this.getName().compareTo(entity.getName());
    }
}
