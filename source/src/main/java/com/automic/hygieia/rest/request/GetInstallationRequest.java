package com.automic.hygieia.rest.request;

import com.automic.hygieia.rest.model.InstalledPackageComponent;
import com.automic.hygieia.rest.model.SearchResult;
import com.capitalone.dashboard.collector.RestGetRequest;

public class GetInstallationRequest extends RestGetRequest<SearchResult<InstalledPackageComponent>> {

	public GetInstallationRequest(String basePath) {
		super("api/data/v1/installations", basePath);
	}

	@Override
	protected Class<? extends SearchResult<InstalledPackageComponent>> getResponseClass() {
		return Reponse.class;
	}

	public static class Reponse extends SearchResult<InstalledPackageComponent> {

    }
}
