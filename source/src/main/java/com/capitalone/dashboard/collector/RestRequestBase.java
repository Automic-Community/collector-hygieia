package com.capitalone.dashboard.collector;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;

import com.automic.hygieia.exception.CollectorException;
import com.automic.hygieia.utils.UrlUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class RestRequestBase<T> extends HttpRequestBase {

	private String basePath;
    private String path;
    protected Gson gson;

    public RestRequestBase(String path, String basePath) {
        this.path = path;
        this.basePath = basePath;
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    public void init() {
        try {
            String url = UrlUtils.combineUrl(basePath, combinePathWithParameters(path));
            setURI(new URI(url));
        } catch (URISyntaxException e) {
            throw new CollectorException("URI is invalid", e);
        } catch (IllegalArgumentException | IllegalAccessException | UnsupportedEncodingException e) {
            throw new CollectorException(e);
        }
    }

    protected T parseResponse(String response) {
        if (getResponseClass() == Void.class) {
            return null;
        }

        return gson.fromJson(response, getResponseClass());
    }

    protected boolean isLogResponse() {
    	return true;
    }
    
    protected String combinePathWithParameters(String path) throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException {
        return path;
    }

    protected abstract Class<? extends T> getResponseClass();
    
}
