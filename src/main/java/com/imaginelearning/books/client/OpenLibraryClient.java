package com.imaginelearning.books.client;

import com.imaginelearning.books.dto.openlibrary.OpenLibrarySearchResponse;
import com.imaginelearning.books.exception.ExternalServiceBadResponseException;
import com.imaginelearning.books.exception.ExternalServiceUnavailableException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

@Component
public class OpenLibraryClient {

    private static final String SEARCH_URL = "https://openlibrary.org/search.json";

    private final RestTemplate restTemplate;

    public OpenLibraryClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    public OpenLibrarySearchResponse search(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("q", query)
                .build()
                .encode()
                .toUri();

        try {
            OpenLibrarySearchResponse response = restTemplate.getForObject(uri, OpenLibrarySearchResponse.class);
            if (response == null) {
                throw new ExternalServiceBadResponseException("External book service returned an empty response.");
            }

            return response;
        } catch (ResourceAccessException exception) {
            throw new ExternalServiceUnavailableException("External book service is unavailable.", exception);
        } catch (HttpMessageConversionException exception) {
            throw new ExternalServiceBadResponseException("External book service returned a malformed response.", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceBadResponseException("Failed to retrieve books from external service.", exception);
        }
    }
}
