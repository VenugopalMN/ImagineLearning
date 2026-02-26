package com.imaginelearning.books.controller;

import com.imaginelearning.books.dto.api.BookSummary;
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
                .andExpect(jsonPath("$.message").value("q must be present and not blank"));
    }

    @Test
    void returnsBadRequestWhenQIsBlank() throws Exception {
        mockMvc.perform(get("/api/books/search").param("q", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("q must be present and not blank"));
    }

    @Test
    void returnsBadRequestWhenQIsTooLong() throws Exception {
        String longQuery = "a".repeat(200);

        mockMvc.perform(get("/api/books/search").param("q", longQuery))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("q length must be less than 200 characters"));
    }

    @Test
    void returnsBadRequestWhenQContainsBlockedKeywordCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/books/search").param("q", "History of NaZi Germany"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("q contains blocked keyword: nazi"));
    }
}
