package com.imaginelearning.books.controller;

import com.imaginelearning.books.dto.api.BookSummary;
import com.imaginelearning.books.exception.BadRequestException;
import com.imaginelearning.books.service.BookSearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@Validated
@RestController
public class BookSearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookSearchController.class);

    private static final List<String> BLOCKED_KEYWORDS = List.of("porn", "drugs", "nazi");

    private final BookSearchService bookSearchService;

    public BookSearchController(BookSearchService bookSearchService) {
        this.bookSearchService = bookSearchService;
    }

    @GetMapping("/api/books/search")
    public ResponseEntity<List<BookSummary>> searchBooks(
            @RequestParam("q")
            @NotBlank(message = "query must be present and not blank")
            @Size(max = 199, message = "query length must be less than 200 characters")
            String query
    ) {
        LOGGER.info("Received book search request for query='{}'", query);
        validateBlockedKeywords(query);
        List<BookSummary> books = bookSearchService.searchBooks(query);
        return ResponseEntity.ok(books);
    }

    private void validateBlockedKeywords(String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        for (String blockedKeyword : BLOCKED_KEYWORDS) {
            if (normalizedQuery.contains(blockedKeyword)) {
                throw new BadRequestException(query + "String contains blocked keyword: " + blockedKeyword);
            }
        }
    }
}
