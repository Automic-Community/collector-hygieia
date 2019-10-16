package com.automic.hygieia.rest.request;

import com.automic.hygieia.rest.model.BaseEntityInfo;
import com.automic.hygieia.rest.model.SearchResult;
import com.capitalone.dashboard.collector.Parameter;
import com.capitalone.dashboard.collector.RestGetRequest;

public class GetApplicationsRequest extends RestGetRequest<SearchResult<BaseEntityInfo>> {

    @Parameter(name = "max_results")
    private int maxResults = Integer.MAX_VALUE;

    public GetApplicationsRequest(String basePath) {
        super("/api/data/v1/applications", basePath);
    }

    @Override
    protected Class<? extends SearchResult<BaseEntityInfo>> getResponseClass() {
        return Reponse.class;
    }

    public static class Reponse extends SearchResult<BaseEntityInfo> {

    }
}
