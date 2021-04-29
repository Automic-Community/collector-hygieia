package com.automic.hygieia.rest.request;

import com.automic.hygieia.rest.model.BaseEntityInfo;
import com.automic.hygieia.rest.model.SearchResult;
import com.capitalone.dashboard.collector.Parameter;
import com.capitalone.dashboard.collector.RestGetRequest;

import lombok.Setter;

public class GetComponentsRequest extends RestGetRequest<SearchResult<BaseEntityInfo>> {
	
	@Setter
	@Parameter(name = "ids")
	private String ids;

	public GetComponentsRequest(String basePath) {
		super("/api/internal/installation/components", basePath);
	}

	@Override
    protected Class<? extends SearchResult<BaseEntityInfo>> getResponseClass() {
        return Reponse.class;
    }

    public static class Reponse extends SearchResult<BaseEntityInfo> {

    }
}
