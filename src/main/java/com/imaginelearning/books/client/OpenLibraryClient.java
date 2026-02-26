package com.imaginelearning.books.client;

import com.imaginelearning.books.dto.openlibrary.OpenLibrarySearchResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class OpenLibraryClient {

    private static final String SEARCH_URL = "https://openlibrary.org/search.json";

    private final RestTemplate restTemplate;

    public OpenLibraryClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public OpenLibrarySearchResponse search(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("q", query)
                .build()
                .encode()
                .toUri();

        OpenLibrarySearchResponse response = restTemplate.getForObject(uri, OpenLibrarySearchResponse.class);
        return response != null ? response : new OpenLibrarySearchResponse();
    }
}
