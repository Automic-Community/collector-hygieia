package com.capitalone.dashboard.collector;

public interface IRestClient {
	<T> T execute(RestRequestBase<T> request);
}
