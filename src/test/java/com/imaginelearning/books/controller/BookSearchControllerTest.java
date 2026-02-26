package com.imaginelearning.books.controller;

import com.imaginelearning.books.dto.api.BookSummary;
import com.imaginelearning.books.exception.ExternalServiceBadResponseException;
import com.imaginelearning.books.exception.ExternalServiceUnavailableException;
import com.imaginelearning.books.service.BookSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookSearchController.class)
class BookSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookSearchService bookSearchService;

    @Test
    void returnsBooksForValidQuery() throws Exception {
        when(bookSearchService.searchBooks("hobbit"))
                .thenReturn(List.of(new BookSummary("/works/OL1W", "The Hobbit", "J.R.R. Tolkien")));

        mockMvc.perform(get("/api/books/search").param("q", "hobbit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("/works/OL1W"))
                .andExpect(jsonPath("$[0].title").value("The Hobbit"))
                .andExpect(jsonPath("$[0].author").value("J.R.R. Tolkien"));

        verify(bookSearchService).searchBooks("hobbit");
    }

    @Test
    void returnsEmptyArrayForValidQueryWithNoResults() throws Exception {
        when(bookSearchService.searchBooks("unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/books/search").param("q", "unknown"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void returnsBadRequestWhenQIsMissing() throws Exception {
        mockMvc.perform(get("/api/books/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("q must be present and not blank"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsBadRequestWhenQIsBlank() throws Exception {
        mockMvc.perform(get("/api/books/search").param("q", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("q must be present and not blank"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsBadRequestWhenQIsTooLong() throws Exception {
        String longQuery = "a".repeat(200);

        mockMvc.perform(get("/api/books/search").param("q", longQuery))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("q length must be less than 200 characters"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsBadRequestWhenQContainsBlockedKeywordCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/books/search").param("q", "History of NaZi Germany"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("q contains blocked keyword: nazi"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsBadGatewayWhenExternalServiceReturnsMalformedResponse() throws Exception {
        when(bookSearchService.searchBooks("hobbit"))
                .thenThrow(new ExternalServiceBadResponseException("External book service returned a malformed response."));

        mockMvc.perform(get("/api/books/search").param("q", "hobbit"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("BAD_GATEWAY"))
                .andExpect(jsonPath("$.message").value("External book service returned a malformed response."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsServiceUnavailableWhenExternalServiceTimesOut() throws Exception {
        when(bookSearchService.searchBooks("hobbit"))
                .thenThrow(new ExternalServiceUnavailableException("External book service is unavailable."));

        mockMvc.perform(get("/api/books/search").param("q", "hobbit"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("External book service is unavailable."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void returnsInternalServerErrorForUnhandledExceptions() throws Exception {
        when(bookSearchService.searchBooks("hobbit"))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/books/search").param("q", "hobbit"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
