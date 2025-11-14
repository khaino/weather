package com.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Base class for HTTP-based services
 * Provides common HTTP request functionality
 */
public abstract class BaseHttpService {
    
    protected static final int HTTP_OK = 200;
    
    protected final HttpClient client;
    protected final ObjectMapper mapper;

    protected BaseHttpService(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * Performs a GET request and returns the response body as string
     * 
     * @param url The URL to request
     * @param errorMessage Error message if request fails
     * @return Response body as string
     * @throws Exception if request fails or returns non-200 status
     */
    protected String performGetRequest(String url, String errorMessage) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
            
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != HTTP_OK) {
            throw new Exception(errorMessage + " (HTTP " + response.statusCode() + ")");
        }
        
        return response.body();
    }

    /**
     * Performs a GET request and parses JSON response into given class
     * 
     * @param url The URL to request
     * @param responseClass The class to deserialize response into
     * @param errorMessage Error message if request fails
     * @return Deserialized response object
     * @throws Exception if request fails or JSON parsing fails
     */
    protected <T> T performGetRequest(String url, Class<T> responseClass, String errorMessage) throws Exception {
        String responseBody = performGetRequest(url, errorMessage);
        return mapper.readValue(responseBody, responseClass);
    }
}

