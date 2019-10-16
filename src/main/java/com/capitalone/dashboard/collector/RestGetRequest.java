package com.capitalone.dashboard.collector;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

public abstract class RestGetRequest<T> extends RestRequestBase<T> {

    @Setter
    private boolean isRespectArchiveSettings = false;
    
    @Setter
    private Map<String, String> parameterMap = Maps.newHashMap();

    public RestGetRequest(String path, String basePath) {
        super(path, basePath);
    }

    public RestGetRequest(String path, String basePath, Object... args) {
        this(MessageFormat.format(path, args), basePath);
    }

    @Override
    protected String combinePathWithParameters(String path) throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException {
        URI uri = URI.create(path);
        URIBuilder builder = new URIBuilder(uri);

        getParameters().forEach((x, y) -> builder.addParameter(x, y));

        parameterMap.forEach((key, value) -> {
	        if(!Strings.isNullOrEmpty(value)) {
	        	builder.addParameter(key, value);
	        }
        });

        return builder.toString();
    }

    protected Map<String, String> getParameters() throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException {
    	Map<String, String> parameters = Maps.newHashMap();
        for (Field field : this.getClass().getDeclaredFields()) {
        	if (!field.isAnnotationPresent(Parameter.class)) {
        		continue;
        	}
            Parameter annotation = field.getAnnotation(Parameter.class);
            field.setAccessible(true);
            Object value = field.get(this);
            if (value == null) {
                continue;
            }
            
            String key = URLEncoder.encode(annotation.name(), "UTF-8");
            extractListToParameter(parameters, key, value);
            extractArrayToParameter(parameters, key, value);
            extractNormalTextToParameter(parameters, key, value, annotation.isWildCard());
        }
        return parameters;
    }
    
    private void extractNormalTextToParameter(Map<String, String> parameters, String key, Object value, boolean isWildCard) {
    	if(value instanceof List || value.getClass().isArray()) {
    		return;
    	}
    	String valueStr = value.toString();
        if(!Strings.isNullOrEmpty(valueStr)) {
        	valueStr = isWildCard ? String.format("*%s*", valueStr) : valueStr;
        	parameters.put(key, valueStr);
        }
    }
    
    private void extractArrayToParameter(Map<String, String> parameters, String key, Object value) {
    	if(value.getClass().isArray()) {
        	Object[] arrayObj =  (Object[]) value;
        	extractListToParameter(parameters, key, Arrays.asList(arrayObj));
        }
    }
    
    private void extractListToParameter(Map<String, String> parameters, String key, Object value) {
    	if(value instanceof List) {
        	List<?> listObj = (List<?>) value;
        	if(listObj.isEmpty()) {
        		return;
        	}
        	parameters.put(key, joinListValues(listObj));
        }
    }
    
    private String joinListValues(List<?> listObj) {
    	if(listObj == null || listObj.isEmpty()) return "";
    	return listObj.stream().map(x -> String.valueOf(x))
    			.collect(Collectors.joining(RestConstants.COMMA));
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Data
    @AllArgsConstructor
    public static class ParameterKeyValuePair {

        private String key;
        private String value;
    }

}
