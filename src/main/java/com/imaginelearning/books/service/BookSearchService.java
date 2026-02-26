package com.imaginelearning.books.service;

import com.imaginelearning.books.client.OpenLibraryClient;
import com.imaginelearning.books.dto.api.BookSummary;
import com.imaginelearning.books.dto.openlibrary.OpenLibrarySearchResponse;
import com.imaginelearning.books.exception.BadRequestException;
import com.imaginelearning.books.mapper.BookMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class BookSearchService {

    private final OpenLibraryClient openLibraryClient;
    private final BookMapper bookMapper;

    public BookSearchService(OpenLibraryClient openLibraryClient, BookMapper bookMapper) {
        this.openLibraryClient = openLibraryClient;
        this.bookMapper = bookMapper;
    }

    public List<BookSummary> searchBooks(String query) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("q must be present and not blank");
        }

        OpenLibrarySearchResponse response = openLibraryClient.search(query);
        if (response.getDocs() == null) {
            return Collections.emptyList();
        }

        return response.getDocs().stream()
                .map(bookMapper::toBookSummary)
                .filter(Objects::nonNull)
                .toList();
    }
}
