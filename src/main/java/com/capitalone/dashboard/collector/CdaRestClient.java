package com.capitalone.dashboard.collector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.automic.hygieia.exception.CdaRestApiException;
import com.automic.hygieia.exception.CdaRestApiException.ApiExceptionContent;
import com.automic.hygieia.exception.CollectorException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class CdaRestClient implements IRestClient {
	protected final Logger LOGGER = LoggerFactory.getLogger(CdaRestClient.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private Gson gson;
    
    private CdaSettings settings;

    @Autowired
    public CdaRestClient(CdaSettings settings) {
    	this.settings = settings;
        gson = new GsonBuilder().setDateFormat(DATE_FORMAT)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    @SuppressWarnings("unchecked")
    public <T> T execute(RestRequestBase<T> request) {
        try {
			request.init();
            attachHeader(request);
            HttpResponse response = HttpClientBuilder.create()
                    .build()
                    .execute(request);
            Optional<HttpEntity> responseEntity = handleResponse(response);

            if (request.getResponseClass() == InputStream.class) {
                return (T) responseEntity.get().getContent();
            }

            String body = "";
            if (responseEntity.isPresent()) {
                body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            }
            LOGGER.debug(body);
            return request.parseResponse(body);
        } catch (UnsupportedOperationException unsupportEx) {
            throw new CollectorException(unsupportEx);
        } catch (IOException io) {
            throw new CollectorException("Send REST request failed", io);
        }
    }

    private Optional<HttpEntity> handleResponse(HttpResponse response) throws UnsupportedOperationException, IOException {
        if (isSuccessWithoutContent(response)) {
            return Optional.empty();
        }

        if (isSuccessWithContent(response)) {
            return Optional.of(response.getEntity());
        }

        String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

        if (isClientError(response)) {
            ApiExceptionContent apiException = gson.fromJson(body, ApiExceptionContent.class);
            LOGGER.error(apiException.getError());
            throw new CdaRestApiException(apiException);
        }

        String unexpectedException = MessageFormat.format("Release Manager error: {0}", body);
        LOGGER.error(unexpectedException);
        throw new CollectorException(response.getStatusLine().toString(), new RuntimeException(unexpectedException));
    }

    private void attachHeader(HttpRequestBase request) {
        request.setHeader("User-Agent", "Mozilla/5.0");
        request.setHeader(HttpHeaders.AUTHORIZATION, createAuthenticationHeader());
    }

    private String createAuthenticationHeader() {
        String auth = settings.getUsername() + ":" + settings.getPassword();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
        return String.format("Basic %s", new String(encodedAuth));
    }

    private boolean isSuccessWithContent(HttpResponse response) {
        return getSuccessStatusCode().stream()
                .anyMatch(x -> x == response.getStatusLine().getStatusCode());
    }

    private boolean isSuccessWithoutContent(HttpResponse response) {
        return HttpStatus.SC_NO_CONTENT == response.getStatusLine().getStatusCode();
    }

    private List<Integer> getSuccessStatusCode() {
        return Arrays.asList(
                HttpStatus.SC_OK,
                HttpStatus.SC_CREATED,
                HttpStatus.SC_ACCEPTED,
                HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION,
                HttpStatus.SC_RESET_CONTENT,
                HttpStatus.SC_PARTIAL_CONTENT,
                HttpStatus.SC_MULTI_STATUS,
                HttpStatus.SC_NO_CONTENT
        );
    }

    private boolean isClientError(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST 
    		|| response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN;
    }
}
