package com.automic.hygieia.rest.model;

import java.util.List;

public class SearchResult<T> {
	private boolean hasMore;
	private int total;
	private List<T> data;
	
	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
}